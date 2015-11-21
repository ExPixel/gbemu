package gbemu;

import gbemu.cpu.memory.GBMemory;
import gbemu.cpu.memory.cartridge.GBCartridge;
import gbemu.cpu.z80.Z80Cpu;
import gbemu.glutil.Disposable;
import gbemu.graphics.GameBoyLCD;
import gbemu.graphics.LWJGLContainer;
import gbemu.graphics.LWJGLKeyListener;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;

/**
 * @author Adolph C.
 */
public class GameBoy implements LWJGLKeyListener, Disposable {
	private static final int JOYPAD_TYPE_DIRECTION = 0x10;
	private static final int JOYPAD_TYPE_BUTTON = 0x20;
	private static final int JOYPAD_RIGHT = 0x1;
	private static final int JOYPAD_A = 0x1;
	private static final int JOYPAD_LEFT = 0x2;
	private static final int JOYPAD_B = 0x2;
	private static final int JOYPAD_UP = 0x4;
	private static final int JOYPAD_SELECT = 0x4;
	private static final int JOYPAD_DOWN = 0x8;
	private static final int JOYPAD_START = 0x8;

	private GBCartridge cartridge;
	private GBMemory memory;
	private Z80Cpu cpu;
	private GameBoyLCD lcd;
	private LWJGLContainer lwjglContainer;
	private boolean lwjglDisposed = false;
	private boolean lcdDisposed = false;

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

		memory.ioPorts.P1_DIRECTIONS = 0x0f;
		memory.ioPorts.P1_BUTTONS = 0x0f;
	}

	private void initializeGraphics() {
		this.lwjglContainer = new LWJGLContainer();
		this.lwjglContainer.init();
		this.lcd = new GameBoyLCD(this.lwjglContainer.getWindow(), cpu, memory);
	}

	public void run() {
		lcd.init();
		Runtime.getRuntime().addShutdownHook(new Thread(this::dispose));
		this.lwjglContainer.setKeyListener(this);
		this.lwjglContainer.setFrameHandler(lcd::render);
		this.lwjglContainer.loop();
		this.dispose();
	}

	@Override
	public void onKeyEvent(int key, int scancode, int action, int mods) {
		if(action == GLFW.GLFW_PRESS) {
			if (key == GLFW.GLFW_KEY_D && (mods & GLFW.GLFW_MOD_CONTROL) != 0) {
				File file = new File("debug/memory.dmp");
				file.getParentFile().mkdirs();
				try(FileOutputStream stream = new FileOutputStream(file)) {
					for(int idx = 0; idx <= 0xFFFF; idx++) {
						stream.write(memory.read8(idx));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Dumped memory.");
			}

			switch (key) {
				case GLFW.GLFW_KEY_LEFT:
					joypadPress(JOYPAD_TYPE_DIRECTION, JOYPAD_LEFT);
					break;
				case GLFW.GLFW_KEY_RIGHT:
					joypadPress(JOYPAD_TYPE_DIRECTION, JOYPAD_RIGHT);
					break;
				case GLFW.GLFW_KEY_UP:
					joypadPress(JOYPAD_TYPE_DIRECTION, JOYPAD_UP);
					break;
				case GLFW.GLFW_KEY_DOWN:
					joypadPress(JOYPAD_TYPE_DIRECTION, JOYPAD_DOWN);
					break;
				case GLFW.GLFW_KEY_Z:
					joypadPress(JOYPAD_TYPE_BUTTON, JOYPAD_A);
					break;
				case GLFW.GLFW_KEY_X:
					joypadPress(JOYPAD_TYPE_BUTTON, JOYPAD_B);
					break;
				case GLFW.GLFW_KEY_A:
					joypadPress(JOYPAD_TYPE_BUTTON, JOYPAD_START);
					break;
				case GLFW.GLFW_KEY_S:
					joypadPress(JOYPAD_TYPE_BUTTON, JOYPAD_SELECT);
					break;
			}
		} else if(action == GLFW.GLFW_RELEASE) {
			switch (key) {
				case GLFW.GLFW_KEY_LEFT:
					joypadRelease(JOYPAD_TYPE_DIRECTION, JOYPAD_LEFT);
					break;
				case GLFW.GLFW_KEY_RIGHT:
					joypadRelease(JOYPAD_TYPE_DIRECTION, JOYPAD_RIGHT);
					break;
				case GLFW.GLFW_KEY_UP:
					joypadRelease(JOYPAD_TYPE_DIRECTION, JOYPAD_UP);
					break;
				case GLFW.GLFW_KEY_DOWN:
					joypadRelease(JOYPAD_TYPE_DIRECTION, JOYPAD_DOWN);
					break;
				case GLFW.GLFW_KEY_Z:
					joypadRelease(JOYPAD_TYPE_BUTTON, JOYPAD_A);
					break;
				case GLFW.GLFW_KEY_X:
					joypadRelease(JOYPAD_TYPE_BUTTON, JOYPAD_B);
					break;
				case GLFW.GLFW_KEY_A:
					joypadRelease(JOYPAD_TYPE_BUTTON, JOYPAD_START);
					break;
				case GLFW.GLFW_KEY_S:
					joypadRelease(JOYPAD_TYPE_BUTTON, JOYPAD_SELECT);
					break;
			}
		}
	}

	private void joypadPress(int joypadType, int joypadInput) {
		if(joypadType == JOYPAD_TYPE_BUTTON) {
			int before = memory.ioPorts.P1_BUTTONS;
			memory.ioPorts.P1_BUTTONS &= ~joypadInput;
			if(before != memory.ioPorts.P1_BUTTONS) {
				if((memory.ioPorts.P1 & 0x20) == 0) {
					this.cpu.fireJoypadInterrupt();
				}
			}
		} else if(joypadType == JOYPAD_TYPE_DIRECTION) {
			int before = memory.ioPorts.P1_DIRECTIONS;
			memory.ioPorts.P1_DIRECTIONS &= ~joypadInput;
			if(before != memory.ioPorts.P1_DIRECTIONS) {
				if((memory.ioPorts.P1 & 0x10) == 0) {
					this.cpu.fireJoypadInterrupt();
				}
			}
		}
	}

	private void joypadRelease(int joypadType, int joypadInput) {
		if(joypadType == JOYPAD_TYPE_BUTTON) memory.ioPorts.P1_BUTTONS |= joypadInput;
		else if(joypadType == JOYPAD_TYPE_DIRECTION) memory.ioPorts.P1_DIRECTIONS |= joypadInput;
	}

	public GBCartridge getCartridge() {
		return cartridge;
	}

	public GBMemory getMemory() {
		return memory;
	}

	public Z80Cpu getCpu() {
		return cpu;
	}

	@Override
	public void dispose() {
		try {
			if(!this.lcdDisposed && this.lcd != null) {
				this.lcd.dispose();
				this.lcdDisposed = true;
				System.out.println("Disposed of LCD.");
			}

			if (!this.lwjglDisposed && this.lwjglContainer != null) {
				this.lwjglContainer.dispose();
				this.lwjglDisposed = true;
				System.out.println("Disposed of LWJGL Container.");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
