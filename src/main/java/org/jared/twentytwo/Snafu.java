package org.jared.twentytwo;

import io.vavr.control.Option;
import org.jared.util.ScanIterator;

import java.util.Scanner;

public class Snafu {
    public static void main(String... args) {
        ScanIterator sc = new ScanIterator(new Scanner(Snafu.class.getResourceAsStream("/2022/day25_input.txt")));

        Option<Long> result = sc.map(Snafu::fromString).reduceOption(Long::sum);
        result.forEach(System.out::println);
        result.map(Snafu::toSnafu).forEach(System.out::println);

        /*
        1
        12
        124
        13-
        2=-
        2=-3
        2=0=
        2=0=0
         */

        //                                12430134333413301410
//        System.out.println(fromString("2=0=02-0----2-=02-10"));

        Option<Long> verifySymmetry = result.map(Snafu::toSnafu).map(Snafu::fromString);

        verifySymmetry.forEach(System.out::println);

    }

    public static long fromString(String s) {
        long total = 0;
        for (char c : s.toCharArray()) {
            long val = switch (c) {
                case '0' -> 0;
                case '1' -> 1;
                case '2' -> 2;
                case '-' -> -1;
                case '=' -> -2;
                default -> Integer.MAX_VALUE;
            };

            total *= 5L;
            total += val;
        }

        return total;
    }

    public static String toSnafu(long l) {
        char[] base5 = Long.toString(l, 5).toCharArray();
        boolean again = true;
        while(again) {
            again = false;
            for (int i = 0; i < base5.length; i++) {
                if (base5[i] > '2') {
                    base5[i] -= 5;
                    base5[i - 1]++;
                    if (base5[i - 1] > '2') {
                        again = true;
                    }
                }
            }
        }
        for(int i = 0; i < base5.length; i++) {
            if (base5[i] == '/') base5[i] = '-';
            if (base5[i] == '.') base5[i] = '=';
        }
        return String.valueOf(base5);
    }
}
