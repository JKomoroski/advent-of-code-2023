import static java.util.function.Predicate.not;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day6 {

    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "inputs", "day-6", "input.txt").toAbsolutePath();
        final var strings = Files.readAllLines(input);

        final var times = Arrays.stream(strings.get(0).split(": ")[1].split(" ")).filter(not(String::isBlank)).map(String::trim).mapToLong(Long::parseLong).toArray();
        final var distances = Arrays.stream(strings.get(1).split(": ")[1].split(" ")).filter(not(String::isBlank)).map(String::trim).mapToLong(Long::parseLong).toArray();
        final var multiple = IntStream.range(0, times.length)
                .mapToObj(i -> new Race(times[i], distances[i]))
                .map(Day6::computePossibleOutcomes)
                .mapToLong(l -> l.stream().filter(Outcome::isWin).count())
                .reduce(1, (x, y) -> x * y);

        System.out.println("Part 1: " + multiple);

        final var time = Long.parseLong(strings.get(0).split(": ")[1].replace(" ", "").trim());
        final var distance = Long.parseLong(strings.get(1).split(": ")[1].replace(" ", "").trim());
        final var singleBig = computePossibleOutcomes(new Race(time, distance))
                .stream()
                .filter(Outcome::isWin)
                .count();

        System.out.println("Part 2: " + singleBig);
    }

    record Race(long time, long distance) {}

    record Outcome(long hold, long distance, boolean isWin){}

    static List<Outcome> computePossibleOutcomes(Race r) {
        return LongStream.range(0, r.time)
                .mapToObj(hold -> {
                    final var distance = hold * (r.time - hold);
                    return new Outcome(hold, distance, distance > r.distance);
                })
                .toList();
    }
}
