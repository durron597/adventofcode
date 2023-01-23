package org.jared.twentytwo;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.Value;
import org.jared.util.Dir;

import java.util.Arrays;
import java.util.Scanner;

public class RopeBridge {
    public static void main(String... args) throws Throwable {
        Scanner sc = new Scanner(RopeBridge.class.getResourceAsStream("/2022/day9_input.txt"));

        Pair[] rope = new Pair[10];
        Arrays.fill(rope, new Pair(0, 0));

        Set<Pair> visited = HashSet.of(rope[9]);


        while(sc.hasNextLine()) {
            String[] next = sc.nextLine().split(" ");
            Dir d = Dir.valueOf(next[0]);
            int count = Integer.parseInt(next[1]);

            for (int i = 0; i < count; i++) {
                rope[0] = new Pair(rope[0].getX() + d.getX(), rope[0].getY() + d.getY());

                for(int j = 1; j < 10; j++) {
                    int tailX = rope[j - 1].getX() - rope[j].getX();
                    int tailY = rope[j - 1].getY() - rope[j].getY();
                    if (Math.abs(tailX) > 1) {
                        if (Math.abs(tailY) > 0) {
                            rope[j] = new Pair(rope[j].getX() + (int) Math.signum(tailX), rope[j].getY() + (int) Math.signum(tailY));
                        } else {
                            rope[j] = new Pair(rope[j].getX() + (int) Math.signum(tailX), rope[j].getY());
                        }
                    } else if (Math.abs(tailY) > 1) {
                        if (Math.abs(tailX) > 0) {
                            rope[j] = new Pair(rope[j].getX() + (int) Math.signum(tailX), rope[j].getY() + (int) Math.signum(tailY));
                        } else {
                            rope[j] = new Pair(rope[j].getX(), rope[j].getY() + (int) Math.signum(tailY));
                        }
                    }
                }
                visited = visited.add(rope[9]);
            }
        }

        System.out.println(visited.size());
    }

    @Value
    public static class Pair {
        int x;
        int y;
    }
}