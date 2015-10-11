package gbemu.cpu.memory.cartridge;

import java.nio.ByteBuffer;

/**
 * @author Adolph C.
 */
public class GBCartridge {
	private MBC mbc;
	private GBCartridgeHeader header;
	private ByteBuffer cartridgeData;

	public GBCartridge(ByteBuffer cartridgeData) {
		this.cartridgeData = cartridgeData;
		this.header = GBCartridgeHeader.from(cartridgeData);
		this.mbc = new MBC1(header, this.cartridgeData);
				//new NoMBC(this.cartridgeData); // todo need to get the real MBC chip somewhere and put it here.
	}

	public MBC getMbc() {
		return mbc;
	}

	public GBCartridgeHeader getHeader() {
		return header;
	}

	public ByteBuffer getCartridgeData() {
		return cartridgeData;
	}
}
