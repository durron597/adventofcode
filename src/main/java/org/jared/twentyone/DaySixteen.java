package org.jared.twentyone;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import lombok.Value;
import org.jared.util.ScanIterator;

import java.util.Scanner;


public class DaySixteen {
    @Value
    public static class Node {
        int type;

        int version;

        long literal;

        List<Node> children;

        public Node(int type, int version, long literal) {
            this.type = type;
            this.version = version;
            this.literal = literal;
            this.children = List.empty();
        }

        public Node(int type, int version, List<Node> children) {
            this.type = type;
            this.version = version;
            this.literal = -1L;
            this.children = children;
        }
    }

    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(DaySixteen.class.getResourceAsStream("/2021/day16_input.txt")));

        while(iter.hasNext()) {
            char[] packetString = iter.next().toCharArray();

            for (int i = 0; i < packetString.length; i++) {
                if (packetString[i] > '9') {
                    char newValue = (char) (packetString[i] - 'A' + 10);
                    packetString[i] = newValue;
                } else {
                    char newValue = (char) (packetString[i] - '0');
                    packetString[i] = newValue;
                }
            }

            System.out.println(renderPacketString(packetString));

            var bitList = List.ofAll(packetString)
                    .map(Integer::toBinaryString)
                    .map(s -> String.format("%4s", s).replace(' ', '0'))
                    .flatMap(s -> List.ofAll(s.toCharArray()))
                    .map(c -> c != '0');

            Tuple2<Node, List<Boolean>> result = makePacket(bitList);

            System.out.println(result);

            int count = versionSum(result._1());

//            System.out.println(count);

            System.out.println(evaluate(result._1()));
        }
    }

    private static int versionSum(Node node) {
        return node.getVersion() + node.getChildren().foldLeft(0, (acc, n) -> acc + versionSum(n));
    }

    private static long evaluate(Node node) {
        return switch (node.getType()) {
            case 0 -> node.getChildren().foldLeft(0L, (acc, n) -> acc + evaluate(n));
            case 1 -> node.getChildren().foldLeft(1L, (acc, n) -> acc * evaluate(n));
            case 2 -> node.getChildren().foldLeft(Long.MAX_VALUE, (acc, n) -> Math.min(acc, evaluate(n)));
            case 3 -> node.getChildren().foldLeft(Long.MIN_VALUE, (acc, n) -> Math.max(acc, evaluate(n)));
            case 4 -> node.getLiteral();
            case 5 -> evaluate(node.getChildren().get(0)) > evaluate(node.getChildren().get(1)) ? 1 : 0;
            case 6 -> evaluate(node.getChildren().get(0)) < evaluate(node.getChildren().get(1)) ? 1 : 0;
            default -> evaluate(node.getChildren().get(0)) == evaluate(node.getChildren().get(1)) ? 1 : 0;
        };
    }

    private static String renderPacketString(char[] packetString) {
        return List.ofAll(packetString)
                .map(Integer::toHexString)
                .reduce((x, y) -> x + y);
    }

    private static Tuple2<Node, List<Boolean>> makePacket(List<Boolean> bitList) {
        Tuple2<Integer, List<Boolean>> version = readInteger(bitList, 3);
        Tuple2<Integer, List<Boolean>> type = readInteger(version._2(), 3);

        if (type._1() == 4) {
            return readLiteral(type._2()).map1(l -> new Node(type._1(), version._1(), l));
        } else {
            return buildSubpackets(type._2()).map1(l -> new Node(type._1(), version._1(), l));
        }
    }

    private static Tuple2<List<Node>, List<Boolean>> buildSubpackets(List<Boolean> bitList) {
        boolean lengthType = bitList.head();
        if (lengthType) {
            return buildCountSubpackets(bitList.tail());
        } else {
            return buildLengthSubpackets(bitList.tail());
        }
    }

    private static Tuple2<List<Node>, List<Boolean>> buildCountSubpackets(List<Boolean> tail) {
        var readCount = readInteger(tail, 11);
        List<Boolean> rest = readCount._2();
        int count = readCount._1();

        List<Node> nodeList = List.empty();

        for(int i = 0; i < count; i++) {
            var nextPacket = makePacket(rest);
            nodeList = nodeList.append(nextPacket._1());
            rest = nextPacket._2();
        }

        return Tuple.of(nodeList, rest);
    }

    private static Tuple2<List<Node>, List<Boolean>> buildLengthSubpackets(List<Boolean> tail) {
        var readCount = readInteger(tail, 15);
        tail = readCount._2();
        int count = readCount._1();

        List<Node> nodeList = List.empty();

        while(count > 0) {
            var nextPacket = makePacket(tail);
            nodeList = nodeList.append(nextPacket._1());
            count -= (tail.size() - nextPacket._2().size());
            tail = nextPacket._2();
        }

        return Tuple.of(nodeList, tail);
    }

    private static Tuple2<Long, List<Boolean>> readLiteral(List<Boolean> booleans) {
        long total = 0;
        boolean goAgain;
        do {
            goAgain = booleans.head();
            Tuple2<Integer, List<Boolean>> nextDigits = readInteger(booleans.tail(), 4);
            total = (total << 4) + nextDigits._1();
            booleans = nextDigits._2();
        } while (goAgain);
        return Tuple.of(total, booleans);
    }

    private static Tuple2<Integer, List<Boolean>> readInteger(List<Boolean> bitList, int count) {
        int result = bitList.take(count).foldLeft(0, (acc, next) -> acc * 2 + (next ? 1 : 0));
        return Tuple.of(result, bitList.drop(count));
    }

}
