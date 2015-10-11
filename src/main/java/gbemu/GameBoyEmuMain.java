package gbemu;

import gbemu.cpu.memory.GBMemory;
import gbemu.cpu.memory.cartridge.GBCartridge;
import gbemu.cpu.z80.Z80Cpu;
import gbemu.graphics.GameBoyLCD;
import gbemu.graphics.LWJGLContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * Main entry point for the GameBoy emulator.
 * @author Adolph C.
 */
public class GameBoyEmuMain {
	private final static int[] __breakpoints = {
	};
	private final static HashSet<Integer> breakpoints;

	static int BP = 0;
	static int instructionCount = 0; // number of instructions run.

	static long secondDelta = 0;
	static long secondCycles = 0;

	static {
		breakpoints = new HashSet<>(__breakpoints.length);
		for (int __breakpoint : __breakpoints) breakpoints.add(__breakpoint);
	}

	public static void main(String[] args) throws IOException {
		GBMemory memory = new GBMemory();
		Z80Cpu cpu = new Z80Cpu(memory);
		GBCartridge cartridge = loadROM(new File("roms/cpu_instrs.gb"));
//		GBCartridge cartridge = loadROM(new File("roms/licensed/Tetris (World).gb"));
		memory.setCartridge(cartridge);
		cartridge.getHeader().printOut();
		cpu.reg.setPC(0x0100);
		cpu.reg.setA(0x01);
		cpu.reg.setF(0xB0);
		cpu.reg.setBC(0x0013);
		cpu.reg.setDE(0x00D8);
		cpu.reg.setHL(0x014D);
		cpu.reg.setSP(0xFFFE);

		LWJGLContainer container = new LWJGLContainer();
		container.init();
		GameBoyLCD lcd = new GameBoyLCD(container.getWindow(), cpu, memory);
		lcd.init();
		container.loop(delta -> {
			lcd.render(delta);
		});
		lcd.dispose();

//		while(cpu.reg.getPC() <= 0xFFFF) {
//			cpu.execute();
//			instructionCount++;
//			if((memory.read8(0xFF02) & 0x80) != 0) {
//				char c = (char) memory.read8(0xFF01);
//				System.out.print(c);
//				memory.write8(0xFF02, memory.read8(0xFF02) & ~0x80);
//			}
//			if(breakpoints.contains(cpu.reg.getPC())) breakpointHit(cpu);
//		}
	}

	public static void runCPUCycles(Z80Cpu cpu, long delta) {
		// target: 4194304
		// rounded: 4195000
		long cycles = (long) (10000000 * (delta / 1000.0));
		cpu.clock.clearCyclesElapsed();
		while(cpu.clock.getCyclesElapsed() < cycles)
			cpu.execute();
		secondCycles += cpu.clock.getCyclesElapsed();
	}

	private static void breakpointHit(Z80Cpu cpu) {
		BP = 1;
//		System.out.printf("breakpoint at 0x%04x\n", cpu.reg.getPC());
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
