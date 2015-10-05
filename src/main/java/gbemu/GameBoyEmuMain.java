package gbemu;

import gbemu.cpu.memory.GBMemory;
import gbemu.cpu.memory.IMemory;
import gbemu.cpu.z80.Z80Cpu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Main entry point for the GameBoy emulator.
 * @author Adolph C.
 */
public class GameBoyEmuMain {
	public static void main(String[] args) {
		IMemory memory = new GBMemory();
		Z80Cpu cpu = new Z80Cpu(memory);
		loadROM(new File("roms/test/test.gb"), memory);
		while(cpu.reg.getPC() < 0x3FFF) {
			cpu.execute();
		}
	}

	private static void loadROM(File file, IMemory memory) {
		try(FileInputStream input = new FileInputStream(file)) {
			final int MAX = 0x3FFF;
			int idx = 0;
			int read;
			while(idx < MAX && (read = input.read()) != -1) {
				memory.write8(idx++, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
