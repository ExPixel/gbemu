package gbemu.graphics.util;

import org.lwjgl.opengl.GL11;

/**
 * @author Adolph C.
 */
public class GLColor {
	public float r, g, b, a;

	public GLColor() {
	}

	public GLColor(float r, float g, float b) {
		this(r, g, b, 1.0f);
	}

	public GLColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public static GLColor fromRGB8(float r, float g, float b) {
		return fromRGB8(r, g, b, 255f);
	}

	public static GLColor fromRGB8(float r, float g, float b, float a) {
		GLColor c = new GLColor();
		c.setRGB8(r, g, b, a);
		return c;
	}

	public void setRGB(float r, float g, float b) {
		setRGB(r, g, b, 1.0f);
	}

	public void setRGB(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setRGB8(float r, float g, float b) {
		setRGB8(r, g, b, 255f);
	}

	public void setRGB8(float r, float g, float b, float a) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		this.a = a / 255f;
	}

	public void bind3() {
		GL11.glColor3f(this.r, this.g, this.b);
	}

	public void bind4() {
		GL11.glColor4f(this.r, this.g, this.b, this.a);
	}
}
