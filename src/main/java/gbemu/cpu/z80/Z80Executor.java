package gbemu.cpu.z80;

import gbemu.cpu.memory.GBMemory;

/**
 * @author Adolph C.
 */
public class Z80Executor {
	private Z80Cpu cpu;
	private GBMemory memory;
	private Z80Registers reg;
	private Z80Clock clock;
	private Z80ALU alu;

	public Z80Executor(Z80Cpu cpu, GBMemory memory, Z80Registers reg, Z80Clock clock, Z80ALU alu) {
		this.cpu = cpu;
		this.memory = memory;
		this.reg = reg;
		this.clock = clock;
		this.alu = alu;
	}

	/**
	 * I'm not sure what this constant really means to the Z80 CPU,
	 * but I do know that is is the GameBoy's IO ports.
	 */
	private final static int IO_LOC = 0xFF00;

	/**
	 * This is the next offset that will be read in a next8() pr next16() call.
	 * Set to PC + 1 on execute.
	 * ^^^ Disreguard the above, it is wrong. The PC is moved along and used instead.
	 */
	@Deprecated
	int address = 0;

	public void execute(int instr) {
		// work integers 1 & 2
		int w0; // Have to define this outside (switch is one big scope)

//		if(cpu.getLastExecutedAddress() == 0x0661) {
//			System.out.print("");
//		}
//
//		if(cpu.getLastExecutedAddress() == 0x06f1) {
//			System.out.print("");
//		}

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
				w0 = ext8(next8());
				reg.setPC(reg.getPC() + w0);
				cpu.onJump(reg.getPC());
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
					w0 = ext8(next8());
					reg.setPC(reg.getPC() + w0);
					cpu.onJump(reg.getPC());
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
					w0 = ext8(next8());
					reg.setPC(reg.getPC() + w0);
					cpu.onJump(reg.getPC());
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
					w0 = ext8(next8());
					reg.setPC(reg.getPC() + w0);
					cpu.onJump(reg.getPC());
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
				reg.setHL(alu.dec16(reg.getHL()));
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
					w0 = ext8(next8());
					reg.setPC(reg.getPC() + w0);
					cpu.onCall(reg.getPC());
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
				// todo make sure that this is right.
				reg.clearNFlag();
				reg.clearHFlag();
				reg.putCFlag(!reg.getCFlag());
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
				reg.setA(alu.or(reg.getA(), reg.getB()));
				clock.inc(4);
				break;
			case 0xB1: // opcode:OR C | flags:Z 0 0 0 | length: 1
				reg.setA(alu.or(reg.getA(), reg.getC()));
				clock.inc(4);
				break;
			case 0xB2: // opcode:OR D | flags:Z 0 0 0 | length: 1
				reg.setA(alu.or(reg.getA(), reg.getD()));
				clock.inc(4);
				break;
			case 0xB3: // opcode:OR E | flags:Z 0 0 0 | length: 1
				reg.setA(alu.or(reg.getA(), reg.getE()));
				clock.inc(4);
				break;
			case 0xB4: // opcode:OR H | flags:Z 0 0 0 | length: 1
				reg.setA(alu.or(reg.getA(), reg.getH()));
				clock.inc(4);
				break;
			case 0xB5: // opcode:OR L | flags:Z 0 0 0 | length: 1
				reg.setA(alu.or(reg.getA(), reg.getL()));
				clock.inc(4);
				break;
			case 0xB6: // opcode:OR (HL) | flags:Z 0 0 0 | length: 1
				reg.setA(alu.or(reg.getA(), read8(reg.getHL())));
				clock.inc(8);
				break;
			case 0xB7: // opcode:OR A | flags:Z 0 0 0 | length: 1
				reg.setA(alu.or(reg.getA(), reg.getA()));
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
					cpu.onJump(reg.getPC());
					clock.inc(16);
				} else {
					next16();
					clock.inc(12);
				}
				break;
			case 0xC3: // opcode:JP a16 | flags:- - - - | length: 3
				reg.setPC(next16());
				cpu.onJump(reg.getPC());
				clock.inc(16);
				break;
			case 0xC4: // opcode:CALL NZ,a16 | flags:- - - - | length: 3
				if(!reg.getZFlag()) {
					call(next16());
					clock.inc(24);
				} else {
					next16();
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
					ret();
					clock.inc(20);
				} else {
					clock.inc(8);
				}
				break;
			case 0xC9: // opcode:RET | flags:- - - - | length: 1
				ret();
				clock.inc(16);
				break;
			case 0xCA: // opcode:JP Z,a16 | flags:- - - - | length: 3
				if(reg.getZFlag()) {
					reg.setPC(next16());
					cpu.onJump(reg.getPC());
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
					ret();
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
					cpu.onJump(reg.getPC());
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
					ret();
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
					cpu.onJump(reg.getPC());
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
				// fixme make sure the following code is correct.
				w0 = reg.getSP() + next8();
				reg.clearZFlag();
				reg.clearNFlag();
				reg.putCFlag( (w0 & 0xFF) < (reg.getSP() & 0xFF) );
				reg.putHFlag( (w0 & 0xF) < (reg.getSP() & 0xF) );
				reg.setSP(w0);
				clock.inc(16);
				break;
			case 0xE9: // opcode:JP (HL) | flags:- - - - | length: 1
				// fixme confusing parenthesis, is (HL) an address or just the register?
				reg.setPC(reg.getHL());
				cpu.onJump(reg.getPC());
				clock.inc(4);
				break;
			case 0xEA: // opcode:LD (a16),A | flags:- - - - | length: 3
				write8(next16(), reg.getA());
				clock.inc(16);
				break;
			case 0xEE: // opcode:XOR d8 | flags:Z 0 0 0 | length: 2
				reg.setA(alu.xor(reg.getA(), next8()));
				clock.inc(8);
				break;
			case 0xEF: // opcode:RST 28H | flags:- - - - | length: 1
				call(0x28);
				clock.inc(16);
				break;
			case 0xF0: // opcode:LDH A,(a8) | flags:- - - - | length: 2
				reg.setA(read8(IO_LOC + next8()));
				clock.inc(12);
				break;
			case 0xF1: // opcode:POP AF | flags:Z N H C | length: 1
				reg.setAF(pop16());
				clock.inc(12);
				break;
			case 0xF2: // opcode:LD A,(C) | flags:- - - - | length: 2
				// clock.inc(8);
				cpu.removedInstr();
				break;
			case 0xF3: // opcode:DI | flags:- - - - | length: 1
				// todo Should I set (0xffffh) to 1, 0, 0xff????
				clock.inc(4);
				break;
			case 0xF5: // opcode:PUSH AF | flags:- - - - | length: 1
				push16(reg.getAF());
				clock.inc(16);
				break;
			case 0xF6: // opcode:OR d8 | flags:Z 0 0 0 | length: 2
				reg.setA(alu.or(reg.getA(), next8()));
				clock.inc(8);
				break;
			case 0xF7: // opcode:RST 30H | flags:- - - - | length: 1
				call(0x30);
				clock.inc(16);
				break;
			case 0xF8: // opcode:LD HL,SP+r8 | flags:0 0 H C | length: 2
				// fixme make sure the following code is correct.
				w0 = reg.getSP() + next8();
				reg.clearZFlag();
				reg.clearNFlag();
				reg.putCFlag( (w0 & 0xFF) < (reg.getSP() & 0xFF) );
				reg.putHFlag( (w0 & 0xF) < (reg.getSP() & 0xF) );
				reg.setHL(w0);
				clock.inc(12);
				break;
			case 0xF9: // opcode:LD SP,HL | flags:- - - - | length: 1
				reg.setSP(reg.getHL());
				clock.inc(8);
				break;
			case 0xFA: // opcode:LD A,(a16) | flags:- - - - | length: 3
				reg.setA(read16(next16()));
				clock.inc(16);
				break;
			case 0xFB: // opcode:EI | flags:- - - - | length: 1
				// todo enable interrupts, see 0xF3
				clock.inc(4);
				break;
			case 0xFE: // opcode:CP d8 | flags:Z 1 H C | length: 2
				alu.cmp(reg.getA(), next8());
				clock.inc(8);
				break;
			case 0xFF: // opcode:RST 38H | flags:- - - - | length: 1
				call(0x38);
				clock.inc(16);
			default:
				// This should probably be done differently in order to mix well with
				// removed instructions. Maybe those should be causing errors though :\
				System.err.printf("[%04x] undefined instruction 0x%02x\n", reg.getPC()-1, instr);
				break;
		}
	}

	private void executeCB(int instr) {
		// this.address = this.reg.getPC();
		int register = instr & 0xF;
		int cbinstr = (instr >> 4) & 0xF;
		if((instr & 0xF) > 7) {
			switch(cbinstr) {
				case 0x0: cbRegSet(register, alu.rrc(cbRegGet(register))); break;
				case 0x1: cbRegSet(register, alu.rr(cbRegGet(register))); break;
				case 0x2: cbRegSet(register, alu.sra(cbRegGet(register))); break;
				case 0x3: cbRegSet(register, alu.srl(cbRegGet(register))); break;
				case 0x4: alu.bit(cbRegGet(register), 1); break;
				case 0x5: alu.bit(cbRegGet(register), 3); break;
				case 0x6: alu.bit(cbRegGet(register), 5); break;
				case 0x7: alu.bit(cbRegGet(register), 7); break;
				case 0x8: cbRegSet(register, alu.res(cbRegGet(register), 1)); break;
				case 0x9: cbRegSet(register, alu.res(cbRegGet(register), 3)); break;
				case 0xA: cbRegSet(register, alu.res(cbRegGet(register), 5)); break;
				case 0xB: cbRegSet(register, alu.res(cbRegGet(register), 7)); break;
				case 0xC: cbRegSet(register, alu.set(cbRegGet(register), 1)); break;
				case 0xD: cbRegSet(register, alu.set(cbRegGet(register), 3)); break;
				case 0xE: cbRegSet(register, alu.set(cbRegGet(register), 5)); break;
				case 0xF: cbRegSet(register, alu.set(cbRegGet(register), 7)); break;
				default:
					System.out.printf("[%04x] undefined cb instruction %02x", reg.getPC()-1, instr);
					break;
			}
		} else {
			switch(cbinstr) {
				case 0x0: cbRegSet(register, alu.rlc(cbRegGet(register))); break;
				case 0x1: cbRegSet(register, alu.rl(cbRegGet(register))); break;
				case 0x2: cbRegSet(register, alu.sla(cbRegGet(register))); break;
				case 0x3: cbRegSet(register, alu.swap(cbRegGet(register))); break;
				case 0x4: alu.bit(cbRegGet(register), 0); break;
				case 0x5: alu.bit(cbRegGet(register), 2); break;
				case 0x6: alu.bit(cbRegGet(register), 4); break;
				case 0x7: alu.bit(cbRegGet(register), 5); break;
				case 0x8: cbRegSet(register, alu.res(cbRegGet(register), 0)); break;
				case 0x9: cbRegSet(register, alu.res(cbRegGet(register), 2)); break;
				case 0xA: cbRegSet(register, alu.res(cbRegGet(register), 4)); break;
				case 0xB: cbRegSet(register, alu.res(cbRegGet(register), 6)); break;
				case 0xC: cbRegSet(register, alu.set(cbRegGet(register), 0)); break;
				case 0xD: cbRegSet(register, alu.set(cbRegGet(register), 2)); break;
				case 0xE: cbRegSet(register, alu.set(cbRegGet(register), 4)); break;
				case 0xF: cbRegSet(register, alu.set(cbRegGet(register), 6)); break;
				default:
					System.out.printf("[%04x] undefined cb instruction %02x", reg.getPC()-1, instr);
					break;
			}
		}
		clock.inc(4);
	}

	private int cbRegGet(int r) {
		switch(r) {
			case 0x0: return reg.getB();
			case 0x1: return reg.getC();
			case 0x2: return reg.getD();
			case 0x3: return reg.getE();
			case 0x4: return reg.getH();
			case 0x5: return reg.getL();
			case 0x6: return read8(reg.getHL());
			case 0x7: return reg.getA();

			case 0x8: return reg.getB();
			case 0x9: return reg.getC();
			case 0xA: return reg.getD();
			case 0xB: return reg.getE();
			case 0xC: return reg.getH();
			case 0xD: return reg.getL();
			case 0xE: return reg.getHL();
			case 0xF: return reg.getA();

			default:
				System.err.println("Bad CB Register Get!");
				return 0;
		}
	}

	private void cbRegSet(int r, int v) {
		switch(r) {
			case 0x0: reg.setB(v); break;
			case 0x1: reg.setC(v); break;
			case 0x2: reg.setD(v); break;
			case 0x3: reg.setE(v); break;
			case 0x4: reg.setH(v); break;
			case 0x5: reg.setL(v); break;
			case 0x6:
				write8(reg.getHL(), v);
				break;
			case 0x7: reg.setA(v); break;

			case 0x8: reg.setB(v); break;
			case 0x9: reg.setC(v); break;
			case 0xA: reg.setD(v); break;
			case 0xB: reg.setE(v); break;
			case 0xC: reg.setH(v); break;
			case 0xD: reg.setL(v); break;
			case 0xE:
				write8(reg.getHL(), v);
				break;
			case 0xF: reg.setA(v); break;

			default:
				System.err.println("Bad CB Register Get!");
				break;
		}
	}

	private void ret() {
		int from = reg.getPC();
		reg.setPC(pop16());
		cpu.onRet(from, reg.getPC());
	}

	private void call(int addr) {
		cpu.onCall(addr);
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
		int ret = reg.getPC();
		reg.setPC(reg.getPC() + 1);
		return ret;
	}

	private int nextAddr16() {
		int ret = reg.getPC();
		reg.setPC(reg.getPC() + 2);
		return ret;
	}

	private int read8(int address) {
		return memory.read8(address);
	}

	private void write8(int address, int value) {
		memory.write8(address, value);
	}

	private int read16(int address) {
		return memory.read16(address);
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
