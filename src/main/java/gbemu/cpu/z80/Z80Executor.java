package gbemu.cpu.z80;

import gbemu.cpu.memory.IMemory;

/**
 * @author Adolph C.
 */
public class Z80Executor {
	private Z80Cpu cpu;
	private IMemory memory;
	private Z80Registers reg;
	private Z80Clock clock;
	private Z80ALU alu;

	/**
	 * I'm not sure what this constant really means to the Z80 CPU,
	 * but I do know that is is the GameBoy's IO ports.
	 */
	private int IO_LOC = 0xFF00;

	/**
	 * This is the next offset that will be read in a next8() pr next16() call.
	 * Set to PC + 1 on execute.
	 * ^^^ Disreguard the above, it is wrong. The PC is moved along and used instead.
	 */
	@Deprecated
	int address = 0;

	public void execute(int instr) {
		// work integers 1 & 2
		int w0 = 0, w1 = 0; // Have to define this outside (switch is one big scope)

		switch (instr & 0xff) {
			case 0x00: // opcode:NOP | flags:- - - - | length: 1
				clock.inc(4);
				break;
			case 0x01: // opcode:LD BC,d16 | flags:- - - - | length: 3
				reg.setBC(next16());
				clock.inc(12);
				break;
			case 0x02: // opcode:LD (BC),A | flags:- - - - | length: 1
				write8(reg.getBC(), reg.getA());
				clock.inc(8);
				break;
			case 0x03: // opcode:INC BC | flags:- - - - | length: 1
				reg.setBC(alu.inc16(reg.getBC()));
				clock.inc(8);
				break;
			case 0x04: // opcode:INC B | flags:Z 0 H - | length: 1
				reg.setB(alu.inc8(reg.getB()));
				clock.inc(4);
				break;
			case 0x05: // opcode:DEC B | flags:Z 1 H - | length: 1
				reg.setB(alu.dec8(reg.getB()));
				clock.inc(4);
				break;
			case 0x06: // opcode:LD B,d8 | flags:- - - - | length: 2
				reg.setB(next8());
				clock.inc(8);
				break;
			case 0x07: // opcode:RLCA | flags:0 0 0 C | length: 1
				alu.rlca();
				clock.inc(4);
				break;
			case 0x08: // opcode:LD (a16),SP | flags:- - - - | length: 3
				write16(next16(), reg.getSP());
				clock.inc(20);
				break;
			case 0x09: // opcode:ADD HL,BC | flags:- 0 H C | length: 1
				reg.setHL(alu.add16(reg.getHL(), reg.getBC()));
				clock.inc(8);
				break;
			case 0xA: // opcode:LD A,(BC) | flags:- - - - | length: 1
				reg.setA(read8(reg.getBC()));
				clock.inc(8);
				break;
			case 0xB: // opcode:DEC BC | flags:- - - - | length: 1
				reg.setBC(alu.dec16(reg.getBC()));
				clock.inc(8);
				break;
			case 0xC: // opcode:INC C | flags:Z 0 H - | length: 1
				reg.setC(alu.inc8(reg.getC()));
				clock.inc(4);
				break;
			case 0xD: // opcode:DEC C | flags:Z 1 H - | length: 1
				reg.setC(alu.dec8(reg.getC()));
				clock.inc(4);
				break;
			case 0xE: // opcode:LD C,d8 | flags:- - - - | length: 2
				reg.setC(next8());
				clock.inc(8);
				break;
			case 0xF: // opcode:RRCA | flags:0 0 0 C | length: 1
				alu.rrca();
				clock.inc(4);
				break;
			case 0x10: // opcode:STOP 0 | flags:- - - - | length: 2
				cpu.stop();
				clock.inc(4);
				break;
			case 0x11: // opcode:LD DE,d16 | flags:- - - - | length: 3
				reg.setDE(next16());
				clock.inc(12);
				break;
			case 0x12: // opcode:LD (DE),A | flags:- - - - | length: 1
				write8(reg.getDE(), reg.getA());
				clock.inc(8);
				break;
			case 0x13: // opcode:INC DE | flags:- - - - | length: 1
				reg.setDE(alu.inc16(reg.getDE()));
				clock.inc(8);
				break;
			case 0x14: // opcode:INC D | flags:Z 0 H - | length: 1
				reg.setD(alu.inc8(reg.getD()));
				clock.inc(4);
				break;
			case 0x15: // opcode:DEC D | flags:Z 1 H - | length: 1
				reg.setD(alu.dec8(reg.getD()));
				clock.inc(4);
				break;
			case 0x16: // opcode:LD D,d8 | flags:- - - - | length: 2
				reg.setD(next8());
				clock.inc(8);
				break;
			case 0x17: // opcode:RLA | flags:0 0 0 C | length: 1
				alu.rla();
				clock.inc(4);
				break;
			case 0x18: // opcode:JR r8 | flags:- - - - | length: 2
				reg.setPC(reg.getPC() + ext8(next8()));
				clock.inc(12);
				break;
			case 0x19: // opcode:ADD HL,DE | flags:- 0 H C | length: 1
				reg.setHL(reg.getHL() + reg.getDE());
				clock.inc(8);
				break;
			case 0x1A: // opcode:LD A,(DE) | flags:- - - - | length: 1
				reg.setA(memory.read8(reg.getDE()));
				clock.inc(8);
				break;
			case 0x1B: // opcode:DEC DE | flags:- - - - | length: 1
				reg.setDE(alu.dec16(reg.getDE()));
				clock.inc(8);
				break;
			case 0x1C: // opcode:INC E | flags:Z 0 H - | length: 1
				reg.setE(alu.inc8(reg.getE()));
				clock.inc(4);
				break;
			case 0x1D: // opcode:DEC E | flags:Z 1 H - | length: 1
				reg.setE(alu.dec8(reg.getE()));
				clock.inc(4);
				break;
			case 0x1E: // opcode:LD E,d8 | flags:- - - - | length: 2
				reg.setE(next8());
				clock.inc(8);
				break;
			case 0x1F: // opcode:RRA | flags:0 0 0 C | length: 1
				alu.rra();
				clock.inc(4);
				break;
			case 0x20: // opcode:JR NZ,r8 | flags:- - - - | length: 2
				if (!reg.getZFlag()) {
					reg.setPC(reg.getPC() + ext8(next8()));
					clock.inc(12);
				} else {
					next8();
					clock.inc(8);
				}
				break;
			case 0x21: // opcode:LD HL,d16 | flags:- - - - | length: 3
				reg.setHL(next16());
				clock.inc(12);
				break;
			case 0x22: // opcode:LD (HL+),A | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getA());
				reg.setHL(alu.inc16(reg.getHL()));
				clock.inc(8);
				break;
			case 0x23: // opcode:INC HL | flags:- - - - | length: 1
				reg.setHL(alu.inc16(reg.getHL()));
				clock.inc(8);
				break;
			case 0x24: // opcode:INC H | flags:Z 0 H - | length: 1
				reg.setH(alu.inc8(reg.getH()));
				clock.inc(4);
				break;
			case 0x25: // opcode:DEC H | flags:Z 1 H - | length: 1
				reg.setH(alu.dec8(reg.getH()));
				clock.inc(4);
				break;
			case 0x26: // opcode:LD H,d8 | flags:- - - - | length: 2
				reg.setH(next8());
				clock.inc(8);
				break;
			case 0x27: // opcode:DAA | flags:Z - 0 C | length: 1
				alu.daa();
				clock.inc(4);
				break;
			case 0x28: // opcode:JR Z,r8 | flags:- - - - | length: 2
				if (reg.getZFlag()) {
					reg.setPC(reg.getPC() + ext8(next8()));
					clock.inc(12);
				} else {
					next8();
					clock.inc(8);
				}
				break;
			case 0x29: // opcode:ADD HL,HL | flags:- 0 H C | length: 1
				reg.setHL(alu.add16(reg.getHL(), reg.getHL()));
				clock.inc(8);
				break;
			case 0x2A: // opcode:LD A,(HL+) | flags:- - - - | length: 1
				reg.setA(read16(reg.getHL()));
				reg.setHL(alu.inc16(reg.getHL()));
				clock.inc(8);
				break;
			case 0x2B: // opcode:DEC HL | flags:- - - - | length: 1
				reg.setHL(alu.dec16(reg.getHL()));
				clock.inc(8);
				break;
			case 0x2C: // opcode:INC L | flags:Z 0 H - | length: 1
				reg.setL(alu.inc8(reg.getL()));
				clock.inc(4);
				break;
			case 0x2D: // opcode:DEC L | flags:Z 1 H - | length: 1
				reg.setL(alu.dec8(reg.getL()));
				clock.inc(4);
				break;
			case 0x2E: // opcode:LD L,d8 | flags:- - - - | length: 2
				reg.setL(next8());
				clock.inc(8);
				break;
			case 0x2F: // opcode:CPL | flags:- 1 1 - | length: 1
				alu.cpl();
				clock.inc(4);
				break;
			case 0x30: // opcode:JR NC,r8 | flags:- - - - | length: 2
				if (!reg.getCFlag()) {
					reg.setPC(reg.getPC() + ext8(next8()));
					clock.inc(12);
				} else {
					next8();
					clock.inc(8);
				}
				break;
			case 0x31: // opcode:LD SP,d16 | flags:- - - - | length: 3
				reg.setSP(next16());
				clock.inc(12);
				break;
			case 0x32: // opcode:LD (HL-),A | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getA());
				reg.setHL(alu.sub16(reg.getHL(), 1));
				clock.inc(8);
				break;
			case 0x33: // opcode:INC SP | flags:- - - - | length: 1
				reg.setSP(alu.inc16(reg.getSP()));
				clock.inc(8);
				break;
			case 0x34: // opcode:INC (HL) | flags:Z 0 H - | length: 1
				w0 = read8(reg.getHL());
				w0 = alu.add8(w0, 1);
				write8(reg.getHL(), w0);
				clock.inc(12);
				break;
			case 0x35: // opcode:DEC (HL) | flags:Z 1 H - | length: 1
				w0 = read8(reg.getHL());
				w0 = alu.sub8(w0, 1);
				write8(reg.getHL(), w0);
				clock.inc(12);
				break;
			case 0x36: // opcode:LD (HL),d8 | flags:- - - - | length: 2
				write8(reg.getHL(), next8());
				clock.inc(12);
				break;
			case 0x37: // opcode:SCF | flags:- 0 0 1 | length: 1
				reg.clearNFlag();
				reg.clearHFlag();
				reg.setCFlag();
				clock.inc(4);
				break;
			case 0x38: // opcode:JR C,r8 | flags:- - - - | length: 2
				if (reg.getCFlag()) {
					reg.setPC(reg.getPC() + ext8(next8()));
					clock.inc(12);
				} else {
					next8();
					clock.inc(8);
				}
				break;
			case 0x39: // opcode:ADD HL,SP | flags:- 0 H C | length: 1
				reg.setHL(alu.add16(reg.getHL(), reg.getSP()));
				clock.inc(8);
				break;
			case 0x3A: // opcode:LD A,(HL-) | flags:- - - - | length: 1
				reg.setA(read8(reg.getHL()));
				reg.setHL(alu.inc16(reg.getHL()));
				clock.inc(8);
				break;
			case 0x3B: // opcode:DEC SP | flags:- - - - | length: 1
				reg.setSP(alu.dec16(reg.getSP()));
				clock.inc(8);
				break;
			case 0x3C: // opcode:INC A | flags:Z 0 H - | length: 1
				reg.setA(alu.inc8(reg.getA()));
				clock.inc(4);
				break;
			case 0x3D: // opcode:DEC A | flags:Z 1 H - | length: 1
				reg.setA(alu.dec8(reg.getA()));
				clock.inc(4);
				break;
			case 0x3E: // opcode:LD A,d8 | flags:- - - - | length: 2
				reg.setA(next8());
				clock.inc(8);
				break;
			case 0x3F: // opcode:CCF | flags:- 0 0 C | length: 1
				reg.clearNFlag();
				reg.clearHFlag();
				reg.setCFlag();
				clock.inc(4);
				break;
			case 0x40: // opcode:LD B,B | flags:- - - - | length: 1
				reg.setB(reg.getB());
				clock.inc(4);
				break;
			case 0x41: // opcode:LD B,C | flags:- - - - | length: 1
				reg.setB(reg.getC());
				clock.inc(4);
				break;
			case 0x42: // opcode:LD B,D | flags:- - - - | length: 1
				reg.setB(reg.getD());
				clock.inc(4);
				break;
			case 0x43: // opcode:LD B,E | flags:- - - - | length: 1
				reg.setB(reg.getE());
				clock.inc(4);
				break;
			case 0x44: // opcode:LD B,H | flags:- - - - | length: 1
				reg.setB(reg.getH());
				clock.inc(4);
				break;
			case 0x45: // opcode:LD B,L | flags:- - - - | length: 1
				reg.setB(reg.getL());
				clock.inc(4);
				break;
			case 0x46: // opcode:LD B,(HL) | flags:- - - - | length: 1
				reg.setB(read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0x47: // opcode:LD B,A | flags:- - - - | length: 1
				reg.setB(reg.getA());
				clock.inc(4);
				break;
			case 0x48: // opcode:LD C,B | flags:- - - - | length: 1
				reg.setC(reg.getB());
				clock.inc(4);
				break;
			case 0x49: // opcode:LD C,C | flags:- - - - | length: 1
				reg.setC(reg.getC());
				clock.inc(4);
				break;
			case 0x4A: // opcode:LD C,D | flags:- - - - | length: 1
				reg.setC(reg.getD());
				clock.inc(4);
				break;
			case 0x4B: // opcode:LD C,E | flags:- - - - | length: 1
				reg.setC(reg.getE());
				clock.inc(4);
				break;
			case 0x4C: // opcode:LD C,H | flags:- - - - | length: 1
				reg.setC(reg.getH());
				clock.inc(4);
				break;
			case 0x4D: // opcode:LD C,L | flags:- - - - | length: 1
				reg.setC(reg.getL());
				clock.inc(4);
				break;
			case 0x4E: // opcode:LD C,(HL) | flags:- - - - | length: 1
				reg.setC(read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0x4F: // opcode:LD C,A | flags:- - - - | length: 1
				reg.setC(reg.getA());
				clock.inc(4);
				break;
			case 0x50: // opcode:LD D,B | flags:- - - - | length: 1
				reg.setD(reg.getB());
				clock.inc(4);
				break;
			case 0x51: // opcode:LD D,C | flags:- - - - | length: 1
				reg.setD(reg.getC());
				clock.inc(4);
				break;
			case 0x52: // opcode:LD D,D | flags:- - - - | length: 1
				reg.setD(reg.getD());
				clock.inc(4);
				break;
			case 0x53: // opcode:LD D,E | flags:- - - - | length: 1
				reg.setD(reg.getE());
				clock.inc(4);
				break;
			case 0x54: // opcode:LD D,H | flags:- - - - | length: 1
				reg.setD(reg.getH());
				clock.inc(4);
				break;
			case 0x55: // opcode:LD D,L | flags:- - - - | length: 1
				reg.setD(reg.getL());
				clock.inc(4);
				break;
			case 0x56: // opcode:LD D,(HL) | flags:- - - - | length: 1
				reg.setD(read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0x57: // opcode:LD D,A | flags:- - - - | length: 1
				reg.setD(reg.getA());
				clock.inc(4);
				break;
			case 0x58: // opcode:LD E,B | flags:- - - - | length: 1
				reg.setE(reg.getB());
				clock.inc(4);
				break;
			case 0x59: // opcode:LD E,C | flags:- - - - | length: 1
				reg.setD(reg.getE());
				clock.inc(4);
				break;
			case 0x5A: // opcode:LD E,D | flags:- - - - | length: 1
				reg.setE(reg.getD());
				clock.inc(4);
				break;
			case 0x5B: // opcode:LD E,E | flags:- - - - | length: 1
				reg.setE(reg.getE());
				clock.inc(4);
				break;
			case 0x5C: // opcode:LD E,H | flags:- - - - | length: 1
				reg.setE(reg.getH());
				clock.inc(4);
				break;
			case 0x5D: // opcode:LD E,L | flags:- - - - | length: 1
				reg.setE(reg.getL());
				clock.inc(4);
				break;
			case 0x5E: // opcode:LD E,(HL) | flags:- - - - | length: 1
				reg.setE(read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0x5F: // opcode:LD E,A | flags:- - - - | length: 1
				reg.setE(reg.getA());
				clock.inc(4);
				break;
			case 0x60: // opcode:LD H,B | flags:- - - - | length: 1
				reg.setH(reg.getB());
				clock.inc(4);
				break;
			case 0x61: // opcode:LD H,C | flags:- - - - | length: 1
				reg.setH(reg.getC());
				clock.inc(4);
				break;
			case 0x62: // opcode:LD H,D | flags:- - - - | length: 1
				reg.setH(reg.getD());
				clock.inc(4);
				break;
			case 0x63: // opcode:LD H,E | flags:- - - - | length: 1
				reg.setH(reg.getE());
				clock.inc(4);
				break;
			case 0x64: // opcode:LD H,H | flags:- - - - | length: 1
				reg.setH(reg.getH());
				clock.inc(4);
				break;
			case 0x65: // opcode:LD H,L | flags:- - - - | length: 1
				reg.setH(reg.getL());
				clock.inc(4);
				break;
			case 0x66: // opcode:LD H,(HL) | flags:- - - - | length: 1
				reg.setH(read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0x67: // opcode:LD H,A | flags:- - - - | length: 1
				reg.setH(reg.getA());
				clock.inc(4);
				break;
			case 0x68: // opcode:LD L,B | flags:- - - - | length: 1
				reg.setL(reg.getB());
				clock.inc(4);
				break;
			case 0x69: // opcode:LD L,C | flags:- - - - | length: 1
				reg.setL(reg.getC());
				clock.inc(4);
				break;
			case 0x6A: // opcode:LD L,D | flags:- - - - | length: 1
				reg.setL(reg.getD());
				clock.inc(4);
				break;
			case 0x6B: // opcode:LD L,E | flags:- - - - | length: 1
				reg.setL(reg.getE());
				clock.inc(4);
				break;
			case 0x6C: // opcode:LD L,H | flags:- - - - | length: 1
				reg.setL(reg.getH());
				clock.inc(4);
				break;
			case 0x6D: // opcode:LD L,L | flags:- - - - | length: 1
				reg.setL(reg.getL());
				clock.inc(4);
				break;
			case 0x6E: // opcode:LD L,(HL) | flags:- - - - | length: 1
				reg.setL(read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0x6F: // opcode:LD L,A | flags:- - - - | length: 1
				reg.setL(reg.getA());
				clock.inc(4);
				break;
			case 0x70: // opcode:LD (HL),B | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getB());
				clock.inc(8);
				break;
			case 0x71: // opcode:LD (HL),C | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getC());
				clock.inc(8);
				break;
			case 0x72: // opcode:LD (HL),D | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getD());
				clock.inc(8);
				break;
			case 0x73: // opcode:LD (HL),E | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getE());
				clock.inc(8);
				break;
			case 0x74: // opcode:LD (HL),H | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getH());
				clock.inc(8);
				break;
			case 0x75: // opcode:LD (HL),L | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getL());
				clock.inc(8);
				break;
			case 0x76: // opcode:HALT | flags:- - - - | length: 1
				cpu.halt();
				clock.inc(4);
				break;
			case 0x77: // opcode:LD (HL),A | flags:- - - - | length: 1
				write8(reg.getHL(), reg.getA());
				clock.inc(8);
				break;
			case 0x78: // opcode:LD A,B | flags:- - - - | length: 1
				reg.setA(reg.getB());
				clock.inc(4);
				break;
			case 0x79: // opcode:LD A,C | flags:- - - - | length: 1
				reg.setA(reg.getC());
				clock.inc(4);
				break;
			case 0x7A: // opcode:LD A,D | flags:- - - - | length: 1
				reg.setA(reg.getD());
				clock.inc(4);
				break;
			case 0x7B: // opcode:LD A,E | flags:- - - - | length: 1
				reg.setA(reg.getE());
				clock.inc(4);
				break;
			case 0x7C: // opcode:LD A,H | flags:- - - - | length: 1
				reg.setA(reg.getH());
				clock.inc(4);
				break;
			case 0x7D: // opcode:LD A,L | flags:- - - - | length: 1
				reg.setA(reg.getL());
				clock.inc(4);
				break;
			case 0x7E: // opcode:LD A,(HL) | flags:- - - - | length: 1
				reg.setA(read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0x7F: // opcode:LD A,A | flags:- - - - | length: 1
				reg.setA(reg.getA());
				clock.inc(4);
				break;
			case 0x80: // opcode:ADD A,B | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0x81: // opcode:ADD A,C | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0x82: // opcode:ADD A,D | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0x83: // opcode:ADD A,E | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0x84: // opcode:ADD A,H | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0x85: // opcode:ADD A,L | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0x86: // opcode:ADD A,(HL) | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0x87: // opcode:ADD A,A | flags:Z 0 H C | length: 1
				reg.setA(alu.add8(reg.getA(), reg.getA()));
				clock.inc(4);
				break;
			case 0x88: // opcode:ADC A,B | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0x89: // opcode:ADC A,C | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0x8A: // opcode:ADC A,D | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0x8B: // opcode:ADC A,E | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0x8C: // opcode:ADC A,H | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0x8D: // opcode:ADC A,L | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0x8E: // opcode:ADC A,(HL) | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0x8F: // opcode:ADC A,A | flags:Z 0 H C | length: 1
				reg.setA(alu.adc8(reg.getA(), reg.getA()));
				clock.inc(4);
				break;
			case 0x90: // opcode:SUB B | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0x91: // opcode:SUB C | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0x92: // opcode:SUB D | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0x93: // opcode:SUB E | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0x94: // opcode:SUB H | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0x95: // opcode:SUB L | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0x96: // opcode:SUB (HL) | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0x97: // opcode:SUB A | flags:Z 1 H C | length: 1
				reg.setA(alu.sub8(reg.getA(), reg.getA()));
				clock.inc(4);
				break;
			case 0x98: // opcode:SBC A,B | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0x99: // opcode:SBC A,C | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0x9A: // opcode:SBC A,D | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0x9B: // opcode:SBC A,E | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0x9C: // opcode:SBC A,H | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0x9D: // opcode:SBC A,L | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0x9E: // opcode:SBC A,(HL) | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0x9F: // opcode:SBC A,A | flags:Z 1 H C | length: 1
				reg.setA(alu.sbc8(reg.getA(), reg.getA()));
				clock.inc(4);
				break;
			case 0xA0: // opcode:AND B | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0xA1: // opcode:AND C | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0xA2: // opcode:AND D | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0xA3: // opcode:AND E | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0xA4: // opcode:AND H | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0xA5: // opcode:AND L | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0xA6: // opcode:AND (HL) | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0xA7: // opcode:AND A | flags:Z 0 1 0 | length: 1
				reg.setA(alu.and(reg.getA(), reg.getA()));
				clock.inc(4);
				break;
			case 0xA8: // opcode:XOR B | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0xA9: // opcode:XOR C | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0xAA: // opcode:XOR D | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0xAB: // opcode:XOR E | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0xAC: // opcode:XOR H | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0xAD: // opcode:XOR L | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0xAE: // opcode:XOR (HL) | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0xAF: // opcode:XOR A | flags:Z 0 0 0 | length: 1
				reg.setA(alu.xor(reg.getA(), reg.getA()));
				clock.inc(4);
				break;
			case 0xB0: // opcode:OR B | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0xB1: // opcode:OR C | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0xB2: // opcode:OR D | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0xB3: // opcode:OR E | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0xB4: // opcode:OR H | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0xB5: // opcode:OR L | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0xB6: // opcode:OR (HL) | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0xB7: // opcode:OR A | flags:Z 0 0 0 | length: 1
				reg.setB(alu.or(reg.getA(), reg.getA()));
				clock.inc(4);
				break;
			case 0xB8: // opcode:CP B | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), reg.getB());
				clock.inc(4);
				break;
			case 0xB9: // opcode:CP C | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), reg.getC());
				clock.inc(4);
				break;
			case 0xBA: // opcode:CP D | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), reg.getD());
				clock.inc(4);
				break;
			case 0xBB: // opcode:CP E | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), reg.getE());
				clock.inc(4);
				break;
			case 0xBC: // opcode:CP H | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), reg.getH());
				clock.inc(4);
				break;
			case 0xBD: // opcode:CP L | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), reg.getL());
				clock.inc(4);
				break;
			case 0xBE: // opcode:CP (HL) | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), read8(reg.getHL()));
				clock.inc(8);
				break;
			case 0xBF: // opcode:CP A | flags:Z 1 H C | length: 1
				alu.cmp(reg.getA(), reg.getA());
				clock.inc(4);
				break;
			case 0xC0: // opcode:RET NZ | flags:- - - - | length: 1
				if (!reg.getZFlag()) {
					reg.setPC(pop16());
					clock.inc(20);
				} else {
					clock.inc(8);
				}
				break;
			case 0xC1: // opcode:POP BC | flags:- - - - | length: 1
				reg.setBC(pop16());
				clock.inc(12);
				break;
			case 0xC2: // opcode:JP NZ,a16 | flags:- - - - | length: 3
				if(!reg.getZFlag()) {
					reg.setPC(next16());
					clock.inc(16);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xC3: // opcode:JP a16 | flags:- - - - | length: 3
				reg.setPC(next16());
				clock.inc(16);
				break;
			case 0xC4: // opcode:CALL NZ,a16 | flags:- - - - | length: 3
				if(!reg.getZFlag()) {
					call(next16());
					clock.inc(24);
				} else {
					clock.inc(12);
				}
				break;
			case 0xC5: // opcode:PUSH BC | flags:- - - - | length: 1
				push16(reg.getBC());
				clock.inc(16);
				break;
			case 0xC6: // opcode:ADD A,d8 | flags:Z 0 H C | length: 2
				reg.setA(alu.add8(reg.getA(), next8()));
				clock.inc(8);
				break;
			case 0xC7: // opcode:RST 00H | flags:- - - - | length: 1
				call(0);
				clock.inc(16);
				break;
			case 0xC8: // opcode:RET Z | flags:- - - - | length: 1
				if(reg.getZFlag()) {
					reg.setPC(pop16());
					clock.inc(20);
				} else {
					clock.inc(8);
				}
				break;
			case 0xC9: // opcode:RET | flags:- - - - | length: 1
				reg.setPC(pop16());
				clock.inc(16);
				break;
			case 0xCA: // opcode:JP Z,a16 | flags:- - - - | length: 3
				if(reg.getZFlag()) {
					reg.setPC(next16());
					clock.inc(16);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xCB: // opcode:PREFIX CB | flags:- - - - | length: 1
				this.executeCB(next8());
				clock.inc(4);
				break;
			case 0xCC: // opcode:CALL Z,a16 | flags:- - - - | length: 3
				if(reg.getZFlag()) {
					call(next16());
					clock.inc(24);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xCD: // opcode:CALL a16 | flags:- - - - | length: 3
				call(next16());
				clock.inc(24);
				break;
			case 0xCE: // opcode:ADC A,d8 | flags:Z 0 H C | length: 2
				reg.setA(alu.adc8(reg.getA(), next8()));
				clock.inc(8);
				break;
			case 0xCF: // opcode:RST 08H | flags:- - - - | length: 1
				call(0x08);
				clock.inc(16);
				break;
			case 0xD0: // opcode:RET NC | flags:- - - - | length: 1
				if(!reg.getNFlag()) {
					reg.setPC(pop16());
					clock.inc(20);
				} else {
					clock.inc(8);
				}
				break;
			case 0xD1: // opcode:POP DE | flags:- - - - | length: 1
				reg.setDE(pop16());
				clock.inc(12);
				break;
			case 0xD2: // opcode:JP NC,a16 | flags:- - - - | length: 3
				if(!reg.getCFlag()) {
					reg.setPC(next16());
					clock.inc(16);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xD4: // opcode:CALL NC,a16 | flags:- - - - | length: 3
				if(!reg.getCFlag()) {
					call(next16());
					clock.inc(24);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xD5: // opcode:PUSH DE | flags:- - - - | length: 1
				push16(reg.getDE());
				clock.inc(16);
				break;
			case 0xD6: // opcode:SUB d8 | flags:Z 1 H C | length: 2
				reg.setA(alu.sub8(reg.getA(), next8()));
				clock.inc(8);
				break;
			case 0xD7: // opcode:RST 10H | flags:- - - - | length: 1
				call(0x10);
				clock.inc(16);
				break;
			case 0xD8: // opcode:RET C | flags:- - - - | length: 1
				if(reg.getCFlag()) {
					reg.setPC(pop16());
					clock.inc(20);
				} else {
					clock.inc(8);
				}
				break;
			case 0xD9: // opcode:RETI | flags:- - - - | length: 1
				// clock.inc(16);
				cpu.removedInstr();
				break;
			case 0xDA: // opcode:JP C,a16 | flags:- - - - | length: 3
				if(reg.getCFlag()) {
					reg.setPC(next16());
					clock.inc(16);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xDC: // opcode:CALL C,a16 | flags:- - - - | length: 3
				if(reg.getCFlag()) {
					call(next16());
					clock.inc(24);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xDE: // opcode:SBC A,d8 | flags:Z 1 H C | length: 2
				reg.setA(alu.sbc8(reg.getA(), next8()));
				clock.inc(8);
				break;
			case 0xDF: // opcode:RST 18H | flags:- - - - | length: 1
				call(0x18);
				clock.inc(16);
				break;
			case 0xE0: // opcode:LDH (a8),A | flags:- - - - | length: 2
				write8(IO_LOC + next8(), reg.getA());
				clock.inc(12);
				break;
			case 0xE1: // opcode:POP HL | flags:- - - - | length: 1
				reg.setHL(pop16());
				clock.inc(12);
				break;
			case 0xE2: // opcode:LDH (C),A | flags:- - - - | length: 2
				// todo this might not actually be an LDH
				write8(IO_LOC + reg.getC(), reg.getA());
				clock.inc(8);
				break;
			case 0xE5: // opcode:PUSH HL | flags:- - - - | length: 1
				push16(reg.getHL());
				clock.inc(16);
				break;
			case 0xE6: // opcode:AND d8 | flags:Z 0 1 0 | length: 2
				reg.setA(alu.and(reg.getA(), next8()));
				clock.inc(8);
				break;
			case 0xE7: // opcode:RST 20H | flags:- - - - | length: 1
				call(0x20);
				clock.inc(16);
				break;
			case 0xE8: // opcode:ADD SP,r8 | flags:0 0 H C | length: 2
				// todo make sure the following code is correct.
				w0 = reg.getSP() + next8();
				reg.clearZFlag();
				reg.clearNFlag();
				reg.putCFlag( (w0 & 0xFF) < (reg.getSP() & 0xFF) );
				reg.putHFlag( (w0 & 0xF) < (reg.getSP() & 0xF) );
				reg.setSP(w0);
				clock.inc(16);
				break;
			case 0xE9: // opcode:JP (HL) | flags:- - - - | length: 1
				reg.setPC(read16(reg.getHL()));
				clock.inc(4);
				break;
			case 0xEA: // opcode:LD (a16),A | flags:- - - - | length: 3
				clock.inc(16);
				break;
			case 0xEE: // opcode:XOR d8 | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0xEF: // opcode:RST 28H | flags:- - - - | length: 1
				clock.inc(16);
				break;
			case 0xF0: // opcode:LDH A,(a8) | flags:- - - - | length: 2
				clock.inc(12);
				break;
			case 0xF1: // opcode:POP AF | flags:Z N H C | length: 1
				clock.inc(12);
				break;
			case 0xF2: // opcode:LD A,(C) | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF3: // opcode:DI | flags:- - - - | length: 1
				clock.inc(4);
				break;
			case 0xF5: // opcode:PUSH AF | flags:- - - - | length: 1
				clock.inc(16);
				break;
			case 0xF6: // opcode:OR d8 | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0xF7: // opcode:RST 30H | flags:- - - - | length: 1
				clock.inc(16);
				break;
			case 0xF8: // opcode:LD HL,SP+r8 | flags:0 0 H C | length: 2
				clock.inc(12);
				break;
			case 0xF9: // opcode:LD SP,HL | flags:- - - - | length: 1
				clock.inc(8);
				break;
			case 0xFA: // opcode:LD A,(a16) | flags:- - - - | length: 3
				clock.inc(16);
				break;
			case 0xFB: // opcode:EI | flags:- - - - | length: 1
				clock.inc(4);
				break;
			case 0xFE: // opcode:CP d8 | flags:Z 1 H C | length: 2
				clock.inc(8);
				break;
			case 0xFF: // opcode:RST 38H | flags:- - - - | length: 1
				clock.inc(16);
			default:
				System.err.printf("Undefined instruction %02x\n", instr);
				break;
		}
	}

	private void executeCB(int instr) {
//		this.address = this.reg.getPC();
		switch (instr & 0xff) {
			case 0x0: // opcode:RLC B | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x1: // opcode:RLC C | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x2: // opcode:RLC D | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x3: // opcode:RLC E | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x4: // opcode:RLC H | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x5: // opcode:RLC L | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x6: // opcode:RLC (HL) | flags:Z 0 0 C | length: 2
				clock.inc(16);
				break;
			case 0x7: // opcode:RLC A | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x8: // opcode:RRC B | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x9: // opcode:RRC C | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0xA: // opcode:RRC D | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0xB: // opcode:RRC E | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0xC: // opcode:RRC H | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0xD: // opcode:RRC L | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0xE: // opcode:RRC (HL) | flags:Z 0 0 C | length: 2
				clock.inc(16);
				break;
			case 0xF: // opcode:RRC A | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x10: // opcode:RL B | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x11: // opcode:RL C | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x12: // opcode:RL D | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x13: // opcode:RL E | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x14: // opcode:RL H | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x15: // opcode:RL L | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x16: // opcode:RL (HL) | flags:Z 0 0 C | length: 2
				clock.inc(16);
				break;
			case 0x17: // opcode:RL A | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x18: // opcode:RR B | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x19: // opcode:RR C | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x1A: // opcode:RR D | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x1B: // opcode:RR E | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x1C: // opcode:RR H | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x1D: // opcode:RR L | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x1E: // opcode:RR (HL) | flags:Z 0 0 C | length: 2
				clock.inc(16);
				break;
			case 0x1F: // opcode:RR A | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x20: // opcode:SLA B | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x21: // opcode:SLA C | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x22: // opcode:SLA D | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x23: // opcode:SLA E | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x24: // opcode:SLA H | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x25: // opcode:SLA L | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x26: // opcode:SLA (HL) | flags:Z 0 0 C | length: 2
				clock.inc(16);
				break;
			case 0x27: // opcode:SLA A | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x28: // opcode:SRA B | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x29: // opcode:SRA C | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x2A: // opcode:SRA D | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x2B: // opcode:SRA E | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x2C: // opcode:SRA H | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x2D: // opcode:SRA L | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x2E: // opcode:SRA (HL) | flags:Z 0 0 0 | length: 2
				clock.inc(16);
				break;
			case 0x2F: // opcode:SRA A | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x30: // opcode:SWAP B | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x31: // opcode:SWAP C | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x32: // opcode:SWAP D | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x33: // opcode:SWAP E | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x34: // opcode:SWAP H | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x35: // opcode:SWAP L | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x36: // opcode:SWAP (HL) | flags:Z 0 0 0 | length: 2
				clock.inc(16);
				break;
			case 0x37: // opcode:SWAP A | flags:Z 0 0 0 | length: 2
				clock.inc(8);
				break;
			case 0x38: // opcode:SRL B | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x39: // opcode:SRL C | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x3A: // opcode:SRL D | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x3B: // opcode:SRL E | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x3C: // opcode:SRL H | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x3D: // opcode:SRL L | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x3E: // opcode:SRL (HL) | flags:Z 0 0 C | length: 2
				clock.inc(16);
				break;
			case 0x3F: // opcode:SRL A | flags:Z 0 0 C | length: 2
				clock.inc(8);
				break;
			case 0x40: // opcode:BIT 0,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x41: // opcode:BIT 0,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x42: // opcode:BIT 0,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x43: // opcode:BIT 0,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x44: // opcode:BIT 0,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x45: // opcode:BIT 0,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x46: // opcode:BIT 0,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x47: // opcode:BIT 0,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x48: // opcode:BIT 1,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x49: // opcode:BIT 1,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x4A: // opcode:BIT 1,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x4B: // opcode:BIT 1,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x4C: // opcode:BIT 1,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x4D: // opcode:BIT 1,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x4E: // opcode:BIT 1,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x4F: // opcode:BIT 1,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x50: // opcode:BIT 2,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x51: // opcode:BIT 2,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x52: // opcode:BIT 2,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x53: // opcode:BIT 2,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x54: // opcode:BIT 2,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x55: // opcode:BIT 2,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x56: // opcode:BIT 2,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x57: // opcode:BIT 2,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x58: // opcode:BIT 3,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x59: // opcode:BIT 3,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x5A: // opcode:BIT 3,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x5B: // opcode:BIT 3,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x5C: // opcode:BIT 3,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x5D: // opcode:BIT 3,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x5E: // opcode:BIT 3,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x5F: // opcode:BIT 3,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x60: // opcode:BIT 4,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x61: // opcode:BIT 4,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x62: // opcode:BIT 4,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x63: // opcode:BIT 4,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x64: // opcode:BIT 4,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x65: // opcode:BIT 4,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x66: // opcode:BIT 4,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x67: // opcode:BIT 4,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x68: // opcode:BIT 5,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x69: // opcode:BIT 5,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x6A: // opcode:BIT 5,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x6B: // opcode:BIT 5,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x6C: // opcode:BIT 5,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x6D: // opcode:BIT 5,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x6E: // opcode:BIT 5,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x6F: // opcode:BIT 5,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x70: // opcode:BIT 6,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x71: // opcode:BIT 6,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x72: // opcode:BIT 6,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x73: // opcode:BIT 6,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x74: // opcode:BIT 6,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x75: // opcode:BIT 6,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x76: // opcode:BIT 6,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x77: // opcode:BIT 6,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x78: // opcode:BIT 7,B | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x79: // opcode:BIT 7,C | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x7A: // opcode:BIT 7,D | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x7B: // opcode:BIT 7,E | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x7C: // opcode:BIT 7,H | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x7D: // opcode:BIT 7,L | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x7E: // opcode:BIT 7,(HL) | flags:Z 0 1 - | length: 2
				clock.inc(16);
				break;
			case 0x7F: // opcode:BIT 7,A | flags:Z 0 1 - | length: 2
				clock.inc(8);
				break;
			case 0x80: // opcode:RES 0,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x81: // opcode:RES 0,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x82: // opcode:RES 0,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x83: // opcode:RES 0,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x84: // opcode:RES 0,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x85: // opcode:RES 0,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x86: // opcode:RES 0,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0x87: // opcode:RES 0,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x88: // opcode:RES 1,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x89: // opcode:RES 1,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x8A: // opcode:RES 1,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x8B: // opcode:RES 1,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x8C: // opcode:RES 1,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x8D: // opcode:RES 1,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x8E: // opcode:RES 1,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0x8F: // opcode:RES 1,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x90: // opcode:RES 2,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x91: // opcode:RES 2,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x92: // opcode:RES 2,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x93: // opcode:RES 2,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x94: // opcode:RES 2,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x95: // opcode:RES 2,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x96: // opcode:RES 2,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0x97: // opcode:RES 2,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x98: // opcode:RES 3,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x99: // opcode:RES 3,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x9A: // opcode:RES 3,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x9B: // opcode:RES 3,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x9C: // opcode:RES 3,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x9D: // opcode:RES 3,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0x9E: // opcode:RES 3,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0x9F: // opcode:RES 3,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA0: // opcode:RES 4,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA1: // opcode:RES 4,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA2: // opcode:RES 4,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA3: // opcode:RES 4,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA4: // opcode:RES 4,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA5: // opcode:RES 4,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA6: // opcode:RES 4,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xA7: // opcode:RES 4,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA8: // opcode:RES 5,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xA9: // opcode:RES 5,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xAA: // opcode:RES 5,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xAB: // opcode:RES 5,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xAC: // opcode:RES 5,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xAD: // opcode:RES 5,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xAE: // opcode:RES 5,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xAF: // opcode:RES 5,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB0: // opcode:RES 6,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB1: // opcode:RES 6,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB2: // opcode:RES 6,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB3: // opcode:RES 6,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB4: // opcode:RES 6,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB5: // opcode:RES 6,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB6: // opcode:RES 6,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xB7: // opcode:RES 6,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB8: // opcode:RES 7,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xB9: // opcode:RES 7,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xBA: // opcode:RES 7,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xBB: // opcode:RES 7,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xBC: // opcode:RES 7,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xBD: // opcode:RES 7,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xBE: // opcode:RES 7,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xBF: // opcode:RES 7,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC0: // opcode:SET 0,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC1: // opcode:SET 0,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC2: // opcode:SET 0,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC3: // opcode:SET 0,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC4: // opcode:SET 0,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC5: // opcode:SET 0,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC6: // opcode:SET 0,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xC7: // opcode:SET 0,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC8: // opcode:SET 1,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xC9: // opcode:SET 1,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xCA: // opcode:SET 1,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xCB: // opcode:SET 1,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xCC: // opcode:SET 1,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xCD: // opcode:SET 1,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xCE: // opcode:SET 1,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xCF: // opcode:SET 1,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD0: // opcode:SET 2,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD1: // opcode:SET 2,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD2: // opcode:SET 2,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD3: // opcode:SET 2,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD4: // opcode:SET 2,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD5: // opcode:SET 2,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD6: // opcode:SET 2,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xD7: // opcode:SET 2,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD8: // opcode:SET 3,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xD9: // opcode:SET 3,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xDA: // opcode:SET 3,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xDB: // opcode:SET 3,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xDC: // opcode:SET 3,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xDD: // opcode:SET 3,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xDE: // opcode:SET 3,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xDF: // opcode:SET 3,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE0: // opcode:SET 4,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE1: // opcode:SET 4,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE2: // opcode:SET 4,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE3: // opcode:SET 4,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE4: // opcode:SET 4,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE5: // opcode:SET 4,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE6: // opcode:SET 4,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xE7: // opcode:SET 4,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE8: // opcode:SET 5,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xE9: // opcode:SET 5,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xEA: // opcode:SET 5,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xEB: // opcode:SET 5,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xEC: // opcode:SET 5,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xED: // opcode:SET 5,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xEE: // opcode:SET 5,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xEF: // opcode:SET 5,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF0: // opcode:SET 6,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF1: // opcode:SET 6,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF2: // opcode:SET 6,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF3: // opcode:SET 6,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF4: // opcode:SET 6,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF5: // opcode:SET 6,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF6: // opcode:SET 6,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xF7: // opcode:SET 6,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF8: // opcode:SET 7,B | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xF9: // opcode:SET 7,C | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xFA: // opcode:SET 7,D | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xFB: // opcode:SET 7,E | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xFC: // opcode:SET 7,H | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xFD: // opcode:SET 7,L | flags:- - - - | length: 2
				clock.inc(8);
				break;
			case 0xFE: // opcode:SET 7,(HL) | flags:- - - - | length: 2
				clock.inc(16);
				break;
			case 0xFF: // opcode:SET 7,A | flags:- - - - | length: 2
				clock.inc(8);
				break;
		}
	}

	private void call(int addr) {
		this.push16(reg.getPC());
		reg.setPC(addr);
	}

	private int pop16() {
		int data = read16(reg.getSP());
		reg.setSP(reg.getSP() + 2);
		return data;
	}

	private void push16(int data) {
		reg.setSP(reg.getSP() - 2);
		write16(reg.getSP(), data);
	}

	/**
	 * Sign extends an 8 bit integer to a 32 bit signed integer.
	 *
	 * @param n The number to sign extend.
	 * @return The sign extended integer.
	 */
	private int ext8(int n) {
		return (n << 24) >> 24;
	}

	private int nextAddr8() {
		reg.setPC(reg.getPC() + 1);
		return reg.getPC();
	}

	private int nextAddr16() {
		reg.setPC(reg.getPC() + 2);
		return reg.getPC();
	}

	private int read8(int address) {
		return memory.read8(address);
	}

	private void write8(int address, int value) {
		memory.write8(address, value);
	}

	private int read16(int address) {
		return memory.read8(address);
	}

	private void write16(int address, int value) {
		memory.write16(address, value);
	}

	private int next8() {
		return this.read8(this.nextAddr8());
	}

	private int next16() {
		return this.read16(this.nextAddr16());
	}
}
