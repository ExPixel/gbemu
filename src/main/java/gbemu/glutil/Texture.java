package gbemu.glutil;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Adolph C.
 */
public class Texture implements Disposable {
	/**
	 * The ID for this texture.
	 */
	private int texId;

	private int width;
	private int height;

	/**
	 * The format of the texture object.
	 */
	private int internalFormat = GL_RGBA8;

	/**
	 * The format of the data passed in or the loaded image.
	 */
	private int imageFormat = GL_RGBA;

	/**
	 * The wrapping mode on the S axis.
	 */
	private int wrapS = GL_REPEAT;

	/**
	 * The wrapping mode on the T axis.
	 */
	private int wrapT = GL_REPEAT;

	/**
	 * The filtering mode if texture pixels < screen pixels
	 */
	private int filterMin = GL_LINEAR;

	/**
	 * The filtering mode if texture pixels > screen pixels.
	 */
	private int filterMax = GL_LINEAR;

	public Texture() {
		this.texId = glGenTextures();
	}

	public void create(int width, int height, ByteBuffer data) {
		this.width = width;
		this.height = height;
		glBindTexture(GL_TEXTURE_2D, this.texId);
		glTexImage2D(
				GL_TEXTURE_2D, 0, this.internalFormat,
				width, height, 0,
				this.imageFormat, GL_UNSIGNED_BYTE, data
		);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, this.wrapS);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, this.wrapT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, this.filterMin);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, this.filterMax);
		GL30.glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void update(ByteBuffer data) {
		this.bind();
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, this.width, this.height, this.getImageFormat(), GL_UNSIGNED_BYTE, data);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, this.texId);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getInternalFormat() {
		return internalFormat;
	}

	public void setInternalFormat(int internalFormat) {
		this.internalFormat = internalFormat;
	}

	public int getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(int imageFormat) {
		this.imageFormat = imageFormat;
	}

	public int getWrapS() {
		return wrapS;
	}

	public void setWrapS(int wrapS) {
		this.wrapS = wrapS;
	}

	public int getWrapT() {
		return wrapT;
	}

	public void setWrapT(int wrapT) {
		this.wrapT = wrapT;
	}

	public int getFilterMin() {
		return filterMin;
	}

	public void setFilterMin(int filterMin) {
		this.filterMin = filterMin;
	}

	public int getFilterMax() {
		return filterMax;
	}

	public void setFilterMax(int filterMax) {
		this.filterMax = filterMax;
	}

	/**
	 * @return The ID of this texture.
	 */
	public int getTextureId() {
		return this.texId;
	}

	@Override
	public void dispose() {
		glDeleteTextures(this.texId);
		this.texId = -1;
	}
}
