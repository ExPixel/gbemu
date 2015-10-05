
switch (instr & 0xff) {
		case 0x0: // opcode:RLC B | flags:Z 0 0 C | length: 2
		reg.setB(alu.rlc(reg.getB()));
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