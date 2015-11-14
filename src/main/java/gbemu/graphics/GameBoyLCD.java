package gbemu.graphics;

import com.sun.media.jfxmediaimpl.MediaDisposer;
import gbemu.cpu.memory.GBMemory;
import gbemu.cpu.z80.Z80Cpu;
import gbemu.glutil.*;
import gbemu.graphics.util.FrameCounter;
import gbemu.util.NativeUtils;
import gbemu.util.Utils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * The display for the GameBoy.
 * @author Adolph C.
 */
public class GameBoyLCD implements MediaDisposer.Disposable {
	/**
	 * Pointer to the LWJGL window that we are using for the LCD.
	 */
	private final long window;

	/**
	 * Memory object that this will use to communicate
	 * with the CPU and retrieve the PIXELS that
	 * are to be drawn on the screen.
	 */
	private final GBMemory memory;
	private final Z80Cpu cpu;
	private double frameDelta = 0;

	private Renderer renderer;
	private FontRenderer fontRenderer;

	// Information
	private FrameCounter frameCounter;
	private FrameCounter gameBoyFrameCounter;
	private long renderedFrames = 0;
	private long renderedLines = 0;

	private static final int SCREEN_W = 160;
	private static final int SCREEN_H = 144;

	/**
	 * The size of the display's data in bytes.
	 */
	private static final int GB_SCREEN_BYTES = 3 * SCREEN_W * SCREEN_H;

	ByteBuffer screenData;
	Texture screenTexture;
	private int currentLine;
	private Vector3f textColor = new Vector3f(255, 255, 255).div(255);

	public GameBoyLCD(long window, Z80Cpu cpu, GBMemory memory) {
		this.window = window;
		this.memory = memory;
		this.cpu = cpu;
	}

	public void init() {
		frameCounter = new FrameCounter();
		gameBoyFrameCounter = new FrameCounter();
		renderer = new Renderer();
		fontRenderer = new FontRenderer(12, NativeUtils.readResourceToBuffer("/gbemu/res/Tahoma.ttf"));
		renderer.setFontRenderer(fontRenderer);

		String vertexShaderResource = "/gbemu/res/basic_shader.vert";
		String fragmentShaderResource = "/gbemu/res/basic_shader.frag";
		Shader vertexShader = new Shader(Shader.VERTEX_SHADER,
				Utils.readResourceIntoString(vertexShaderResource));
		Shader fragmentShader = new Shader(Shader.FRAGMENT_SHADER,
				Utils.readResourceIntoString(fragmentShaderResource));
		renderer.init(new ShaderProgram(vertexShader, fragmentShader));
		screenData = BufferUtils.createByteBuffer(GB_SCREEN_BYTES);
		screenData.limit(GB_SCREEN_BYTES);
		screenTexture = new Texture();
		screenTexture.setFilterMin(GL11.GL_NEAREST);
		screenTexture.setFilterMax(GL11.GL_NEAREST);
		screenTexture.setInternalFormat(GL11.GL_RGB8);
		screenTexture.setImageFormat(GL11.GL_RGB);
		screenTexture.create(SCREEN_W, SCREEN_H, screenData);
	}

	public void incFrameDelta(double amount) {
		this.frameDelta += amount;
	}

	private boolean isFrameTime() {
		// 1.0d / 60d
		return this.frameDelta >= (0.015);
	}

	public void render(double delta) {
		this.incFrameDelta(delta);
		if(this.isFrameTime()) this.drawFrame();
		else this.drawSomeLines(); // draws some lines in the mean time.
		renderer.drawTexture(screenTexture, 0, 0);
		this.renderInfo();
		frameCounter.frame(delta);
	}

	private String bin8(int a) {
		String bin = Integer.toBinaryString(a);
		StringBuilder b = new StringBuilder();
		while(b.length() + bin.length() < 8) b.append('0');
		return b.append(bin).toString();
	}

	private void renderInfo() {
		StringBuilder info = new StringBuilder();
		info.append("FPS: ").append(frameCounter.getFPS()).append('\n');
		info.append("FPS (GB): ").append(gameBoyFrameCounter.getFPS()).append('\n');
		info.append("Frames: ").append(String.format("%,d", this.renderedFrames)).append('\n');
		info.append("Lines: ").append(String.format("%,d", this.renderedLines)).append('\n');
		info.append("Clock Cycles: ").append(String.format("%,d", cpu.clock.getCycles())).append('\n');
		info.append("Machine Cycles: ").append(String.format("%,d", cpu.clock.getMachineCycles())).append('\n');
		info.append("Program Counter: ").append(String.format("0x%04d", cpu.reg.getPC())).append('\n');
		info.append("LCDC: ").append(bin8(memory.ioPorts.LCDC)).append('\n');

		Runtime runtime = Runtime.getRuntime();
		long free = runtime.freeMemory();
		long total = runtime.totalMemory();
		long used = total - free;

		info.append("Memory Usage: ")
				.append(Utils.getByteSizeString(used))
				.append(' ').append('/').append(' ')
				.append(Utils.getByteSizeString(total));

		renderer.drawText(8, 24, textColor, info.toString());
	}

	private static int offset = 16;

	public void drawSomeLines() {
		int allowed = Math.max(this.frameCounter.getFPS() / 143, 1);
		// todo For now I want the last line drawn in the drawFrame method but this might not be necessary.
		for(int drawn = 0; currentLine < 143 && drawn < allowed; drawn++, currentLine++) {
			processLine(false);
		}
	}

