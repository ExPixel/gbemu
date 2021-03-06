package gbemu.cpu.memory;

import gbemu.cpu.memory.cartridge.GBCartridge;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Adolph C.
 */
public class GBMemory {
	private GBCartridge cartridge;
	private final ByteBuffer data;
	public final IOPorts ioPorts = new IOPorts();
	private GBMemoryListener listener;


	public interface GBMemoryListener {
		void onWrite8(int address, int data);
		void onWrite16(int address, int data);
	}

	public ByteBuffer getData() {
		return data;
	}

	/**
	 * True if the memory is working with a 16bit value, false otherwise.
	 */
	private boolean b16 = false;

	public GBMemory() {
		this.data = ByteBuffer.allocateDirect(0xFFFF + 1); // Not all of this is used.
		data.order(ByteOrder.LITTLE_ENDIAN);
	}

	public void setCartridge(GBCartridge cartridge) {
		this.cartridge = cartridge;
	}

	private void set16BitMode() {
		b16 = true;
		if(cartridge != null) cartridge.getMbc().set16BitMode();
	}

	private void clear16BitMode() {
		b16 = false;
		if(cartridge != null) cartridge.getMbc().clear16BitMode();
	}

	public int read8(int address) {
		address &= 0xffff;
		if ((address >= 0xFF00 && address <= 0xFF7F) || address == 0xFFFF) return this.ioPorts.read8(address);
		if(address >= 0xE000 && address <= 0xFDFF) {
			/*
			The addresses E000-FE00 appear to access the internal
			RAM the same as C000-DE00. (i.e. If you write a byte to
			address E000 it will appear at C000 and E000.
			Similarly, writing a byte to C000 will appear at C000
			and E000.)
			 */
			return read8(address - 0xE000 + 0xC000);
		} else if(address <= 0x7FFF) {
			return cartridge.getMbc().read8(address);
		}
		return ((int) data.get(address)) & 0xff; // removes sign extension
	}

	public void write8(int address, int value) {
		address &= 0xffff;
		if ((address >= 0xFF00 && address <= 0xFF7F) || address == 0xFFFF) {
			this.ioPorts.write8(address, value & 0xff);
		} else if(address >= 0xE000 && address <= 0xFDFF) {
			/*
			The addresses E000-FE00 appear to access the internal
			RAM the same as C000-DE00. (i.e. If you write a byte to
			address E000 it will appear at C000 and E000.
			Similarly, writing a byte to C000 will appear at C000
			and E000.)
			 */
			write8(address - 0xE000 + 0xC000, value);
			return;
		} if((address >= 0xA000 && address <= 0xBFFF) || address <= 0x7FFF) {
			cartridge.getMbc().write8(address, value);
		}
		onWrite8(address, value);
		data.put(address, (byte) (value & 0xff)); // removes sign extension
	}

	public int read16(int address) {
		int low = read8(address);
		int high = read8(address + 1);
		return low | (high << 8);
	}

	public void write16(int address, int value) {
		write8(address, value & 0xff);
		write8(address + 1, (value >> 8) & 0xff);
		onWrite16(address, value);
	}

	private void onWrite8(int addr, int data) {
		if(this.listener != null) this.listener.onWrite8(addr, data & 0xff);
	}

	private void onWrite16(int addr, int data) {
		if(this.listener != null) this.listener.onWrite16(addr, data & 0xffff);
	}

	public void setListener(GBMemoryListener listener) {
		this.listener = listener;
	}
}
