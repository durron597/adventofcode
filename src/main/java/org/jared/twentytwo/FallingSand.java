package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.Arrays;
import java.util.Scanner;

public class FallingSand implements Runnable {
    public static void main(String... args) {
        new FallingSand().run();
    }

    final char[][] grid = new char[161][700];

    @Override
    public void run() {
        Scanner sc = new Scanner(FallingSand.class.getResourceAsStream("/2022/day14_input.txt"));

        for(var arr : grid) {
            Arrays.fill(arr, '.');
        }

        Arrays.fill(grid[160], '#');

        while(sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            List<Tuple2<Integer, Integer>> listPairs = Arrays.stream(nextLine.split(" -> "))
                    .map(s -> s.split(","))
                    .map(arr -> Tuple.of(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])))
                    .collect(List.collector());
            listPairs.tail()
                     .zip(listPairs)
                    .forEach(this::draw);
        }

        grid[0][500] = '+';

        int i = 0;
        int result = 0;

        while (result >= 0) {
            i++;
            result = placeSand(500, 0);
        }

        System.out.format("result = %d, i = %d%n", result, i);

        System.out.println(i);

        Arrays.stream(grid)
                .forEach(arr -> {
                    List.ofAll(arr).forEach(System.out::print);
                    System.out.println();
                });
    }

    private int placeSand(int x, int y) {
        if (y + 1 == grid.length) {
            return -1;
        } else if (grid[y+1][x] == '.') {
            return placeSand(x, y+1);
        } else if(grid[y + 1][x - 1] == '.') {
            return placeSand(x - 1, y + 1);
        } else if (grid[y + 1][x + 1] == '.') {
            return placeSand(x + 1, y + 1);
        } else if (grid[y][x] == '+') {
            return -2;
        } else {
            grid[y][x] = 'o';
            return y;
        }
    }

    private void draw(Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> pathPairs) {
        var left = pathPairs._2();
        var right = pathPairs._1();

        if (left._1().equals(right._1())) {
            drawLine(left._1(), left._2(), right._2(), true);
        } else if (left._2().equals(right._2())) {
            drawLine(left._2(), left._1(), right._1(), false);
        } else {
            throw new RuntimeException("Not straight vertical or horizontal!");
        }
    }

    private void drawLine(int fixed, int left, int right, boolean vertical) {
        if (left > right) {
            drawLine(fixed, right, left, vertical);
            return;
        }

        for(int i = left; i <= right; i++) {
            if (vertical) {
                grid[i][fixed] = '#';
            } else {
                grid[fixed][i] = '#';
            }
        }
    }


}
