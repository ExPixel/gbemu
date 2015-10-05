package gbemu.cpu.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Adolph C.
 */
public class GBMemory implements IMemory {
	private final ByteBuffer data;

	public GBMemory() {
		this.data = ByteBuffer.allocateDirect(0xFFFF);
		data.order(ByteOrder.LITTLE_ENDIAN);
	}

	@Override
	public int read8(int address) {
		return ((int) data.get(address)) & 0xff; // removes sign extension
	}

	@Override
	public void write8(int address, int value) {
		data.put(address, (byte) (value & 0xff)); // removes sign extension
	}

	@Override
	public int read16(int address) {
		return ((int) data.getShort(address)) & 0xffff; // removes sign extension
	}

	@Override
	public void write16(int address, int value) {
		data.putShort(address, (short) (value & 0xffff)); // removes sign extension
	}
}
