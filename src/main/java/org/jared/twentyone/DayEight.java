package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.jared.util.ScanIterator;

import java.util.*;
import java.util.stream.Collectors;

public class DayEight {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayEight.class.getResourceAsStream("/2021/day8_input.txt")));

        iter.map(s -> s.split(" \\| "))
                .toList()
                .map(arr -> Tuple.of(arr[0], arr[1]))
                .map(t -> t.map1(DayEight::makeKey).map2(DayEight::outputToInt))
                .map(DayEight::keyOutputToValue)
                .reduceOption(Integer::sum)
                .forEach(System.out::println);
    }

    private static int keyOutputToValue(Tuple2<Map<Integer, Integer>,int[]> mapTuple2) {
        int total = 0;
        for(int i = 0; i < 4; i++) {
            total *= 10;
            total += mapTuple2._1().get(mapTuple2._2()[i]);
        }
        return total;
    }

    public static int[] outputToInt(String out) {
        return Arrays.stream(out.split(" "))
                .mapToInt(s -> strToInt(s)._1())
                .toArray();
    }

    public static Map<Integer, Integer> makeKey(String keySide) {
        List<String> split = List.of(keySide.split(" "));
        var result = split.map(DayEight::strToInt)
                .groupBy(Tuple2::_2)
                .mapValues(l -> l.map(Tuple2::_1));

        Map<Integer, Integer> key = new HashMap<>();
        key.put(1, result.get(2).get().head());
        key.put(7, result.get(3).get().head());
        key.put(4, result.get(4).get().head());
        key.put(8, result.get(7).get().head());
        key.put(9, result.get(6).flatMap(l -> l.find(i -> andKey(i, key.get(4)))).get());
        key.put(0, result.get(6).flatMap(l -> l.find(i -> !andKey(i, key.get(4)) && andKey(i, key.get(1)))).get());
        key.put(6, result.get(6).flatMap(l -> l.find(i -> !andKey(i, key.get(4)) && !andKey(i, key.get(1)))).get());
        key.put(3, result.get(5).flatMap(l -> l.find(i -> andKey(i, key.get(1)))).get());
        key.put(5, key.get(6) - (key.get(8) - key.get(9)));
        key.put(2, result.get(5).flatMap(l -> l.find(i -> !Objects.equals(i, key.get(5)) && !Objects.equals(i, key.get(3)))).get());

        return key.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private static boolean andKey(Integer i, int keyResult) {
        return (i & keyResult) == keyResult;
    }

    public static Tuple2<Integer, Integer> strToInt(String s) {
        int result = 0;
        for(int i = 0; i < s.length(); i++) {
            int val = s.charAt(i) - 'a';
            result += (1 << val);
        }
        return Tuple.of(result, s.length());
    }
}
