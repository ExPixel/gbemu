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
	public final Z80Registers reg;
	public final Z80Clock clock;
	public final Z80Executor executor;
	public final Z80ALU alu;
	private int lastExecutedAddress = 0;

	private FileWriter debugWriter;

	public Z80Cpu(GBMemory memory) {
		this.memory = memory;
		this.clock = new Z80Clock();
		this.reg = new Z80Registers();
		this.alu = new Z80ALU(reg);
		this.executor = new Z80Executor(this, memory, reg, clock, alu);
	}

	public int getLastExecutedAddress() {
		return lastExecutedAddress;
	}

	public void execute() {
		lastExecutedAddress = reg.getPC();
		int instr = this.memory.read8(this.reg.getPC());
		this.reg.setPC(this.reg.getPC() + 1);
		this.executor.execute(instr);
	}

	public long executeCycles(long targetCycles) {
		this.clock.clearCyclesElapsed();
		while(this.clock.getCyclesElapsed() < targetCycles)
			this.execute();
		return this.clock.getCyclesElapsed();
	}

	/**
	 * Called when the CPU makes a call to an address.
	 * @param address The address the call is being made to.
	 */
	public void onCall(int address) {
		debug("[%04x] call to %04x", lastExecutedAddress, address);
	}

	/**
	 * Called when the CPU jumps to an address.
	 * @param address The address the CPU attempts to jump to.
	 */
	public void onJump(int address) {
		debug("[%04x] jump to %04x", lastExecutedAddress, address);
	}

	/**
	 * Called when the CPU returns from a call.
	 * @param from The address that the CPU is returning from.
	 * @param to The address that the CPU is returning to.
	 */
	public void onRet(int from, int to) {
		debug("[%04x] return from %04x to %04x", lastExecutedAddress, from, to);
	}

	public void stop() {
		debug("[%04x] stopped", lastExecutedAddress);
	}

	public void halt() {
		debug("[%04x] halt", lastExecutedAddress);
	}

	public void removedInstr() {
		// Called when a removed instruction was called.
		// Just calls clock.inc(0) by default which should do nothing.
		clock.inc(0);
	}

	public void debug(String message) {
		if(debugWriter != null) {
			try {
				debugWriter.write(message);
				debugWriter.write('\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void debug(String message, Object...args) {
		debug(String.format(message, args));
	}

	public void setDebugMode(String file) {
		this.setDebugMode(new File(file));
	}

	public void setDebugMode(File file) {
		File parent = file.getParentFile();
		if(parent != null) parent.mkdirs();
		try {
			debugWriter = new FileWriter(file);
			Runtime.getRuntime().addShutdownHook(
					new Thread(() -> {
						try {
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
