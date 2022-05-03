package com.jbatista.batatinha.core;

public class Buzzer {

    private static byte[] sineWave(double frequency, double amplitude, double sampleRate, int sampleSize) {
        final byte[] output = new byte[sampleSize];
        final double f = frequency / sampleRate;
        final double fadeStart = sampleSize - amplitude;

        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) (((i > fadeStart) ? --amplitude : amplitude) * Math.sin((2d * Math.PI) * f * i));
        }

        return output;
    }

    public static byte[] createBeep(double frequency, double amplitude, double sampleRate, int bitsPerSample) {
        return sineWave(frequency, amplitude, sampleRate, (int) ((sampleRate * bitsPerSample) / 1024));
    }

}
