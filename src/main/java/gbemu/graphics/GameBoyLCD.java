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
import java.util.Arrays;

/**
 * The display for the GameBoy.
 * @author Adolph C.
 */
public class GameBoyLCD implements MediaDisposer.Disposable {
	private static final int SCREEN_W = 160;
	private static final int SCREEN_H = 144;

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

	private boolean[] bgTransparent = new boolean[SCREEN_W];
	private boolean[] windowTransparent = new boolean[SCREEN_W];
	private int[] spritePriorities = new int[SCREEN_W];

	private boolean[] transparencySave = bgTransparent;

	private FontRenderer fontRenderer;
	// Information
	private FrameCounter frameCounter;
	private FrameCounter gameBoyFrameCounter;
	private long renderedFrames = 0;
	private long renderedLines = 0;

	private boolean renderDebug = false;

	/**
	 * The size of the display's data in bytes.
	 */
	private static final int GB_SCREEN_BYTES = 3 * SCREEN_W * SCREEN_H;

	ByteBuffer screenData;
	Texture screenTexture;
	private int currentLine;

	/**
	 * Reference for the monochrome palette
	 * <pre>
	 * 0  White
	 * 1  Light gray
	 * 2  Dark gray
	 * 3  Black
	 * </pre>
	 */
//	private static final int[] monochromePaletteReference = {0xffffff, 0xBBBBBB, 0x555555, 0x000000};
	private static final int[] monochromePaletteReference = {0x607D8B, 0x673AB7, 0x3F51B5, 0xF44336};
	private int[] monochromePalette = {0, 0, 0, 0};
	private int[] monochromeSpritePalette0 = {0, 0, 0, 0};
	private int[] monochromeSpritePalette1 = {0, 0, 0, 0};

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

	public void incFrameDelta(double amount) {this.frameDelta += amount;}

	private boolean isFrameTime() {
		// 1.0d / 60d
		return this.frameDelta >= (0.015);
	}

	public void render(double delta) {
		this.incFrameDelta(delta);
		if(this.isFrameTime())
			this.drawFrame();
		else this.drawSomeLines(); // draws some lines in the mean time.
		renderer.drawTexture(screenTexture, 0, 0);
		if(renderDebug) this.renderInfo(delta);
		frameCounter.frame(delta);
	}

	private CharSequence bin(int len, int a) {
		StringBuilder b = new StringBuilder();
		len--; // we don't want that one.
		while(len >= 0) {
			b.append((a & (1 << len)) >> len);
			len--;
		}
		return b;
	}


	private double renderInfoDelta = 0;
	private String renderInfoCachedText = "";
	private void renderInfo(double delta) {
		renderInfoDelta += delta;
		if(renderInfoDelta > 0.1) {
			renderInfoDelta = 0;
			StringBuilder info = new StringBuilder();
			info.append("FPS: ").append(frameCounter.getFPS()).append('\n');
			info.append("FPS (GB): ").append(gameBoyFrameCounter.getFPS()).append('\n');
			info.append("Frames: ").append(String.format("%,d", this.renderedFrames)).append('\n');
			info.append("Lines: ").append(String.format("%,d", this.renderedLines)).append('\n');
			info.append("Clock Cycles: ").append(String.format("%,d", cpu.clock.getCycles())).append('\n');
			info.append("Machine Cycles: ").append(String.format("%,d", cpu.clock.getMachineCycles())).append('\n');
			info.append("Program Counter: ").append(String.format("0x%04d", cpu.reg.getPC())).append('\n');
			info.append("LCDC: ").append(bin(8, memory.ioPorts.LCDC)).append('\n');

			Runtime runtime = Runtime.getRuntime();
			long free = runtime.freeMemory();
			long total = runtime.totalMemory();
			long used = total - free;

			info.append("Memory Usage: ")
					.append(Utils.getByteSizeString(used))
					.append(' ').append('/').append(' ')
					.append(Utils.getByteSizeString(total));
			renderInfoCachedText = info.toString();
		}

		renderer.drawText(8, 24, textColor, renderInfoCachedText);
	}

