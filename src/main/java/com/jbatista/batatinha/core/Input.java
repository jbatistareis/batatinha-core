package com.jbatista.batatinha.core;

import java.util.HashSet;
import java.util.Set;

public class Input {

    private static boolean pressRegistred;
    private static char lastKey;
    private static final Set<Character> pressedKeys = new HashSet<>(16);

    public static void toggleKey(int key) {
        if (!pressedKeys.contains((char) key)) {
            pressedKeys.add((char) key);
            pressRegistred = true;
            lastKey = (char) key;
        } else {
            pressedKeys.remove((char) key);
        }
    }

    public static void press(int key) {
        if (!pressedKeys.contains((char) key)) {
            pressedKeys.add((char) key);
            pressRegistred = true;
            lastKey = (char) key;
        }
    }

    public static void release(int key) {
        if (pressedKeys.contains((char) key)) {
            pressedKeys.remove((char) key);
        }
    }

    public static boolean isPressed(Character key) {
        return pressedKeys.contains(key);
    }

    public static boolean pressRegistred() {
        return pressRegistred;
    }

    public static char getLastKey() {
        return lastKey;
    }

    public static void resetPressResgister() {
        pressRegistred = false;
    }

}
