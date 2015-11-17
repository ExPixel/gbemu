package gbemu.cpu.z80;

/**
 * <h2>Registers</h2>
 * <table><tbody><tr><td><pre>  16bit Hi   Lo   Name/Function
 * AF    A    -    Accumulator &amp; Flags
 * BC    B    C    BC
 * DE    D    E    DE
 * HL    H    L    HL
 * SP    -    -    Stack Pointer
 * PC    -    -    Program Counter/Pointer
 * </pre></td></tr></tbody></table>
 * <h2>Flags</h2>
 * <table><tbody><tr><td><pre>  Bit  Name  Set Clr  Expl.
 * 7    zf    Z   NZ   Zero Flag
 * 6    n     -   -    Add/Sub-Flag (BCD)
 * 5    h     -   -    Half Carry Flag (BCD)
 * 4    cy    C   NC   Carry Flag
 * 3-0  -     -   -    Not used (always zero)
 * </pre></td></tr></tbody></table>
 * @author Adolph C.
 */
public class Z80Registers {
	/**
	 * Accumulator & Flags
	 */
	private int AF;
	private int BC;
	private int DE;
	private int HL;

	/**
	 * Stack pointer
	 */
	private int SP;

	/**
	 * Program counter
	 */
	private int PC;

	private int setLow(int n, int low) {
		n &= ~0xff;
		return n | (low & 0xff);
	}

	private int setHigh(int n, int high) {
		n &= ~0xff00;
		return n | ((high & 0xff) << 8);
	}

	private int getLow(int n) {
		return n & 0xff;
	}

	private int getHigh(int n) {
		return (n >> 8) & 0xff;
	}

	public int getAF() { return AF & 0xffff; }
	public void setAF(int value) { AF = value & 0xffff; AF &= ~0xf; }

	public int getA() { return getHigh(AF); }
	public void setA(int value) { AF = setHigh(AF, value); }

	public int getF() { return getLow(AF); }
	public void setF(int value) { AF = setLow(AF, value); AF &= ~0xf; } // last 4 bits must be clear

	public int getBC() { return BC & 0xffff; }
	public void setBC(int value) { BC = value & 0xffff; }

	public int getB() { return getHigh(BC); }
	public void setB(int value) { BC = setHigh(BC, value); }

	public int getC() { return getLow(BC); }
	public void setC(int value) { BC = setLow(BC, value); }

	public int getDE() { return DE & 0xffff; }
	public void setDE(int value) { DE = value & 0xffff; }

	public int getD() { return getHigh(DE); }
	public void setD(int value) { DE = setHigh(DE, value); }

	public int getE() { return getLow(DE); }
	public void setE(int value) { DE = setLow(DE, value); }

	public int getHL() { return HL & 0xffff; }
	public void setHL(int value) { HL = value & 0xffff; }

	public int getH() { return getHigh(HL); }
	public void setH(int value) { HL = setHigh(HL, value); }

	public int getL() { return getLow(HL); }
	public void setL(int value) { HL = setLow(HL, value); }

	public int getSP() { return this.SP & 0xffff; }
	public void setSP(int value) { this.SP = value & 0xffff; }

	public int getPC() { return this.PC & 0xffff; }
	public void setPC(int value) {
		this.PC = value & 0xffff;
	}

	/** Sets Zero Flag */
	public void setZFlag() { AF |= 0b10000000; }
	/** Clears Zero Flag */
	public void clearZFlag() { AF &= ~0b10000000; }
	public boolean getZFlag() { return (AF & 0b10000000) != 0; }
	public void putZFlag(boolean v) {
		if(v) this.setZFlag();
		else this.clearZFlag();
	}

	/** Sets Add/Sub-Flag (BCD) */
	public void setNFlag() { AF |= 0b01000000; }
	/** Clears Add/Sub-Flag (BCD) */
	public void clearNFlag() { AF &= ~0b01000000; }
	public boolean getNFlag() { return (AF & 0b01000000) != 0; }
	public void putNFlag(boolean v) {
		if(v) this.setNFlag();
		else this.clearNFlag();
	}

	/** Sets Half Carry Flag (BCD) */
	public void setHFlag() { AF |= 0b00100000; }
	/** Clears Half Carry Flag (BCD) */
	public void clearHFlag() { AF &= ~0b00100000; }
	public boolean getHFlag() { return (AF & 0b00100000) != 0; }
	public void putHFlag(boolean v) {
		if(v) this.setHFlag();
		else this.clearHFlag();
	}

	/** Sets Carry Flag */
	public void setCFlag() { AF |= 0b00010000; }
	/** Clears Carry Flag */
	public void clearCFlag() { AF &= ~0b00010000; }
	public boolean getCFlag() { return (AF & 0b00010000) != 0; }
	public void putCFlag(boolean v) {
		if(v) this.setCFlag();
		else this.clearCFlag();
	}

	public void clearFlags() {
		AF &= ~0b11110000;
	}

	public void incPC() {
		this.PC++;
	}
}
