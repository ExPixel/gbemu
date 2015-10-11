package gbemu.cpu.memory.cartridge;

import gbemu.util.Utils;

import java.util.Map;

/**
 * @author Adolph C.
 */
public class CartridgeDataMaps {
	public static final Map<Integer, String> cartridgeTypes = Utils.map(
			0x00, "ROM ONLY",
			0x01, "ROM + MBC1",
			0x02, "ROM + MBC1 + RAM",
			0x03, "ROM + MBC1 + RAM + BATT",
			0x05, "ROM + MBC2",
			0x06, "ROM + MBC2 + BATTERY",
			0x07, "ROM + RAM",
			0x08, "ROM + RAM",
			0x09, "ROM + RAM + BATTERY",
			0x0B, "ROM + MMM01",
			0x0C, "ROM + MMM01 + SRAM",
			0x0D, "ROM + MMM01 + SRAM + BATT",
			0x0F, "ROM + MBC3 + TIMER + BATT",
			0x10, "ROM + MBC3 + TIMER + RAM + BATT",
			0x11, "ROM + MBC3",
			0x12, "ROM + MBC3 + RAM",
			0x13, "ROM + MBC3 + RAM + BATT",
			0x19, "ROM + MBC5",
			0x1A, "ROM + MBC5 + RAM",
			0x1B, "ROM + MBC5 + RAM + BATT",
			0x1C, "ROM + MBC5 + RUMBLE",
			0x1D, "ROM + MBC5 + RUMBLE + SRAM",
			0x1E, "ROM + MBC5 + RUMBLE + SRAM + BATT",
			0x1F, "Pocket Camera",
			0xFD, "Bandai TAMA5",
			0xFE, "Hudson HuC-3",
			0xFF, "Hudson HuC-1"
	);

	public static final Map<Integer, String> ROMSizes = Utils.map(
			0x00, "256 Kbit = 32 KByte = 2 banks",
			0x01, "512 Kbit = 64 KByte = 4 banks",
			0x02, "1 Mbit = 128 KByte = 8 banks",
			0x03, "2 Mbit = 256 KByte = 16 banks",
			0x04, "4 Mbit = 512 KByte = 32 banks",
			0x05, "8 Mbit = 1 MByte = 64 banks",
			0x06, "16 Mbit = 2 MByte = 128 banks",
			0x52, "9 Mbit = 1.1 MByte = 72 banks",
			0x53, "10 Mbit = 1.2 MByte = 80 banks",
			0x54, "12 Mbit = 1.5 MByte = 96 banks"
	);

	public static final Map<Integer, String> RAMSizes = Utils.map(
			0, "None",
			1, "16 kBit = 2 kB = 1 bank",
			2, "64 kBit = 8 kB = 1 bank",
			3, "256 kBit = 32 kB = 4 banks",
			4, "1 MBit = 128 kB =16 banks"
	);

	public static final Map<Integer, String> destinationCode = Utils.map(
			0, "Japanese",
			1, "Non-Japanese"
	);

	public static final Map<Integer, String> OldLicenseeCode = Utils.map(
			0x33, "(NEW)",
			0x79, "Accolade",
			0xA4, "Konami"
	);
}
