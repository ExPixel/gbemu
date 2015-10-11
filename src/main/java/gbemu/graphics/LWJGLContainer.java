package gbemu.graphics;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Adolph C.
 */
public class LWJGLContainer {
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private long window;

	private static final int WINDOW_HEIGHT = 640; // 160;
	private static final int WINDOW_WIDTH = 480; // 144;

	public void init() {
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint());
		if(glfwInit()!= GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "GameBoy Emulator", NULL, NULL);
		if(window == NULL)
			throw new IllegalStateException("Unable to create window.");

		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
					LWJGLContainer.this.close();
			}
		});

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
				window,
				(vidmode.getWidth() - WINDOW_WIDTH) / 2,
				(vidmode.getHeight() - WINDOW_HEIGHT) / 2
		);


		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// Enable v-sync
		glfwSwapInterval(0);

		// Make the window visible
		glfwShowWindow(window);


		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		System.out.println("OpenGL: " + glGetString(GL_VERSION));
	}

	public void loop(LWJGLFrameHandler handler) {
		// Set the clear color
		glClearColor(0.2f, 0.2f, 0.2f, 0.2f);

		double lastFrameTime = glfwGetTime();

		// Some initialization functions for...things.
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( glfwWindowShouldClose(window) == GL_FALSE ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();

			// todo there might be a better way to do this using openGL's functionality.

			double frameTime = glfwGetTime();
			handler.frame(frameTime - lastFrameTime);
			lastFrameTime = frameTime;

			glfwSwapBuffers(window); // swap the color buffers
		}
	}

	public void close() {
		glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
	}

	public long getWindow() {
		return window;
	}
}
