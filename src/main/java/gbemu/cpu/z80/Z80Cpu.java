package gbemu.cpu.z80;

import gbemu.cpu.memory.GBMemory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Adolph C.
 */
public class Z80Cpu {
	public final GBMemory memory;
	public final GameBoyMiscFunctions functions;
	public final Z80Registers reg;
	public final Z80Clock clock;
	public final Z80Executor executor;
	public final Z80ALU alu;
	private int lastExecutedAddress = 0;
	private boolean halted;

	private FileWriter debugWriter;
	private String lastDebugMessage;
	private int debugMessageCount = 0;

	public Z80Cpu(GBMemory memory) {
		this.memory = memory;
		this.clock = new Z80Clock(this);
		this.reg = new Z80Registers();
		this.alu = new Z80ALU(reg);
		this.functions = new GameBoyMiscFunctions(this, memory);
		this.executor = new Z80Executor(this, memory, reg, clock, alu);
	}

	public boolean handleInterrupts() {
		int interrupts = memory.ioPorts.IF & (memory.ioPorts.IE & 0xf);
		if(interrupts == 0) return false; // No interrupt has occured so we end here.
		this.halted = false; // CPU is no longer halted.
		if(this.memory.ioPorts.IME != 0) {
			this.memory.ioPorts.IME = 0;
			int vector;
			if((interrupts & 0x1) != 0) {
				// V-Blank Interrupt
				memory.ioPorts.IF &= ~0x1;
				vector = 0x40;
			} else if((interrupts & 0x3) != 0) {
				// LCD STAT Interrupt
				memory.ioPorts.IF &= ~0x3;
				vector = 0x48;
			} else if((interrupts & 0x7) != 0) {
				// Timer Interrupt
				memory.ioPorts.IF &= ~0x7;
				vector = 0x50;
			} else if((interrupts & 0xf) != 0) {
				// Serial Interrupt
				memory.ioPorts.IF &= ~0xf;
				vector = 0x58;
			} else /* if((interrupts & 0x1f) != 0) */ {
				// Joypad Interrupt
				memory.ioPorts.IF &= ~0x1f;
				vector = 0x60;
			}
			this.executor.call(vector);
//			System.out.println(String.format("Jump to vector: %04x", vector));
			return true;
		}
		return false;
	}

	public void fireVBlankInterrupt() {
		this.memory.ioPorts.IF |= 0x1;
	}

	public void fireLCDStatInterrupt() {
		this.memory.ioPorts.IF |= 0x3;
	}

	public void fireTimerInterrupt() {
		this.memory.ioPorts.IF |= 0x7;
	}

	public void fireSerialInterrupt() {
		this.memory.ioPorts.IF |= 0xF;
	}

	public void fireJoypadInterrupt() {
		this.memory.ioPorts.IF |= 0x1f;
	}

	public int getLastExecutedAddress() {
		return lastExecutedAddress;
	}

	public void execute() {
		if(this.halted) return;
		if(this.handleInterrupts()) return;
		lastExecutedAddress = reg.getPC();
		int instr = this.memory.read8(this.reg.getPC());
		this.reg.incPC();
		this.executor.execute(instr);
		this.clock.updateTimerRegisters();
	}

	public long executeCycles(long targetCycles) {
		if(this.halted) {
			this.clock.incIO(targetCycles);
			this.clock.updateTimerRegisters();
			this.handleInterrupts();
			return 0;
		}
		this.clock.clearCyclesElapsed();
		while(this.clock.getCyclesElapsed() < targetCycles && !this.halted) {
			this.execute();
			this.functions.cycle();
			// todo remove test code
//			if(lastExecutedAddress >= 0xC338 && lastExecutedAddress <= 0xC359) {
//				GeneralDebug.printfn("[%04x] %04x %04x %04x %04x %04x", this.getLastExecutedAddress(), reg.getAF(), reg.getBC(), reg.getDE(), reg.getHL(), reg.getSP());
//			}
		}
		return this.clock.getCyclesElapsed();
	}

	public boolean isHalted() {
		return halted;
	}

	/**
	 * Called when the CPU makes a call to an address.
	 * @param address The address the call is being made to.
	 */
	public void onCall(int address) {
		// todo remove debug code.
		// debug("[%04x] call to %04x", lastExecutedAddress, address);
	}

	/**
	 * Called when the CPU jumps to an address.
	 * @param address The address the CPU attempts to jump to.
	 */
	public void onJump(int address) {
		// todo remove debug code.
		// debug("[%04x] jump to %04x", lastExecutedAddress, address);
	}

	/**
	 * Called when the CPU returns from a call.
	 * @param from The address that the CPU is returning from.
	 * @param to The address that the CPU is returning to.
	 */
	public void onRet(int from, int to) {
		// todo remove debug code.
		// debug("[%04x] return from %04x to %04x", lastExecutedAddress, from, to);
	}

	public void stop() {
		// todo remove debug code.
		// debug("[%04x] stopped", lastExecutedAddress);
	}

	public void halt() {
		// todo remove debug code.
		// debug("[%04x] halt", lastExecutedAddress);
		this.halted = true;
	}

	public void removedInstr() {
		// Called when a removed instruction was called.
		// Just calls clock.inc(0) by default which should do nothing.
		clock.inc(0);
	}

	public void debug(String message) {
		if(debugWriter == null) return;
		if(this.lastDebugMessage == null) {
			this.lastDebugMessage = message;
		} else {
			if(this.lastDebugMessage.hashCode() == message.hashCode()) {
				if(this.lastDebugMessage.equals(message)) {
					this.debugMessageCount++;
					return;
				}
			}
			this.debugWriteOut();
			this.lastDebugMessage = message;
		}
	}

	public void debugWriteOut() {
		if(lastDebugMessage != null && debugWriter != null) {
			try {
				if(this.debugMessageCount == 0) {
					debugWriter.write(this.lastDebugMessage);
				} else {
					debugWriter.write(this.lastDebugMessage + " | x" + this.debugMessageCount);
				}
				this.lastDebugMessage = null;
				debugWriter.write('\n');
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				this.debugMessageCount = 0;
			}
		}
	}

	public void debug(String message, Object...args) {
		if(debugWriter == null) return;
		debug(String.format(message, args));
	}

	public void setDebugMode(String file) {
		this.setDebugMode(new File(file));
	}

	public void setDebugMode(File file) {
		File parent = file.getParentFile();
		if(parent != null) //noinspection ResultOfMethodCallIgnored
			parent.mkdirs();
		try {
			debugWriter = new FileWriter(file);
			Runtime.getRuntime().addShutdownHook(
					new Thread(() -> {
						try {
							debugWriteOut();
							debugWriter.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					})
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
