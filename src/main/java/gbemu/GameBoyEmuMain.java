package gbemu;

import gbemu.cpu.memory.GBMemory;
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
		if(args.length < 1) {
			System.err.println("Must provide a ROM file as an argument.");
			System.exit(0);
			return;
		}

		File romFile = new File(args[0]);

		if(!romFile.exists()) {
			System.err.println("Could not find file: " + args[0]);
			System.exit(1);
			return;
		}

		GBCartridge cartridge = loadROM(romFile);
		cartridge.getHeader().printOut();

		GameBoy gameBoy = new GameBoy();
		gameBoy.setCartridge(cartridge);
		gameBoy.bootUp();

		gameBoy.getMemory().setListener(new GBMemory.GBMemoryListener() {
			@Override
			public void onWrite8(int address, int data) {
				if(address == 0xFF02) {
					if(gameBoy.getMemory().ioPorts.SC == 0x81) {
						System.out.print((char) gameBoy.getMemory().ioPorts.SB);
						gameBoy.getMemory().ioPorts.SC &= ~0b10000000; // Clear the bit
					}
				}
			}

			@Override
			public void onWrite16(int address, int data) {

			}
		});

		gameBoy.run();
	}

	private static GBCartridge loadROM(File file) throws IOException {
		try(FileInputStream input = new FileInputStream(file)) {
			int len = (int) file.length();
			ByteBuffer buffer = ByteBuffer.allocateDirect(len);
			final int MAX = 0x7FFF;
			int idx = 0;
			int read;
			while (idx < MAX && (read = input.read()) != -1) {
				buffer.put(idx++, (byte) (read & 0xff));
			}
			return new GBCartridge(buffer);
		}
	}
}
