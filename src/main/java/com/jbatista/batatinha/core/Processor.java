package com.jbatista.batatinha.core;

import com.jbatista.batatinha.core.Display.Mode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

class Processor {

    private static final Random random = new Random();

    // CPU, memory, registers, program counter
    private char opcode;
    private final char[] memory = new char[4096];
    private final char[] v = new char[16];
    private char i;
    private char programCounter;

    // hardcoded fonts
    private static final char[] chip8Font = {
        0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
        0x20, 0x60, 0x20, 0x20, 0x70, // 1
        0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
        0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
        0x90, 0x90, 0xF0, 0x10, 0x10, // 4
        0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
        0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
        0xF0, 0x10, 0x20, 0x40, 0x40, // 7
        0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
        0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
        0xF0, 0x90, 0xF0, 0x90, 0x90, // A
        0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
        0xF0, 0x80, 0x80, 0x80, 0xF0, // C
        0xE0, 0x90, 0x90, 0x90, 0xE0, // D
        0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
        0xF0, 0x80, 0xF0, 0x80, 0x80 // F
    };

    // this font was copied from octo [ https://github.com/JohnEarnest/Octo ], the original super chip font has rounded edges
    // acording to [ https://github.com/Chromatophore/HP48-Superchip/blob/master/investigations/quirk_font.md ], A-F are not used
    private static final char[] superChipFont = {
        0xFF, 0xFF, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF, // 0
        0x18, 0x78, 0x78, 0x18, 0x18, 0x18, 0x18, 0x18, 0xFF, 0xFF, // 1
        0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, // 2
        0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, // 3
        0xC3, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF, 0x03, 0x03, 0x03, 0x03, // 4
        0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, // 5
        0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, // 6
        0xFF, 0xFF, 0x03, 0x03, 0x06, 0x0C, 0x18, 0x18, 0x18, 0x18, // 7
        0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, // 8
        0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, // 9
        0x7E, 0xFF, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF, 0xC3, 0xC3, 0xC3, // A
        0xFC, 0xFC, 0xC3, 0xC3, 0xFC, 0xFC, 0xC3, 0xC3, 0xFC, 0xFC, // B
        0x3C, 0xFF, 0xC3, 0xC0, 0xC0, 0xC0, 0xC0, 0xC3, 0xFF, 0x3C, // C
        0xFC, 0xFE, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xFE, 0xFC, // D
        0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, // E
        0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC0, 0xC0, 0xC0, 0xC0 // F
    };

    // jump routine stack
    private final char[] stack = new char[16];
    private char stackPointer;

    // timers
    private char soundTimer;
    private char delayTimer;

    // auxiliary
    private final Display display;
    private final Input input;
    private final Map<Character, Consumer<Character>> opcodesMap = new HashMap<>();
    private boolean beep;
    private char decodedOpcode;
    private char tempResult;
    private int drawN;

    Processor(Display display, Input input) {
        this.display = display;
        this.input = input;

        // load both fonts
        for (int i = 0; i < chip8Font.length; i++) {
            memory[i] = chip8Font[i];
        }
        for (int i = 0; i < superChipFont.length; i++) {
            memory[i + chip8Font.length] = superChipFont[i];
        }

        // <editor-fold defaultstate="collapsed" desc="hardcoded opcode functions, double click to expand (Netbeans)">
        // chip-8 opcodes
        opcodesMap.put((char) 0xE0, this::dispClear);
        opcodesMap.put((char) 0xEE, this::returnSubRoutine);
        opcodesMap.put((char) 0x1000, this::goTo);
        opcodesMap.put((char) 0x2000, this::callSubroutine);
        opcodesMap.put((char) 0x3000, this::skipVxEqNN);
        opcodesMap.put((char) 0x4000, this::skipVxNotEqNN);
        opcodesMap.put((char) 0x5000, this::skipVxEqVy);
        opcodesMap.put((char) 0x6000, this::setVx);
        opcodesMap.put((char) 0x7000, this::addNNtoVx);
        opcodesMap.put((char) 0x8000, this::setVxTovY);
        opcodesMap.put((char) 0x8001, this::setVxToVxOrVy);
        opcodesMap.put((char) 0x8002, this::setVxToVxAndVy);
        opcodesMap.put((char) 0x8003, this::setVxToVxXorVy);
        opcodesMap.put((char) 0x8004, this::addVxToVyCarry);
        opcodesMap.put((char) 0x8005, this::subtractVyFromVx);
        opcodesMap.put((char) 0x8006, this::shiftVxRightBy1);
        opcodesMap.put((char) 0x8007, this::subtractVxFromVy);
        opcodesMap.put((char) 0x800E, this::shiftVxLeftBy1);
        opcodesMap.put((char) 0x9000, this::skipVxNotEqVy);
        opcodesMap.put((char) 0xA000, this::setI);
        opcodesMap.put((char) 0xB000, this::goToV0);
        opcodesMap.put((char) 0xC000, this::rand);
        opcodesMap.put((char) 0xD000, this::draw);
        opcodesMap.put((char) 0xE09E, this::skipVxEqKey);
        opcodesMap.put((char) 0xE0A1, this::skipVxNotEqKey);
        opcodesMap.put((char) 0xF007, this::vxToDelay);
        opcodesMap.put((char) 0xF00A, this::waitKey);
        opcodesMap.put((char) 0xF015, this::setDelayTimer);
        opcodesMap.put((char) 0xF018, this::setSoundTimer);
        opcodesMap.put((char) 0xF01E, this::addsVxToI);
        opcodesMap.put((char) 0xF029, this::setIToSpriteInVx5bit);
        opcodesMap.put((char) 0xF033, this::bcd);
        opcodesMap.put((char) 0xF055, this::dump);
        opcodesMap.put((char) 0xF065, this::load);

        // superchip opcodes
        opcodesMap.put((char) 0x10, this::exitWithCode);
        opcodesMap.put((char) 0xC0, this::scrollDown);
        opcodesMap.put((char) 0xFA, this::compat);
        opcodesMap.put((char) 0xFB, this::scrollRight);
        opcodesMap.put((char) 0xFC, this::scrollLeft);
        opcodesMap.put((char) 0xFD, this::terminate);
        opcodesMap.put((char) 0xFE, this::loRes);
        opcodesMap.put((char) 0xFF, this::hiRes);
        opcodesMap.put((char) 0xF030, this::setIToSpriteInVx10bit);
        opcodesMap.put((char) 0xF075, this::flagSave);
        opcodesMap.put((char) 0xF085, this::flagRestore);
        // </editor-fold>
    }

