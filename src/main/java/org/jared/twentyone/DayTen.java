package org.jared.twentyone;


import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.jared.util.MedianFinder;
import org.jared.util.ScanIterator;

import java.util.LinkedList;
import java.util.Scanner;

public class DayTen {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayTen.class.getResourceAsStream("/2021/day10_input.txt")));

        MedianFinder finder = new MedianFinder();

        iter
                .flatMap(DayTen::findIllegal)
                .map(DayTen::scoreStack)
                .forEach(finder::addNum);

        System.out.println((long) finder.findMedian());
    }

    private static long scoreStack(LinkedList<Character> stack) {
        long total = 0;
        while(!stack.isEmpty()) {
            total *= 5L;
            total += (long) flipped.get(stack.pop()).map(DayTen::score).get();
        }

        return total;
    }
    private static int score(char c) {
        return switch (c) {
            case ')' -> 1;
            case ']' -> 2;
            case '}' -> 3;
            case '>' -> 4;
            default -> 0;
        };
    }

    static final Map<Character, Character> pairs =
            HashMap.of(']', '[', '>', '<', ')', '(', '}', '{');
    static final Map<Character, Character> flipped = pairs.toMap(Tuple2::_2, Tuple2::_1);

    private static Option<LinkedList<Character>> findIllegal(String s) {
        LinkedList<Character> stack = new LinkedList<>();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '[', '<', '{', '(' -> stack.push(c);
                default -> {
                    char top = stack.pop();
                    if (top != pairs.get(c).get()) {
                        return Option.none();
                    }
                }
            }
        }
        return Option.some(stack);
    }
}
