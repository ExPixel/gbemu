package gbemu;

import gbemu.cpu.memory.GBMemory;
import gbemu.cpu.memory.cartridge.GBCartridge;
import gbemu.cpu.z80.Z80Cpu;
import gbemu.graphics.GameBoyLCD;
import gbemu.graphics.LWJGLContainer;

/**
 * @author Adolph C.
 */
public class GameBoy {
	private GBCartridge cartridge;
	private GBMemory memory;
	private Z80Cpu cpu;
	private GameBoyLCD lcd;
	private LWJGLContainer lwjglContainer;

	public GameBoy() {
		this.memory = new GBMemory();
		this.cpu = new Z80Cpu(memory);
	}

	public void setCartridge(GBCartridge cartridge) {
		this.cartridge = cartridge;
	}

	public void bootUp() {
		this.memory.setCartridge(this.cartridge);
		cpu.reg.setPC(0x0100);
		cpu.reg.setA(0x01);
		cpu.reg.setF(0xB0);
		cpu.reg.setBC(0x0013);
		cpu.reg.setDE(0x00D8);
		cpu.reg.setHL(0x014D);
		cpu.reg.setSP(0xFFFE);

		initializeGraphics();

		memory.write8(0xFF05, 0x00); // TIMA
		memory.write8(0xFF06, 0x00); // TMA
		memory.write8(0xFF07, 0x00); // TAC
		memory.write8(0xFF10, 0x80); // NR10
		memory.write8(0xFF11, 0xBF); // NR11
		memory.write8(0xFF12, 0xF3); // NR12
		memory.write8(0xFF14, 0xBF); // NR14
		memory.write8(0xFF16, 0x3F); // NR21
		memory.write8(0xFF17, 0x00); // NR22
		memory.write8(0xFF19, 0xBF); // NR24
		memory.write8(0xFF1A, 0x7F); // NR30
		memory.write8(0xFF1B, 0xFF); // NR31
		memory.write8(0xFF1C, 0x9F); // NR32
		memory.write8(0xFF1E, 0xBF); // NR33
		memory.write8(0xFF20, 0xFF); // NR41
		memory.write8(0xFF21, 0x00); // NR42
		memory.write8(0xFF22, 0x00); // NR43
		memory.write8(0xFF23, 0xBF); // NR30
		memory.write8(0xFF24, 0x77); // NR50
		memory.write8(0xFF25, 0xF3); // NR51
		memory.write8(0xFF26, 0xF1); // NR52 // todo add SGB value: [$FF26] = $F1-GB, $F0-SGB ; NR52
		memory.write8(0xFF40, 0x91); // LCDC
		memory.write8(0xFF42, 0x00); // SCY
		memory.write8(0xFF43, 0x00); // SCX
		memory.write8(0xFF45, 0x00); // LYC
		memory.write8(0xFF47, 0xFC); // BGP
		memory.write8(0xFF48, 0xFF); // OBP0
		memory.write8(0xFF49, 0xFF); // OBP1
		memory.write8(0xFF4A, 0x00); // WY
		memory.write8(0xFF4B, 0x00); // WX
		memory.write8(0xFFFF, 0x00); // IE
	}

	private void initializeGraphics() {
		this.lwjglContainer = new LWJGLContainer();
		this.lwjglContainer.init();
		this.lcd = new GameBoyLCD(this.lwjglContainer.getWindow(), cpu, memory);
	}

	public void run() {
		lcd.init();
		this.lwjglContainer.loop(lcd::render);
		lcd.dispose();
		this.lwjglContainer.dispose();
	}
}
