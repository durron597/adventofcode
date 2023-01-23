package org.jared.util;

import io.vavr.Lazy;
import lombok.Getter;

public enum Dir {
    R(1, 0),
    D(0, 1),
    L(-1, 0),
    U(0, -1);

    @Getter
    private final int x;

    @Getter
    private final int y;

    Dir(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private static final Lazy<Dir[]> values = Lazy.of(Dir::values);

    public static Dir[] getValues() {
        return values.get();
    }
}
