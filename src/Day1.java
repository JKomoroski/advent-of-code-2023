import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day1 {

    public static void main(String[] args) throws Exception {
        final var strings = Files.readAllLines(Path.of("/home/jkomoroski/IdeaProjects/advent-of-code-2023/day-1/in.txt"));

        final var sum = strings.stream()
                .map(s -> s.replaceAll("\\D", ""))
                .mapToInt(s -> Integer.parseInt("" + s.charAt(0) + s.charAt(s.length() - 1)))
                .sum();
        System.out.println("Part 1: " + sum);
        final var sum2 = strings.stream()
                .map(Day1::replaceDigits)
                .map(s -> s.replaceAll("\\D", ""))
                .mapToInt(s -> Integer.parseInt("" + s.charAt(0) + s.charAt(s.length() - 1)))
                .sum();
        System.out.println("Part 2: " + sum2);

    }

    static String replaceDigits(String in ) {
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