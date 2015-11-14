package gbemu.glutil;

import gbemu.util.Utils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author Adolph C.
 */
public class FontRenderer implements Disposable {
	private final float height;
	private STBTTBakedChar.Buffer cdata;
	private int bitmapWidth = 512;
	private int bitmapHeight = 512;
	private Texture texture;
	private ShaderProgram shaderProgram;

	private FloatBuffer _xBuffer = BufferUtils.createFloatBuffer(1);
	private FloatBuffer _yBuffer = BufferUtils.createFloatBuffer(1);

	public FontRenderer(float height, ByteBuffer fontDataBuffer) {
		this.height = height;
		this.init(fontDataBuffer);
	}

	public void initForRenderer(Renderer renderer) {
		renderer.initializeProgram(this.shaderProgram);
	}

	private void init(ByteBuffer fontDataBuffer) {
		Shader vertexShader = new Shader(Shader.VERTEX_SHADER,
				Utils.readResourceIntoString("/gbemu/res/text_shader.vert"));
		Shader fragmentShader = new Shader(Shader.FRAGMENT_SHADER,
				Utils.readResourceIntoString("/gbemu/res/text_shader.frag"));
		this.shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
		cdata = STBTTBakedChar.mallocBuffer(96);
		ByteBuffer tempBitmap = BufferUtils.createByteBuffer(512 * 512);
		STBTruetype.stbtt_BakeFontBitmap(
				fontDataBuffer, 0, this.height, tempBitmap,
				bitmapWidth, bitmapHeight,
				32, 96, cdata);
		texture = new Texture();
		texture.setInternalFormat(GL30.GL_R8);
		texture.setImageFormat(GL11.GL_RED);
		texture.setFilterMax(GL11.GL_LINEAR);
		texture.setFilterMin(GL11.GL_LINEAR);
		texture.create(bitmapWidth, bitmapHeight, tempBitmap);
		MemoryUtil.memFree(tempBitmap); // No longer need the temporary bitmap.
	}

	public void print(Renderer renderer, float startX, float startY, Vector3f color, String text) {
		this._xBuffer.clear();
		this._yBuffer.clear();
		FloatBuffer xbuffer = this._xBuffer.put(0, startX);
		FloatBuffer ybuffer = this._yBuffer.put(0, startY);
		STBTTAlignedQuad quad = STBTTAlignedQuad.create();
		for(int idx = 0; idx < text.length(); idx++) {
			char c = text.charAt(idx);
			if(c == '\n') {
				xbuffer.put(0, startX);
				ybuffer.put(0, ybuffer.get(0) + height);
			} else if(c >= 32 && c < 128) {
				STBTruetype.stbtt_GetBakedQuad(cdata, bitmapWidth, bitmapHeight, c - 32, xbuffer, ybuffer, quad, 1);
				float tx = quad.s0() * bitmapWidth;
				float ty = quad.t0() * bitmapHeight;
				float tw = quad.s1() * bitmapWidth - tx;
				float th = quad.t1() * bitmapHeight - ty;
				float pw = quad.x1() - quad.x0();
				float ph = quad.y1() - quad.y0();
//				System.out.printf("%f, %f : %f, %f\n", quad.getX0(), quad.getY0(), quad.getX1(), quad.getY1());
				renderer.drawTexture(this.texture, this.shaderProgram, tx, ty, tw, th, quad.x0(), quad.y0(), pw, ph, 0.0f, color);
			}
		}
		// quad.free();
	}

	public float getHeight() {
		return height;
	}

	public STBTTBakedChar.Buffer getCdata() {
		return cdata;
	}

	public int getBitmapWidth() {
		return bitmapWidth;
	}

	public int getBitmapHeight() {
		return bitmapHeight;
	}

	public Texture getTexture() {
		return texture;
	}

	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}

	@Override
	public void dispose() {
		MemoryUtil.memFree(this.cdata);
		this.texture.dispose();
		this.shaderProgram.dispose();
	}
}
