package gbemu.glutil;

import gbemu.graphics.LWJGLContainer;
import gbemu.util.NativeUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Adolph C.
 */
public class Renderer implements Disposable {
	private ShaderProgram shaderProgram;
	private int quadVAO;
	private Matrix4f model;
	private Matrix3f texCoordTransform;
	private FontRenderer fontRenderer;

	public Renderer() {
		this.initRenderData();
	}

	public void init(ShaderProgram program) {
		this.texCoordTransform = new Matrix3f();
		this.model = new Matrix4f();
//		Matrix4f projection = new Matrix4f().ortho(
//				0.0f, LWJGLContainer.WINDOW_WIDTH,
//				LWJGLContainer.WINDOW_HEIGHT, 0.0f,
//				-1.0f, 1.0f
//		);

		this.shaderProgram = program;
		this.initializeProgram(this.shaderProgram);
	}

	/**
	 * Initializes a program that will be used by this renderer.
	 * @param program The program to initialize.
	 */
	public void initializeProgram(ShaderProgram program) {
		Matrix4f projection = new Matrix4f().ortho2D(0, LWJGLContainer.WINDOW_WIDTH, LWJGLContainer.WINDOW_HEIGHT, 0);
		program.use();
		program.cacheUniformLoc("image", UniformIndexes.IMAGE);
		program.cacheUniformLoc("projection", UniformIndexes.PROJECTION);
		program.cacheUniformLoc("model", UniformIndexes.MODEL);
		program.cacheUniformLoc("textureColor", UniformIndexes.TEXTURE_COLOR);
		program.cacheUniformLoc("texCoordTransform", UniformIndexes.TEX_COORD_TRANSFORM);
		program.setInteger(program.getCachedLoc(UniformIndexes.IMAGE), 0);
		program.setMatrix4f(program.getCachedLoc(UniformIndexes.PROJECTION), projection);
	}

	public void drawTexture(Texture texture,
						float x, float y) {
		drawTexture(texture, this.shaderProgram, 0, 0, texture.getWidth(), texture.getHeight(), x, y, texture.getWidth(), texture.getHeight(), 0.0f, GLColor.white3);
	}

	public void drawTexture(Texture texture,
						float sx, float sy,
						float swidth, float sheight,
						float x, float y,
						float width, float height) {
		drawTexture(texture, this.shaderProgram, sx, sy, swidth, sheight, x, y, width, height, 0.0f, GLColor.white3);
	}


	public void drawTexture(Texture texture,
						float x, float y,
						float rotate,
						Vector3f color) {
		drawTexture(texture, this.shaderProgram, 0, 0, texture.getWidth(), texture.getHeight(), x, y, texture.getWidth(), texture.getHeight(), rotate, color);
	}

	public void drawTexture(Texture texture,
							ShaderProgram program,
							float sx, float sy,
							float swidth, float sheight,
							float x, float y,
							float width, float height,
							float rotate,
							Vector3f color) {
		model.identity(); // reset our model to the identity matrix.
		program.use();

		model.translate(x, y ,1.0f);
		model.translate(0.5f * width, 0.5f * height, 0.0f);
		model.rotate(rotate, 0.0f, 0.0f, 1.0f);
		model.translate(-0.5f * width, -0.5f * height, 0.0f);
		model.scale(width, height, 1.0f);

		program.setMatrix4f(program.getCachedLoc(UniformIndexes.MODEL), model);
		program.setVector3f(program.getCachedLoc(UniformIndexes.TEXTURE_COLOR), color);

		this.texCoordTransform.set(
				swidth / texture.getWidth(), 0, 0,
				0, sheight / texture.getHeight(), 0,
				sx / texture.getWidth(), sy / texture.getHeight(), 1.0f
		);

		program.setMatrix3f(program.getCachedLoc(UniformIndexes.TEX_COORD_TRANSFORM), this.texCoordTransform);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		texture.bind();

		glBindVertexArray(this.quadVAO);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glBindVertexArray(0);
	}

	public void drawText(float x, float y, Vector3f color, String text) {
		this.fontRenderer.print(this, x, y, color, text);
	}

	private void initRenderData() {
		int VBO;
		FloatBuffer vertices = NativeUtils.wrapFloats(
			// Pos      // Tex
			0.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 0.0f,

			0.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 0.0f
		);

		this.quadVAO = glGenVertexArrays();
		VBO = glGenBuffers();

		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		glBindVertexArray(this.quadVAO);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	@Override
	public void dispose() {
		this.shaderProgram.dispose();
		glDeleteBuffers(this.quadVAO);
	}

	public void setFontRenderer(FontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
		this.fontRenderer.initForRenderer(this);
	}

	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}
}
