package gbemu.cpu.memory;

import java.nio.ByteBuffer;

/**
 * @deprecated This only saves 7 kilobytes use {@link GBMemory} instead
 * @author Adolph C.
 */
@Deprecated
public class GBChunkMemory implements IMemory {
	/**
	 * Interrupt Enable Register Address
	 */
	private final static int IER_ADDR = 0xFFFF;

	/**
	 * Buffer containing memory of the GameBoy.
	 */
	private ByteBuffer data;

	private MemoryChunk romBank00;
	private MemoryChunk romBank01;
	private MemoryChunk videoRam;
	private MemoryChunk externalRam;
	private MemoryChunk workRam0;
	private MemoryChunk workRam1;
	private MemoryChunk workRam0Echo;
	private MemoryChunk oam;
	private MemoryChunk ioPorts;
	private MemoryChunk highRam;

	private MemoryChunk[] chunks;

	private int interruptEnableRegister;

	private void init() {
		int offset = 0;
		romBank00 = new MemoryChunk(offset, 0, 0x3FFF);
		offset += romBank00.length;
		romBank01 = new MemoryChunk(offset, 0x4000, 0x7FFF);
		offset += romBank01.length;
		videoRam = new MemoryChunk(offset, 0x8000, 0x9FFF);
		offset += videoRam.length;
		externalRam = new MemoryChunk(offset, 0xA000, 0xBFFF);
		offset += externalRam.length;
		workRam0 = new MemoryChunk(offset, 0xC000, 0xCFFF);
		workRam0Echo = new MemoryChunk(offset, 0xE000, 0xFDFF);
		offset += workRam0.length;
		workRam1 = new MemoryChunk(offset, 0xD000, 0xDFFF);
		offset += workRam1.length;
		oam = new MemoryChunk(offset, 0xFE00, 0xFE9F);
		offset += oam.length;
		ioPorts = new MemoryChunk(offset, 0xFF00, 0xFF7F);
		offset += ioPorts.length;
		highRam = new MemoryChunk(offset, 0xFF80, 0xFFFE);
		offset += highRam.length;
		this.data = ByteBuffer.allocateDirect(offset);
		this.chunks = new MemoryChunk[] {
				romBank00, romBank01,
				videoRam, externalRam,
				workRam0, workRam1,
				oam, workRam0Echo,
				ioPorts, highRam
		};
	}

	private int map(int address) {
		for(MemoryChunk chunk : this.chunks) {
			if(address >= chunk.start && address <= chunk.end)
				return (address - chunk.start) + chunk.offset;
		}
		return -1;
	}

	@Override
	public int read8(int address) {
		if(address == IER_ADDR) return this.interruptEnableRegister;
		int offset = this.map(address);
		return ((int)this.data.get(offset)) & 0xff;
	}

	@Override
	public void write8(int address, int value) {
		if(address == IER_ADDR) {
			this.interruptEnableRegister = value;
		} else {
			int offset = this.map(address);
			value &= 0xff; // only want the byte.
			this.data.put(offset, (byte) value);
		}
	}

	@Override
	public int read16(int address) {
		int out = read8(address);
		out |= read8(address + 1) << 8;
		return out;
	}

	@Override
	public void write16(int address, int value) {
		write8(address, value & 0xff);
		write8(address + 1, (value >> 8) & 0xff);
	}

	public static class MemoryChunk {
		private final int offset;
		private final int start;
		private final int end;
		private final int length;

		public MemoryChunk(int offset, int start, int end) {
			this.offset = offset;
			this.start = start;
			this.end = end;
			this.length = start - end + 1; // +1 because inclusive of beginning and end.
		}
	}
}