    void loadProgram(char[] program) {
        Arrays.fill(memory, 512, memory.length, (char) 0);

        for (i = 0; i < program.length; i++) {
            memory[i + 512] = program[i];
        }
    }

    void reset() {
        Arrays.fill(v, (char) 0);
        Arrays.fill(stack, (char) 0);

        display.clear();
        i = 0;
        stackPointer = 0;
        soundTimer = 0;
        delayTimer = 0;
        programCounter = 512;
    }

    // into main loop
    void cpuTick() {
        opcode = (char) (memory[programCounter] << 8 | memory[programCounter + 1]);
        decodedOpcode = (char) (opcode & 0xF000);

        // special cases, for instructions that use the last and/or the before last value
        // namely 0x00XX, 0x00X#, 0x8##X, 0xF#XX and 0xE#XX
        if (decodedOpcode == 0x8000) {
            decodedOpcode = (char) (opcode & 0xF00F);
        } else if ((decodedOpcode == 0xE000) || (decodedOpcode == 0xF000)) {
            decodedOpcode = (char) (opcode & 0xF0FF);
        } else if (decodedOpcode == 0x0) {
            // special case 0x00C# and 0x001#
            decodedOpcode = (char) (opcode & 0x00F0);
            if ((decodedOpcode != 0xC0) && (decodedOpcode != 0x10)) {
                decodedOpcode = opcode;
            }
        }

        if (opcodesMap.containsKey(decodedOpcode)) {
            opcodesMap.get(decodedOpcode).accept(opcode);
        } else {
            throw new RuntimeException("UNKNOWN OPCODE - 0x" + Integer.toHexString(opcode).toUpperCase());
        }
    }

