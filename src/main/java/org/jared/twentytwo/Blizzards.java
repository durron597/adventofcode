package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import org.jared.util.Dir;
import org.jared.util.ScanIterator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

// start = 0
// goal = 1
// wall = 2
// normal = 3
// ----
// R - 3rd bit
// D - 4th bit
// L - 5th bit
// U - 6th bit

public class Blizzards {
    private static final int START = 0;
    private static final int END = 1;
    private static final int WALL = 2;
    private static final int NORMAL = 3;
    private static final int R = 1 << 2;

    private static final int D = 1 << 3;
    private static final int L = 1 << 4;
    private static final int U = 1 << 5;

    public static void main(String... args) {
        ScanIterator sc = new ScanIterator(new Scanner(Blizzards.class.getResourceAsStream("/2022/day24_input.txt")));

        var inputStr = sc.toList();

        int xBound = inputStr.head().length();
        int yBound = inputStr.size();

        int startX = 1;
        int startY = 0;

        int endX = xBound - 2;
        int endY = yBound - 1;

        int[][] grid = buildGrid(inputStr, xBound, yBound, startX, startY, endX, endY);
        int[][] grid2 = buildGrid(inputStr, xBound, yBound, startX, startY, endX, endY);
        int[][] temp = null;

        long initial = longFromXYT(startX, startY, 0);

        LinkedList<Long> bfs = new LinkedList<>();
        HashSet<Long> cache = new HashSet<>();
        bfs.offer(initial);

        int turncount = -1;
        int minDelta = Integer.MAX_VALUE;

        outer:
        while(true) {
            long current = bfs.poll();

            int x = xFromLong(current);
            int y = yFromLong(current);
            int oldT = turnFromLong(current);
            int newT = oldT + 1;

            if (oldT > turncount) {
                iterate(grid, grid2);
                temp = grid2;
                grid2 = grid;
                grid = temp;
                turncount = oldT;

                printGrid(grid);

                System.out.format("on turn %d, bfs.size() = %d and minDelta = %d%n", turncount, bfs.size(), minDelta);
            }

            minDelta = offerNewValue(endX, endY, grid, bfs, cache, minDelta, newT, x, y);

            for (Dir d : Dir.getValues()) {
                int newX = x + d.getX();
                int newY = y + d.getY();
                if (newY == -1 || newY == grid.length) continue;
                if ((grid[newY][newX] & 3) == END) {
                    break outer;
                }
                minDelta = offerNewValue(endX, endY, grid, bfs, cache, minDelta, newT, newX, newY);
            }
        }

        minDelta = Integer.MAX_VALUE;
        bfs.clear();
        bfs.add(longFromXYT(endX, endY, turncount + 1));

        outer:
        while(true) {
            long current = bfs.poll();

            int x = xFromLong(current);
            int y = yFromLong(current);
            int oldT = turnFromLong(current);
            int newT = oldT + 1;

            if (oldT > turncount) {
                iterate(grid, grid2);
                temp = grid2;
                grid2 = grid;
                grid = temp;
                turncount = oldT;

                printGrid(grid);

                System.out.format("on turn %d, bfs.size() = %d and minDelta = %d%n", turncount, bfs.size(), minDelta);
            }

            minDelta = offerNewValue(startX, startY, grid, bfs, cache, minDelta, newT, x, y);

            for (Dir d : Dir.getValues()) {
                int newX = x + d.getX();
                int newY = y + d.getY();
                if (newY == -1 || newY == grid.length) continue;
                if ((grid[newY][newX] & 3) == START) {
                    break outer;
                }
                minDelta = offerNewValue(startX, startY, grid, bfs, cache, minDelta, newT, newX, newY);
            }
        }

        minDelta = Integer.MAX_VALUE;
        bfs.clear();
        bfs.add(longFromXYT(startX, startY, turncount + 1));

        outer:
        while(true) {
            long current = bfs.poll();

            int x = xFromLong(current);
            int y = yFromLong(current);
            int oldT = turnFromLong(current);
            int newT = oldT + 1;

            if (oldT > turncount) {
                iterate(grid, grid2);
                temp = grid2;
                grid2 = grid;
                grid = temp;
                turncount = oldT;

                printGrid(grid);

                System.out.format("on turn %d, bfs.size() = %d and minDelta = %d%n", turncount, bfs.size(), minDelta);
            }

            minDelta = offerNewValue(endX, endY, grid, bfs, cache, minDelta, newT, x, y);

            for (Dir d : Dir.getValues()) {
                int newX = x + d.getX();
                int newY = y + d.getY();
                if (newY == -1 || newY == grid.length) continue;
                if ((grid[newY][newX] & 3) == END) {
                    turncount = newT;
                    break outer;
                }
                minDelta = offerNewValue(endX, endY, grid, bfs, cache, minDelta, newT, newX, newY);
            }
        }

        System.out.println("Stopping with turncount " + turncount);
    }

