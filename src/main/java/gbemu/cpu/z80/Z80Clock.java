package gbemu.cpu.z80;

/**
 * @author Adolph C.
 */
public class Z80Clock {
	/**
	 * Monotonic clock.
	 */
	private long cycles = 0;
	private long cyclesElapsed = 0;
	private long clockSpeed = 4194304; // Hz
	private long divCycles = 0;
	private long divIncCycles = 256;
	private long timaCycles = 0;

	private Z80Cpu cpu;

	public Z80Clock(Z80Cpu cpu) {
		this.cpu = cpu;
	}

	public void inc(long amt) {
		cyclesElapsed += amt;
		cycles += amt;
		this.incIO(amt);
	}

	public void incIO(long amt) {
		divCycles += amt >> 2;
		if(cpu.memory.ioPorts._TAC_TIMER_RUNNING)
			timaCycles += amt >> 2;
	}

	public long getCycles() {
		return cycles;
	}

	public long getMachineCycles() {
		return cycles >> 2;
	}

	public long getCyclesElapsed() {
		return cyclesElapsed;
	}

	public void clearCyclesElapsed() {
		cyclesElapsed = 0;
	}

	public void updateTimerRegisters() {
		if(divCycles >= divIncCycles) {
			cpu.memory.ioPorts.DIV = cpu.memory.ioPorts.DIV++ & 0xFF;
			divCycles -= divIncCycles;
		}
		if(cpu.memory.ioPorts._TAC_TIMER_RUNNING && timaCycles > (clockSpeed / cpu.memory.ioPorts._TAC_SPEED)) {
			timaCycles = 0;
			int tima = cpu.memory.ioPorts.TIMA + 1;
			if((tima & 0xFF) == 0) {
				// timer overflow has occured.
				tima = cpu.memory.ioPorts.TMA;
				this.cpu.fireTimerInterrupt();
			}
			cpu.memory.ioPorts.TIMA = tima;
		}
	}
}
