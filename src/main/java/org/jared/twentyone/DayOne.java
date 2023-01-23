package org.jared.twentyone;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.jared.util.ScanIterator;

import java.util.Scanner;

public class DayOne {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayOne.class.getResourceAsStream("/2021/day1_input.txt")));

        var asList = iter
                .map(Integer::parseInt)
                .sliding(3,1)
                .toList()
                .flatMap(it -> it.size() == 3 ? Option.some(it.reduce(Integer::sum)) : Option.none());
        int result = asList.tail().zip(asList).map(t -> t._1() - t._2())
                .foldLeft(0, (acc, n) -> acc + (n > 0 ? 1 : 0));

        System.out.println(result);
    }
}
