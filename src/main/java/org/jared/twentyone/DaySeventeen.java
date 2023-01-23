package org.jared.twentyone;

import io.vavr.control.Option;
import org.jared.util.ScanIterator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaySeventeen {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DaySeventeen.class.getResourceAsStream("/2021/day17_input.txt")));

        Pattern p = Pattern.compile("^target area: x=(\\d+)\\.\\.(\\d+), y=(-\\d+)\\.\\.(-\\d+)$");
        Matcher m = p.matcher(iter.next());
        m.find();

        int x1 = Integer.parseInt(m.group(1));
        int x2 = Integer.parseInt(m.group(2));
        int y1 = Integer.parseInt(m.group(3));
        int y2 = Integer.parseInt(m.group(4));

        int total = 0;

        int xMin = (int) Math.sqrt(x1 * 2) + 1;

        for(int y = y1 - 1; y < (-y1) + 1; y++) {
            for(int x = xMin; x < x2 + 1; x++) {
                int xPos = 0;
                int yPos = 0;
                int xDelta = x;
                int yDelta = y;
                while (xPos <= x2 && yPos >= y1) {
                    xPos += xDelta;
                    yPos += yDelta;
                    if (xPos >= x1 && xPos <= x2 && yPos >= y1 && yPos <= y2) {
                        total++;
                        System.out.format("%d,%d%n", x, y);
                        break;
                    }
                    xDelta = Math.max(0, xDelta - 1);
                    yDelta--;
                }
            }
        }

        System.out.println(total);
    }
}
