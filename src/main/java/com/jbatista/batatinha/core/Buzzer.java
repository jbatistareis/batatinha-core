package com.jbatista.batatinha.core;

class Buzzer {

    private static final double pi2 = 2 * Math.PI;

    private static byte[] sineWave(int frequency, int amplitude, int sampleRate, int sampleSize) {
        final byte[] output = new byte[sampleSize];
        final double f = (double) frequency / sampleRate;
        final int fadeStart = sampleSize - amplitude - 1;

        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) (((i > fadeStart) ? --amplitude : amplitude) * Math.sin(pi2 * f * i));
        }

        return output;
    }

    static byte[] createNote(Note note, int amplitude, Format format) {
        return null;
    }

    static byte[] createNote(int frequency, int amplitude, int sampleRate, int bitsPerSample, int channels) {
        return sineWave(frequency, amplitude, sampleRate, (sampleRate * bitsPerSample * channels) / 1024);
    }

}
