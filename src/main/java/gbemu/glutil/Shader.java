package gbemu.glutil;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author Adolph C.
 */
public class Shader implements Disposable {
	public static final int VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
	public static final int FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;

	/**
	 * The pointing to the actual shader.
	 */
	private int shaderId;

	/**
	 * Creates a new shader.
	 * @param shaderType The type of the shader (usually one of {@link org.lwjgl.opengl.GL20#GL_VERTEX_SHADER}
	 *                   or {@link org.lwjgl.opengl.GL20#GL_FRAGMENT_SHADER})
	 * @param source The source of the shader that will be compiled.
	 */
	public Shader(int shaderType, String source) {
		this.shaderId = glCreateShader(shaderType);
		glShaderSource(this.shaderId, source);
		glCompileShader(this.shaderId);
		IntBuffer statusBuffer = BufferUtils.createIntBuffer(1);
		glGetShaderiv(this.shaderId, GL_COMPILE_STATUS, statusBuffer);
		if(statusBuffer.get(0) == 0) {
			String log = glGetShaderInfoLog(this.shaderId);
			throw new ShaderCompileException(log);
		}
	}

	/**
	 * @return The ID used to reference the actual shader.
	 */
	public int getShaderId() {
		return shaderId;
	}

	@Override
	public void dispose() {
		glDeleteShader(this.shaderId);
	}

	public static class ShaderCompileException extends RuntimeException {
		public ShaderCompileException(String message) {
			super(message);
		}
	}
}
