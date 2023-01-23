package org.jared.util;

import io.vavr.Lazy;
import lombok.Getter;

public enum Diag {
    R(1, 0),
    RD(1, 1),
    D(0, 1),
    DL (-1, 1),
    L(-1, 0),
    LU(-1, -1),
    U(0, -1),
    UR(1, -1);

    @Getter
    private final int x;

    @Getter
    private final int y;

    Diag(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private static final Lazy<Diag[]> values = Lazy.of(Diag::values);

    public static Diag[] getValues() {
        return values.get();
    }
}
