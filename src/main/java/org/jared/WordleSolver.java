package org.jared;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class WordleSolver {
    @AllArgsConstructor
    @Value
    public static class WordId {
        String word;
        int id;

        @Override
        public boolean equals(Object wordId) {
            if (wordId == null) return false;
            if (!(wordId instanceof WordId)) return false;

            return this.id == ((WordId) wordId).id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }


    public static void main(String[] args) {
        List<WordId> words = fileToLines("/wordlist.txt").map(str -> new WordId(str, wordToId(str)));

        for(int i = 0; i < 5; i++) {
            int iMask = 31 << (i * 5);

            for(int j = 0; j < i; j++) {

                int jMask = 31 << (j * 5);
                int fullMask = ~(iMask + jMask);

                Map<WordId, List<WordId>> tuple2s = words.groupBy(word -> idToWord(word.getId() & fullMask));
                Option<Tuple2<WordId, List<WordId>>> tuple2s1 = tuple2s.maxBy(Comparator.comparingInt(tuple -> tuple._2().length()));
                tuple2s1.peek(val -> System.out.format("wordId: %s, length: %d, example:%s%n", val._1(), val._2().length(), val._2().head()));
            }

            Map<WordId, List<WordId>> tuple2s = words.groupBy(word -> idToWord(word.getId() & ~(iMask)));
            Option<Tuple2<WordId, List<WordId>>> tuple2s1 = tuple2s.maxBy(Comparator.comparingInt(tuple -> tuple._2().length()));
            tuple2s1.peek(val -> System.out.format("wordId: %s, length: %d, example:%s%n", val._1(), val._2().length(), val._2().head()));
        }
    }

    private static WordId idToWord(int id) {
        return new WordId(List.range(0, 5)
            .map(rangeIndex -> {
                int charBase = (id >> (rangeIndex * 5)) & 31;
                if (charBase == 0) {
                    return '_';
                } else {
                    return (char) (charBase - 1 + 'a');
                }
            })
            .foldRight("", WordleSolver::plop), id);
    }

    private static String plop(char chr, String str) {
        return str + chr;
    }

    private static int wordToId(String toConvert) {
        if (toConvert.length() != 5) {
            throw new IllegalArgumentException(String.format("toConvert must be 5 characters, was: %s", toConvert));
        }

        int result = 0;

        for(char c : toConvert.toCharArray()) {
            result <<= 5;
            int bitMask = 0;
            if (c != '_') {
                bitMask = (c - 'a') + 1;
            }
            result = result + bitMask;
        }

        return result;
    }

    private static List<String> fileToLines(String filename) {
        try (InputStream resourceAsStream = WordleSolver.class.getResourceAsStream(filename)) {
            Scanner sc = new Scanner(resourceAsStream);

            Stream.Builder<String> fileStream = Stream.builder();

            while (sc.hasNextLine()) {
                String next = sc.nextLine();
                if (next.length() == 5) {
                    fileStream.accept(next);
                }
            }

            return List.ofAll(fileStream.build());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}