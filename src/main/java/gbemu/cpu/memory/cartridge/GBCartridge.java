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
		this.loadMBCChip();
	}

	private void loadMBCChip() {
		int cartridgeType = this.header.getCartridgeType();
		switch(cartridgeType) {
			case 0:
				this.mbc = new NoMBC(this.cartridgeData);
				break;
			case 1:
				this.mbc = new MBC1(header, this.cartridgeData);
				break;
			default:
				String cartridgeString = CartridgeDataMaps.cartridgeTypes.get(cartridgeType);
				throw new IllegalStateException("Unsupported cartridge type: " + cartridgeString);
		}
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
