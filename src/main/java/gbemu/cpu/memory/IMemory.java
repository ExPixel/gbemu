package gbemu.cpu.memory;

/**
 * @author Adolph C.
 */
public interface IMemory {
	int read8(int address);
	void write8(int address, int value);
	int read16(int address);
	void write16(int address, int value);
}
