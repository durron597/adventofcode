package org.jared.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public class Conversions {
    public static long longFromXY(int x, int y) {
        return (((long) x) << 32) + y;
    }

    public static Tuple2<Integer, Integer> xyFromLong(long xy) {
        return Tuple.of(xFromLong(xy), yFromLong(xy));
    }

    public static int xFromLong(long xy) {
        return (int) (xy >>> 32);
    }

    public static int yFromLong(long xy) {
        return (int) (xy & 0xFFFFFFFFL);
    }
}
