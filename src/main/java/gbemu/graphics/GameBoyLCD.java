package gbemu.graphics;

import com.sun.media.jfxmediaimpl.MediaDisposer;
import gbemu.cpu.memory.GBMemory;
import gbemu.cpu.z80.Z80Cpu;
import gbemu.glutil.*;
import gbemu.graphics.util.FrameCounter;
import gbemu.util.NativeUtils;
import gbemu.util.Utils;
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
	 * with the CPU and retrieve the pixels that
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

	private static int SCREEN_W = 160;
	private static int SCREEN_H = 144;

	ByteBuffer screenData;
	Texture screenTexture;
	private int currentLine;

	public GameBoyLCD(long window, Z80Cpu cpu, GBMemory memory) {
		this.window = window;
		this.memory = memory;
		this.cpu = cpu;
	}

	public void init() {
		frameCounter = new FrameCounter();
		gameBoyFrameCounter = new FrameCounter();
		renderer = new Renderer();
		fontRenderer = new FontRenderer(12, NativeUtils.readResourceToBuffer("/gbemu/res/Roboto-Regular.ttf"));
		renderer.setFontRenderer(fontRenderer);

		String vertexShaderResource = "/gbemu/res/basic_shader.vert";
		String fragmentShaderResource = "/gbemu/res/basic_shader.frag";
		Shader vertexShader = new Shader(Shader.VERTEX_SHADER,
				Utils.readResourceIntoString(vertexShaderResource));
		Shader fragmentShader = new Shader(Shader.FRAGMENT_SHADER,
				Utils.readResourceIntoString(fragmentShaderResource));
		renderer.init(new ShaderProgram(vertexShader, fragmentShader));

		final int pixels = 3 * SCREEN_W * SCREEN_H;
		screenData = BufferUtils.createByteBuffer(pixels);
		for(int idx = 0; idx < pixels; idx += 3) {
			screenData.put((byte) (idx & 0xFF));
			screenData.put((byte) ((idx >> 8) & 0xFF));
			screenData.put((byte) ((idx >> 16) & 0xFF));
		}
		screenData.flip();
		screenTexture = new Texture();
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

	private void renderInfo() {
		StringBuilder info = new StringBuilder();
		info.append("FPS: ").append(frameCounter.getFPS()).append('\n');
		info.append("FPS (GB): ").append(gameBoyFrameCounter.getFPS()).append('\n');
		info.append("Frames: ").append(String.format("%,d", this.renderedFrames)).append('\n');
		info.append("Lines: ").append(String.format("%,d", this.renderedLines)).append('\n');
		info.append("Clock Cycles: ").append(String.format("%,d", cpu.clock.getCycles())).append('\n');
		info.append("Machine Cycles: ").append(String.format("%,d", cpu.clock.getMachineCycles())).append('\n');
		info.append("Program Counter: ").append(String.format("0x%04d", cpu.reg.getPC())).append('\n');

		Runtime runtime = Runtime.getRuntime();
		long free = runtime.freeMemory();
		long total = runtime.totalMemory();
		long used = total - free;

		info.append("Memory Usage: ")
				.append(Utils.getByteSizeString(used))
				.append(' ').append('/').append(' ')
				.append(Utils.getByteSizeString(total));

		renderer.drawText(8, 24, GLColor.white3, info.toString());
	}

	private static int offset = 16;

	public void drawSomeLines() {
		int allowed = 10;
		// todo For now I want the last line drawn in the drawFrame method but this might not be necessary.
		for(int drawn = 0; currentLine < 143 && drawn < allowed; drawn++) {
			this.cpu.executeCycles(436); // or 439
			this.line(currentLine);
			currentLine++;
		}
	}

	public void drawFrame() {
		gameBoyFrameCounter.frame(frameDelta);
		this.frameDelta = 0;
		for(currentLine = 0; currentLine < 144; currentLine++) {
			this.cpu.executeCycles(436); // or 439
			this.line(currentLine);
		}
		currentLine = 0;


		final int pixels = 3 * SCREEN_W * SCREEN_H;
		screenData.clear();
		for(int idx = 0; idx < pixels; idx += 3) {
			screenData.put((byte) ((idx + offset) & 0xFF));
			screenData.put((byte) (((idx + offset) >> 8) & 0xFF));
			screenData.put((byte) (((idx + offset) >> 16) & 0xFF));
		}
		screenData.flip();
		this.screenTexture.update(screenData);
		offset += 8;


		this.renderedFrames++;
	}

	private void line(int line) {
		this.renderedLines++;
	}

	public void dispose() {
		this.screenTexture.dispose();
		this.renderer.dispose();
		this.fontRenderer.dispose();
//		MemoryUtil.memFree(this.screenData);
	}
}
