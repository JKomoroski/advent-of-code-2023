import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.LongStream;

public class Day5 {

    static ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);


    public static void main(String[] args) throws Exception {
        final var input = Paths.get("", "inputs", "day-5", "input.txt").toAbsolutePath();
        final var strings = Files.readAllLines(input);

        List<Seed> seeds = Arrays.stream(strings.get(0).split(": ")[1].split(" "))
                .map(Long::parseLong)
                .map(Seed::new)
                .toList();

        TreeSet<AlmanacMapping> seedToSoil = new TreeSet<>();
        TreeSet<AlmanacMapping> soilToFert = new TreeSet<>();
        TreeSet<AlmanacMapping> fertToWater = new TreeSet<>();
        TreeSet<AlmanacMapping> waterToLight = new TreeSet<>();
        TreeSet<AlmanacMapping> lightToTemp = new TreeSet<>();
        TreeSet<AlmanacMapping> tempToHumid = new TreeSet<>();
        TreeSet<AlmanacMapping> humidToLocation = new TreeSet<>();

        Set<AlmanacMapping> updating = null;

        for (String s : strings.subList(1, strings.size())) {
            switch (s) {
                case "seed-to-soil map:":
                    updating = seedToSoil;
                    continue;
                case "soil-to-fertilizer map:":
                    updating = soilToFert;
                    continue;
                case "fertilizer-to-water map:":
                    updating = fertToWater;
                    continue;
                case "water-to-light map:":
                    updating = waterToLight;
                    continue;
                case "light-to-temperature map:":
                    updating = lightToTemp;
                    continue;
                case "temperature-to-humidity map:":
                    updating = tempToHumid;
                    continue;
                case "humidity-to-location map:":
                    updating = humidToLocation;
                    continue;
                case "":
                    updating = null;
                    continue;
            }
            final var mappings = Arrays.stream(s.split(" ")).mapToLong(Long::parseLong).toArray();
            updating.add(new AlmanacMapping(mappings[0], mappings[1], mappings[2]));
        }

        final var lowestLocation = seeds.stream()
                .mapToLong(Seed::id)
                .map(l -> mapData(seedToSoil, l))
                .map(l -> mapData(soilToFert, l))
                .map(l -> mapData(fertToWater, l))
                .map(l -> mapData(waterToLight, l))
                .map(l -> mapData(lightToTemp, l))
                .map(l -> mapData(tempToHumid, l))
                .map(l -> mapData(humidToLocation, l))
                .min()
                .orElseThrow();

        System.out.println("Part 1: " + lowestLocation);

        ArrayList<Future<Long>> futures = new ArrayList<>();

        for (int i = 0; i < seeds.size(); i += 2) {
            final var start = seeds.get(i).id;
            final var limit = seeds.get(i + 1).id;
            // This is a hack, the right solution must be to filter these ranges or order them better.
            final var future = EXECUTOR.submit(() -> LongStream.range(start, start + limit)
                    .map(l -> mapData(seedToSoil, l))
                    .map(l -> mapData(soilToFert, l))
                    .map(l -> mapData(fertToWater, l))
                    .map(l -> mapData(waterToLight, l))
                    .map(l -> mapData(lightToTemp, l))
                    .map(l -> mapData(tempToHumid, l))
                    .map(l -> mapData(humidToLocation, l))
                    .min()
                    .orElseThrow());
            futures.add(future);
        }

        // ugly garbage since we're collecting futures
        long lowestLocation2 = Long.MAX_VALUE;
        for (Future<Long> f : futures) {
            Long newMin = f.get();
            lowestLocation2 = newMin < lowestLocation2 ? newMin : lowestLocation2;
        }
        System.out.println("Part 2: " + lowestLocation2);
        EXECUTOR.close();

    }

    static Long mapData(TreeSet<AlmanacMapping> mappings, Long l) {
        return mappings.stream()
                .filter(m -> m.isMapFor(l))
                .map(m -> m.convert(l))
                .findFirst()
                .orElse(l);
    }

    record Seed(long id) {

    }

    record AlmanacMapping(long destinationStart, long sourceStart, long length) implements Comparable<AlmanacMapping> {

        boolean isMapFor(long l) {
            return l >= sourceStart && l < sourceStart + length;
        }

        long convert(long l) {
            return (l - sourceStart) + destinationStart;
        }

        @Override
        public int compareTo(AlmanacMapping m) {
            return Comparator.comparing(AlmanacMapping::sourceStart).compare(this, m);
        }
    }

}
