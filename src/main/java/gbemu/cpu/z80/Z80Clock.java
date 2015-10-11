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

	public void inc(int amt) {
		cyclesElapsed += amt;
		cycles += amt;
	}

	public long getCycles() {
		return cycles;
	}

	public long getMachineCycles() {
		return cycles / 4;
	}

	public long getCyclesElapsed() {
		return cyclesElapsed;
	}

	public void clearCyclesElapsed() {
		cyclesElapsed = 0;
	}
}
