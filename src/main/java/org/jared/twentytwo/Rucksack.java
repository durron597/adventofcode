package org.jared.twentytwo;

import java.util.Scanner;
import io.vavr.collection.*;

public class Rucksack {
    public static void main(String... args) throws Throwable {
        Scanner sc = new Scanner(Rucksack.class.getResourceAsStream("/2022/day3_input.txt"));

        int total = 0;
        int counter = 0;
        String[] arr = new String[3];

        while(sc.hasNextLine()) {
            String next = sc.nextLine();
            arr[counter] = next;

            counter++;

            if (counter == 3) {
                counter = 0;

                char common = Iterator.ofAll(arr[0].toCharArray()).toSet()
                .intersect(Iterator.ofAll(arr[1].toCharArray()).toSet())
                .intersect(Iterator.ofAll(arr[2].toCharArray()).toSet())
                .head();

                int priority = common > 'Z' ? common - 'a' + 1 : common - 'A' + 27;

                total += priority;
            }
        }

        System.out.println(total);
    }
}