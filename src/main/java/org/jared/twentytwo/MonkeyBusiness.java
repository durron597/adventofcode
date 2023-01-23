package org.jared.twentytwo;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import lombok.Data;
import lombok.Value;

import java.util.Comparator;

public class MonkeyBusiness {
    public static void main(String... args) throws Throwable {
        List<Monkey> monkeyList = List.of(
                new Monkey0(),
                new Monkey1(),
                new Monkey2(),
                new Monkey3(),
                new Monkey4(),
                new Monkey5(),
                new Monkey6(),
                new Monkey7());

        monkeyList.get(0).setTargets(monkeyList.get(6), monkeyList.get(7));
        monkeyList.get(1).setTargets(monkeyList.get(5), monkeyList.get(2));
        monkeyList.get(2).setTargets(monkeyList.get(4), monkeyList.get(5));
        monkeyList.get(3).setTargets(monkeyList.get(6), monkeyList.get(0));
        monkeyList.get(4).setTargets(monkeyList.get(0), monkeyList.get(3));
        monkeyList.get(5).setTargets(monkeyList.get(3), monkeyList.get(4));
        monkeyList.get(6).setTargets(monkeyList.get(1), monkeyList.get(7));
        monkeyList.get(7).setTargets(monkeyList.get(2), monkeyList.get(1));

        for(int i = 0; i < 10000; i++) {
            for(Monkey m : monkeyList) {
                m.takeTurn();
            }
            monkeyList.forEach(System.out::println);
            System.out.println();
        }

        long result = monkeyList.map(Monkey::getInspect)
                .sorted(Comparator.<Long>naturalOrder().reversed())
                .take(2)
                .fold(1L, (x, y) -> x * y);
        System.out.print(result);
    }

    @Value
    public static class Item {
        long worry;

        @Override
        public String toString() {
            return String.valueOf(worry);
        }
    }
    public static abstract class Monkey {
        List<Item> items = List.empty();
        public Monkey trueTarget;
        public Monkey falseTarget;

        private long inspect = 0;

        public long getInspect() {
            return inspect;
        }

        void takeTurn() {
            for(Item item : items) {
                inspect(item);
            }
            this.items = List.empty();
        }
        void inspect(Item item) {
            inspect++;
            Item operated = operate(item);
            operated = new Item(operated.getWorry() % (7L * 13L * 5L * 19L * 2L * 11L * 17L * 3L));
            if (test(operated)) {
                trueTarget.receiveItem(operated);
            } else {
                falseTarget.receiveItem(operated);
            }
        }
        abstract boolean test(Item item);
        abstract Item operate(Item item);
        List<Item> getItems() {
            return items;
        }
        void receiveItem(Item item) {
            items = items.append(item);
        }

        void setTargets(Monkey trueTarget, Monkey falseTarget) {
            this.trueTarget = trueTarget;
            this.falseTarget = falseTarget;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + " " + getItems();
        }
    }

    public static class Monkey0 extends Monkey {
        public Monkey0() {
            this.items = List.ofAll(66, 79).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 7 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() * 11);
        }
    }

    public static class Monkey1 extends Monkey {
        public Monkey1() {
            this.items = List.ofAll(84, 94, 94, 81, 98, 75).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 13 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() * 17);
        }
    }

    public static class Monkey2 extends Monkey {
        public Monkey2() {
            this.items = List.ofAll(85, 79, 59, 64, 79, 95, 67).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 5 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() + 8);
        }
    }

    public static class Monkey3 extends Monkey {
        public Monkey3() {
            this.items = List.ofAll(70).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 19 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() + 3);
        }
    }

    public static class Monkey4 extends Monkey {
        public Monkey4() {
            this.items = List.ofAll(57, 69, 78, 78).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 2 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() + 4);
        }
    }

    public static class Monkey5 extends Monkey {
        public Monkey5() {
            this.items = List.ofAll(65, 92, 60, 74, 72).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 11 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() + 7);
        }
    }

    public static class Monkey6 extends Monkey {
        public Monkey6() {
            this.items = List.ofAll(77, 91, 91).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 17 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() * item.getWorry());
        }
    }

    public static class Monkey7 extends Monkey {
        public Monkey7() {
            this.items = List.ofAll(76, 58, 57, 55, 67, 77, 54, 99).map(Item::new);
        }

        @Override
        boolean test(Item item) {
            return item.getWorry() % 3 == 0;
        }

        @Override
        Item operate(Item item) {
            return new Item(item.getWorry() + 6);
        }
    }
}