package com.jbatista.batatinha.core;

import java.util.HashSet;
import java.util.Set;

public class Input {

    private boolean pressRegistered;
    private char lastKey;
    private final Set<Character> pressedKeys = new HashSet<>(16);

    public void press(Key key) {
        if (pressedKeys.add(key.getCode())) {
            lastKey = key.getCode();
            pressRegistered = true;
        }
    }

    public void release(Key key) {
        pressedKeys.remove(key.getCode());
        pressRegistered = false;
    }

    boolean isPressed(Character key) {
        return pressedKeys.contains(key);
    }

    boolean pressRegistered() {
        return pressRegistered;
    }

    char getLastKey() {
        return lastKey;
    }

}
