package gbemu.cpu.z80;

/**
 * @author Adolph C.
 */
public class Z80ALU {
	private Z80Registers reg;

	public Z80ALU(Z80Registers reg) {
		this.reg = reg;
	}

	public int add8(int a, int b) {
		int result = a + b;
		checkZ8(result);
		reg.clearNFlag();
		reg.putHFlag((a & 0xf) + (b & 0xf) > 0xf);
		reg.putCFlag(result > 0xFF);
		return result & 0xFF;
	}

	public int adc8(int a, int b) {
		int result = a + b + (reg.getCFlag() ? 1 : 0);
		checkZ8(result);
		reg.clearNFlag();
		reg.putHFlag((a & 0xf) + (b & 0xf) + (reg.getCFlag() ? 1 : 0) > 0xf);
		reg.putCFlag(result > 0xFF);
		return result & 0xFF;
	}

	public int sub8(int a, int b) {
		int result = a - b;
		checkZ8(result);
		reg.setNFlag();
		reg.putHFlag(((a - b)&0xF) > (a & 0xF));
		reg.putCFlag(result < 0);
		return result;
	}

	public int sbc8(int a, int b) {
		int _c = (reg.getCFlag() ? 1 : 0);
		int temp = _c + b;
		int result = a - b - _c;
		checkZ8(result);
		reg.setNFlag();
		reg.putHFlag(((a & 0xF) - (b & 0xF) - _c) < 0);
		reg.putCFlag(result < 0);
		return result;
	}

	public int inc8(int a) {
		reg.putHFlag((a & 0xF) == 0xF);
		a++;
		checkZ8(a);
		reg.clearNFlag();
		return a;
	}

	public int dec8(int a) {
		a--;
		checkZ8(a);
		reg.setNFlag();
		reg.putHFlag((a & 0xF) == 0xF);
		return a;
	}

	public int xor(int a, int b) {
		int result = a ^ b;
		reg.setF(0);
		checkZ8(result);
		return result;
	}

	public int and(int a, int b) {
		int result = a & b;
		reg.setF(0);
		checkZ8(result);
		reg.setHFlag();
		return result;
	}

	public int or(int a, int b) {
		int result = a | b;
		reg.setF(0);
		checkZ8(result);
		return result;
	}

	public void cmp(int a, int b) {
		this.sub8(a, b);
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
		int result = a + b;
		reg.clearNFlag();
		reg.putHFlag(((result & 0x7FF) < (reg.getHL() & 0x7FF)));
		reg.putCFlag(result > 0xffff);
		return result;
	}

	public int sub16(int a, int b) {
		throw new UnsupportedOperationException("I don't feel like writing this for completeness.");
	}

	/**
	 * Rotate A left with carry.
	 */
	public void rlca() {
		reg.setA(this.rlc(reg.getA()));
		clearZNH();
	}

	/**
	 * Rotate A left.
	 */
	public void rla() {
		reg.setA(this.rl(reg.getA()));
		clearZNH();
	}

	/**
	 * Rotate A right.
	 */
	public void rra() {
		reg.setA(this.rr(reg.getA()));
		clearZNH();
	}

	/**
	 * Rotate A right with carry.
	 */
	public void rrca() {
		reg.setA(this.rrc(reg.getA()));
		clearZNH();
	}

	private void clearZNH() {
		reg.clearZFlag();
		reg.clearNFlag();
		reg.clearHFlag();
	}

	/**
	 * Adjust A for BCD addition.
	 */
	public void daa() {
		int a = reg.getA();
		if (!reg.getNFlag()) {
			if(reg.getHFlag() || ((a & 0xF) > 9)) a += 0x06;
			if(reg.getCFlag() || (a > 0x9F)) a += 0x60;
		} else {
			if (reg.getHFlag()) a = (a - 6) & 0xFF;
			if(reg.getCFlag()) a -= 0x60;
		}
		reg.clearHFlag();
		reg.clearZFlag();
		if((a & 0x100) == 0x100) {
			reg.setCFlag();
		}
		a &= 0xFF;
		checkZ8(a);
		reg.setA(a);
	}

	/**
	 * Complement (Logical Not) On A.
	 */
	public void cpl() {
		reg.setA(~reg.getA());
		reg.setNFlag();
		reg.setHFlag();
	}

	/**
	 * Rotate left with carry.
	 * @param i number to rotate
	 * @return rotated number
	 */
	public int rlc(int i) {
		int temp = ((i & 0x80) >> 7) & 1;
		reg.clearFlags();
		reg.putCFlag(temp == 1);
		i = (i << 1) | temp;
		checkZ8(i);
		return i;
	}

	/**
	 * Rotate right with carry.
	 * @param i number to rotate
	 * @return rotated number
	 */
	public int rrc(int i) {
		int temp = i & 1;
		reg.clearFlags();
		reg.putCFlag(temp == 1);
		i = (i >> 1) | (temp << 7);
		checkZ8(i);
		return i;
	}

	/**
	 * Rotate right.
	 * @param i number to rotate
	 * @return rotated number
	 */
	public int rr(int i) {
		int carry = reg.getCFlag() ? 0x80 : 0;
		reg.clearFlags();
		reg.putCFlag((i & 1) == 1);
		i = (i >> 1) | carry;
		checkZ8(i);
		return i;
	}

	/**
	 * Shift right preserving sign.
	 * @param i number to shift
	 * @return shifted number.
	 */
	public int sra(int i) {
		reg.clearFlags();
		reg.putCFlag((i & 1) == 1);
		int temp = i & 0x80;
		i = (i >> 1) | temp;
		checkZ8(i);
		return i;
	}

	/**
	 * Shift right.
	 * @param i number to shift
	 * @return shifted number.
	 */
	public int srl(int i) {
		reg.clearFlags();
		reg.putCFlag((i & 1) == 1);
		i = i >> 1;
		checkZ8(i);
		return i;
	}

	/**
	 * Test bit.
	 * @param i number to test
	 * @param b bit to test
	 */
	public void bit(int i, int b) {
		checkZ8((i & (1 << b)));
		reg.clearNFlag();
		reg.setHFlag();
	}

	/**
	 * Resets (clears) a bit
	 * @param i number to reset bit for
	 * @param b bit to reset
	 * @return number with bit reset
	 */
	public int res(int i, int b) {
		return i & ~(1 << b);
	}


	/**
	 * Sets a bit
	 * @param i number to set bit for
	 * @param b bit to reset
	 * @return number with bit set.
	 */
	public int set(int i, int b) {
		return i | (1 << b);
	}

	/**
	 * Rotates left
	 * @param i number to rotate
	 * @return rotated number
	 */
	public int rl(int i) {
		reg.putCFlag((i & 0x80) != 0);
		i = (i << 1) | (reg.getCFlag() ? 1 : 0);
		checkZ8(i);
		return i;
	}

	/**
	 * Shifts a number left preserving sign
	 * @param i number to shift
	 * @return shifted number
	 */
	public int sla(int i) {
		reg.putCFlag((i & 0x80) != 0);
		i = i << 1;
		checkZ8(i);
		return i;
	}

	/**
	 * Swaps the nybbles in a number.
	 * @param i number to swap nybbles for.
	 * @return number with nybbles swapped.
	 */
	public int swap(int i) {
		int result = ((i & 0xF) << 4) | ((i & 0xF0) >> 4);
		reg.setF(0);
		checkZ8(i);
		return result;
	}

	private void checkZ8(int a) {
		reg.putZFlag((a & 0xff) == 0);
	}

	private void checkZ16(int a) {
		reg.putZFlag((a & 0xffff) == 0);
	}
}
