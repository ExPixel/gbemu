package gbemu.cpu.z80;

/**
 * @author Adolph C.
 */
public class Z80Clock {
	private long cycles = 0;

	public void inc(int amt) {
		cycles += amt;
	}

	public long getCycles() {
		return cycles;
	}

	public long getMachineCycles() {
		return cycles << 2; // cycles * 4
	}
}
