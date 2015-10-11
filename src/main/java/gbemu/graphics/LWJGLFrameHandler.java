package gbemu.graphics;

/**
 * @author Adolph C.
 */
@FunctionalInterface
public interface LWJGLFrameHandler {
	/**
	 * Called to render the next frame.
	 * @param delta The number of milliseconds since the last frame.
	 */
	void frame(double delta);
}
