package gbemu.util;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Adolph C.
 */
public class NativeUtils {
	public static ByteBuffer readResourceToBuffer(String resource) {
		return readResourceToBuffer(NativeUtils.class, resource);
	}

	public static ByteBuffer readResourceToBuffer(Class<?> parent, String resource) {
		try(InputStream stream = parent.getResourceAsStream(resource)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] readBuffer = new byte[512];
			int read;
			while((read = stream.read(readBuffer)) != -1)
				out.write(readBuffer, 0, read);
			return wrap(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static ByteBuffer wrap(byte...data) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public static ByteBuffer readFileToBuffer(String file) {
		try(FileChannel channel = new FileInputStream(file).getChannel()) {
			ByteBuffer buffer = BufferUtils.createByteBuffer((int) channel.size() + 1);
			//noinspection StatementWithEmptyBody
			while(channel.read(buffer) != -1);
			buffer.flip();
			return buffer;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static FloatBuffer wrapFloats(float...data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public static IntBuffer wrapInts(int...data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
