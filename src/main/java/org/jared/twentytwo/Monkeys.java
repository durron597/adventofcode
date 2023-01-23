package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import lombok.Getter;
import lombok.Value;
import org.jared.util.ScanIterator;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;

public class Monkeys {
    public interface Monkey {
        long evaluate();
        String getName();
    }

    @Value
    public class MathMonkey implements Monkey {
        Operation op;

        String name;

        String left;

        String right;

        @Override
        public long evaluate() {
            return op.applyAsLong(monkeyMap.get(left).get().evaluate(),
                    monkeyMap.get(right).get().evaluate());
        }
    }

    @Value
    public class ValueMonkey implements Monkey {
        String name;

        long value;

        @Override
        public long evaluate() {
            return value;
        }
    }

    @Value
    public class HumanMonkey implements Monkey {
        String name;

        @Override
        public long evaluate() {
            return humanTry;
        }
    }

    public Map<String, Monkey> monkeyMap = HashMap.empty();

    public long humanTry = 0;

    public enum Operation implements LongBinaryOperator {
        ADD ('+', Long::sum),
        SUBTRACT ('-', (x, y) -> x - y),
        MULTIPLY ('*', (x, y) -> x * y),
        DIVIDE ('/', (x, y) -> x / y);

        @Getter
        private final char op;
        private final LongBinaryOperator operation;

        private static final Map<Character, Operation> charToOperationMap =
                HashMap.ofEntries(Iterator.of(Operation.values()).map(o -> Tuple.of(o.getOp(), o)));

        Operation(char op, LongBinaryOperator operation) {
            this.op = op;
            this.operation = operation;
        }

        @Override
        public long applyAsLong(long left, long right) {
            return operation.applyAsLong(left, right);
        }

        public static Operation fromCharacter(char c) {
            return charToOperationMap.get(c).get();
        }
    }

    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(Monkeys.class.getResourceAsStream("/2022/day21_input.txt")));

        System.out.println(new Monkeys(iter).evaluate());
    }

    public Monkeys(Iterator<String> monkeys) {
        monkeyMap = monkeys.map(this::makeMonkey).toMap(Monkey::getName, Function.identity());
    }

    public long evaluate() {
        long leftTry = humanTry = 3006709231781L;
        long left = monkeyMap.get("root").get().evaluate();
        long rightTry = humanTry = 3006709234281L;
        long right = monkeyMap.get("root").get().evaluate();

        long middleTry;

        while(true) {
            middleTry = humanTry = (leftTry + rightTry) / 2L;
            long middle = monkeyMap.get("root").get().evaluate();

            if (middle == 0) break;

            if (middle > 0) {
                leftTry = middleTry;
            } else {
                rightTry = middleTry;
            }
            System.out.println(middleTry + ": " + middle);
        }

        return middleTry;
    }

    private Monkey makeMonkey(String row) {
        String[] parsed = row.split(": ");
        Monkey newMonkey;
        if ("humn".equals(parsed[0])) {
            newMonkey = new HumanMonkey(parsed[0]);
        } else if (Character.isDigit(parsed[1].charAt(0))) {
            newMonkey = new ValueMonkey(parsed[0], Long.parseLong(parsed[1]));
        } else {
            String[] rightSide = parsed[1].split(" ");
            Operation op = Operation.fromCharacter(rightSide[1].charAt(0));
            newMonkey = new MathMonkey("root".equals(parsed[0]) ? Operation.SUBTRACT : op,
                    parsed[0], rightSide[0], rightSide[2]);
        }
        return newMonkey;
    }
}
