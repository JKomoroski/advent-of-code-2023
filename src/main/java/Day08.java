import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Day08 {

    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "inputs", "day-8", "input.txt").toAbsolutePath();
        final var strings = Files.readAllLines(input);
        final var nodeMap = strings.stream().skip(2)
                .map(Day08::fromLine)
                .collect(Collectors.toMap(Node::value, Function.identity()));

        final var directions = strings.get(0).toCharArray();

        long steps = countSteps(nodeMap.get("AAA"), s -> s.equals("ZZZ"), nodeMap, directions);

        System.out.println("Part 1: " + steps);

        final var sortedCounts = nodeMap.keySet()
                .stream()
                .filter(s -> s.endsWith("A"))
                .mapToLong(k -> countSteps(nodeMap.get(k), s -> s.endsWith("Z"), nodeMap, directions))
                .sorted()
                .toArray();

        //init as first
        long totalCounts = sortedCounts[0];
        // start on second
        for (int i = 1; i < sortedCounts.length; i++) {
            totalCounts = lcm(totalCounts, sortedCounts[i]);
        }

        // Brute force == forever
//        long count2 = 0;
//        List<Entry> currentNodes = starts.stream().map(nodeMap::get).toList();
//        do {
//            for (int i = 0; i < directions.length; i++) {
//                final int finalI = i;
//                currentNodes = currentNodes.stream()
//                        .map(c -> nodeMap.get(next(directions[finalI], c)))
//                        .collect(Collectors.toList());
//                count++;
//                if(currentNodes.stream().map(Entry::value).allMatch(s -> s.charAt(2) == 'Z')) {
//                    break;
//                }
//            }
//        } while (!currentNodes.stream().map(Entry::value).allMatch(s -> s.charAt(2) == 'Z'));



        System.out.println("Part 2: " + totalCounts);

    }

    // https://www.baeldung.com/java-least-common-multiple#1-lcm-of-two-numbers
    public static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0)
            return 0;
        else {
            long gcd = gcd(number1, number2);
            return Math.abs(number1 * number2) / gcd;
        }
    }

    public static long gcd(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            long absNumber1 = Math.abs(number1);
            long absNumber2 = Math.abs(number2);
            long biggerValue = Math.max(absNumber1, absNumber2);
            long smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    static long countSteps(Node start, Predicate<String> isDestination, Map<String, Node> nodeMap, char[] directions) {
        long count = 0;
        Node current = start;
        do {
            for (int i = 0; i < directions.length; i++) {
                current = nodeMap.get(next(directions[i], current));
                count++;
                if(isDestination.test(current.value)) {
                    break;
                }
            }
        } while (!isDestination.test(current.value));
        return count;
    }

    static String next(char direction, Node current) {
        if (direction == 'R') {
            return current.right;
        }

        if (direction == 'L') {
            return current.left;
        }

        throw new IllegalArgumentException();
    }

    static Node fromLine(String s) {
        final var k = s.split(" = ")[0];
        final var node = s.split(" = ")[1].split(", ");
        final var l = node[0].replace("(", "");
        final var r = node[1].replace(")", "");
        return new Node(k, l, r);

    }

    record Node(String value, String left, String right){}
}
