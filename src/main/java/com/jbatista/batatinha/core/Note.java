package com.jbatista.batatinha.core;

public enum Note {
    A(440), B(493), C(523), D(587), E(659), F(698), G(783);

    private int frequency;

    Note(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return this.frequency;
    }

}
