package org.jared.twentytwo;

import java.util.Scanner;

import io.vavr.Tuple2;
import io.vavr.collection.*;

public class StackMove {
    public static void main(String... args) throws Throwable {
        Scanner sc = new Scanner(StackMove.class.getResourceAsStream("/2022/day5_input.txt"));

        List<String> crateStrings = List.empty();
        String nextLine = null;

        do {
            nextLine = sc.nextLine();
            if (!Character.isDigit(nextLine.charAt(1))) {
                crateStrings = crateStrings.append(nextLine);
            } else {
                break;
            }
        } while (sc.hasNextLine());

        Map<Integer, List<Character>> stacks = buildStacks(crateStrings);

        sc.nextLine();

        stacks.forEach(System.out::println);
        System.out.println();

        while(sc.hasNextLine()) {
            nextLine = sc.nextLine();
            List<Integer> instructions = List.of(nextLine.split(" "))
            .filter(s -> Character.isDigit(s.charAt(0)))
            .map(Integer::parseInt);

            stacks = processInstructions(stacks, instructions);
//
//        stacks.forEach(System.out::println);
//        System.out.println();

        }

        stacks.map(t -> t._2().head())
            .forEach(System.out::print);
    }

    private static Map<Integer, List<Character>> processInstructions(
            Map<Integer, List<Character>> stacks,
            List<Integer> instructions) {
        int count = instructions.get(0);
        int fromIndex = instructions.get(1) - 1;
        int toIndex = instructions.get(2) - 1;

        return stacks.replaceValue(toIndex, stacks.getOrElse(toIndex, List.empty())
                                .prependAll(stacks.getOrElse(fromIndex, List.empty())
                                                    .take(count)))
            .replaceValue(fromIndex, stacks.getOrElse(fromIndex, List.empty()).drop(count));
    }

    private static Map<Integer, List<Character>> buildStacks(List<String> crateStrings) {
        List<List<List<Character>>> stackMap = crateStrings.map(str -> List.ofAll(str.toCharArray()))
            .map(List::zipWithIndex)
            .map(l -> l.filter(t -> t._2() % 4 == 1)
            .map(Tuple2::_1))
            .map(l -> l.map(c -> c == ' ' ? List.empty() : List.of(c)));

        final int max = stackMap.map(List::length).reduce(Math::max);

        return stackMap.map(l -> l.padTo(max, List.empty()))
            .reduce((x, y) -> x.zipWith(y, List::appendAll))
            .zipWithIndex()
            .toLinkedMap(Tuple2::_2, Tuple2::_1);
    }
}