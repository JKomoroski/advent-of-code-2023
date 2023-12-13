import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

public class Day07 {

    static final String CARDS = "23456789TJQKA";
    static final String JOKER_CARDS = "J23456789TQKA";

    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "inputs", "day-7", "input.txt").toAbsolutePath();
        final var strings = Files.readAllLines(input);
        final var inputArr = strings.stream()
                .map(s -> toInput(s, CARDS))
                .sorted()
                .toList();

        long sum = 0;
        for (int i = 0; i < inputArr.size(); i++) {
            sum += inputArr.get(i).bid * (i + 1);
        }

        System.out.println("Part 1: " + sum);

        final var inputArr2 = strings.stream()
                .map(s -> toInput2(s, JOKER_CARDS))
                .sorted()
                .toList();

        long sum2 = 0;
        for (int i = 0; i < inputArr2.size(); i++) {
            sum2 += inputArr2.get(i).bid * (i + 1);
        }

        System.out.println("Part 2: " + sum2);

    }

    static Input toInput(String s, String cardSet) {
        final var s1 = s.split(" ");
        return new Input(s1[0], Long.parseLong(s1[1].trim()), cardSet);
    }

    static Input2 toInput2(String s, String cardSet) {
        final var s1 = s.split(" ");
        return new Input2(s1[0], Long.parseLong(s1[1].trim()), cardSet);
    }

    record Input2(String hand, long bid, String cardSet) implements Comparable<Input2> {

        // the following methods are actively harmful. Don't read them.

        long jokerCount() {
            return countChars(hand, 'J', 0);
        }

        boolean isFiveOfAKind() {
            final var jokerCount = jokerCount();
            return jokerCount == 0
                    ? hand.chars().distinct().count() == 1
                    : hand.chars().filter(c -> c != 'J').distinct().count() <= 1;
        }

        boolean isFourOfAKind() {
            return hand.chars().filter(c -> c != 'J').map(c -> countChars(hand, (char) c, 0))
                    .max().orElse(0) + jokerCount() == 4;
        }

        boolean isFullHouse() {
            return hand.chars().filter(c -> c != 'J').distinct().count() == 2
                    && hand.chars().filter(c -> c != 'J').distinct().map(c -> countChars(hand, (char) c, 0)).max().orElse(0) + jokerCount() == 3;
        }

        // the biggest of yikes.
        boolean isThreeOfAKind() {
            final var jokerCount = jokerCount();

            if(jokerCount == 0) {
                final var list = hand.chars().distinct().map(c -> countChars(hand, (char) c, 0)).mapToObj(Long::valueOf).toList();
                return list.size() == 3 && list.contains(3L);
            }

            if(jokerCount == 1) {
                final var list = hand.chars().filter(c -> c != 'J').distinct().map(c -> countChars(hand, (char) c, 0)).mapToObj(Long::valueOf).toList();
                return list.size() == 3 && list.contains(2L);
            }

            if(jokerCount == 2) {
                return hand.chars().filter(c -> c != 'J').distinct().map(c -> countChars(hand, (char) c, 0)).mapToObj(Long::valueOf).count() == 3;
            }

            return false;
        }

        boolean isTwoPair() {
            return jokerCount() == 0 && hand.chars().distinct().map(c -> countChars(hand, (char) c, 0)).filter(i -> i == 2).count() == 2;
        }

        boolean isOnePair() {
            final var jokerCount = jokerCount();
            if(jokerCount == 0) {
                final var list = hand.chars().distinct().map(c -> countChars(hand, (char) c, 0)).filter(i -> i < 3)
                        .mapToObj(Long::valueOf).toList();
                return list.size() == 4 && list.contains(2L);
            }
            if (jokerCount == 1) {
                return hand.chars().distinct().count() == 5;
            }

            return false;
        }

        boolean isHighCard() {
            return hand.chars().filter(c -> c != 'J').distinct().count() == 5;
        }

        int handType() {

            if(isFiveOfAKind()) {
                return 6;
            }

            if(isFourOfAKind()) {
                return 5;
            }
            if(isFullHouse()) {
                return 4;
            }
            if(isThreeOfAKind()) {
                return 3;
            }
            if(isTwoPair()) {
                return 2;
            }
            if(isOnePair()) {
                return 1;
            }

            if(isHighCard()) {
                return 0;
            }

            throw new IllegalStateException();
        }

        @Override
        public int compareTo(Input2 o) {
            final var handResult = Comparator.comparingInt(Input2::handType).compare(this, o);
            return handResult != 0 ? handResult : compareOrderedHighCardTo(this.hand, o.hand, cardSet);
        }
    }

    record Input(String hand, long bid, String cardSet) implements Comparable<Input> {

        // not my proudest moments here
        boolean isFiveOfAKind() {
            return hand.chars().distinct().count() == 1;
        }

        boolean isFourOfAKind() {
            return hand.chars().map(c -> countChars(hand, (char) c, 0)).max().orElse(0) == 4;
        }

        boolean isFullHouse() {
            return hand.chars().distinct().count() == 2
                    && hand.chars().distinct().map(c -> countChars(hand, (char) c, 0)).max().orElse(0) == 3;
        }

        boolean isThreeOfAKind() {
            final var list = hand.chars().distinct().map(c -> countChars(hand, (char) c, 0)).mapToObj(Long::valueOf).toList();
            return list.size() == 3 && list.contains(3L);
        }

        boolean isTwoPair() {
            return hand.chars().distinct().map(c -> countChars(hand, (char) c, 0)).filter(i -> i == 2).count() == 2;
        }

        boolean isOnePair() {
            final var list = hand.chars().distinct().map(c -> countChars(hand, (char) c, 0)).filter(i -> i < 3).mapToObj(Long::valueOf).toList();
            return list.size() == 4 && list.contains(2L);
        }

        boolean isHighCard() {
            return hand.chars().distinct().count() == 5;
        }

        int handType() {

            if(isFiveOfAKind()) {
                return 6;
            }

            if(isFourOfAKind()) {
                return 5;
            }
            if(isFullHouse()) {
                return 4;
            }
            if(isThreeOfAKind()) {
                return 3;
            }
            if(isTwoPair()) {
                return 2;
            }
            if(isOnePair()) {
                return 1;
            }

            if(isHighCard()) {
                return 0;
            }

            throw new IllegalStateException();
        }

        @Override
        public int compareTo(Input o) {
            final var handResult = Comparator.comparingInt(Input::handType).compare(this, o);
            return handResult != 0 ? handResult : compareOrderedHighCardTo(this.hand, o.hand, cardSet);
        }
    }

    static int compareHighCardTo(String h1, String h2) {
        int c1 = -1;
        int c2 = -1;
        for (int i = 0; i < h1.length(); i++) {
            c1 = Math.max(CARDS.indexOf(h1.charAt(i)), c1);
            c2 = Math.max(CARDS.indexOf(h2.charAt(i)), c2);
        }
        return Integer.compare(c1, c2);
    }

    static int compareOrderedHighCardTo(String h1, String h2, String cardSet) {
        for (int i = 0; i < h1.length(); i++) {
            final var c1 = cardSet.indexOf(h1.charAt(i));
            final var c2 = cardSet.indexOf(h2.charAt(i));
            if(c1 != c2) {
                return Integer.compare(c1, c2);
            }
        }
        return 0;
    }

    static int compareOrderedHighCardTo(String h1, String h2) {
        for (int i = 0; i < h1.length(); i++) {
            final var c1 = CARDS.indexOf(h1.charAt(i));
            final var c2 = CARDS.indexOf(h2.charAt(i));
            if(c1 != c2) {
                return Integer.compare(c1, c2);
            }
        }
        return 0;
    }

    static int countChars(String s, char c, int i) {
        if (i >= s.length()) {
            return 0;
        }
        int count = s.charAt(i) == c ? 1 : 0;

        return count + countChars(s, c, i + 1);
    }

}
