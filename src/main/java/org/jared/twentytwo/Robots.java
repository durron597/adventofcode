package org.jared.twentytwo;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jared.util.ScanIterator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Robots {
    @Value
    @Builder(toBuilder = true)
    @AllArgsConstructor
    public static class State {
        int ore;
        int clay;
        int obsidian;
        int geode;

        int oreRobot;

        int clayRobot;

        int obsidianRobot;

        int geodeRobot;

        int minute;
    }

    @Value
    @Builder
    public static class BluePrint {
        int oreCost;

        int clayCost;

        int obsidianOreCost;

        int obsidianClayCost;

        int geodeOreCost;

        int geodeObsidianCost;
    }

    private static Pattern p = Pattern.compile(
            "Blueprint ([0-9]{1,2}): " +
                    "Each ore robot costs ([0-9]) ore. " +
                    "Each clay robot costs ([0-9]) ore. " +
                    "Each obsidian robot costs ([0-9]) ore and ([0-9]{1,2}) clay. " +
                    "Each geode robot costs ([0-9]) ore and ([0-9]{1,2}) obsidian.");

    public static void main(String... args) {
        ScanIterator iter = new ScanIterator(new Scanner(Robots.class.getResourceAsStream("/2022/day19_input.txt")));

        List<Tuple2<BluePrint, Integer>> buildBlueprints = List.ofAll(iter)
                .take(3)
                .map(p::matcher)
                .map(m -> {
                    m.find();
                    return m;
                })
                .map(m -> BluePrint.builder()
                        .oreCost(Integer.parseInt(m.group(2)))
                        .clayCost(Integer.parseInt(m.group(3)))
                        .obsidianOreCost(Integer.parseInt(m.group(4)))
                        .obsidianClayCost(Integer.parseInt(m.group(5)))
                        .geodeOreCost(Integer.parseInt(m.group(6)))
                        .geodeObsidianCost(Integer.parseInt(m.group(7)))
                        .build())
                .zipWithIndex()
                .map(t -> t.map2(i -> i + 1));

        long start = System.nanoTime();

        List<Tuple2<State, Integer>> result = buildBlueprints
                .flatMap(t -> playGame(t._1(), t._2()).map(s -> Tuple.of(s, t._2())));

        long end = System.nanoTime();

        System.out.println(result);

        System.out.println(result.foldLeft(1, (acc, t) -> acc * t._1().getGeode()));

        System.out.format("Elapsed: %f seconds%n", (((double) (end - start)) / 1_000_000_000.));
    }

    private static Option<State> playGame(BluePrint b, int i) {
        LinkedList<State> bfs = new LinkedList<>();

        State start = new State(0,0, 0, 0, 1, 0, 0, 0, 0);

        bfs.add(start);

        int maxMinute = -1;

        Set<State> visited = new HashSet<>();
        State bestForMinute = null;

        int bfsMax = -1;

        while(bfs.peek().getMinute() < 32) {
            int minute = bfs.peek().getMinute();
            if (minute > maxMinute) {
                maxMinute = minute;
                bestForMinute = bfs.stream().max(Comparator.comparingInt(State::getGeode)
                        .thenComparing(State::getGeodeRobot)
                        .thenComparing(State::getObsidianRobot)
                        .thenComparing(State::getObsidian)).get();
            }
            State next = bfs.poll();

            if (visited.contains(next)) continue;
            if (isGeodeWorse(bestForMinute, next)) continue;

            visited.add(next);

            State produced = next.toBuilder()
                .ore(next.getOre() + next.getOreRobot())
                .clay(next.getClay() + next.getClayRobot())
                .obsidian(next.getObsidian() + next.getObsidianRobot())
                .geode(next.getGeode() + next.getGeodeRobot())
                .minute(next.getMinute() + 1)
                .build();

            aggressiveLogic(b, bfs, next, produced);
        }

        return Option.ofOptional(bfs.stream().max(Comparator.comparingInt(State::getGeode)));
    }

    private static void safeLogic(BluePrint b, LinkedList<State> bfs, State next, State produced) {
        if (canBuildGeode(b, next)) {
            bfs.offer(produced.toBuilder()
                    .ore(produced.getOre() - b.getGeodeOreCost())
                    .obsidian(produced.getObsidian() - b.getGeodeObsidianCost())
                    .geodeRobot(next.getGeodeRobot() + 1)
                    .build());
        } else if (canBuildObsidian(b, next)) {
            bfs.offer(produced.toBuilder()
                    .ore(produced.getOre() - b.getObsidianOreCost())
                    .clay(produced.getClay() - b.getObsidianClayCost())
                    .obsidianRobot(next.getObsidianRobot() + 1)
                    .build());
        } else {
            boolean builtOne = false;
            if (canBuildOre(b, next)) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getOreCost())
                        .oreRobot(next.getOreRobot() + 1)
                        .build());
                builtOne = true;
            }
            if (canBuildClay(b, next)) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getClayCost())
                        .clayRobot(next.getClayRobot() + 1)
                        .build());
                builtOne = true;
            }
            if (!builtOne) {
                bfs.offer(produced);
            }
        }
    }

    private static void improveLogic(BluePrint b, LinkedList<State> bfs, State next, State produced) {
        if (next.getOreRobot() == 1) {
            if (canBuildOre(b, next)) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getOreCost())
                        .oreRobot(next.getOreRobot() + 1)
                        .build());
            } else {
                bfs.offer(produced);
            }
            return;
        }
        if (canBuildGeode(b, next)) {
            bfs.offer(produced.toBuilder()
                    .ore(produced.getOre() - b.getGeodeOreCost())
                    .obsidian(produced.getObsidian() - b.getGeodeObsidianCost())
                    .geodeRobot(next.getGeodeRobot() + 1)
                    .build());
        } else {
            boolean builtObsidian = false;
            if (canBuildObsidian(b, next) && next.getObsidianRobot() < 8) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getObsidianOreCost())
                        .clay(produced.getClay() - b.getObsidianClayCost())
                        .obsidianRobot(next.getObsidianRobot() + 1)
                        .build());
                builtObsidian = true;
            }
            if (canBuildClay(b, next) && next.getObsidianRobot() == 0) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getClayCost())
                        .clayRobot(next.getClayRobot() + 1)
                        .build());
            }
            if (canBuildOre(b, next) && next.getObsidianRobot() == 0) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getOreCost())
                        .oreRobot(next.getOreRobot() + 1)
                        .build());
            }
            if (!builtObsidian || next.getObsidianRobot() == 0 ) {
                bfs.offer(produced);
            }
        }
    }

    private static void aggressiveLogic(BluePrint b, LinkedList<State> bfs, State next, State produced) {
        if (next.getOreRobot() == 1) {
            if (canBuildOre(b, next)) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getOreCost())
                        .oreRobot(next.getOreRobot() + 1)
                        .build());
            } else {
                bfs.offer(produced);
            }
            return;
        }
        if (canBuildGeode(b, next)) {
            bfs.offer(produced.toBuilder()
                    .ore(produced.getOre() - b.getGeodeOreCost())
                    .obsidian(produced.getObsidian() - b.getGeodeObsidianCost())
                    .geodeRobot(next.getGeodeRobot() + 1)
                    .build());
        } else {
            if (canBuildObsidian(b, next)) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getObsidianOreCost())
                        .clay(produced.getClay() - b.getObsidianClayCost())
                        .obsidianRobot(next.getObsidianRobot() + 1)
                        .build());
            }
            if (canBuildOre(b, next) && next.getOreRobot() < 4) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getOreCost())
                        .oreRobot(next.getOreRobot() + 1)
                        .build());
            }
            if (canBuildClay(b, next)) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getClayCost())
                        .clayRobot(next.getClayRobot() + 1)
                        .build());
            }
            bfs.offer(produced);
        }
    }

    private static void newLogic(BluePrint b, LinkedList<State> bfs, State next, State produced) {
        if (canBuildGeode(b, next)) {
            bfs.offer(produced.toBuilder()
                    .ore(produced.getOre() - b.getGeodeOreCost())
                    .obsidian(produced.getObsidian() - b.getGeodeObsidianCost())
                    .geodeRobot(next.getGeodeRobot() + 1)
                    .build());
        } else if (canBuildGeode(b, produced)) {
            bfs.offer(produced);
        } else {
            if (canBuildObsidian(b, next)) {
                bfs.offer(produced.toBuilder()
                        .ore(produced.getOre() - b.getObsidianOreCost())
                        .clay(produced.getClay() - b.getObsidianClayCost())
                        .obsidianRobot(next.getObsidianRobot() + 1)
                        .build());
            } else if (canBuildObsidian(b, produced)) {
                bfs.offer(produced);
            } else {
                boolean builtOne = false;
                if (canBuildClay(b, next)) {
                    bfs.offer(produced.toBuilder()
                            .ore(produced.getOre() - b.getClayCost())
                            .clayRobot(next.getClayRobot() + 1)
                            .build());
                    builtOne = true;
                }
                if (canBuildOre(b, next)) {
                    bfs.offer(produced.toBuilder()
                            .ore(produced.getOre() - b.getOreCost())
                            .oreRobot(next.getOreRobot() + 1)
                            .build());
                    builtOne = true;
                }
                if (!builtOne) {
                    bfs.offer(produced);
                }
            }
        }
    }

    public static boolean isGeodeWorse(State max, State candidate) {
        return (candidate.getGeode() < max.getGeode() &&
                candidate.getGeodeRobot() < max.getGeodeRobot()) ||
                (max.getGeodeRobot() == candidate.getGeodeRobot() && candidate.getObsidianRobot() < max.getObsidianRobot() &&
                        candidate.getObsidian() < max.getObsidian());
    }

    private static boolean canBuildGeode(BluePrint b, State next) {
        return next.getOre() >= b.getGeodeOreCost() && next.getObsidian() >= b.getGeodeObsidianCost();
    }

    private static boolean canBuildObsidian(BluePrint b, State next) {
        return next.getOre() >= b.getObsidianOreCost() && next.getClay() >= b.getObsidianClayCost();
    }

    private static boolean canBuildClay(BluePrint b, State next) {
        return next.getOre() >= b.getClayCost();
    }

    private static boolean canBuildOre(BluePrint b, State next) {
        return next.getOre() >= b.getOreCost();
    }
}
