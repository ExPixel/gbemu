package gbemu.graphics;

import gbemu.cpu.memory.GBMemory;
import gbemu.cpu.z80.Z80Cpu;
import gbemu.graphics.util.Disposable;
import gbemu.graphics.util.GLColor;
import gbemu.graphics.util.Renderer;
import org.lwjgl.opengl.GL11;

/**
 * The display for the GameBoy.
 * @author Adolph C.
 */
public class GameBoyLCD implements Disposable {
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
	private GLColor textColor = GLColor.fromRGB8(0, 0, 0, 255);

	private Renderer renderer;

	private double frameDelta = 0;

	public GameBoyLCD(long window, Z80Cpu cpu, GBMemory memory) {
		this.window = window;
		this.memory = memory;
		this.cpu = cpu;
	}

	public void init() {
		renderer = new Renderer();
		renderer.init();
	}

	public void incFrameDelta(double amount) {
		this.frameDelta += amount;
	}

	private boolean isFrameTime() {
		// 1.0d / 60d
		return this.frameDelta >= (0.016666666666666666);
	}

	public void render(double delta) {
		this.incFrameDelta(delta);
		if(this.isFrameTime()) this.drawFrame();
		renderer.begin();
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
		renderer.end();
	}

	public void drawFrame() {
		this.frameDelta = 0;
		for(int line = 0; line < 144; line++) {
			this.cpu.executeCycles(436); // or 439
			this.line(line);
		}
	}

	private void line(int line) {
	}

	public void dispose() {
		renderer.dispose();
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
