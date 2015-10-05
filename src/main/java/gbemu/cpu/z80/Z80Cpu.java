package gbemu.cpu.z80;

/**
 * @author Adolph C.
 */
public class Z80Cpu {
	private Z80Registers reg;
	private Z80Clock clock;

	public Z80Cpu(Z80Registers reg) {
		this.reg = reg;
	}

	public void stop() {

	}

	public void halt() {

	}

	public void removedInstr() {
		// Called when a removed instruction was called.
		// Just calls clock.inc(0) by default which should do nothing.
		clock.inc(0);
	}
}
