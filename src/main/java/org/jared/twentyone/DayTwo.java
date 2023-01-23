package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Option;
import org.jared.util.ScanIterator;

import java.util.Scanner;

public class DayTwo {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayTwo.class.getResourceAsStream("/2021/day2_input.txt")));

        var state = iter
                .map(s -> s.split(" "))
                .toList()
                .map(arr -> Tuple.of(arr[0].charAt(0), Integer.parseInt(arr[1])))
                        .foldLeft(Tuple.of(0,0,0), DayTwo::accumulate);

        System.out.println(state._2() * state._3());
    }

    private static Tuple3<Integer, Integer, Integer> accumulate(
            Tuple3<Integer, Integer, Integer> acc, Tuple2<Character, Integer> next) {
        return switch (next._1()) {
            case 'd' -> acc.map1(i -> i + next._2());
            case 'u' -> acc.map1(i -> i - next._2());
            default -> acc.map2(i -> i + next._2()).map3(i -> i + acc._1() * next._2());
        };
    }
}
