package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import lombok.*;

import java.io.*;
import java.util.Scanner;

public class Directories {
    public static void main(String... args) throws Exception {
        InputStream input = Communicator.class.getResourceAsStream("/2022/day7_input.txt");
        Scanner sc = new Scanner(input);
        ScanIterator iter = new ScanIterator(sc);

        Tuple2<List<String>, Directory> initial =
            Tuple.of(List.of("/"), new Directory("/", HashMap.empty()));

        Tuple2<List<String>, Directory> listDirectoryTuple2 = iter.foldLeft(initial, Directories::applyCommand);

        Directory d = listDirectoryTuple2._2();

        int amountNeeded = 30_000_000 - (70_000_000 - d.getValue());

        System.out.println(amountNeeded);

        int answer = computeBest(d, Integer.MAX_VALUE, amountNeeded);

        System.out.println(answer);
    }

    public static int computeSum(Directory d) {
        int myValue = d.getValue();
        myValue = myValue < 100000 ? myValue : 0;

        return d.getChildren()
                .values()
                .flatMap(e -> e.fold(f -> List.empty(), List::of))
                .foldLeft(myValue, (i, dir) -> i + computeSum(dir));
    }

    public static int computeBest(Directory d, int minSoFar, int target) {
        int myValue = d.getValue();
        myValue = myValue > target ? Math.min(minSoFar, myValue) : minSoFar;

        return d.getChildren()
                .values()
                .flatMap(e -> e.fold(f -> List.empty(), List::of))
                .foldLeft(myValue, (i, dir) -> Math.min(i, computeBest(dir, i, target)));
    }


    private static Tuple2<List<String>, Directory>
    applyCommand(Tuple2<List<String>, Directory> currentState,
                 String command) {
        if (command.charAt(0) == '$') {
            String[] split = command.split(" ");
            if ("ls".equals(split[1])) return currentState;
            if ("/".equals(split[2])) return currentState.map1(l -> List.of("/"));
            if ("..".equals(split[2])) return currentState.map1(l -> l.dropRight(1));
            return currentState.map1(l -> l.append(split[2]));
        }

        Either<File, Directory> either;
        if (command.startsWith("dir")) {
            either = Either.right(new Directory(command.substring(4), HashMap.empty()));
        } else {
            String[] spl = command.split(" ");
            either = Either.left(new File(spl[1], Integer.parseInt(spl[0])));
        }
        return currentState.map2(d -> d.add(currentState._1(), either));
    }
    @Value
    public static class Directory {
        @NonNull
        String name;
        @NonNull
        Map<String, Either<File, Directory>> children;

        int getValue() {
            return children.values().foldLeft(0, (v, e) -> v + e.fold(File::getValue, Directory::getValue));
        }

        Directory add(List<String> path, Either<File, Directory> entry) {
            if (path.size() == 0 || !path.head().equals(name)) {
                return this;
            }

            if (path.size() == 1) {
                return new Directory(name,
                        children.put(entry.fold(File::getName, Directory::getName), entry));
            } else {
                return path.tail()
                        .headOption()
                        .flatMap(n -> children.get(n).map(e -> Tuple.of(n, e)))
                        .map(t -> new Directory(name,
                            children.put(t._1(), t._2().map(d -> d.add(path.tail(), entry)))))
                        .getOrElse(this);
            }
        }

        public String draw() {
            final StringBuilder builder = new StringBuilder();
            drawAux("", builder);
            return builder.toString();
        }

        private void drawAux(String indent, StringBuilder builder) {
            builder.append(name).append("-").append(getValue());
            for (List<Either<File, Directory>> it = children.values().toList(); !it.isEmpty(); it = it.tail()) {
                final boolean isLast = it.tail().isEmpty();
                builder.append('\n')
                        .append(indent)
                        .append(isLast ? "└──" : "├──");
                Either<File, Directory> head = it.head();
                if (head.isRight()) {
                    head.get().drawAux(indent + (isLast ? "   " : "│  "), builder);
                } else {
                    builder.append(head.getLeft());
                }
            }
        }
    }

    @Value
    public static class File {
        String name;
        int value;
    }

    private static class ScanIterator implements Iterator<String> {
        private final Scanner s;

        public ScanIterator(Scanner s) {
            this.s = s;
        }

        @Override
        public boolean hasNext() {
            return s.hasNextLine();
        }

        @Override
        public String next() {
            return s.nextLine();
        }
    }
}