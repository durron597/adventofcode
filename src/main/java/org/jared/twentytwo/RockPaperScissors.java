package org.jared.twentytwo;

import java.io.*;
import java.util.Scanner;

public class RockPaperScissors {
    public static void main(String... args) throws Exception {
        InputStream input = RockPaperScissors.class.getResourceAsStream("/2022/day2_input.txt");
        Scanner sc = new Scanner(input);

        int total = 0;

        while (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            String[] split = nextLine.split(" ");
            total += score(split[0].charAt(0), split[1].charAt(0));
        }

        System.out.println(total);
    }

    public static int score(char a, char b) {
        int playScore = (a + b + 2) % 3 + 1;
        int winLossScore = ((int) b - 'X') * 3;
        return playScore + winLossScore;
    }
}