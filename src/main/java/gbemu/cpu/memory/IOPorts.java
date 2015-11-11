package gbemu.cpu.memory;

/**
 * @author Adolph C.
 */
public class IOPorts {
	public int P1; // 0xFF00
	public int SB; // 0xFF01
	public int SC; // 0xFF02
	public int DIV; // 0xFF04
	public int TIMA; // 0xFF05
	public int TMA; // 0xFF06
	public int TAC; // 0xFF07
	public int IF; // 0xFF0F
	public int CH1_ENT; // 0xFF10
	public int CH1_WAV; // 0xFF11
	public int CH1_ENV; // 0xFF12
	public int CH1_FREQ_LO; // 0xFF13
	public int CH1_FREQ_HI_KICK; // 0xFF14
	public int CH2_WAV; // 0xFF16
	public int CH2_ENV; // 0xFF17
	public int CH2_FREQ_LO; // 0xFF18
	public int CH2_FREQ_HI_KICK; // 0xFF19
	public int CH3_ONOFF; // 0xFF1A
	public int CH3_LENGTH; // 0xFF1B
	public int CH3_VOLUME; // 0xFF1C
	public int CH3_FREQ_LO; // 0xFF1D
	public int CH3_FREQ_HI_KICK; // 0xFF1E
	public int CH4_WAV; // 0xFF20 todo this might not be correct :(
	public int CH4_ENV; // 0xFF21
	public int CH4_POLY; // 0xFF22
	public int CH4_KICK; // 0xFF23
	public int SND_VIN; // 0xFF24
	public int SND_STEREO; // 0xFF25
	public int SND_STAT; // 0xFF26
	public int[] CH3_SAMPLES = new int[16]; // 0xFF30 - 0xFF3F

	/**
	 * <B>FF40 - LCDC - LCD Control (R/W)<BR>
	 * </B><TABLE><TR><TD><PRE>  Bit 7 - LCD Display Enable             (0=Off, 1=On)
	 * Bit 6 - Window Tile Map Display Select (0=9800-9BFF, 1=9C00-9FFF)
	 * Bit 5 - Window Display Enable          (0=Off, 1=On)
	 * Bit 4 - BG & Window Tile Data Select   (0=8800-97FF, 1=8000-8FFF)
	 * Bit 3 - BG Tile Map Display Select     (0=9800-9BFF, 1=9C00-9FFF)
	 * Bit 2 - OBJ (Sprite) Size              (0=8x8, 1=8x16)
	 * Bit 1 - OBJ (Sprite) Display Enable    (0=Off, 1=On)
	 * Bit 0 - BG Display (for CGB see below) (0=Off, 1=On)
	 * </TD></TR></TABLE>
	 */
	public int LCDC; // 0xFF40
	public int STAT; // 0xFF41
	public int SCY; // 0xFF42
	public int SCX; // 0xFF43
	public int LY; // 0xFF44
	public int LYC; // 0xFF45
	public int DMA; // 0xFF46
	public int BGP; // 0xFF47
	public int OBP0; // 0xFF48
	public int OBP1; // 0xFF49
	public int WY; // 0xFF4A
	public int WX; // 0xFF4B
	public int IE; // 0xFFFF

	/**
	 * <pre>
	 * IME - Interrupt Master Enable Flag (Write Only)
	 * 0 - Disable all Interrupts
	 * 1 - Enable all Interrupts that are enabled in IE Register (FFFF)
	 * The IME flag is used to disable all interrupts, overriding any enabled bits in the IE Register. It isn't possible to access the IME flag by using a I/O address, instead IME is accessed directly from the CPU, by the following opcodes/operations:
	 * EI     ;Enable Interrupts  (ie. IME=1)
	 * DI     ;Disable Interrupts (ie. IME=0)
	 * RETI   ;Enable Ints & Return (same as the opcode combination EI, RET)
	 * <INT>  ;Disable Ints & Call to Interrupt Vector
	 * Whereas <INT> means the operation which is automatically executed by the CPU when it executes an interrupt.
	 * </pre>
	 */
	public int IME;

