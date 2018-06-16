package com.jbatista.batatinha.core;

public enum Note {
    C(262), C_SHRP(278), D(294), D_SHRP(311), E(330), F(349), F_SHRP(370), G(392), G_SHRP(415), A(440), A_SHRP(466), B(494);

    private int frequency;

    Note(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return this.frequency;
    }

}
