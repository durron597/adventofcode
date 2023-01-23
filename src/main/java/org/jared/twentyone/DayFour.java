package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DayFour {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayFour.class.getResourceAsStream("/2021/day4_input.txt")));

        String[] numbersStr = iter.next().split(",");
        int[] numbers = Arrays.stream(numbersStr).mapToInt(Integer::parseInt).toArray();

        var sliding = iter.toList().sliding(6, 6)
                .toList()
                .map(DayFour::boardFromList)
                .zipWithIndex()
                .map(b -> b.append(0));

        for (int i = 0; i < numbers.length; i++) {
            final int j = i;
            sliding = sliding.map(t -> t.map3(state -> t._1()
                    .getOrElse(numbers[j], Function.identity())
                    .apply(state))
                    .map1(board -> board.remove(numbers[j])));
            if (sliding.size() > 1) {
                sliding = sliding.filter(t -> !isWinner(t._3()));
            } else {
                var map = sliding.find(t -> isWinner(t._3()));
                if (map.isDefined()) {
                    var mapped = map.map(t -> t._1().keySet().reduce(Integer::sum) * numbers[j]);
                    System.out.println(numbers[j]);
                    System.out.println(mapped.get());
                    break;
                }
            }
        }
    }

    private static Map<Integer, Function<Integer, Integer>> boardFromList(List<String> list) {
        Map<Integer, Function<Integer, Integer>> result = HashMap.empty();
        for (int i = 0; i < list.size() - 1; i++) {
            int[] row = Arrays.stream(list.get(i + 1).split(" "))
                    .filter(s -> s.length() > 0)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            for (int j = 0; j < row.length; j++) {
                int key = row[j];
                int bit = 1 << (i * 5 + j);
                result = result.put(key, l -> l | bit);
            }
        }

        return result;
    }

    private static boolean isWinner(int state) {
        return ((state & 0x1F) == 0x1F) ||
               ((state & 0x3E0) == 0x3E0) ||
               ((state & 0x7C00) == 0x7C00) ||
               ((state & 0xF8000) == 0xF8000) ||
               ((state & 0x1F00000) == 0x1F00000) ||
                ((state & 0x108421) == 0x108421) ||
                ((state & 0x210842) == 0x210842) ||
                ((state & 0x421084) == 0x421084) ||
                ((state & 0x842108) == 0x842108) ||
                ((state & 0x1084210) == 0x1084210);



    }
}