	int read8(int address) {
		switch (address) {
			case 0xFF00:
				return P1;
			case 0xFF01:
				return SB;
			case 0xFF02:
				return SC;
			case 0xFF04:
				return DIV;
			case 0xFF05:
				return TIMA;
			case 0xFF06:
				return TMA;
			case 0xFF07:
				return TAC;
			case 0xFF0F:
				return IF;
			case 0xFF10:
				return CH1_ENT;
			case 0xFF11:
				return CH1_WAV;
			case 0xFF12:
				return CH1_ENV;
			case 0xFF13:
				return CH1_FREQ_LO;
			case 0xFF14:
				return CH1_FREQ_HI_KICK;
			case 0xFF16:
				return CH2_WAV;
			case 0xFF17:
				return CH2_ENV;
			case 0xFF18:
				return CH2_FREQ_LO;
			case 0xFF19:
				return CH2_FREQ_HI_KICK;
			case 0xFF1A:
				return CH3_ONOFF;
			case 0xFF1B:
				return CH3_LENGTH;
			case 0xFF1C:
				return CH3_VOLUME;
			case 0xFF1D:
				return CH3_FREQ_LO;
			case 0xFF1E:
				return CH3_FREQ_HI_KICK;
			case 0xFF20:
				return CH4_WAV;
			case 0xFF21:
				return CH4_ENV;
			case 0xFF22:
				return CH4_POLY;
			case 0xFF23:
				return CH4_KICK;
			case 0xFF24:
				return SND_VIN;
			case 0xFF25:
				return SND_STEREO;
			case 0xFF26:
				return SND_STAT;
			case 0xFF30:
			case 0xFF31:
			case 0xFF32:
			case 0xFF33:
			case 0xFF34:
			case 0xFF35:
			case 0xFF36:
			case 0xFF37:
			case 0xFF38:
			case 0xFF39:
			case 0xFF3A:
			case 0xFF3B:
			case 0xFF3C:
			case 0xFF3D:
			case 0xFF3E:
			case 0xFF3F:
				return CH3_SAMPLES[address - 0xFF30];
			case 0xFF40:
				return LCDC;
			case 0xFF41:
				return STAT;
			case 0xFF42:
				return SCY;
			case 0xFF43:
				return SCX;
			case 0xFF44:
				return LY;
			case 0xFF45:
				return LYC;
			case 0xFF46:
				return DMA;
			case 0xFF47:
				return BGP;
			case 0xFF48:
				return OBP0;
			case 0xFF49:
				return OBP1;
			case 0xFF4A:
				return WY;
			case 0xFF4B:
				return WX;
			case 0xFFFF:
				return IE;
			default:
				return 0; // fixme Not sure if this is right, some of them might actually not be here.
		}
	}

	void write8(int address, int data) {
		switch (address) {
			case 0xFF00:
				P1 = data;
				break;
			case 0xFF01:
				SB = data;
				break;
			case 0xFF02:
				SC = data;
				break;
			case 0xFF04:
				DIV = data;
				break;
			case 0xFF05:
				TIMA = data;
				break;
			case 0xFF06:
				TMA = data;
				break;
			case 0xFF07:
				TAC = data;
				break;
			case 0xFF0F:
				IF = data;
				break;
			case 0xFF10:
				CH1_ENT = data;
				break;
			case 0xFF11:
				CH1_WAV = data;
				break;
			case 0xFF12:
				CH1_ENV = data;
				break;
			case 0xFF13:
				CH1_FREQ_LO = data;
				break;
			case 0xFF14:
				CH1_FREQ_HI_KICK = data;
				break;
			case 0xFF16:
				CH2_WAV = data;
				break;
			case 0xFF17:
				CH2_ENV = data;
				break;
			case 0xFF18:
				CH2_FREQ_LO = data;
				break;
			case 0xFF19:
				CH2_FREQ_HI_KICK = data;
				break;
			case 0xFF1A:
				CH3_ONOFF = data;
				break;
			case 0xFF1B:
				CH3_LENGTH = data;
				break;
			case 0xFF1C:
				CH3_VOLUME = data;
				break;
			case 0xFF1D:
				CH3_FREQ_LO = data;
				break;
			case 0xFF1E:
				CH3_FREQ_HI_KICK = data;
				break;
			case 0xFF20:
				CH4_WAV = data;
				break;
			case 0xFF21:
				CH4_ENV = data;
				break;
			case 0xFF22:
				CH4_POLY = data;
				break;
			case 0xFF23:
				CH4_KICK = data;
				break;
			case 0xFF24:
				SND_VIN = data;
				break;
			case 0xFF25:
				SND_STEREO = data;
				break;
			case 0xFF26:
				SND_STAT = data;
				break;
			case 0xFF30:
			case 0xFF31:
			case 0xFF32:
			case 0xFF33:
			case 0xFF34:
			case 0xFF35:
			case 0xFF36:
			case 0xFF37:
			case 0xFF38:
			case 0xFF39:
			case 0xFF3A:
			case 0xFF3B:
			case 0xFF3C:
			case 0xFF3D:
			case 0xFF3E:
			case 0xFF3F:
				CH3_SAMPLES[address - 0xFF30] = data;
				break;
			case 0xFF40:
				LCDC = data;
				break;
			case 0xFF41:
				STAT = data;
				break;
			case 0xFF42:
				SCY = data;
				break;
			case 0xFF43:
				SCX = data;
				break;
			case 0xFF44:
				LY = data;
				break;
			case 0xFF45:
				LYC = data;
				break;
			case 0xFF46:
				DMA = data;
				break;
			case 0xFF47:
				BGP = data;
				break;
			case 0xFF48:
				OBP0 = data;
				break;
			case 0xFF49:
				OBP1 = data;
				break;
			case 0xFF4A:
				WY = data;
				break;
			case 0xFF4B:
				WX = data;
				break;
			case 0xFFFF:
				IE = data;
				break;
			default:
				break; // fixme Refer to the IOPort read problem with default.
		}
	}
}
