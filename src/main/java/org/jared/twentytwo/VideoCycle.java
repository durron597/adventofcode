package org.jared.twentytwo;

import java.util.Scanner;

public class VideoCycle {
    public static void main(String... args) throws Throwable {
        Scanner sc = new Scanner(VideoCycle.class.getResourceAsStream("/2022/day10_input.txt"));

        int cycle = 1;
        int addInst = 0;
        int register = 1;
        int total = 0;

        boolean wait = false;

        while(wait || sc.hasNextLine()) {
            if (wait) {
//                System.out.format("cycle: %d, register: %d%n", cycle, register);
                printPixel(cycle, register);

                // cycle is complete
                cycle++;
                register += addInst;
                wait = false;
            } else {
                String[] next = sc.nextLine().split(" ");
                if ("addx".equals(next[0])) {
                    addInst = Integer.parseInt(next[1]);
                    wait = true;
                }

//                System.out.format("cycle: %d, register: %d%n", cycle, register);
                printPixel(cycle, register);

                // cycle is complete

                cycle++;
            }
        }


        System.out.format("cycle: %d, register: %d, total:%d%n", cycle, register, total);

    }

    private static void printPixel(int cycle, int register) {
        if (Math.abs(register - ((cycle - 1) % 40)) < 2) {
            System.out.print("#");
        } else {
            System.out.print(".");
        }
        if (cycle % 40 == 0) {
            System.out.println();
        }
    }
}