package org.jared.twentyone;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

public class DayFive {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayFive.class.getResourceAsStream("/2021/day5_input.txt")));

        var numbersStr = iter.map(s -> s.split("( -> |,)")).toList();
        var typeMap = numbersStr.map(arr -> Arrays.stream(arr).mapToInt(Integer::parseInt).toArray());

        int[][] grid = new int[1000][1000];

        typeMap.forEach(t -> updateGrid(grid, t));

        int count = 0;

        for(int y = 0; y < grid.length; y++) {
            for(int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] > 1) count++;
//                System.out.print((char) (grid[y][x] > 0 ? grid[y][x] + '0' : '.'));
            }
//            System.out.println();
        }

        System.out.println(count);
    }

    private static void updateGrid(int[][] grid, int[] line) {
        int xDelta = Integer.compare(line[2], line[0]);
        int yDelta = Integer.compare(line[3], line[1]);

        int x = line[0];
        int y = line[1];

        while (true) {
            grid[y][x]++;
            x += xDelta;
            y += yDelta;
            if (x == line[2] && y == line[3]) {
                grid[y][x]++;
                break;
            }
        }
    }
}