	public void drawSomeLines() {
		int allowed = Math.max(this.frameCounter.getFPS() / 143, 1);
		// todo For now I want the last line drawn in the drawFrame method but this might not be necessary.
		for(int drawn = 0; currentLine < 143 && drawn < allowed; drawn++, currentLine++) {
			this.processVDrawLine();
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
		for(currentLine = 0; currentLine < 144; currentLine++) {
			processVDrawLine();
		}
	}
	private void runVBlank() {
		this.lcdMode(1); // The actual mode value stays the same during vblank.
		this.cpu.fireVBlankInterrupt();
		for (currentLine = 144; currentLine < 154; currentLine++) {
			processVBlankLine();
		}
	}

	private void processVDrawLine() {
		this.memory.ioPorts.LY = this.currentLine;
		this.checkCoincidence();
		this.lcdMode(2);
		this.cpu.executeCycles(80); // Mode 2: 77-83 clks

		this.lcdMode(3);
		this.line();
		this.cpu.executeCycles(172); // Mode 3: 169-175 clks

		this.lcdMode(0);
		this.cpu.executeCycles(204); // Mode 0: 201-207 clks
	}

	private void processVBlankLine() {
		this.memory.ioPorts.LY = this.currentLine;
		this.checkCoincidence();
		this.lcdMode(1); // The actual mode value stays the same during vblank.
		this.cpu.executeCycles(80); // Mode 2: 77-83 clks
		this.cpu.executeCycles(172); // Mode 3: 169-175 clks
		this.cpu.executeCycles(204); // Mode 0: 201-207 clks
	}

	private void checkCoincidence() {
		if(this.memory.ioPorts.LY == this.memory.ioPorts.LYC) {
			this.memory.ioPorts.STAT |= 0x4;
			if((this.memory.ioPorts.STAT & 0x40) != 0) this.cpu.fireLCDStatInterrupt();
		} else {
			this.memory.ioPorts.STAT &= ~0x4;
		}
	}

	/**
	 * Pokes a pixel into the current frame.
	 * @param x The x coordinate of the pixel.
	 * @param y The y coordinate of the pixel.
	 * @param color The color of the pixel.
	 */
	private void poke(int x, int y, int color) {
		try {
			int offset = (x * 3) + ((y * 3) * SCREEN_W);
			this.screenData.put(offset, (byte) (color & 0xFF));
			this.screenData.put(offset + 1, (byte) ((color >> 8) & 0xFF));
			this.screenData.put(offset + 2, (byte) ((color >> 8) & 0xFF));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void line() {
		// todo add color capabilities
		setupMonochromePalette();
		Arrays.fill(bgTransparent, true);
		Arrays.fill(windowTransparent, true);

		// Bit 0 - BG Display (for CGB see below) (0=Off, 1=On)
		if((memory.ioPorts.LCDC & 0x01) != 0) drawMonochromeBGLine();

		// Bit 5 - Window Display Enable          (0=Off, 1=On)
		if((memory.ioPorts.LCDC & 0x20) != 0) drawMonochromeWindowLine();

		// Bit 1 - OBJ (Sprite) Display Enable    (0=Off, 1=On)
		if((memory.ioPorts.LCDC & 0x03) != 0) drawMonochromeSpriteLine();
		this.renderedLines++;
	}

	private void setupMonochromePalette() {
		monochromePalette[0] =
				monochromePaletteReference[memory.ioPorts.BGP & 0x3];
		monochromePalette[1] =
				monochromePaletteReference[(memory.ioPorts.BGP >> 2) & 0x3];
		monochromePalette[2] =
				monochromePaletteReference[(memory.ioPorts.BGP >> 4) & 0x3];
		monochromePalette[3] =
				monochromePaletteReference[(memory.ioPorts.BGP >> 6) & 0x3];

		monochromeSpritePalette0[0] =
				monochromePaletteReference[memory.ioPorts.OBP0 & 0x3];
		monochromeSpritePalette0[1] =
				monochromePaletteReference[(memory.ioPorts.OBP0 >> 2) & 0x3];
		monochromeSpritePalette0[2] =
				monochromePaletteReference[(memory.ioPorts.OBP0 >> 4) & 0x3];
		monochromeSpritePalette0[3] =
				monochromePaletteReference[(memory.ioPorts.OBP0 >> 6) & 0x3];

		monochromeSpritePalette1[0] =
				monochromePaletteReference[memory.ioPorts.OBP1 & 0x3];
		monochromeSpritePalette1[1] =
				monochromePaletteReference[(memory.ioPorts.OBP1 >> 2) & 0x3];
		monochromeSpritePalette1[2] =
				monochromePaletteReference[(memory.ioPorts.OBP1 >> 4) & 0x3];
		monochromeSpritePalette1[3] =
				monochromePaletteReference[(memory.ioPorts.OBP1 >> 6) & 0x3];
	}

	private void drawMonochromeTileLine(int[] palette, int least8, int most8, int dx, int dy) {
		dx += 7;
		for(int i = 7; i >= 0; i--) {
			if (dx < 0 || dy >= SCREEN_H || dy < 0) return;
			if (dx >= SCREEN_W) { dx--; continue; }
			int c = ((least8 & 1) | ((most8 & 1) << 1));
			transparencySave[dx] = (c == 0);
			poke(dx--, dy, palette[c]);
			least8 >>= 1;
			most8 >>= 1;
		}
	}

	private void drawMonochromeSpriteTileLine(int[] palette, int priority, boolean flipped, int least8, int most8, int dx, int dy) {
		if(flipped) {
			for(int i = 7; i >= 0; i--) {
				if (dx >= SCREEN_W|| dy >= SCREEN_H || dy < 0) return;
				if (dx < 0 ) { dx--; continue; }
				if(!transparencySave[dx] || priority < spritePriorities[dx]) continue;
				spritePriorities[dx] = priority;
				int c = ((least8 & 1) | ((most8 & 1) << 1));
				poke(dx++, dy, palette[c]);
				least8 >>= 1;
				most8 >>= 1;
			}
		} else {
			dx += 7;
			for(int i = 7; i >= 0; i--) {
				if (dx < 0 || dy >= SCREEN_H || dy < 0) return;
				if (dx >= SCREEN_W) { dx--; continue; }
				if(!transparencySave[dx] || priority < spritePriorities[dx]) continue;
				spritePriorities[dx] = priority;
				int c = ((least8 & 1) | ((most8 & 1) << 1));
				poke(dx--, dy, palette[c]);
				least8 >>= 1;
				most8 >>= 1;
			}
		}
	}

	// todo Optimize graphics.

	public void drawMonochromeBGLine() {
		// if this is true then tiles are numbered from -128 to 127
		int scx = memory.ioPorts.SCX;
		int scy = memory.ioPorts.SCY;
		boolean tileData8800 = (memory.ioPorts.LCDC & 0x10) == 0;
		int bgTileDataSelect = tileData8800 ? 0x8800 : 0x8000;
		int bgTileMapSelect = (memory.ioPorts.LCDC & 0x8) == 0 ? 0x9800 : 0x9C00;
		int bgTileMapSelectEnd = (memory.ioPorts.LCDC & 0x8) == 0 ? 0x9BFF : 0x9FFF;

		int line = this.currentLine + scy;
		int mapTileAddress = bgTileMapSelect + ((line / 8) * 32);
		mapTileAddress += scx / 8;
		int tileYOffset = line % 8;

		int dx = -(scx % 8);
//		int dy = line-(scy & 7);

		transparencySave = bgTransparent; // To save transparency

		for(; dx < SCREEN_W; dx += 8) {
			if(mapTileAddress > bgTileMapSelectEnd) // I still don't know why +1 works
				mapTileAddress -= bgTileMapSelectEnd - bgTileMapSelect + 1;
			int tile = memory.read8(mapTileAddress++);
			if (tileData8800) {
				tile = (tile << 24) >> 24; // sign extend the tile
				tile += 255; // normalize the tile number
			}
			tile = bgTileDataSelect + (tile * 16) + (tileYOffset * 2);
			int least8 = memory.read8(tile);
			int most8 = memory.read8(tile + 1);
			drawMonochromeTileLine(monochromePalette, least8, most8, dx, currentLine);
		}
	}

	public void drawMonochromeWindowLine() {
		int wx = memory.ioPorts.WX;
		int wy = memory.ioPorts.WY;
		boolean tileData8800 = (memory.ioPorts.LCDC & 0x10) == 0;
		int windowTileDataSelect = tileData8800 ? 0x8800 : 0x8000;
		int windowTileMapSelect = (memory.ioPorts.LCDC & 0x40) == 0 ? 0x9800 : 0x9C00;
		int windowTileMapSelectEnd = (memory.ioPorts.LCDC & 0x40) == 0 ? 0x9BFF : 0x9FFF;

		int line = this.currentLine + wy;
		int mapTileAddress = windowTileMapSelect + ((line / 8) * 32);
		mapTileAddress += wx / 8;
		int tileYOffset = line % 8;

		int dx = -(wy % 8);
//		int dy = line-(scy & 7);

		transparencySave = windowTransparent; // Saves transparency values here.

		for(; dx < SCREEN_W; dx += 8) {
			if(mapTileAddress > windowTileMapSelectEnd) // I still don't know why +1 works
				mapTileAddress -= windowTileMapSelectEnd - windowTileMapSelect + 1;
			int tile = memory.read8(mapTileAddress++);
			if (tileData8800) {
				tile = (tile << 24) >> 24; // sign extend the tile
				tile += 255; // normalize the tile number
			}
			tile = windowTileDataSelect + (tile * 16) + (tileYOffset * 2);
			int least8 = memory.read8(tile);
			int most8 = memory.read8(tile + 1);
			drawMonochromeTileLine(monochromePalette, least8, most8, dx, currentLine);
		}
	}

	public void drawMonochromeSpriteLine() {
		Arrays.fill(spritePriorities, 0);
		int spritesDrawn = 0;
		int sheight = (memory.ioPorts.LCDC & 0x4) == 0 ? 8 : 16;
		for(int sprite = 0xFE00; sprite <= 0xFE9F && spritesDrawn < 40; sprite += 4) {
			int y = memory.read8(sprite);
			if(y == 0 || y >= 160) continue;
			int x = memory.read8(sprite + 1);
			if(x == 0 || x >= 168) continue;

			y -= 16;
			if(currentLine > (y + sheight - 1) || currentLine < y) continue;
			x -= 8;

			int tile = memory.read8(sprite + 2);
			int attr = memory.read8(sprite + 3);

			transparencySave = (attr & 128) == 0 ? windowTransparent : bgTransparent;
			boolean xflip = (attr & 32) != 0;
			boolean yflip = (attr & 64) != 0;
			int[] palette = (attr & 16) == 0 ? monochromeSpritePalette0 : monochromeSpritePalette1;

			if(sheight == 8) {
				if(yflip) tile = 0x8000 + (tile * 16) + 14 - (currentLine - y) % 8 * 2;
				else tile = 0x8000 + (tile * 16) + ((currentLine - y) % 8 * 2);
				int least8 = memory.read8(tile);
				int most8 = memory.read8(tile + 1);
				drawMonochromeSpriteTileLine(palette, x, xflip, least8, most8, x, currentLine);
			} else {
				System.out.println(":(");
			}
			spritesDrawn++;
		}
	}

	private void lcdMode(int mode) {
		this.memory.ioPorts.STAT &= ~0x3;
		this.memory.ioPorts.STAT |= mode & 0x3;

		int check = 0;
		switch (mode) {
			case 0: check = 0x8; break;
			case 1: check = 0x10; break;
			case 2: check = 0x20; break;
		}
		if((memory.ioPorts.STAT & check) != 0) this.cpu.fireLCDStatInterrupt();
	}

	public void toggleRenderDebug() {
		this.renderDebug = !renderDebug;
	}

	public void dispose() {
		this.screenTexture.dispose();
		this.renderer.dispose();
		this.fontRenderer.dispose();
	}
}
