package com.jbatista.batatinha.core;

class Buzzer {

    private static final double pi2 = 2 * Math.PI;

    private static byte[] sineWave(int frequency, int amplitude, int sampleRate, int sampleSize) {
        final byte[] output = new byte[sampleSize];
        final double f = (double) frequency / sampleRate;
        final int fadeStart = sampleSize - amplitude;

        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) (((i > fadeStart) ? --amplitude : amplitude) * Math.sin(pi2 * f * i));
        }

        return output;
    }

    static byte[] createBeep(int frequency, int amplitude, int sampleRate, int bitsPerSample) {
        return sineWave(frequency, amplitude, sampleRate, (sampleRate * bitsPerSample) / 1024);
    }

}