    // 60Hz
    boolean timerTick() {
        if (soundTimer > 0) {
            if ((--soundTimer == 0) && beep) {
                beep = false;
                return true;
            }
        }

        if (delayTimer > 0) {
            delayTimer--;
        }

        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="opcode methods, double click to expand (Netbeans)">
    // 0000
    private void call(char opc) {
        // not used (?)
        // calls a routine on the RCA 1802 chip
        // acording to the internet nobody ever used it
    }

    // 00E0
    private void dispClear(char opc) {
        display.clear();
        programCounter += 2;
    }

    // 00EE
    private void returnSubRoutine(char opc) {
        programCounter = (char) (stack[--stackPointer] + 2);
    }

    // 1NNN
    private void goTo(char opc) {
        programCounter = (char) (opc & 0x0FFF);
    }

    // 2NNN
    private void callSubroutine(char opc) {
        stack[stackPointer++] = programCounter;
        programCounter = (char) (opc & 0x0FFF);
    }

    // 3XNN
    private void skipVxEqNN(char opc) {
        if (v[(opc & 0x0F00) >> 8] == (opc & 0x00FF)) {
            programCounter += 4;
        } else {
            programCounter += 2;
        }
    }

    // 4XNN
    private void skipVxNotEqNN(char opc) {
        if (v[(opc & 0x0F00) >> 8] != (opc & 0x00FF)) {
            programCounter += 4;
        } else {
            programCounter += 2;
        }
    }

    // 5XY0
    private void skipVxEqVy(char opc) {
        if (v[(opc & 0x0F00) >> 8] == v[(opc & 0x00F0) >> 4]) {
            programCounter += 4;
        } else {
            programCounter += 2;
        }
    }

    // 6XNN
    private void setVx(char opc) {
        v[(opc & 0x0F00) >> 8] = (char) (opc & 0x00FF);
        programCounter += 2;
    }

    // 7XNN
    private void addNNtoVx(char opc) {
        v[(opc & 0x0F00) >> 8] += (opc & 0x00FF);
        v[(opc & 0x0F00) >> 8] &= 0xFF;
        programCounter += 2;
    }

    // 8XY0
    private void setVxTovY(char opc) {
        v[(opc & 0x0F00) >> 8] = v[(opc & 0x00F0) >> 4];
        programCounter += 2;
    }

    // 8XY1
    private void setVxToVxOrVy(char opc) {
        v[(opc & 0x0F00) >> 8] = (char) (v[(opc & 0x0F00) >> 8] | v[(opc & 0x00F0) >> 4]);
        programCounter += 2;
    }

    // 8XY2
    private void setVxToVxAndVy(char opc) {
        v[(opc & 0x0F00) >> 8] &= v[(opc & 0x00F0) >> 4];
        programCounter += 2;
    }

    // 8XY3
    private void setVxToVxXorVy(char opc) {
        v[(opc & 0x0F00) >> 8] ^= v[(opc & 0x00F0) >> 4];
        programCounter += 2;
    }

    // 8XY4
    private void addVxToVyCarry(char opc) {
        tempResult = (char) (v[(opc & 0x0F00) >> 8] + v[(opc & 0x00F0) >> 4]);
        v[0xF] = (char) ((tempResult > 0xFF) ? 1 : 0);
        v[(opc & 0x0F00) >> 8] = (char) (tempResult & 0xFF);
        programCounter += 2;
    }

    // 8XY5    
    private void subtractVyFromVx(char opc) {
        v[0xF] = (char) ((v[(opc & 0x0F00) >> 8] >= v[(opc & 0x00F0) >> 4]) ? 1 : 0);
        v[(opc & 0x0F00) >> 8] -= v[(opc & 0x00F0) >> 4];
        programCounter += 2;
    }

    // 8XY6
    // see [ https://github.com/Chromatophore/HP48-Superchip/blob/master/investigations/quirk_shift.md ]
    private void shiftVxRightBy1(char opc) {
        v[0xF] = (char) (v[(opc & 0x0F00) >> 8] & 1);
        v[(opc & 0x0F00) >> 8] >>= 1;
        programCounter += 2;
    }

    // 8XY7
    private void subtractVxFromVy(char opc) {
        v[0xF] = (char) ((v[(opc & 0x00F0) >> 4] >= v[(opc & 0x0F00) >> 8]) ? 1 : 0);
        v[(opc & 0x0F00) >> 8] = (char) (v[(opc & 0x00F0) >> 4] - v[(opc & 0x0F00) >> 8]);
        programCounter += 2;
    }

    // 8XYE
    // see [ https://github.com/Chromatophore/HP48-Superchip/blob/master/investigations/quirk_shift.md ]
    private void shiftVxLeftBy1(char opc) {
        v[0xF] = (char) ((v[(opc & 0x0F00) >> 8] >> 7) & 1);
        v[(opc & 0x0F00) >> 8] <<= 1;
        programCounter += 2;
    }

    // 9XY0
    private void skipVxNotEqVy(char opc) {
        if (v[(opc & 0x0F00) >> 8] != v[(opc & 0x00F0) >> 4]) {
            programCounter += 4;
        } else {
            programCounter += 2;
        }
    }

    // ANNN
    private void setI(char opc) {
        i = (char) (opc & 0x0FFF);
        programCounter += 2;
    }

    // BNNN
    private void goToV0(char opc) {
        programCounter = (char) (v[0x0] + (opc & 0x0FFF));
    }

    // CXNN
    private void rand(char opc) {
        v[(opc & 0x0F00) >> 8] = (char) (random.nextInt(255) & (opc & 0x00FF));
        programCounter += 2;
    }

    // DXYN
    // if N = 0, and hi res is active, it loads a 16 x 16 sprite, else is 8 x N
    private void draw(char opc) {
        drawN = (((drawN = opc & 0x000F) == 0) && display.getDisplayMode().equals(Mode.HIGH_RES)) ? 16 : drawN;

        if (display.getDisplayMode().equals(Mode.HIGH_RES) && (drawN == 16)) {
            for (int index = 0; index < 32; index += 2) {
                display.addSpriteData((char) (memory[i + index] << 8 | memory[i + index + 1]));
            }
        } else {
            for (int index = 0; index < drawN; index++) {
                display.addSpriteData(memory[i + index]);
            }
        }

        v[0xF] = display.draw(v[(opcode & 0x0F00) >> 8], v[(opcode & 0x00F0) >> 4], (drawN == 16 ? 16 : 8));
        programCounter += 2;
    }

    // EX9E
    private void skipVxEqKey(char opc) {
        if (input.isPressed(v[(opc & 0x0F00) >> 8])) {
            programCounter += 4;
        } else {
            programCounter += 2;
        }
    }

    // EXA1
    private void skipVxNotEqKey(char opc) {
        if (!input.isPressed(v[((opc & 0x0F00) >> 8)])) {
            programCounter += 4;
        } else {
            programCounter += 2;
        }
    }

    // FX07
    private void vxToDelay(char opc) {
        v[(opc & 0x0F00) >> 8] = (char) delayTimer;
        programCounter += 2;
    }

    // FX0A
    private void waitKey(char opc) {
        // only advances the program counter if there was a key press
        if (input.pressRegistred()) {
            v[(opc & 0x0F00) >> 8] = input.getLastKey();
            programCounter += 2;
        }
    }

    // FX15
    private void setDelayTimer(char opc) {
        delayTimer = v[(opc & 0x0F00) >> 8];
        programCounter += 2;
    }

    // FX18
    private void setSoundTimer(char opc) {
        beep = true;
        soundTimer = v[(opc & 0x0F00) >> 8];
        programCounter += 2;
    }

    // FX1E
    private void addsVxToI(char opc) {
        i += v[(opc & 0x0F00) >> 8];
        programCounter += 2;
    }

    // FX29
    private void setIToSpriteInVx5bit(char opc) {
        i = (char) ((v[(opc & 0x0F00) >> 8] * 5));
        programCounter += 2;
    }

    // FX33
    // this was copied, shame on me :( [ source: https://github.com/JohnEarnest/Octo ]
    private void bcd(char opc) {
        memory[i] = (char) ((v[(opc & 0x0F00) >> 8] / 100) % 10);
        memory[i + 1] = (char) ((v[(opc & 0x0F00) >> 8] / 10) % 10);
        memory[i + 2] = (char) (v[(opc & 0x0F00) >> 8] % 10);
        programCounter += 2;
    }

    // FX55
    private void dump(char opc) {
        for (int vx = 0; vx <= ((opc & 0x0F00) >> 8); vx++) {
            memory[i + vx] = v[vx];
        }
        programCounter += 2;
    }

    // FX65
    private void load(char opc) {
        for (int vx = 0; vx <= ((opc & 0x0F00) >> 8); vx++) {
            v[vx] = memory[i + vx];
        }
        programCounter += 2;
    }

    // superchip opcodes
    // DXY0 is implemented inside DXYN
    // 00CX
    private void scrollDown(char opc) {
        display.scrollDown(opc & 0x000F);
        programCounter += 2;
    }

    // 00FA
    // not used?, makes the i register read only
    private void compat(char opc) {
        // ?, create a flag or something
        programCounter += 2;
    }

    // 00FB
    private void scrollRight(char opc) {
        display.scrollR4();
        programCounter += 2;
    }

    // 00FC
    private void scrollLeft(char opc) {
        display.scrollL4();
        programCounter += 2;
    }

    // 00FE
    private void loRes(char opc) {
        display.setDisplayMode(Mode.LOW_RES);
        programCounter += 2;
    }

    // 00FF
    private void hiRes(char opc) {
        display.setDisplayMode(Mode.HIGH_RES);
        programCounter += 2;
    }

    // F030
    private void setIToSpriteInVx10bit(char opc) {
        i = (char) ((v[(opc & 0x0F00) >> 8] * 10 + chip8Font.length));
        programCounter += 2;
    }

    // FX75
    private void flagSave(char opc) {
        // HP48 function, i think nobody knows what it does
        programCounter += 2;
    }

    // FX85
    private void flagRestore(char opc) {
        // HP48 function, i think nobody knows what it does
        programCounter += 2;
    }

    // 00FD
    // exit 0 is too extreme, just reset the whole thing =^)
    private void terminate(char opc) {
        reset();
    }

    // 001X
    // exit with a code: 0 means normal, 1 means error
    // since it is not used (?), reset ;)
    private void exitWithCode(char opc) {
        // skeleton, in case i come up with some other idea 
        final int exitCode = opc & 0x000F;
        if (exitCode == 0) {
            reset();
        } else if (exitCode == 1) {
            reset();
        }
    }

    // </editor-fold>
}
