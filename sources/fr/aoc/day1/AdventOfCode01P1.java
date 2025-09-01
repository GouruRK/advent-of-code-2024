package fr.aoc.day1;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public final class AdventOfCode01P1 {

  private static final String EXAMPLE = """
          3   4
          4   3
          2   5
          1   3
          3   9
          3   3
          """;
  
  public static void main(String[] args) throws IOException {
    List<String[]> lines;
    
    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n")).map(line -> line.split(" +")).toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).map(line -> line.split(" +")).toList();
    } else {
      System.err.println("Usage: java AdventOfCode01P1.java [FILE ?]");
      return;
    }
    
    var left = lines.stream()
        .mapToInt(tuple -> Integer.parseInt(tuple[0]))
        .sorted()
        .toArray();
    var right = lines.stream()
        .mapToInt(tuple -> Integer.parseInt(tuple[1]))
        .sorted()
        .toArray();
    
    System.out.println(IntStream.range(0, lines.size()).map(i -> Math.abs(left[i] - right[i])).sum());
  }
  
}
