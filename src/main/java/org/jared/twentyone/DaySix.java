package org.jared.twentyone;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

public class DaySix {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DaySix.class.getResourceAsStream("/2021/day6_input.txt")));

        Map<Long, Integer> fishToCount =
                List.of(iter.next().split(","))
                        .map(Long::parseLong)
                        .groupBy(Function.identity())
                        .mapValues(List::size);

        long[] counts = new long[9];

        for(int i = 0; i < 9; i++) {
            final int j = i;
            long[] finalCounts = counts;
            fishToCount.get((long) i).peek(count -> finalCounts[j] = count);
        }

        for(long j = 0; j < 256; j++) {
            long[] newCount = new long[9];

            System.arraycopy(counts, 1, newCount, 0, 8);
            newCount[8] = counts[0];
            newCount[6] += counts[0];

            counts = newCount;
        }

        System.out.println(Arrays.stream(counts).sum());
    }
}
