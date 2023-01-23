package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.Scanner;

public class Jets {

    public static final int BUFFER_SIZE = 64;

    @Value
    @Builder(toBuilder = true)
    public static class Rock {
        Tuple2<Integer, Integer> bottomLeft;

        List<Tuple2<Integer, Integer>> pieces;
    }

    @Value
    public static class State {
        long diff;
        long oldArchive;

        long i;

        int dupeCount;
    }

    public static List<List<Tuple2<Integer, Integer>>> pieceDefinitions =
            List.of(
                    List.of(Tuple.of(0,0),
                            Tuple.of(1,0),
                            Tuple.of(2,0),
                            Tuple.of(3,0)),
                    List.of(Tuple.of(1,0),
                            Tuple.of(0,1),
                            Tuple.of(1,1),
                            Tuple.of(2,1),
                            Tuple.of(1,2)),
                    List.of(Tuple.of(0,0),
                            Tuple.of(1,0),
                            Tuple.of(2,0),
                            Tuple.of(2,1),
                            Tuple.of(2,2)),
                    List.of(Tuple.of(0,0),
                            Tuple.of(0,1),
                            Tuple.of(0,2),
                            Tuple.of(0,3)),
                    List.of(Tuple.of(0,0),
                            Tuple.of(0,1),
                            Tuple.of(1,0),
                            Tuple.of(1,1)));

    public static void main(String... args) {
        char[][] grid = buildGrid();
        int[] maximums = new int[9];
        int jet = 0;
        int nextPiece = 0;
        long archived = 0;
        boolean skipOnce = false;
        Map<Tuple2<Integer, Integer>, State> progress = HashMap.empty();

        String directions = getDirections();

        for(long i = 0; i < 1_000_000_000_000L; i++) {
            Rock r = Rock.builder()
                    .bottomLeft(Tuple.of(3, max(maximums) + 4))
                    .pieces(pieceDefinitions.get(nextPiece))
                    .build();
            while (true) {
                int d = directions.charAt(jet) - '=';
                r = tryMoveSideways(grid, r, d);
                Rock downR = tryMoveDownwards(grid, r);
                jet = (jet + 1) % directions.length();
                if (r.equals(downR)) {
                    placeRock(grid, r, maximums);
                    break;
                } else {
                    r = downR;
                }
            }
            nextPiece = (nextPiece + 1) % 5;

            long min = Math.max(min(maximums) - 1, 0);
            grid = buildGrid(grid, (int) min);
            for(int q = 1; q < 8; q++) {
                maximums[q] -= min;
            }
            archived += min;

            long top = ((long) max(maximums)) + archived;

            Tuple2<Integer, Integer> mapKey = Tuple.of(jet, nextPiece);
            State s = progress.get(mapKey)
                    .getOrElse(new State(0, top, i, 1));

            long newDiff = top - s.getOldArchive();
            if (s.getDupeCount() > 2 && !skipOnce && newDiff > 0 &&
                    progress.containsKey(mapKey) && newDiff == s.getDiff()) {
                skipOnce = true;
                long iDiff = i - s.getI();
                long mult = ((1_000_000_000_000L - i) / iDiff) - 1L;
                archived += newDiff * mult;
                i += iDiff * mult;
            }

            progress = progress.put(mapKey, new State(newDiff, top, i, s.getDupeCount() + 1));
        }

        renderGrid(grid);

        long fullMax = ((long) max(maximums)) + archived;

        System.out.println(fullMax);
    }

    private static String getDirections() {
        Scanner sc = new Scanner(Jets.class.getResourceAsStream("/2022/day17_input.txt"));

        String line = sc.nextLine();

        sc.close();

        return line;
    }

    private static int min(int[] maximums) {
        int min = Integer.MAX_VALUE;
        for(int i = 1; i < maximums.length - 1; i++) {
            min = Math.min(maximums[i], min);
        }
        return min;
    }

    private static int max(int[] maximums) {
        int max = -1;
        for(int i = 1; i < maximums.length - 1; i++) {
            max = Math.max(maximums[i], max);
        }
        return max;
    }

    private static char[][] buildGrid(char[][] original, int min) {
        char[][] grid = new char[BUFFER_SIZE][9];
        for(int y = 0; y < grid.length; y++) {
            if (y + min < BUFFER_SIZE) {
                grid[y] = original[y + min];
            } else {
                grid[y][0] = '|';
                grid[y][8] = '|';
            }
        }
        return grid;
    }

    private static char[][] buildGrid() {
        char[][] grid = new char[BUFFER_SIZE][9];
        Arrays.fill(grid[0], '-');
        for(int y = 1; y < grid.length; y++) {
            grid[y][0] = '|';
            grid[y][8] = '|';
        }
        return grid;
    }

    public static void renderGrid(char[][] grid) {
        renderGrid(grid, Option.none());
    }
    public static void renderGrid(char[][] grid, Option<Rock> rOpt) {
        for(int y = grid.length - 1; y >= 0; y--) {
            for(int x = 0; x < grid[0].length; x++) {
                final int lambdaX = x;
                final int lambdaY = y;
                Option<Character> optOverride = rOpt.flatMap(r -> r.getPieces()
                                .filter(t -> r.getBottomLeft()._1() + t._1() == lambdaX &&
                                        r.getBottomLeft()._2() + t._2() == lambdaY)
                                .headOption())
                        .map(t -> '@');
                System.out.print(optOverride.getOrElse(grid[y][x] == 0 ? '.' : grid[y][x]));
            }
            System.out.println();
        }
    }

    public static void placeRock(char[][] grid, Rock r, int[] maximums) {
        for (var piece : r.pieces) {
            int x = r.bottomLeft._1() + piece._1();
            int y = r.bottomLeft._2() + piece._2();
            grid[y][x] = '#';
            maximums[x] = Math.max(maximums[x], y);
        }
    }

    public static Rock tryMoveSideways(char[][] grid, Rock r, int d) {
        if (Math.abs(d) != 1) throw new RuntimeException();

        for (var piece : r.pieces) {
            if (grid[r.bottomLeft._2() + piece._2()][r.bottomLeft._1() + piece._1() + d] != 0) {
                return r;
            }
        }

        return r.toBuilder().bottomLeft(r.bottomLeft.map1(x -> x + d)).build();
    }

    public static Rock tryMoveDownwards(char[][] grid, Rock r) {
        for (var piece : r.pieces) {
            if (grid[r.bottomLeft._2() + piece._2() - 1][r.bottomLeft._1() + piece._1()] != 0) {
                return r;
            }
        }

        return r.toBuilder().bottomLeft(r.bottomLeft.map2(y -> y - 1)).build();
    }
}
