import static java.util.function.Predicate.not;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Day04 {

    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "inputs", "day-4", "input.txt").toAbsolutePath();

        final var strings = Files.readAllLines(input);
        final var winnerLookups = strings.stream().map(Day04::fromLine).toList();

        final var points = winnerLookups.stream()
                .mapToLong(Day04::countWinners)
                .map(l -> l == 0 ? 0 : doubleNumberRecursively(1, l))
                .sum();

        System.out.println("Part 1: " + points);

        Map<Long, Long> totalCards = winnerLookups.stream().collect(Collectors.toMap(x -> x.id, x -> 0L));
        for (int i = 0; i < winnerLookups.size(); i++) {
            final var card = winnerLookups.get(i);
            final var count = totalCards.get(card.id);
            for (int y = 0; y <= count; y++) {
                final var winners = countWinners(card);
                for (int j = i + 1; j < winnerLookups.size() && j <= i + winners; j++) {
                    final var card1 = winnerLookups.get(j);
                    final var newTotal = totalCards.get(card1.id) + 1;
                    totalCards.put(card1.id, newTotal); // Most time is spent boxing these puts
                }
            }
            totalCards.put(card.id, count + 1); // Most time is spent boxing these puts
        }

        System.out.println("Part 2: " + totalCards.values().stream().mapToLong(Long::longValue).sum());


    }


    static long doubleNumberRecursively(long number, long times) {
        if (times == 1) {
            return number;
        } else {
            return doubleNumberRecursively(number * 2, times - 1);
        }
    }

    static long countWinners(Card card) {
        return card.result;
    }

    static Card fromLine(String s) {
        final var headerSplit = s.split(":");
        final var id = headerSplit[0].trim().split("\\s+")[1];
        final var winners = headerSplit[1].split("\\|")[0].split(" ");
        final var actual = headerSplit[1].split("\\|")[1].split(" ");
        final var winnersArr = Arrays.stream(winners).filter(not(String::isBlank)).map(String::trim).mapToLong(Long::parseLong)
                .toArray();
        final var actualsArr = Arrays.stream(actual).filter(not(String::isBlank)).map(String::trim).mapToLong(Long::parseLong)
                .toArray();
        final var result = Arrays.stream(actualsArr).filter(c -> Arrays.stream(winnersArr).anyMatch(w -> w == c)).count();

        return new Card(Integer.parseInt(id),
                winnersArr,
                actualsArr,
                result
        );
    }


    record Card(long id, long[] winners, long[] actuals, long result) {

    }

    ;
}
