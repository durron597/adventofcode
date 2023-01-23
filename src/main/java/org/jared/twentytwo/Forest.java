package org.jared.twentytwo;

import java.io.InputStream;

public class Forest {
    public static void main(String... args) throws Throwable {
        InputStream is = Forest.class.getResourceAsStream("/2022/day8_input.txt");

        int dp[][][][] = new int[100][100][10][4];
        int map[][] = new int[100][100];

        int x = 0;
        int y = 0;
        int next;
        int xMax = -1;
        int yMax = -1;

        while((next = is.read()) != -1) {
            if(next == '\r') {
                is.read();
                x = 0;
                y++;
            } else {
                map[x][y] = next;
                for(int i = 0; i < 10; i++) {
                    dp[x][y][i][0] = x > 0 ? (map[x - 1][y] >= i + '0' ? 1 : dp[x - 1][y][i][0] + 1) : 0;
                    dp[x][y][i][1] = y > 0 ? (map[x][y - 1] >= i + '0' ? 1 : dp[x][y - 1][i][1] + 1) : 0;
                }

                xMax = Math.max(x, xMax);
                yMax = Math.max(y, yMax);

                x++;
            }
        }

        xMax++;
        yMax++;

        int max = 0;

        int scoreGrid[][] = new int[100][100];

        for(x = xMax - 1; x >= 0; x--) {
            for(y = yMax - 1; y >= 0; y--) {
                for(int i = 0; i < 10; i++) {
                    dp[x][y][i][2] = x < xMax - 1 ? (map[x + 1][y] >= i + '0' ? 1 : dp[x + 1][y][i][2] + 1) : 0;
                    dp[x][y][i][3] = y < yMax - 1 ? (map[x][y + 1] >= i + '0' ? 1 : dp[x][y + 1][i][3] + 1) : 0;
                }

                int dpIndex = map[x][y] - '0';
                int myScore = 1;
                for(int i = 0; i < 4; i++) {
                    myScore *= dp[x][y][dpIndex][i];
                }

                scoreGrid[x][y] = myScore;

                max = Math.max(myScore, max);
            }
//            System.out.println();
        }


        for(int k = 0; k < 4; k++) {
            for(int i = 0; i < xMax; i++) {
                for (int j = 0; j < yMax; j++) {
                    System.out.print(dp[j][i][5][k]);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }


       for(int i = 0; i < xMax; i++) {
            for (int j = 0; j < yMax; j++) {
                System.out.print(scoreGrid[j][i]);
            }
            System.out.println();
       }


       System.out.println(max);
    }
}