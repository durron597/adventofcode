package org.jared.twentytwo;

import java.io.*;
import java.util.Comparator;
import java.util.Scanner;

import io.vavr.collection.*;

public class Calories {
    public static void main(String... args) throws Exception {
        InputStream input = Calories.class.getResourceAsStream("/2022/day1_input.txt");
        Scanner sc = new Scanner(input);

        PriorityQueue<Integer> queue = PriorityQueue.empty(Comparator.<Integer>naturalOrder());

        int total = 0;

        while (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            if (nextLine.isEmpty()) {
                if (queue.size() < 3) {
                    queue = queue.enqueue(total);
                    System.out.println(queue);
                } else {
                    System.out.print(queue.peek());
                    System.out.print(" ");
                    if (queue.peek() < total) {
                        queue = queue.tail();
                        queue = queue.enqueue(total);
                        System.out.println(queue);
                    }
                }
                total = 0;
            } else {
                total += Integer.parseInt(nextLine);
            }
        }

        System.out.println(queue.fold(0, Integer::sum));
    }
}