	public void drawFrame() {
		gameBoyFrameCounter.frame(frameDelta);
		this.frameDelta = 0;

		this.runVDraw();
		this.runVBlank();
		currentLine = 0;

		screenData.flip();
		screenData.limit(GB_SCREEN_BYTES); // I set a hard limit so that the entire buffer is used.
		this.screenTexture.update(screenData);
		screenData.flip();
		screenData.limit(GB_SCREEN_BYTES);


		this.renderedFrames++;
	}

	private void runVDraw() {
		this.handleVDrawValues();
		for(currentLine = 0; currentLine < 144; currentLine++) {
			processLine(false);
		}
	}

	private void runVBlank() {
		this.handleVBlankValues();
		for (currentLine = 144; currentLine < 154; currentLine++) {
			processLine(true);
		}
	}

	private void processLine(boolean vblank) {
		this.memory.ioPorts.LY = this.currentLine;
		this.handleHDrawValues();
		this.cpu.executeCycles(436); // or 439
		if(!vblank) this.line();
		this.handleHBlankValues();
		this.cpu.executeCycles(262); // Hblank
	}

	/**
	 * Pokes a pixel into the current frame.
	 * @param x The x coordinate of the pixel.
	 * @param y The y coordinate of the pixel.
	 * @param color The color of the pixel.
	 */
	private void poke(int x, int y, int color) {
		try {
			if (x > SCREEN_W || y > SCREEN_H) return;
			if (x < 0 || y < 0) return;
			int offset = (x * 3) + ((y * 3) * SCREEN_W);
			this.screenData.put(offset, (byte) (color & 0xFF));
			this.screenData.put(offset + 1, (byte) ((color >> 8) & 0xFF));
			this.screenData.put(offset + 2, (byte) ((color >> 8) & 0xFF));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void pokeP(int x, int y, int paletteColor) {
		poke(x, y, colors[paletteColor]);
	}

	private void line() {
//		 drawTiles();
		 drawBGLine();
		this.renderedLines++;
	}

	private static int[] colors = {0xBBBBBB, 0x999999, 0x777777, 0x555555};

	private void drawTileLine(int least8, int most8, int dx, int dy) {
		dx += 7;
		for(int i = 7; i >= 0; i--) {
			int c = (least8 & 1) | ((most8 & 1) << 1);
			pokeP(dx--, dy, c);
			least8 >>= 1;
			most8 >>= 1;
		}
	}

	private int signExtendTile(int tile) {
		return (tile << 24) >> 24;
	}

	public void drawFirstTile() {
		if(currentLine > 7) return;
		int bgTileDataSelect = (memory.ioPorts.LCDC & 0x10) == 0 ? 0x8800 : 0x8000;
		bgTileDataSelect += currentLine * 2;
		drawTileLine(memory.read8(bgTileDataSelect), memory.read8(bgTileDataSelect + 1), 0, currentLine);
	}

	public void drawTiles() {
		int bgTileDataSelect = (memory.ioPorts.LCDC & 0x10) == 0 ? 0x8800 : 0x8000;
		int tile = bgTileDataSelect + ((currentLine / 8) * 16) + (currentLine * 2);
		for(int x = 0; x < SCREEN_W; x += 8) {
			drawTileLine(memory.read8(tile), memory.read8(tile + 1), x, currentLine);
			tile += 16;
		}
	}

	public void drawBGLine() {
		int SCX = memory.ioPorts.SCX;
		int SCY = memory.ioPorts.SCY;

		// if this is true then tiles are numbered from -128 to 127
		boolean tileData8800 = (memory.ioPorts.LCDC & 0x10) == 0;
		int bgTileDataSelect = tileData8800 ? 0x8800 : 0x8000;
		int bgTileMapSelect = (memory.ioPorts.LCDC & 0x8) == 0 ? 0x9800 : 0x9C00;

		int tileLineOffset = currentLine & 7;

		int mapTileAddress = bgTileMapSelect + ((currentLine + SCY) / 8 * 32);
		mapTileAddress += (SCX / 8);

		int dy = currentLine - (SCY & 7);

		for(int x = -(SCX & 7); x < SCREEN_W; x += 8) {
			int tileNumber = memory.read8(mapTileAddress);
			if(tileData8800) tileNumber = ((tileNumber << 24) >> 24) + 255;
			int tileAddr = bgTileDataSelect + tileNumber * 16 + (tileLineOffset * 2);
			drawTileLine(memory.read8(tileAddr), memory.read8(tileAddr + 1), x, dy);
			mapTileAddress++;
		}
	}

	public void drawWindow() {
	}

	private void handleVDrawValues() {
		this.memory.ioPorts.LCDC &= ~3;
		this.memory.ioPorts.LCDC |= 3;
	}

	private void handleVBlankValues() {
		this.memory.ioPorts.LCDC &= ~3;
		this.memory.ioPorts.LCDC |= 1;
	}

	/**
	 * Handles IO port values and interrupts for hdraw.
	 */
	private void handleHDrawValues() {
		this.memory.ioPorts.LCDC &= ~3;
		this.memory.ioPorts.LCDC |= 2;
	}

	/**
	 * Handles IO port values and interrupts for hblank.
	 */
	private void handleHBlankValues() {
		this.memory.ioPorts.LCDC &= ~3; // This is just clear during hblank
	}

	public void dispose() {
		this.screenTexture.dispose();
		this.renderer.dispose();
		this.fontRenderer.dispose();
	}
}
