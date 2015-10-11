package gbemu.cpu.memory.cartridge;

import java.nio.ByteBuffer;

/**
 * @author Adolph C.
 */
public class NoMBC extends MBC {
	public NoMBC(ByteBuffer data) {
		super(data);
	}

	@Override
	public int read8(int address) {
		return ((int) data.get(address)) & 0xff;
	}

	@Override
	public void write8(int address, int value) {
		// You can't write to ROM.
		// data.put(address, (byte) (value & 0xff));
	}
}
