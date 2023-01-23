package org.jared.twentyone;

import io.vavr.collection.List;
import io.vavr.collection.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import org.jared.util.Diag;
import org.jared.util.Dir;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import static org.jared.util.Conversions.*;


public class DayFifteen {
    final int[][] grid;

    final int[] bestByDistance;
    public DayFifteen(int[][] grid) {
        this.grid = grid;
        bestByDistance = new int[grid.length + grid[0].length];
        Arrays.fill(bestByDistance, 1_000_000);
    }

    @ToString
    @EqualsAndHashCode
    private class State implements Comparable<State> {
        @Getter
        List<Long> path;
        @Getter
        int risk;

        public State(List<Long> path) {
            this.path = path;
            this.risk = path.foldLeft(0, (acc, i) -> acc + grid[yFromLong(i)][xFromLong(i)]);
        }

        public State(State previous, long next) {
            this.path = previous.getPath().prepend(next);
            this.risk = previous.getRisk() + grid[yFromLong(next)][xFromLong(next)];
        }

        @Override
        public int compareTo(State o) {
            return Integer.compare(this.getRisk(), o.getRisk());
        }
    }

    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayFifteen.class.getResourceAsStream("/2021/day15_input.txt")));

        var asString = iter.toList();

        int[][] grid = new int[asString.size() * 5 + 2][asString.head().length() * 5 + 2];

        for (int[] row : grid)
            Arrays.fill(row, 10);

        for(int y = 1; y < grid.length - 1; y++) {
            String next = asString.get((y - 1) % asString.size());
            for (int x = 1; x < grid[0].length - 1; x++) {
                int extraRisk = ((y - 1) / asString.size()) + ((x - 1) / asString.head().length());
                grid[y][x] = (next.charAt((x - 1) % asString.head().length()) - '1' + extraRisk) % 9 + 1;
            }
        }

        new DayFifteen(grid).run();
    }

    private void run() {
        LinkedList<State> bfs = new LinkedList<State>();

        long initial = longFromXY(1, 1);

        State[][] best = new State[grid.length][grid[0].length];

        bfs.offer(new State(List.of(initial)));

        while(!bfs.isEmpty()) {
            State next = bfs.poll();
            int x = xFromLong(next.getPath().head());
            int y = yFromLong(next.getPath().head());

            if(best[y][x] == null || best[y][x].compareTo(next) > 0) {
                best[y][x] = next;

                int distance = (grid.length - 1 - y) + (grid[0].length - 1 - x);

                if (next.getRisk() < bestByDistance[distance] + distance) {
                    bestByDistance[distance] = Math.min(next.getRisk(), bestByDistance[distance]);

                    for (Dir d : Dir.getValues()) {
                        if (grid[y + d.getY()][x + d.getX()] == 10) continue;
                        bfs.offer(new State(next, longFromXY(x + d.getX(), y + d.getY())));
                    }
                }
            }
        }

        State bottomCorner = best[best.length - 2][best[0].length - 2];

        Set<Long> longs = bottomCorner.getPath().toSet();

        for(int y = 1; y < best.length - 1; y++) {
            for (int x = 1; x < best[0].length - 1; x++) {
                if (longs.contains(longFromXY(x, y))) {
                    System.out.print(grid[y][x]);
                } else {
//                    System.out.print(grid[y][x]);
                    System.out.print('.');
                }
            }
            System.out.println();
        }

        System.out.println(bottomCorner.getRisk() - grid[1][1]);
    }

}
