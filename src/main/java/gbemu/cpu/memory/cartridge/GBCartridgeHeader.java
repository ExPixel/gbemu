package gbemu.cpu.memory.cartridge;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author Adolph C.
 */
public class GBCartridgeHeader {
	private String gameTitle;
	private boolean colorGB;
	private int licenseeCode;
	private boolean sgbFunctions;
	private int cartridgeType;
	private int romSize;
	private int ramSize;
	private int destinationCode;
	private int oldLicenseeCode;
	private int maskRomVersionNumber;
	private int complementCheck;
	private int checksum;

	private GBCartridgeHeader() {}

	public void printOut() {
		System.out.println("--- GameBoy Cartridge Header ---");
		System.out.printf("Game Title:\t\t\t\t%s\n", gameTitle);
		System.out.printf("Color GB:\t\t\t\t%b\n", colorGB);
		System.out.printf("Licensee Code:\t\t\t0x%04x\n", licenseeCode);
		System.out.printf("SGB Functions:\t\t\t%b\n", sgbFunctions);
		System.out.printf("Cartridge Type:\t\t\t[0x%02x] %s\n", cartridgeType, CartridgeDataMaps.cartridgeTypes.get(cartridgeType));
		System.out.printf("ROM Size:\t\t\t\t[0x%02x] %s\n", romSize, CartridgeDataMaps.ROMSizes.get(romSize));
		System.out.printf("RAM Size:\t\t\t\t[0x%02x] %s\n", ramSize, CartridgeDataMaps.RAMSizes.get(ramSize));
		System.out.printf("Destination Code:\t\t[0x%02x] %s\n", destinationCode, CartridgeDataMaps.destinationCode.get(destinationCode));
		System.out.printf("Old Licensee Code:\t\t[0x%02x] %s\n", oldLicenseeCode, CartridgeDataMaps.OldLicenseeCode.get(oldLicenseeCode));
		System.out.printf("Mask ROM Version #:\t\t0x%02x\n", maskRomVersionNumber);
		System.out.printf("Complement Check:\t\t0x%02x\n", complementCheck);
		System.out.printf("Checksum:\t\t\t\t0x%04x\n", checksum);
		System.out.println("--------------------------------");
	}

	public static GBCartridgeHeader from(ByteBuffer data) {
		GBCartridgeHeader header = new GBCartridgeHeader();

		byte[] title = new byte[16];
		int titleLen = 0;
		for(int tidx = 0x0134; tidx <= 0x0142; tidx++) {
			byte c = data.get(tidx);
			if(c == 0) continue;
			title[titleLen++] = c;
		}

		header.gameTitle = new String(title, 0, titleLen, StandardCharsets.UTF_8);
		header.colorGB = u8(data.get(0x0143)) == 0x80;
		header.licenseeCode = (u8(data.get(0x0144)) << 4) | u8(data.get(0x0145));
		header.sgbFunctions = u8(data.get(0x0146)) == 0x03;
		header.cartridgeType = u8(data.get(0x0147));
		header.romSize = u8(data.get(0x0148));
		header.ramSize = u8(data.get(0x0149));
		header.destinationCode = u8(data.get(0x014A));
		header.oldLicenseeCode = u8(data.get(0x014B));
		header.maskRomVersionNumber = u8(data.get(0x014C));
		header.complementCheck = u8(data.get(0x014D));
		header.checksum = (u8(data.get(0x014E)) << 4) | u8(data.get(0x014F));
		return header;
	}

	private static int u8(byte b) { return ((int)b) & 0xff; }
}
