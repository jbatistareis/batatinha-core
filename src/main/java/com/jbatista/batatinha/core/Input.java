package com.jbatista.batatinha.core;

import java.util.HashSet;
import java.util.Set;

public class Input {

    private boolean pressRegistred;
    private char lastKey;
    private final Set<Character> pressedKeys = new HashSet<>(16);

    public void press(int key) {
        if (pressedKeys.add((char) key)) {
            lastKey = (char) key;
            pressRegistred = true;
        }
    }

    public void release(int key) {
        pressedKeys.remove((char) key);
        pressRegistred = false;
    }

    boolean isPressed(Character key) {
        return pressedKeys.contains(key);
    }

    boolean pressRegistred() {
        return pressRegistred;
    }

    char getLastKey() {
        return lastKey;
    }

}
