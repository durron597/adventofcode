package org.jared.twentytwo;

import java.util.Scanner;
import io.vavr.collection.*;

public class Range {
    public static void main(String... args) throws Throwable {
        Scanner sc = new Scanner(Rucksack.class.getResourceAsStream("/2022/day4_input.txt"));

        int total = 0;

        while(sc.hasNextLine()) {
            String next = sc.nextLine();
            String[] split = next.split("-|,");

            int res = comp(split);

//            System.out.println(res);

            total += res > 0 ? 1 : 0;
//            break;
        }

        System.out.println(total);
    }

    private static int comp(String[] split) {
        List<Integer> map = List.of(split).map(Integer::parseInt);
        return compInt(map.slice(0, 2), map.slice(2, 4));
    }

    private static int compInt(List<Integer> first, List<Integer> second) {
        if (first.get(0) > second.get(0)) return compInt(second, first);
//        System.out.print(first + " " + second + " ");
        if (first.get(0).equals(second.get(0))) {
            int eqDiff = Math.min(first.get(1), second.get(1)) - first.get(0) + 1;
            return eqDiff;
        }

        int diff = first.get(1) - second.get(0);

        int secondDiff = second.get(1) - second.get(0);
        return Math.min(Math.max(diff, -1), secondDiff) + 1;
    }
}