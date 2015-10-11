package gbemu.graphics.util;

/**
 * @author Adolph C.
 */
public class FrameCounter {
	private int fps = 0;
	private int frames = 0;

	// No the fuck it can't
	@SuppressWarnings("FieldCanBeLocal")
	private double delta = 0;

	public void frame(double d) {
		this.frames++;
		this.delta += d;
		if(this.delta >= 1.0d) {
			this.fps = frames;
			this.frames = 0;
			this.delta -= 1.0d;
		}
	}

	public int getFPS() {
		return fps;
	}
}
