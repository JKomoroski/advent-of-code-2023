import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public abstract class AOCBase {

    private final String folder;
    private final String fileName;

    protected Instant start1 = null;
    protected Instant end1 = null;
    protected Instant start2 = null;
    protected Instant end2 = null;

    protected AOCBase(String folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }


    void run() throws Exception {
        final var input = Paths.get("","inputs", folder, fileName).toAbsolutePath();
        runPart1(input);
        runPart2(input);
        System.out.println("Total Time Elapsed: " + Duration.between(start1, end2).toMillis() + "ms");

    }

    private static LocalDateTime fromInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS);
    }

    private void runPart1(Path input) throws Exception {
        start1 = Instant.now();
        System.out.println("Starting Part 1 @ " + fromInstant(start1));
        part1(Files.lines(input));
        end1 = Instant.now();
        System.out.println("Finished Part 1 @ " + fromInstant(end1));
        System.out.println("Finished Part 1 in " + Duration.between(start1, end1).toMillis() + "ms");
    }

    private void runPart2(Path input) throws Exception {
        start2 = Instant.now();
        System.out.println("Starting Part 2 @ " + fromInstant(start2));
        part2(Files.lines(input));
        end2 = Instant.now();
        System.out.println("Finished Part 2 @ " + fromInstant(end2));
        System.out.println("Finished Part 2 in " + Duration.between(start2, end2).toMillis() + "ms");
    }


    abstract void part1(Stream<String> fileInput) throws Exception;
    abstract void part2(Stream<String> fileInput) throws Exception;

}