    private static int offerNewValue(int endX, int endY, int[][] grid,
                                     LinkedList<Long> bfs, HashSet<Long> cache,
                                     int minDelta, int newT, int newX, int newY) {
        if (grid[newY][newX] == NORMAL || grid[newY][newX] == START) {
            int delta = Math.abs(endX + endY - newX - newY);
            if (delta - 15 <= minDelta) {
                long newLong = longFromXYT(newX, newY, newT);
                if (!cache.contains(newLong))
                    bfs.offer(newLong);
                cache.add(newLong);
            }
            minDelta = Math.min(delta, minDelta);
        }
        return minDelta;
    }

    private static void iterate(int[][] grid, int[][] grid2) {
        for (int y = 1; y < grid.length - 1; y++) {
            for (int x = 1; x < grid[0].length - 1; x++) {
                grid2[y][x] = NORMAL;
            }
        }

        for (int y = 1; y < grid.length - 1; y++) {
            for (int x = 1; x < grid[0].length - 1; x++) {
                final int c = grid[y][x];
                if ((c & R) > 0) {
                    int newX = x + 1;
                    if (newX == grid[0].length - 1) {
                        newX = 1;
                    }
                    grid2[y][newX] |= R;
                }
                if ((c & D) > 0) {
                    int newY = y + 1;
                    if (newY == grid.length - 1) {
                        newY = 1;
                    }
                    grid2[newY][x] |= D;
                }
                if ((c & L) > 0) {
                    int newX = x - 1;
                    if (newX == 0) {
                        newX = grid[0].length - 2;
                    }
                    grid2[y][newX] |= L;
                }
                if ((c & U) > 0) {
                    int newY = y - 1;
                    if (newY == 0) {
                        newY = grid.length - 2;
                    }
                    grid2[newY][x] |= U;
                }
            }
        }
    }

    public static long longFromXYT(int x, int y, int t) {
        return (((long) t) << 32) + (((long) x) << 16) + y;
    }

    public static Tuple3<Integer, Integer, Integer> xytFromLong(long xyt) {
        return Tuple.of(xFromLong(xyt), yFromLong(xyt), turnFromLong(xyt));
    }

    public static int xFromLong(long xyt) {
        return (int) ((xyt >>> 16) & 0xFFFFL);
    }

    public static int yFromLong(long xyt) {
        return (int) (xyt & 0xFFFFL);
    }

    public static int turnFromLong(long xyt) {
        return (int) (xyt >>> 32);
    }

    private static void printGrid(int[][] grid) {
        for (int[] ints : grid) {
            for (int x = 0; x < grid[0].length; x++) {
                System.out.print(fromInt(ints[x]));
            }
            System.out.println();
        }
    }

    private static int[][] buildGrid(List<String> inputStr, int xBound, int yBound, int startX, int startY, int endX, int endY) {
        int[][] grid = new int[yBound][xBound];

        for(int y = 0; y < yBound; y++) {
            String str = inputStr.get(y);
            for (int x = 0; x < xBound; x++) {
                char c = str.charAt(x);
                switch (c) {
                    case '#' -> grid[y][x] = WALL;
                    case '>' -> grid[y][x] = NORMAL + R;
                    case 'v' -> grid[y][x] = NORMAL + D;
                    case '<' -> grid[y][x] = NORMAL + L;
                    case '^' -> grid[y][x] = NORMAL + U;
                    default -> {
                        if (x == startX && y == startY) {
                            grid[y][x] = START;
                        } else if (x == endX && y == endY) {
                            grid[y][x] = END;
                        } else {
                            grid[y][x] = NORMAL;
                        }
                    }
                }
            }
        }

        return grid;
    }

    public static char fromInt(int value) {
        if (value == START) {
            return 'S';
        } else if (value == END) {
            return 'E';
        } else if (value == WALL) {
            return '#';
        } else {
            char toReturn = 0;
            int count = 0;

            if ((value & R) > 0) {
                toReturn = '>';
                count++;
            }
            if ((value & D) > 0) {
                toReturn = 'v';
                count++;
            }
            if ((value & L) > 0) {
                toReturn = '<';
                count++;
            }
            if ((value & U) > 0) {
                toReturn = '^';
                count++;
            }
            if (count > 1) {
                toReturn = (char) (count + '0');
            }
            if (count == 0) {
                toReturn = '.';
            }
            return toReturn;
        }
    }
}
