package com.jbatista.batatinha.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Chip8 {

    private final Display display = new Display();
    private final Input input = new Input();
    private final Processor processor = new Processor(display, input);

    public void loadProgram(File program) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(program);
        final char[] dataArray = new char[(int) program.length()];
        int data;
        int index = 0;
        while ((data = fileInputStream.read()) != -1) {
            dataArray[index++] = (char) data;
        }
        fileInputStream.close();

        processor.loadProgram(dataArray);
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
