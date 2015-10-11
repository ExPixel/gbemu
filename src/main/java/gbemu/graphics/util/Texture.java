package gbemu.graphics.util;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengles.GLES20.glGenerateMipmap;

/**
 * @author Adolph C.
 */
public class Texture implements Disposable {
	/**
	 * The openGL id for this texture.
	 */
	private int id;
	private int width;
	private int height;

	private void Texture(int width, int height, ByteBuffer buffer) {
		this.id = glGenTextures();
		this.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D); // todo not sure about this one.
		// todo eventually change these to gl clamp to border, can't seem to do that right now:
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	/**
	 * Binds this texture.
	 */
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, this.id);
	}


	@Override
	public void dispose() {
		glDeleteTextures(this.id);
	}
}
