package org.jared.twentytwo;

import io.vavr.Lazy;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.Map;

import org.jared.util.Conversions;
import org.jared.util.ScanIterator;

import java.util.Scanner;
import java.util.stream.Collectors;

import static org.jared.util.Conversions.*;

public class SpreadOut {
    public enum DirCandidate {
        N(new int[][]{new int[]{-1, -1}, new int[]{0, -1}, new int[]{1, -1}}),
        S(new int[][]{new int[]{-1, 1}, new int[]{0, 1}, new int[]{1, 1}}),
        W(new int[][]{new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1}}),
        E(new int[][]{new int[]{1, -1}, new int[]{1, 0}, new int[]{1, 1}});

        int[][] deltas;

        static final Lazy<DirCandidate[]> values = Lazy.of(DirCandidate::values);

        DirCandidate(int[][] deltas) {
            this.deltas = deltas;
        }

        static DirCandidate[] getValues() {
            return values.get();
        }
    }

    public static long newCell(boolean[][] grid, long xy, int firstDir) {
        int x = xFromLong(xy);
        int y = yFromLong(xy);

        boolean good = isGood(grid, x, y);

        if (good) {
            return xy;
        }

        DirCandidate[] valArr = DirCandidate.getValues();

        for (int i = 0; i < 4; i++) {
            DirCandidate next = valArr[(i + firstDir) & 3];
            boolean foundIt = true;
            for (int[] delta : next.deltas) {
                if (grid[y + delta[1]][x + delta[0]]) {
                    foundIt = false;
                    break;
                }
            }
            if (foundIt) {
                return longFromXY(x + next.deltas[1][0], y + next.deltas[1][1]);
            }
        }

        return xy;
    }

    private static boolean isGood(boolean[][] grid, int x, int y) {
        boolean good = true;
        outer:
        for (int yd = -1; yd <= 1; yd++) {
            for (int xd = -1; xd <= 1; xd++) {
                if (xd == 0 && yd == 0) continue;
                if (grid[y + yd][x + xd]) {
                    good = false;
                    break outer;
                }
            }
        }
        return good;
    }

    public static char gridToLetter(long xy) {
        return gridToLetter(xFromLong(xy), yFromLong(xy));
    }

    public static char gridToLetter(int xOrig, int yOrig) {
        var x = xOrig - 14;
        var y = yOrig - 14;

        return (char) ((y * 12) + x + 32);
    }

    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(SpreadOut.class.getResourceAsStream("/2022/day23_input.txt")));

        List<String> gridStrings = iter.toList();

        int xSize = gridStrings.head().length();
        int ySize = gridStrings.size();

//        for (int i = 0; i < 1_000_000; i++) {

        final boolean[][] grid = new boolean[ySize * 5][xSize * 5];

        List<Long> coords = List.empty();

        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                boolean b = gridStrings.get(y).charAt(x) == '#';
                grid[ySize * 2 + y][xSize * 2 + x] = b;
                if (b) coords = coords.prepend(longFromXY(x + 2 * xSize, y + 2 * ySize));
            }
        }

        long start = System.nanoTime();

        int zeroInArow = 0;
        int lastRound = 0;

        for (int round = 0; round < 1_000_000; round++) {
            int firstDir = round & 3;

            var targetToSource = findNeighbors(grid, coords, firstDir);

            var sourceToTarget = filterAndInvert(targetToSource);

            if (coords.size() - sourceToTarget.size() == 0) {
                if (lastRound + 1 != round) {
                    zeroInArow = 0;
                    System.out.println("--------");
                }
                zeroInArow++;
                System.out.println(round);
                if (zeroInArow > 4 || round > 1000) {
                    boolean allGood = coords.map(c -> isGood(grid, xFromLong(c), yFromLong(c)))
                            .reduce((b1, b2) -> b1 && b2);
                    if (allGood) {
                        System.out.println("Finally all good!");
                        break;
                    }
                }
                lastRound = round;
            }

            coords.forEach(l -> updateGrid(grid, sourceToTarget, l));

            coords = coords.map(c -> sourceToTarget.getOrDefault(c, c));
        }

        long stop = System.nanoTime();

        System.out.println("Time taken (s): " + ((double) (stop - start))/(1_000_000_000.));
    }

    private static void updateGrid(boolean[][] grid, Map<Long, Long> sourceToTarget, Long l) {
        long target = sourceToTarget.getOrDefault(l, l);
        grid[yFromLong(l)][xFromLong(l)] = false;
        grid[yFromLong(target)][xFromLong(target)] = true;
    }

    private static Map<Long, Long> filterAndInvert(Map<Long, java.util.List<Long>> targetToSource) {
        return targetToSource.entrySet().parallelStream()
                .filter(t -> t.getValue().size() == 1)
                .collect(Collectors.toMap(t -> t.getValue().get(0), Map.Entry::getKey));
    }

    private static Map<Long, java.util.List<Long>> findNeighbors(boolean[][] grid, List<Long> coords, int firstDir) {
        return coords.toJavaStream().collect(Collectors.groupingBy(coord -> newCell(grid, coord, firstDir)));
    }

    private static void computeArea(List<Long> coords) {
        Tuple2<Integer, Integer> diff;
        Tuple2<Integer, Integer> maxs;
        Tuple2<Integer, Integer> mins;
        List<Tuple2<Integer, Integer>> asPairs;
        asPairs = coords.map(Conversions::xyFromLong);
        mins = asPairs.foldLeft(Tuple.of(Integer.MAX_VALUE, Integer.MAX_VALUE),
                (current, next) -> Tuple.of(Math.min(current._1(), next._1()), Math.min(current._2(), next._2())));
        maxs = asPairs.foldLeft(Tuple.of(Integer.MIN_VALUE, Integer.MIN_VALUE),
                (current, next) -> Tuple.of(Math.max(current._1(), next._1()), Math.max(current._2(), next._2())));

        System.out.println(maxs);
        System.out.println(mins);
        diff = Tuple.of(maxs._1() - mins._1() + 1, maxs._2() - mins._2() + 1);
        System.out.println(diff);
        System.out.println(diff._1() * diff._2());
        System.out.println(diff._1() * diff._2() - coords.size());
    }

    public static void printMap(boolean[][] grid) {
        for (int y = 0; y < grid[0].length; y++) {
            for (int x = 0; x < grid.length; x++) {
                if (grid[y][x]) {
                    char toPrint = '#'; // gridToLetter(x, y);
                    System.out.print(toPrint);
                } else System.out.print('.');
            }
            System.out.println();
        }
    }
}
