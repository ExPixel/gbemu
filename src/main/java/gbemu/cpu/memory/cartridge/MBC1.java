package gbemu.cpu.memory.cartridge;

import gbemu.util.Utils;

import java.nio.ByteBuffer;

/**
 * @author Adolph C.
 */
public class MBC1 extends NoMBC {
    private final GBCartridgeHeader header;

    private static final int ROM_MASK_RAM_BANKING_MODE = 0b0011111;
    private static final int ROM_MASK_ROM_BANKING_MODE = 0b1111111;
    private static final int RAM_MASK_RAM_BANKING_MODE = 0b0000011;
    private static final int RAM_MASK_ROM_BANKING_MODE = 0b0000000;

    private int romBankNumber = 1;
    private int ramBankNumber = 0;
    private int romBankNumberMask = ROM_MASK_ROM_BANKING_MODE;
    private int ramBankNumberMask = RAM_MASK_ROM_BANKING_MODE;

    /**
     * <pre>
     * Before external RAM can be read or written, it must be enabled by writing to this address space. It is recommended to disable external RAM after accessing it, in order to protect its contents from damage during power down of the gameboy. Usually the following values are used:
     * 00h  Disable RAM (default)
     * 0Ah  Enable RAM
     * Practically any value with 0Ah in the lower 4 bits enables RAM, and any other value disables RAM.
     * </pre>
     */
    private boolean externalRamEnabled = false;

    /**
     * <pre>
     * This 1bit Register selects whether the two bits of the above register should be used as upper two bits of the ROM Bank, or as RAM Bank Number.
     * 00h = ROM Banking Mode (up to 8KByte RAM, 2MByte ROM) (default)
     * 01h = RAM Banking Mode (up to 32KByte RAM, 512KByte ROM)
     * The program may freely switch between both modes, the only limitiation is that only RAM Bank 00h can be used during Mode 0, and only ROM Banks 00-1Fh can be used during Mode 1.
     * </pre>
     */
    private boolean ramBankingMode = false;

    public MBC1(GBCartridgeHeader header, ByteBuffer data) {
        super(data);
        this.header = header;
    }

    @Override
    public int read8(int address) {
        return this.mbc1Read8(address);
    }

    @Override
    public void write8(int address, int value) {
        if(address >= 0 && address <= 0x1FFF) {
            externalRamEnabled = (value & 0xf) == 0xA;
            return;
        } else if(address >= 0x2000 && address <= 0x3FFF) {
            romBankNumber &= ~0x1f; // clear the last 5 bits
            romBankNumber |= value & 0x1f; // set the last 5 bits
            // corret the rom bank number.
            correctROMBankNumber();
            return;
        } else if(address >= 0x4000 && address <= 0x5FFF) {
            ramBankNumber = value & 0x3;
            romBankNumber &= ~0x60;
            romBankNumber |= ramBankNumber << 5;
            correctROMBankNumber();
            return;
        } else if(address >= 0x6000 && address <= 0x7FFF) {
            this.ramBankingMode = (value & 1) == 1;
            if(this.ramBankingMode) {
                this.romBankNumberMask = ROM_MASK_RAM_BANKING_MODE;
                this.ramBankNumberMask = RAM_MASK_RAM_BANKING_MODE;
            } else {
                this.romBankNumberMask = ROM_MASK_ROM_BANKING_MODE;
                this.ramBankNumberMask = RAM_MASK_ROM_BANKING_MODE;
            }
            return;
        }
        this.mbc1Write8(address, value);
    }

    private void correctROMBankNumber() {
        int tempRomBankNumber = this.romBankNumber & this.romBankNumberMask;
        if(tempRomBankNumber == 0 ||
                tempRomBankNumber == 0x20 ||
                tempRomBankNumber == 0x40 ||
                tempRomBankNumber == 0x60) {
            tempRomBankNumber++;
        }
        this.romBankNumber = tempRomBankNumber;
        System.out.println("ROM Bank Number: " + this.romBankNumber);
    }

    private int mbc1Read8(int address) {
        int oldAddress = address;
        if(address >= 0x4000 && address <= 0x7FFF)
            address += ((romBankNumber & this.romBankNumberMask) - 1) * (0x4000);
            // -1 above because romBank1 should actually be at 0x4000
        return this.data.get(address) & 0xff;
    }

    private void mbc1Write8(int address, int value) {
        if(address >= 0x4000 && address <= 0x7FFF) address += (romBankNumber & this.romBankNumberMask) * (0x4000);
        this.data.put(address, (byte) (value & 0xff));
    }
}
