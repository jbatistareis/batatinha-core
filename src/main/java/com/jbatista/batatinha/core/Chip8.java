package com.jbatista.batatinha.core;

import java.io.IOException;
import java.io.InputStream;

public class Chip8 {

    private final Display display = new Display();
    private final Input input = new Input();
    private final Processor processor = new Processor(display, input);

    public void loadProgram(InputStream program) throws IOException {
        processor.loadProgram(program);
    }

    public void reset() {
        processor.reset();
    }

    public char[] getDisplayBuffer() {
        return display.getBuffer();
    }

    public void presKey(Key key) {
        input.press(key);
    }

    public void releaseKey(Key key) {
        input.release(key);
    }

    public void cpuTick() {
        processor.cpuTick();
    }

    public boolean timerTick() {
        return processor.timerTick();
    }

}
