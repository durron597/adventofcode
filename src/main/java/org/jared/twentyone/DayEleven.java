package org.jared.twentyone;

import org.jared.util.Diag;
import org.jared.util.Dir;
import org.jared.util.ScanIterator;

import java.util.*;


public class DayEleven {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayEleven.class.getResourceAsStream("/2021/day11_input.txt")));

        var asString = iter.toList();

        int[][] grid = new int[asString.size() + 2][asString.head().length() + 2];

        int maxTotal = -1;
        int maxTurn = -1;

        for(int y = 1; y < grid.length - 1; y++) {
            String next = asString.get(y - 1);
            for (int x = 0; x < grid[0].length; x++) {
                if (x == 0 || x == grid[0].length - 1) grid[y][x] = -1;
                else grid[y][x] = next.charAt(x - 1) - '0';
            }
        }

        Arrays.fill(grid[0], -1);
        Arrays.fill(grid[grid.length - 1], -1);

        for(int i = 0; i < 20000; i++) {
            int total = 0;

            for (int y = 1; y < grid.length - 1; y++) {
                for (int x = 1; x < grid[0].length - 1; x++) {
                    grid[y][x]++;
                }
            }

            for (int y = 1; y < grid.length - 1; y++) {
                for (int x = 1; x < grid[0].length - 1; x++) {
                    if (grid[y][x] == 10) {
                        total += new DfsRunner().dfs(grid, x, y);
                    }
                }
            }

            if (total > maxTotal) {
                System.out.format("new total %d on turn %d%n", total, i);
                maxTotal = total;
                if (total == 100) break;
            }

            for (int y = 1; y < grid.length - 1; y++) {
                for (int x = 1; x < grid[0].length - 1; x++) {
                    if (grid[y][x] > 9) {
                        grid[y][x] = 0;
                    }
                }
            }
        }

        for(int y = 1; y < grid.length - 1; y++) {
            for (int x = 1; x < grid[0].length - 1; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
        System.out.println();

//        System.out.println(total);
    }

    public static class DfsRunner {
        int size = 0;
        private int dfs(int[][] grid, int x, int y) {
            if (grid[y][x] == -1) return size;
            if (grid[y][x] == 11) return size;

            grid[y][x]++;

            if (grid[y][x] == 10) {
                grid[y][x]++;
            }

            if (grid[y][x] == 11) {
                size++;

                for (Diag d : Diag.getValues()) {
                    dfs(grid, x + d.getX(), y + d.getY());
                }
            }

            return size;
        }
    }
}
