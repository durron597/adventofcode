package org.jared.twentyone;

import io.vavr.collection.List;
import org.jared.util.ScanIterator;

import java.util.Arrays;
import java.util.Scanner;

public class DayThree {
    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DayThree.class.getResourceAsStream("/2021/day3_input.txt")));

        var arrays = iter
                .map(String::toCharArray)
                .toList();

        List<char[]> oxyList = arrays;
        List<char[]> co2List = arrays;

        for (int i = 0; i < arrays.head().length; i++) {

            oxyList = filterChars(oxyList, i, '1', '0');
            System.out.println(oxyList.size());
            co2List = filterChars(co2List, i, '0', '1');
            System.out.println(co2List.size());
        }

        oxyList.forEach(arr -> System.out.format("%s = %d%n", Arrays.toString(arr), Integer.parseInt(String.valueOf(arr), 2)));
        System.out.println("--------");
        co2List.forEach(arr -> System.out.format("%s = %d%n", Arrays.toString(arr), Integer.parseInt(String.valueOf(arr), 2)));
    }

    private static List<char[]> filterChars(List<char[]> list, int i, char left, char right) {
        if (list.size() == 1) return list;

        var result = list.foldLeft(0, (acc, arr) -> DayThree.accumulate(acc, arr, i));
        final int size = list.size();
        list = list.filter(arr -> arr[i] == (result * 2 >= size ? left : right));
        return list;
    }

    private static int accumulate(int acc, char[] chars, int i) {
        if (chars[i] == '1') acc++;
        return acc;
    }

}
