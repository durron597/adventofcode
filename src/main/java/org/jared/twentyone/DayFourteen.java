package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.jared.util.ScanIterator;

import java.util.Scanner;
import java.util.function.Function;


public class DayFourteen {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayFourteen.class.getResourceAsStream("/2021/day14_input.txt")));

        String nextStr = iter.next();

        Map<String, Long> pairCount = List.ofAll(nextStr.toCharArray())
                .sliding(2, 1)
                .map(l -> l.foldLeft("", (s, c) -> s + c))
                .groupBy(Function.identity())
                .mapValues(strings -> (long) strings.size());

        iter.next();

        Map<String, Tuple2<String, String>> mapRules = iter.map(s -> s.split(" -> "))
                .toMap(arr -> arr[0], arr -> Tuple.of(
                        "" + arr[0].charAt(0) + arr[1],
                        "" + arr[1] + arr[0].charAt(1))
                );

        for (long i = 0; i < 40; i++) {
            pairCount = pairCount.foldLeft((Map<String, Long>) HashMap.<String, Long>empty(),
                    (acc, next) -> generateNewPairs(acc, next, mapRules));
        }

        var letterCount = pairCount.foldLeft((Map<Character, Long>) HashMap.<Character, Long>empty(),
                DayFourteen::countLetters);

        letterCount = letterCount.put(nextStr.charAt(0), letterCount.get(nextStr.charAt(0)).get() + 1)
                .put(nextStr.charAt(nextStr.length() - 1), letterCount.get(nextStr.charAt(nextStr.length() - 1)).get() + 1);

        System.out.println(pairCount);
        System.out.println(letterCount);

        Option<Tuple2<Character, Long>> left = letterCount.maxBy(Tuple2::_2);
        System.out.println(left);
        Option<Tuple2<Character, Long>> right = letterCount.minBy(Tuple2::_2);
        System.out.println(right);
        System.out.println(left.flatMap(m -> right.map(n -> (m._2() - n._2()) / 2)));
    }

    private static Map<Character, Long> countLetters(Map<Character, Long> acc, Tuple2<String, Long> next) {
        acc = acc.put(next._1().charAt(0), acc.getOrElse(next._1().charAt(0), 0L) + next._2());
        return acc.put(next._1().charAt(1), acc.getOrElse(next._1().charAt(1), 0L) + next._2());
    }

    private static Map<String, Long> generateNewPairs(Map<String, Long> acc,
                                                             Tuple2<String, Long> next,
                                                             Map<String, Tuple2<String, String>> mapRules) {
        Tuple2<String, String> newPairs = mapRules.get(next._1()).get();
        return acc.put(newPairs._1(), acc.getOrElse(newPairs._1(), 0L) + next._2())
                .put(newPairs._2(), acc.getOrElse(newPairs._2(), 0L) + next._2());
    }

}
