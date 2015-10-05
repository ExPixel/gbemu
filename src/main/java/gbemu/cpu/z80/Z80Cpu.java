package gbemu.cpu.z80;

import gbemu.cpu.memory.IMemory;

/**
 * @author Adolph C.
 */
public class Z80Cpu {
	public final IMemory memory;
	public final Z80Registers reg;
	public final Z80Clock clock;
	public final Z80Executor executor;
	public final Z80ALU alu;

	public Z80Cpu(IMemory memory) {
		this.memory = memory;
		this.clock = new Z80Clock();
		this.reg = new Z80Registers();
		this.alu = new Z80ALU(reg);
		this.executor = new Z80Executor(this, memory, reg, clock, alu);
	}

	public void execute() {
		int instr = this.memory.read8(this.reg.getPC());
		this.reg.setPC(this.reg.getPC() + 1);
		this.executor.execute(instr);
	}

	public void stop() {

	}

	public void halt() {
	}

	public void removedInstr() {
		// Called when a removed instruction was called.
		// Just calls clock.inc(0) by default which should do nothing.
		clock.inc(0);
	}
}
