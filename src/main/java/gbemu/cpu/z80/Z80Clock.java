package gbemu.cpu.z80;

/**
 * @author Adolph C.
 */
public class Z80Clock {
	private long cycles = 0;

	public void inc(int amt) {
		cycles += amt;
	}
}
