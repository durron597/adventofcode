package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.TreeSet;
import io.vavr.control.Either;
import lombok.SneakyThrows;

import java.text.ParseException;
import java.util.Scanner;

public class ListCompare implements Runnable {
    public static void main(String... args) {
        new ListCompare().run();
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(ListCompare.class.getResourceAsStream("/2022/day13_input.txt"));

        TreeSet<ListNode> treeSet = TreeSet.empty();

        while(sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            if (nextLine.length() > 0 && nextLine.charAt(0) == '[') {
                treeSet = treeSet.add(deserialize(nextLine));
            }
        }

        final ListNode two = new ListNode(List.of(Either.right(new ListNode(List.of(Either.left(2))))));
        final ListNode six = new ListNode(List.of(Either.right(new ListNode(List.of(Either.left(6))))));

        treeSet = treeSet.add(two);
        treeSet = treeSet.add(six);

        TreeSet<Tuple2<ListNode, Integer>> indexed = treeSet.zipWithIndex();
        int twoIndex = indexed.find(t -> t._1().equals(two)).map(Tuple2::_2).getOrElse(-1);
        int sixIndex = indexed.find(t -> t._1().equals(six)).map(Tuple2::_2).getOrElse(-1);

        indexed.forEach(System.out::println);

        System.out.println(twoIndex);
        System.out.println(sixIndex);

        System.out.println((twoIndex + 1) * (sixIndex + 1));
    }

    public ListNode deserialize(String s) {
        return new ListNode(deserializeHelper(s, 0)._1());
    }

    @SneakyThrows
    public Tuple2<List<Either<Integer, ListNode>>, Integer> deserializeHelper(String s, int index) {
        if (s.charAt(index) != '[') throw new ParseException("Expected [ at position: ", index);
        index++;

        List<Either<Integer, ListNode>> toReturn = List.empty();

        while(s.charAt(index) != ']') {
            if (s.charAt(index) == '[') {
                Tuple2<List<Either<Integer, ListNode>>, Integer> nextTuple = deserializeHelper(s, index);
                index = nextTuple._2() + 1;
                toReturn = toReturn.append(Either.right(new ListNode(nextTuple._1())));
            } else if (Character.isDigit(s.charAt(index))) {
                int temp = index;
                while (Character.isDigit(s.charAt(++temp)));
                toReturn = toReturn.append(Either.left(Integer.parseInt(s.substring(index, temp))));
                index = temp;
            } else if (s.charAt(index) == ',') {
                index++;
            } else {
                throw new ParseException("Unexpected character at position: ", index);
            }
        }

        return Tuple.of(toReturn, index);
    }

}
