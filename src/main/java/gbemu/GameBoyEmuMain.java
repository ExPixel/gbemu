package gbemu;

import gbemu.cpu.memory.cartridge.GBCartridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Main entry point for the GameBoy emulator.
 * @author Adolph C.
 */
public class GameBoyEmuMain {
	public static void main(String[] args) throws IOException {
		GBCartridge cartridge = loadROM(new File("roms/cpu_instrs.gb"));
		cartridge.getHeader().printOut();

		GameBoy gameBoy = new GameBoy();
		gameBoy.setCartridge(cartridge);
		gameBoy.bootUp();
		gameBoy.run();
	}

	private static GBCartridge loadROM(File file) throws IOException {
		try(FileInputStream input = new FileInputStream(file)) {
			int len = (int) file.length();
			ByteBuffer buffer = ByteBuffer.allocateDirect(len);
			final int MAX = 0x3FFF;
			int idx = 0;
			int read;
			while (idx < MAX && (read = input.read()) != -1) {
				buffer.put(idx++, (byte) (read & 0xff));
			}
			return new GBCartridge(buffer);
		}
	}
}
