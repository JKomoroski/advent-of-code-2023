import java.util.stream.Stream;

public class Day01 extends AOCBase {

    public Day01() {
        super("day-1", "in.txt");
    }

    public static void main(String[] args) throws Exception {
        new Day01().run();
    }

    @Override
    void part1(Stream<String> fileInput) throws Exception {
        final var sum = fileInput
                .map(s -> s.replaceAll("\\D", ""))
                .mapToInt(s -> Integer.parseInt("" + s.charAt(0) + s.charAt(s.length() - 1)))
                .sum();
        System.out.println("Part 1: " + sum);
    }

    @Override
    void part2(Stream<String> fileInput) throws Exception {
        final var sum2 = fileInput
                .map(Day01::replaceDigits)
                .map(s -> s.replaceAll("\\D", ""))
                .mapToInt(s -> Integer.parseInt("" + s.charAt(0) + s.charAt(s.length() - 1)))
                .sum();
        System.out.println("Part 2: " + sum2);
    }

    static String replaceDigits(String in) {
        // #HACK
        return in.replaceAll("one", "o1one")
                .replaceAll("two", "t2two")
                .replaceAll("three", "t3three")
                .replaceAll("four", "f4four")
                .replaceAll("five", "f5five")
                .replaceAll("six", "s6six")
                .replaceAll("seven", "s7seven")
                .replaceAll("eight", "e8eight")
                .replaceAll("nine", "n9nine");
    }
}