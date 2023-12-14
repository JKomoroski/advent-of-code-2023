import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day11 extends AOCBase {

    protected Day11() {
        super("day-11", "input.txt");
    }

    public static void main(String[] args) throws Exception {
        new Day11().run();
    }

    @Override
    void part1(Stream<String> fileInput) throws Exception {
        final List<String> grid = fileInput.toList();
        final var emptyY = IntStream.range(0, grid.size())
                .filter(y -> isDoubled(grid.get(y)))
                .boxed()
                .collect(Collectors.toList());

        final var emptyX = IntStream.range(0, grid.get(0).length())
                .filter(x -> grid.stream().allMatch(s -> s.charAt(x) == '.'))
                .boxed()
                .collect(Collectors.toList());

        List<Coordinate> galaxies = new ArrayList<>();
        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.get(0).length(); x++) {
                if (grid.get(y).charAt(x) == '#') {
                    galaxies.add(new Coordinate(x, y));
                }
            }
        }

        final var sum = galaxies.stream()
                .mapToLong(c -> toMinDistance(c, galaxies, emptyX, emptyY))
                .sum();

        System.out.println("Part 1: " + sum /2);

    }

    @Override
    void part2(Stream<String> fileInput) throws Exception {
        final List<String> grid = fileInput.toList();
        final var emptyY = IntStream.range(0, grid.size())
                .filter(y -> isDoubled(grid.get(y)))
                .boxed()
                .collect(Collectors.toList());

        final var emptyX = IntStream.range(0, grid.get(0).length())
                .filter(x -> grid.stream().allMatch(s -> s.charAt(x) == '.'))
                .boxed()
                .collect(Collectors.toList());

        List<Coordinate> galaxies = new ArrayList<>();
        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.get(0).length(); x++) {
                if (grid.get(y).charAt(x) == '#') {
                    galaxies.add(new Coordinate(x, y));
                }
            }
        }

        final var sum = galaxies.stream()
                .mapToLong(c -> toMinBigDistance(c, galaxies, emptyX, emptyY))
                .sum();

        System.out.println("Part 2: " + sum / 2);
    }

    static long toMinDistance(Coordinate c , List<Coordinate> galaxies, List<Integer> emptyX, List<Integer> emptyY) {
        return galaxies.stream().mapToLong(cmp -> calculateDistanceWithExpansion(c, cmp, emptyX, emptyY)).sum();
    }

    static long toMinBigDistance(Coordinate c , List<Coordinate> galaxies, List<Integer> emptyX, List<Integer> emptyY) {
        return galaxies.stream().mapToLong(cmp -> calculateDistanceWithBigExpansion(c, cmp, emptyX, emptyY)).sum();
    }

    static long calculateDistanceWithExpansion(Coordinate a, Coordinate b, List<Integer> emptyX, List<Integer> emptyY) {
        final var base = distanceBetween(a, b);
        final var xExpansion = addExpansionDistance(plainRangeX(a, b), emptyX);
        final var yExpansion = addExpansionDistance(plainRangeY(a, b), emptyY);
        return base + xExpansion + yExpansion;
    }

    static long calculateDistanceWithBigExpansion(Coordinate a, Coordinate b, List<Integer> emptyX, List<Integer> emptyY) {
        final var base = distanceBetween(a, b);
        final var xExpansion = addExpansionDistance(plainRangeX(a, b), emptyX);
        final var yExpansion = addExpansionDistance(plainRangeY(a, b), emptyY);
        return base + (xExpansion * 999_999) + (yExpansion * 999_999);
    }

    static boolean isDoubled(String s) {
        return s.matches("^\\.*$");
    }

    static int distanceBetween(Coordinate a, Coordinate b) {
        return Math.abs(a.x() - b.x()) + Math.abs(b.y() - a.y());
    }
    static List<Integer> plainRangeX(Coordinate a, Coordinate b) {
        final var min = Math.min(a.x(), b.x());
        final var max = Math.max(a.x(), b.x());
        return IntStream.rangeClosed(min, max).sorted().boxed().toList();
    }

    static List<Integer> plainRangeY(Coordinate a, Coordinate b) {
        final var min = Math.min(a.y(), b.y());
        final var max = Math.max(a.y(), b.y());
        return IntStream.rangeClosed(min, max).sorted().boxed().toList();
    }

    static long addExpansionDistance(List<Integer> doubles, List<Integer> plainRange) {
        return plainRange.stream().distinct().filter(doubles::contains).count();
    }

}
