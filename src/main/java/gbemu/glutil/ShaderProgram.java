package gbemu.glutil;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author Adolph C.
 */
public class ShaderProgram implements Disposable {
	/**
	 * The ID pointing to the actual shader program.
	 */
	private int programId;

	public static final int UNIFORM_CACHE_SIZE = 8;
	private int[] uniformCache = new int[UNIFORM_CACHE_SIZE];
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

	/**
	 * Creates a new shader program.
	 * @param vertexShaderSource The source of the vertex shader.
	 * @param fragmentShaderSource The source of the fragment shader.
	 */
	public ShaderProgram(String vertexShaderSource, String fragmentShaderSource) {
		this(
				new Shader(GL_VERTEX_SHADER, vertexShaderSource),
				new Shader(GL_FRAGMENT_SHADER, fragmentShaderSource)
		);
	}


	/**
	 * Creates a new shader program.
	 * @param vertexShader The vertex shader.
	 * @param fragmentShader The fragment shader.
	 */
	public ShaderProgram(Shader vertexShader, Shader fragmentShader) {
		this.programId = glCreateProgram();
		glAttachShader(this.programId, vertexShader.getShaderId());
		glAttachShader(this.programId, fragmentShader.getShaderId());
		this.link();
	}

	/**
	 * Caches a uniform location.
	 * @param identifer The identifier of the uniform location to cache.
	 * @param index The index to cache the uniform location. Must not exceed
	 *              <i>{@link ShaderProgram#UNIFORM_CACHE_SIZE} - 1</i>
	 */
	public void cacheUniformLoc(String identifer, int index) {
		int uniformLocation = this.getUniformLocation(identifer);
		this.uniformCache[index] = uniformLocation;
	}

	public int getCachedLoc(int index) {
		return this.uniformCache[index];
	}

	/**
	 * Links this program.
	 */
	private void link() {
		glLinkProgram(this.programId);
		IntBuffer programStatusBuffer = BufferUtils.createIntBuffer(1);
		glGetProgramiv(this.programId, GL_LINK_STATUS, programStatusBuffer);
		if(programStatusBuffer.get(0) == 0) {
			String log = glGetProgramInfoLog(this.programId);
			throw new ShaderProgramLinkException(log);
		}
	}

	/**
	 * Starts using the program in the current context.
	 */
	public void use() {
		glUseProgram(this.programId);
	}

	/**
	 * Gets the location of a shader uniform.
	 * @param identifier The identifier of the uniform.
	 * @return The location of the uniform.
	 */
	public int getUniformLocation(CharSequence identifier) {
		return glGetUniformLocation(this.programId, identifier);
	}

	public void setFloat(String identifier, float f) {
		glUniform1f(this.getUniformLocation(identifier), f);
	}

	public void setInteger(String identifier, int integer) {
		glUniform1i(this.getUniformLocation(identifier), integer);
	}

	public void setVector2f(String identifier, Vector2f vector) {
		this.setVector2f(getUniformLocation(identifier), vector);
	}

	public void setVector3f(String identifier, Vector3f vector) {
		setVector3f(getUniformLocation(identifier), vector);
	}

	public void setVector4f(String identifier, Vector4f vector) {
		setVector4f(getUniformLocation(identifier), vector);
	}

	public void setMatrix3f(String identifier, Matrix3f matrix) {
		setMatrix3f(getUniformLocation(identifier), matrix);
	}

	public void setMatrix4f(String identifier, Matrix4f matrix) {
		setMatrix4f(getUniformLocation(identifier), matrix);
	}

	public void setInteger(int uniformLocation, int integer) {
		glUniform1i(uniformLocation, integer);
	}

	public void setVector2f(int uniformLocation, Vector2f vector) {
		this.buffer.clear().limit(2);
		vector.get(buffer);
		glUniform2fv(uniformLocation, buffer);
	}

	public void setVector3f(int uniformLocation, Vector3f vector) {
		this.buffer.clear().limit(3);
		vector.get(buffer);
		glUniform3fv(uniformLocation, buffer);
	}

	public void setVector4f(int uniformLocation, Vector4f vector) {
		this.buffer.clear().limit(4);
		vector.get(buffer);
		glUniform4fv(uniformLocation, buffer);
	}

	public void setMatrix3f(int uniformLocation, Matrix3f matrix) {
		this.buffer.clear().limit(9);
		matrix.get(buffer);
		glUniformMatrix3fv(uniformLocation, false, buffer);
	}

	public void setMatrix4f(int uniformLocation, Matrix4f matrix) {
		this.buffer.clear().limit(16);
		matrix.get(buffer);
		glUniformMatrix4fv(uniformLocation, false, buffer);
	}

	@Override
	public void dispose() {
		glDeleteProgram(this.programId);
	}

	public static class ShaderProgramLinkException extends RuntimeException {
		public ShaderProgramLinkException(String message) {
			super(message);
		}
	}
}
