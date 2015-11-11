package gbemu.cpu.memory.cartridge;

import gbemu.util.Utils;

import java.nio.ByteBuffer;

/**
 * @author Adolph C.
 */
public class MBC1 extends NoMBC {
	private final GBCartridgeHeader header;
	private ByteBuffer rom00;
	private ByteBuffer romNN;
	private ByteBuffer ram;

	/**
	 * If this is true, this is in memory mode 0,
	 * or 16Mbit ROM/8KByte RAM. If this is false,
	 * then this chip is in memory mode 1,
	 * or 4MBit ROM/32KByte RAM.
	 */
	private boolean memoryMode0;
	private int romBank_4000_7FFF;
	private int ramBank_A000_C000;

	public MBC1(GBCartridgeHeader header, ByteBuffer data) {
		super(data);
		this.header = header;
		rom00 = this.sliceFromTo(0, 0x3FFF);
	}

	@Override
	public int read8(int address) {
		return super.read8(address);
	}

	@Override
	public void write8(int address, int value) {
		if(Utils.inRange(address, 0x6000, 0x7FFF)) {
			// The MBC1 defaults to 16Mbit ROM/8KByte RAM mode
			// on power up. Writing a value (XXXXXXXS - X = Don't
			// care, S = Memory model select) into 6000-7FFF area
			// will select the memory model to use. S = 0 selects
			// 16/8 mode. S = 1 selects 4/32 mode.
			memoryMode0 = (value & 1) == 0;
			// todo this might not actually clear the top bits of the bank number :\
			if(!memoryMode0)
				romBank_4000_7FFF &= ~0x60; // clears the upper 2 bits of the rom bank number.
		} else if(Utils.inRange(address, 0x2000, 0x3FFF)) {
			// Writing a value (XXXBBBBB - X = Don't care, B =
			// bank select bits) into 2000-3FFF area will select
			// an appropriate ROM bank at 4000-7FFF. Values of 0
			// and 1 do the same thing and point to ROM bank 1.
			// Rom bank 0 is not accessible from 4000-7FFF and can
			// only be read from 0000-3FFF.
			romBank_4000_7FFF &= ~0x1F; // clears the lower 5 bits of the rom bank number
			romBank_4000_7FFF |= value & 0x1F; // sets the lower 5 bits of the bank number.
			fixROMBankNumber();
		} else if(Utils.inRange(address, 0x4000, 0x5FFF)) {
			// This 2bit register can be used to select a RAM Bank in range from 00-03h,
			// or to specify the upper two bits (Bit 5-6) of the ROM Bank number,
			// depending on the current ROM/RAM Mode. (See below.)
			if(memoryMode0) {
				romBank_4000_7FFF &= ~0x60; // clears the upper 2 bits of the rom bank number.
				romBank_4000_7FFF |= value & 0x60; // sets the upper 2 bits of the bank number.
				fixROMBankNumber();
			} else {
				ramBank_A000_C000 = value & 0x3; // gets the last 2 bits for the ram bank number
			}
		}
		super.write8(address, value);
	}

	/**
	 * fixme this might not actually be correct, might have to create getCorrectBankNumber method instead for use.
	 * Increments the ROM bank number if it is 0x00, 0x20, 0x40, or 0x60
	 */
	private void fixROMBankNumber() {
		if(romBank_4000_7FFF == 0 ||
				romBank_4000_7FFF == 0x20 ||
				romBank_4000_7FFF == 0x40 ||
				romBank_4000_7FFF == 0x60) {
//			System.out.println("Bank fixed from: " + romBank_4000_7FFF);
			romBank_4000_7FFF++;
		}
//		System.out.println("Bank switched to: " + romBank_4000_7FFF);
	}
}
