package com.jbatista.batatinha.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Chip8 {

    private final static Buzzer buzzer = new Buzzer();
    private final Display display = new Display();
    private final Input input = new Input();
    private final Processor processor = new Processor(display, input);

    private byte[] buzzerNote = new byte[0];

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

    public void createNote(Note note, int amplitude, Format format) {
        this.buzzerNote = buzzer.createNote(note, amplitude, format);
    }

    public void createNote(int frequency, int amplitude, int sampleRate, int bitsPerSample, int channels) {
        this.buzzerNote = buzzer.createNote(frequency, amplitude, sampleRate, bitsPerSample, channels);
    }

    public byte[] getNote() {
        return this.buzzerNote;
    }

    public char[] getDisplayBuffer() {
        return this.display.getBuffer();
    }

    public Input getInput() {
        return this.input;
    }

    public void cpuTick() {
        processor.cpuTick();
    }

    public boolean timerTick() {
        return processor.timerTick();
    }

}
