package com.jbatista.batatinha.core;

public class Buzzer {

    public static byte[] createBeep(double frequency, double sampleRate, int bitsPerSample) {
        final int sampleSize = (int) ((sampleRate * bitsPerSample) / 1024);
        final byte[] output = new byte[sampleSize];
        final double f = frequency / sampleRate;
        final double fadeStart = sampleSize - (sampleSize * 0.1);
        final double rFreq = 2d * Math.PI * f;

        double amplitude = 1;

        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) (((i > fadeStart) ? --amplitude : amplitude) * Math.sin(rFreq * i));
        }

        return output;
    }

}
