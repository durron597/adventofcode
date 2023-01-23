package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.jared.util.ScanIterator;
import org.jared.util.Dir;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class MonkeyMap {
    @Value
    @Builder(toBuilder = true)
    public static class State {
        Dir direction;

        int x;

        int y;
    }



    public static void main(String... args) {
        ScanIterator sc = new ScanIterator(new Scanner(MonkeyMap.class.getResourceAsStream("/2022/day22_input.txt")));

        ArrayList<String> mapList = new ArrayList<>();

        while(true) {
            String next = sc.next();
            if (next.length() == 0) break;
            mapList.add(next);
        }

        int yMax = mapList.size();
        int xMax = mapList.stream()
                       .mapToInt(String::length)
                       .max()
                       .orElseThrow();

        long[][] grid = new long[yMax + 2][xMax + 2];

        boolean foundStart = false;
        int startX = -1;
        int startY = -1;

        for(int y = 1; y < grid.length - 1; y++) {
            for(int x = 1; x < grid[0].length; x++) {
                String row = mapList.get(y - 1);
                if (!foundStart && row.charAt(x - 1) != ' ') {
                    foundStart = true;
                    startX = x;
                    startY = y;
                }
                if (x - 1 < row.length()) {
                    grid[y][x] = row.charAt(x - 1) == ' ' ? 0 : row.charAt(x - 1);
                }
            }
        }

        int a;
        // 1 -> 16
        int b = 0;
        Target target = new Target(Dir.R, 1, 151);
        for(a = 51; a <= 100; a++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setY(a + 100);
            grid[b][a] += targetToLong(target, Dir.U, true);
        }

        // 2 -> 26
        target = new Target(Dir.U, 1,200);
        for(a = 101; a <= 150; a++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setX(a - 100);
            grid[b][a] += targetToLong(target, Dir.U, true);
        }
        // 2 -> 24
        a = 151;
        target = new Target(Dir.L, 100, 150);
        for(b = 1; b <= 50; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            // reverse order
            target.setY(151 - b);
            grid[b][a] += targetToLong(target, Dir.R, true);
        }
        // 2 -> 23
        b = 51;
        target = new Target(Dir.L, 100, 51);
        for(a = 101; a <= 150; a++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setY(a - 50);
            grid[b][a] += targetToLong(target, Dir.D, true);
        }
        // 3 -> 23
        a = 101;
        target = new Target(Dir.U, 101, 50);
        for(b = 51; b <= 100; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setX(b + 50);
            grid[b][a] += targetToLong(target, Dir.R, false);
        }
        // 4 -> 24
        a = 101;
        target = new Target(Dir.L, 150, 50);
        for(b = 101; b <= 150; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setY(151 - b);
            grid[b][a] += targetToLong(target, Dir.R, false);
        }
        // 4 -> 46
        b = 151;
        target = new Target(Dir.L, 50, 151);
        for(a = 51; a <= 100; a++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setY(a + 100);
            grid[b][a] += targetToLong(target, Dir.D, true);
        }
        // 6 -> 46
        a = 51;
        target = new Target(Dir.U, 51, 150);
        for(b = 151; b <= 200; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setX(b - 100);
            grid[b][a] += targetToLong(target, Dir.R, false);
        }
        // 6 -> 26
        b = 201;
        target = new Target(Dir.D, 101, 1);
        for(a = 1; a <= 50; a++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setX(a + 100);
            grid[b][a] += targetToLong(target, Dir.D, false);
        }
        // 6 -> 16
        a = 0;
        target = new Target(Dir.D, 51, 1);
        for(b = 151; b <= 200; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setX(b - 100);
            grid[b][a] += targetToLong(target, Dir.L, false);
        }
        // 5 -> 15
        a = 0;
        target = new Target(Dir.R, 51, 50);
        for(b = 101; b <= 150; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setY(151 - b);
            grid[b][a] += targetToLong(target, Dir.L, false);
        }
        // 5 -> 35
        b = 100;
        target = new Target(Dir.R, 51, 51);
        for(a = 1; a <= 50; a++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setY(a + 50);
            grid[b][a] += targetToLong(target, Dir.U, false);
        }
        // 3 -> 35
        a = 50;
        target = new Target(Dir.D, 1, 101);
        for(b = 51; b <= 100; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setX(b - 50);
            grid[b][a] += targetToLong(target, Dir.L, true);
        }
        // 1 -> 15
        a = 50;
        target = new Target(Dir.R, 1, 101);
        for(b = 1; b <= 50; b++) {
            if((grid[b][a]) == '.') throw new RuntimeException();
            target.setY(151 - b);
            grid[b][a] += targetToLong(target, Dir.L, true);
        }

        State current = State.builder()
                .direction(Dir.R)
                .x(startX)
                .y(startY)
                .build();

        List<Tuple2<Integer, Option<Character>>> instructionList = toInstructions(sc.next());

        long stepCounter = 0;

        for(var t : instructionList) {
            int currentX = current.getX();
            int currentY = current.getY();
            Dir currentDirection = current.getDirection();

            for(int i = 0; i < t._1(); i++) {
                int nextX = currentX + currentDirection.getX();
                int nextY = currentY + currentDirection.getY();
                long newTile = grid[nextY][nextX];
                Target destination = null;
                if ((newTile & 255) == 0) {
                    destination = longToTarget(newTile, current.getDirection());
                    System.out.format("Jumping from current x:%d,y:%d,%s to %s%n",
                            nextX, nextY, currentDirection, destination);
                    nextY = destination.getY();
                    nextX = destination.getX();
                    newTile = grid[nextY][nextX];
                    if ((newTile & 255) == 0) throw new RuntimeException();
                }
                if ((newTile & 255) == '#') {
                    break;
                }
                currentX = nextX;
                currentY = nextY;
                if (destination != null) {
                    currentDirection = destination.getDir();
                }
                stepCounter++;
                if (grid[currentY][currentX] != '.' && !Character.isAlphabetic((char) grid[currentY][currentX])) throw new RuntimeException();
                grid[currentY][currentX] = ((stepCounter % 26) + 'A');
            }

            final Dir oldDirection = currentDirection;
            Dir newDirection = t._2().map(chr -> rotate(oldDirection, chr)).getOrElse(oldDirection);

            current = State.builder()
                    .x(currentX)
                    .y(currentY)
                    .direction(newDirection)
                    .build();
        }

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                long c = grid[y][x];
                System.out.print((char) (c & 255));
            }
            System.out.println();
        }


        System.out.println(current);

        int ans = (current.getY() * 1000) + (current.getX() * 4) + current.getDirection().ordinal();

        System.out.println("Final password: " + ans);
    }

    private static Dir rotate(Dir direction, Character character) {
        if (character == 'L') {
            return switch (direction) {
                case D -> Dir.R;
                case U -> Dir.L;
                case L -> Dir.D;
                default -> Dir.U;
            };
        }
        return switch (direction) {
            case U -> Dir.R;
            case D -> Dir.L;
            case R -> Dir.D;
            default -> Dir.U;
        };
    }

    @Data
    @AllArgsConstructor
    static class Target {
        Dir dir;

        int x;

        int y;
    }

    private static Target longToTarget(long value, Dir source) {
        long candidate = (value >> 8) & (0xFFFFF);
        if (candidate == 0 || source.ordinal() != (candidate & 3)) {
            candidate = (value >> 28) & (0xFFFFF);
        }
        if (candidate == 0) throw new RuntimeException();
        return new Target(Dir.values()[((int) (candidate >> 2) & 3)],
                (int) ((candidate >> 4) & 0xFF),
                (int) ((candidate >> 12) & 0xFF));
    }

    private static long targetToLong(Target t, Dir source, boolean left) {
        long acc = source.ordinal();
        acc += (t.getDir().ordinal() << 2);
        acc += (((long) t.getX()) << 4);
        acc += (((long) t.getY()) << 12);

        if (left) {
            return acc << 8;
        } else {
            return acc << 28;
        }
    }

    private static List<Tuple2<Integer, Option<Character>>> toInstructions(String next) {
        int pointer = 0;

        LinkedList<Tuple2<Integer, Option<Character>>> holding = new LinkedList<>();

        while(pointer < next.length()) {
            int i = pointer;
            for(; i < next.length() && Character.isDigit(next.charAt(i)); i++);
            int parsed = Integer.parseInt(next.substring(pointer, i));
            if (i == next.length()) {
                holding.add(Tuple.of(parsed, Option.none()));
                break;
            }
            holding.add(Tuple.of(parsed, Option.some(next.charAt(i))));
            pointer = i + 1;
        }

        return List.ofAll(holding);
    }
}
