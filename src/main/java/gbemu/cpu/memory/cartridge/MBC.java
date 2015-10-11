package gbemu.cpu.memory.cartridge;

import java.nio.ByteBuffer;

/**
 * @author Adolph C.
 */
public abstract class MBC {
	ByteBuffer data;

	/**
	 * True if the memory is working with a 16bit value, false otherwise.
	 */
	boolean b16 = false;

	public MBC(ByteBuffer data) {
		this.data = data;
	}

	public void set16BitMode() {
		b16 = true;
	}

	public void clear16BitMode() {
		b16 = false;
	}

	public abstract int read8(int address);
	public abstract void write8(int address, int value);

	ByteBuffer sliceFromTo(int start, int end) {
		ByteBuffer b = (ByteBuffer) ((ByteBuffer) data.position(start)).slice().limit(end);
		this.data.position(0);
		return b;
	}
}
