package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.jared.util.Dir;
import org.jared.util.ScanIterator;

import java.util.*;
import java.util.stream.Collectors;


public class DayNine {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayNine.class.getResourceAsStream("/2021/day9_input.txt")));

        var asString = iter.toList();

        int[][] grid = new int[asString.size() + 2][asString.head().length() + 2];
        int[][] basinId = new int[asString.size() + 2][asString.head().length() + 2];

        for(int y = 1; y < grid.length - 1; y++) {
            String next = asString.get(y - 1);
            for (int x = 1; x < grid[0].length - 1; x++) {
                grid[y][x] = next.charAt(x - 1) - '9';
            }
        }

        int id = 1;

        Map<Integer, Integer> basinScores = new HashMap<>();

        for(int y = 0; y < grid.length; y++) {
            for (int x = 1; x < grid[0].length; x++) {
                if (grid[y][x] == 0) {
                    basinId[y][x] = -1;
                } else if (basinId[y][x] == 0) {
                    int size = new DfsRunner(id).dfs(grid, basinId, x, y);
                    basinScores.put(id, size);
                    id++;
                }
            }
        }

        int product = basinScores.entrySet()
                .stream()
                .sorted(Comparator.<Map.Entry<Integer, Integer>>comparingInt(Map.Entry::getValue).reversed())
                .limit(3)
                .mapToInt(Map.Entry::getValue)
                .reduce(1, (acc, e) -> acc * e);

        System.out.println(product);
    }

    public static class DfsRunner {
        int size = 0;
        final int id;

        public DfsRunner(int id) {
            this.id = id;
        }
        private int dfs(int[][] grid, int[][] basinId, int x, int y) {
            if (basinId[y][x] != 0) return size;
            if (grid[y][x] == 0) {
                basinId[y][x] = -1;
                return size;
            }

            size++;
            basinId[y][x] = id;

            for(Dir d : Dir.getValues()) {
                dfs(grid, basinId, x + d.getX(), y + d.getY());
            }

            return size;
        }
    }
}
