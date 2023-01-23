package org.jared.twentytwo;

import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.io.*;
import java.util.Scanner;

public class Communicator {
    public static void main(String... args) throws Exception {
        InputStream input = Communicator.class.getResourceAsStream("/2022/day6_input.txt");
        Scanner sc = new Scanner(input);

        String line = sc.nextLine();

        Tuple2<List<Character>, Integer> head = List.ofAll(line.toCharArray())
            .sliding(14)
            .zipWithIndex()
            .filter(t -> t._1().toSet().size() == 14)
            .head();

        System.out.println(head._2() + 14);
    }

    public static int score(char a, char b) {
        int playScore = (a + b + 2) % 3 + 1;
        int winLossScore = ((int) b - 'X') * 3;
        return playScore + winLossScore;
    }
}