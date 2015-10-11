package gbemu.graphics.util;

import gbemu.util.NativeUtils;
import gbemu.util.Utils;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Adolph C.
 */
public class Renderer implements Disposable {
	private int vboId;
	private int vaoId;
	private int eboId;
	private int shaderProgramId;

	public void init() {
		FloatBuffer vboBuffer = NativeUtils.wrapFloats(
			0.5f,  0.5f, 0.0f,  // Top Right
			0.5f, -0.5f, 0.0f,  // Bottom Right
			-0.5f, -0.5f, 0.0f,  // Bottom Left
			-0.5f,  0.5f, 0.0f   // Top Left
		);

		IntBuffer indices = NativeUtils.wrapInts(
				0, 1, 3,
				1, 2, 3
		);

		vaoId = glGenVertexArrays();
		vboId = glGenBuffers();
		eboId = glGenBuffers();
		glBindVertexArray(vaoId);
			glBindBuffer(GL_ARRAY_BUFFER, vboId); //Bind buffer (also specifies type of buffer)
			glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW); //Send up the data and specify usage hint.

			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0L);
			glEnableVertexAttribArray(0);
		glBindVertexArray(0); // unbinds the VAO


		IntBuffer shaderStatus = BufferUtils.createIntBuffer(1);
		shaderStatus.clear();
		int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderId, Utils.readResourceIntoString("/gbemu/res/basic_shader.vert"));
		glCompileShader(vertexShaderId);
		glGetShaderiv(vertexShaderId, GL_COMPILE_STATUS, shaderStatus);
		if(shaderStatus.get(0) == 0) {
			System.err.println("VERTEX SHADER COMPILATION FAILED:");
			System.err.println(glGetShaderInfoLog(vertexShaderId));
			System.err.flush();
			throw new RuntimeException("Errror in vertex shader.");
		}

		shaderStatus.clear();
		int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderId, Utils.readResourceIntoString("/gbemu/res/basic_shader.frag"));
		glCompileShader(fragmentShaderId);
		glGetShaderiv(fragmentShaderId, GL_COMPILE_STATUS, shaderStatus);
		if(shaderStatus.get(0) == 0) {
			System.err.println("FRAGMENT SHADER COMPILATION FAILED:");
			System.err.println(glGetShaderInfoLog(fragmentShaderId));
			System.err.flush();
			throw new RuntimeException("Errror in fragment shader.");
		}

		shaderStatus.clear();
		shaderProgramId = glCreateProgram();
		glAttachShader(shaderProgramId, vertexShaderId);
		glAttachShader(shaderProgramId, fragmentShaderId);
		glLinkProgram(shaderProgramId);
		glGetProgramiv(shaderProgramId, GL_LINK_STATUS, shaderStatus);
		if(shaderStatus.get(0) == 0) {
			System.err.println("SHADER PROGRAM LINKING FAILED:");
			System.err.println(glGetProgramInfoLog(shaderProgramId));
			System.err.flush();
			throw new RuntimeException("Errror in linking shader program.");
		}

		// No longer needed now that we have a linked program.
		glDeleteShader(vertexShaderId);
		glDeleteShader(fragmentShaderId);
	}

	public void prepare() {
		FloatBuffer vboBuffer = NativeUtils.wrapFloats(
				0.5f,  0.5f, 0.0f,  // Top Right
				0.5f, -0.5f, 0.0f,  // Bottom Right
				-0.5f, -0.5f, 0.0f,  // Bottom Left
				-0.5f,  0.5f, 0.0f   // Top Left
		);
		glBindVertexArray(vaoId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0L);
		glEnableVertexAttribArray(0);
		glBindVertexArray(0); // unbinds the VAO
		glUseProgram(shaderProgramId);
	}

	public void begin() {
		glUseProgram(shaderProgramId);
		glBindVertexArray(vaoId);
	}

	public void end() {
		glBindVertexArray(0);
	}

	@Override
	public void dispose() {
		this.end();
		glDeleteVertexArrays(this.vaoId);
		glDeleteBuffers(this.vboId);
		glDeleteBuffers(this.eboId);
		glDeleteProgram(this.shaderProgramId);
	}
}
