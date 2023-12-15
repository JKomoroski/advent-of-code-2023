import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day12 extends AOCBase {

    protected Day12(String folder, String fileName) {
        super(folder, fileName);
    }

    public static void main(String[] args) throws Exception {
        new Day12("day-12", "input.txt").run();
    }

    @Override
    void part1(Stream<String> fileInput) throws Exception {
        final var sum = fileInput.map(Day12::fromString)
                .mapToLong(Condition::computeArrangements)
                .sum();

        System.out.println("Part 1: " + sum);

    }

    @Override
    void part2(Stream<String> fileInput) throws Exception {
        final var sum = fileInput.map(Day12::fromString2)
                .mapToLong(Condition::computeArrangements)
                .sum();

        System.out.println("Part 2: " + sum);
    }

    static Condition fromString2(String s) {
        final var s1 = s.split(" ");
        final var conditions = IntStream.range(0, 5).mapToObj(i -> s1[0]).collect(Collectors.joining("?"));
        final var g = Arrays.stream(s1[1].split(",")).map(Integer::parseInt).toList();
        final var groups = IntStream.range(0, 5).mapToObj(i -> g).flatMap(List::stream).mapToInt(Integer::intValue).toArray();
        return new Condition(conditions, groups);
    }

    static Condition fromString(String s) {
        final var s1 = s.split(" ");
        final var groups = Arrays.stream(s1[1].split(",")).mapToInt(Integer::parseInt).toArray();
        return new Condition(s1[0], groups);
    }

    record Condition(String conditions, int[] groups) {

        //https://www.reddit.com/r/adventofcode/comments/18ge41g/comment/kd18cl9
        long computeArrangements() {
            final var computeConditions = "." + conditions + ".";
            final List<Boolean> springs = Arrays.stream(groups)
                    .mapToObj(i -> IntStream.range(0, i).mapToObj(j -> Boolean.TRUE).collect(Collectors.toList()))
                    .peek(l -> l.addFirst(Boolean.FALSE))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            springs.addLast(Boolean.FALSE);

            int n = computeConditions.length();
            int m = springs.size();
            long[][] dp = new long[n + 1][m + 1];
            dp[n][m] = 1;

            for (int i = n - 1; i >= 0; i--) {
                for (int j = m - 1; j >= 0; j--) {
                    final var toCheck = computeConditions.charAt(i);
                    long sum = 0;
                    if((isUnknown(toCheck) || isDamaged(toCheck)) && springs.get(j)){
                        sum += dp[i+1][j+1];
                    } else if ((isUnknown(toCheck) || isWorking(toCheck)) && !springs.get(j)) {
                        sum += dp[i+1][j+1] + dp[i+1][j];
                    }
                    dp[i][j] = sum;
                }
            }
            return dp[0][0];
        }
    }

    static boolean isKnown(char c) {
        return !isUnknown(c);
    }

    static boolean isUnknown(char c) {
        return c == '?';
    }

    static boolean isDamaged(char c) {
        return c == '#';
    }

    static boolean isWorking(char c) {
        return c == '.';
    }
}

