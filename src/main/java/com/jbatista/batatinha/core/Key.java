package com.jbatista.batatinha.core;

public enum Key {
    KEY_0(0), KEY_1(1), KEY_2(2), KEY_3(3), KEY_4(4), KEY_5(5), KEY_6(6), KEY_7(7), KEY_8(8), KEY_9(9), KEY_A(10), KEY_B(11), KEY_C(12), KEY_D(13), KEY_E(14), KEY_F(15);

    private char code;

    Key(int code) {
        this.code = (char) code;
    }

    public char getCode() {
        return this.code;
    }
}
