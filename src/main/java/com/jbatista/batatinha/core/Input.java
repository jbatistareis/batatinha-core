package com.jbatista.batatinha.core;

import java.util.HashSet;
import java.util.Set;

public class Input {

    private static boolean pressRegistred;
    private static char lastKey;
    private static final Set<Character> pressedKeys = new HashSet<>(16);

    public static void press(int key) {
        if (pressedKeys.add((char) key)) {
            pressRegistred = true;
            lastKey = (char) key;
        }
    }

    public static void release(int key) {
        pressedKeys.remove((char) key);
    }

    static boolean isPressed(Character key) {
        return pressedKeys.contains(key);
    }

    static boolean pressRegistred() {
        return pressRegistred;
    }

    static char getLastKey() {
        return lastKey;
    }

    static void resetPressResgister() {
        pressRegistred = false;
    }

}
