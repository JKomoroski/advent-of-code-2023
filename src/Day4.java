import static java.util.function.Predicate.not;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day4 {
    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "day-4", "input.txt").toAbsolutePath();

        final var strings = Files.readAllLines(input);

        final var points = strings.stream()
                .map(Day4::fromLine)
                .mapToLong(Day4::countWinners)
                .map(l -> l == 0 ? 0 : doubleNumberRecursively(1, l))
                .sum();



        System.out.println("Part 1: " + points);

        final var winnerLookups = strings.stream()
                .map(Day4::fromLine)
                .toList();

        List<Card> totalCards = new ArrayList<>();
        for (int i = 0; i < winnerLookups.size(); i++) {
            final var card = winnerLookups.get(i);
            final var count = totalCards.stream().filter(card::equals).count();
            for (int y = 0; y <= count; y++) {
                final var winners = countWinners(card);
                for (int j = i + 1; j < winnerLookups.size() && j <= i + winners; j++) {
                    final var card1 = winnerLookups.get(j);
                    totalCards.add(card1);
                }
            }
            totalCards.add(card);
        }

        System.out.println("Part 2: " + totalCards.size());


    }


    static long doubleNumberRecursively(long number, long times) {
        if (times == 1) {
            return number;
        } else {
            return doubleNumberRecursively(number * 2, times - 1);
        }
    }

    static long countWinners(Card card) {
        return card.actuals.stream()
                .filter(card.winners::contains)
                .count();
    }

    static Card fromLine(String s) {
        final var headerSplit = s.split(":");
        final var id = headerSplit[0].trim().split("\\s+")[1];
        final var winners = headerSplit[1].split("\\|")[0].split(" ");
        final var actual = headerSplit[1].split("\\|")[1].split(" ");

        return new Card(Integer.parseInt(id),
                Arrays.stream(winners).filter(not(String::isBlank)).map(String::trim).map(Integer::parseInt).toList(),
                Arrays.stream(actual).filter(not(String::isBlank)).map(String::trim).map(Integer::parseInt).toList()
        );
    }



    record Card(int id, List<Integer> winners, List<Integer> actuals){};
}
