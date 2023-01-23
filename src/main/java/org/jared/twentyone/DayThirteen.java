package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.jared.util.ScanIterator;

import java.util.Scanner;


public class DayThirteen {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayThirteen.class.getResourceAsStream("/2021/day13_input.txt")));

        Set<Tuple2<Integer, Integer>> coords = iter.takeWhile(s -> s.length() > 0)
                .toSet()
                .map(s -> s.split(","))
                .map(arr -> Tuple.of(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])));

        var foldInstructions = iter.toList().map(s -> s.split("( |=)"))
                .map(arr -> Tuple.of(arr[2].charAt(0), Integer.parseInt(arr[3])));

        for (var nextInstruction : foldInstructions) {
            if (nextInstruction._1() == 'x') {
                Map<Boolean, Set<Tuple2<Integer, Integer>>> grouped =
                        (Map<Boolean, Set<Tuple2<Integer, Integer>>>) coords.groupBy(t -> t._1() > nextInstruction._2());

                coords = grouped.getOrElse(false, HashSet.empty()).addAll(
                    grouped.getOrElse(true, HashSet.empty()).map(t -> t.map1(x -> 2 * nextInstruction._2() - x)));
            } else {
                Map<Boolean, Set<Tuple2<Integer, Integer>>> grouped =
                        (Map<Boolean, Set<Tuple2<Integer, Integer>>>) coords.groupBy(t -> t._2() > nextInstruction._2());

                coords = grouped.getOrElse(false, HashSet.empty()).addAll(
                        grouped.getOrElse(true, HashSet.empty()).map(t -> t.map2(y -> 2 * nextInstruction._2() - y)));
            }
        }

        int xMax = coords.map(Tuple2::_1).max().get();
        int yMax = coords.map(Tuple2::_2).max().get();

        for (int y = 0; y <= yMax; y++) {
            for (int x = 0; x <= xMax; x++) {
                if (coords.contains(Tuple.of(x, y))) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

}
