package com.jbatista.batatinha.core;

import java.util.HashSet;
import java.util.Set;

public class Input {

    private boolean pressRegistred;
    private char lastKey;
    private final Set<Character> pressedKeys = new HashSet<>(16);

    public void press(Key key) {
        if (pressedKeys.add(key.getCode())) {
            lastKey = key.getCode();
            pressRegistred = true;
        }
    }

    public void release(Key key) {
        pressedKeys.remove(key.getCode());
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
