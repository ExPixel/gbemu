package gbemu.graphics.util;

import gbemu.util.NativeUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Adolph C.
 */
public class FontRenderer implements Disposable {
	float height = 32.0f;
	int bitmapWidth = 512;
	int bitmapHeight = 512;
	int texId;
	STBTTBakedChar.Buffer cdata = STBTTBakedChar.mallocBuffer(96);
	STBTTAlignedQuad q = STBTTAlignedQuad.malloc();

	public void init() {
		ByteBuffer fontBuffer = NativeUtils.readResourceToBuffer("/gbemu/res/Anonymous Pro.ttf");
		ByteBuffer fontBitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
		STBTruetype.stbtt_BakeFontBitmap(fontBuffer, height, fontBitmap, bitmapWidth, bitmapHeight, 32, cdata);
		texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texId);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, fontBitmap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	}

	public void render(GLColor color, float x, float y, String text) {

	}

	@Override
	public void dispose() {
		MemoryUtil.memFree(cdata);
		q.free();
		glDeleteTextures(this.texId);
	}
}
