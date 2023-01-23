package org.jared.twentyone;

import org.jared.util.MedianFinder;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.OptionalLong;
import java.util.Scanner;

public class DaySeven {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DaySeven.class.getResourceAsStream("/2021/day7_input.txt")));

        var nums = Arrays.stream(iter.next().split(",")).mapToLong(Long::parseLong).toArray();

        double averageDbl = Math.round(Arrays.stream(nums).average().getAsDouble());
        long average = (long) averageDbl;

        long best = computeFuel(nums, average);

        long diff = 0;
        long current = average;

        while (true) {
            long next;
            if (diff == 0) {
                 next = computeFuel(nums, current - 1);
                 long more = computeFuel(nums, current + 1);
                 diff = next < more ? -1 : 1;
            } else {
                next = computeFuel(nums, current + diff);
            }
            if (next > best) {
                break;
            }
            best = next;
            current = current + diff;
        }

        System.out.println(current);
        System.out.println(best);

        System.out.println("----");

        System.out.println(computeFuel(nums, 487));
        System.out.println(computeFuel(nums, 488));
        System.out.println(computeFuel(nums, 489));
    }

    private static long computeFuel(long[] nums, long difference) {
        return Arrays.stream(nums).map(x -> Math.abs(x - difference))
                .map(x -> (x * (x + 1)) / 2)
                .reduce(Long::sum)
                .getAsLong();
    }
}
