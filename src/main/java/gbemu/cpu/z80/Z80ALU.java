package gbemu.cpu.z80;

/**
 * @author Adolph C.
 */
public class Z80ALU {
	private Z80Registers reg;

	public int add8(int a, int b) {
		return a + b;
	}

	public int adc8(int a, int b) {
		return a + b;
	}

	public int sub8(int a, int b) {
		return a + b;
	}

	public int sbc8(int a, int b) {
		return a - b;
	}

	public int inc8(int a) {
		return a + 1;
	}

	public int dec8(int a) {
		return a - 1;
	}

	public int xor(int a, int b) {
		return a ^ b;
	}

	public int and(int a, int b) {
		return a & b;
	}

	public int or(int a, int b) {
		return a | b;
	}

	public int cmp(int a, int b) {
		return a;
	}

	/**
	 * This sets no flags.
	 * @param a The number to increment.
	 * @return Incremented 16 bit integer.
	 */
	public int inc16(int a) {
		return (a + 1) & 0xffff;
	}

	public int dec16(int a) {
		return (a - 1) & 0xffff;
	}

	public int add16(int a, int b) {
		return a + b;
	}

	public int sub16(int a, int b) {
		return a - b;
	}

	/**
	 * Rotate A left with carry.
	 */
	public void rlca() {
	}

	/**
	 * Rotate A left.
	 */
	public void rla() {
	}

	/**
	 * Rotate A right.
	 */
	public void rra() {

	}

	/**
	 * Rotate A right with carry.
	 */
	public void rrca() {

	}

	/**
	 * Adjust A for BCD addition.
	 */
	public void daa() {
	}

	/**
	 * Complement (Logical Not) On A.
	 */
	public void cpl() {

	}
}
