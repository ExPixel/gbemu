package gbemu.cpu.z80;

import gbemu.cpu.memory.GBMemory;

/**
 * Miscellaneous functions of the CPU that I couldn't quite place.
 * Like DMA transfers and what have you.
 * @author Adolph C.
 */
public class GameBoyMiscFunctions {
    private Z80Cpu cpu;
    private GBMemory memory;

    public GameBoyMiscFunctions(Z80Cpu cpu, GBMemory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    public void cycle() {
        if(this.memory.ioPorts.DMAWrite) {
            this.executeDMA();
            this.memory.ioPorts.DMAWrite = false;
        }
    }

    public void executeDMA() {
        int addr = Math.min(memory.ioPorts.DMA, 0xf1) << 8;
        for(int offset = 0; offset < 160; offset++) {
            memory.write8(0xFE00 + offset, memory.read8(addr + offset));
        }
    }
}
