package fr.aoc.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class AdventOfCode01P2 {
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
      System.err.println("Usage: java AdventOfCode01P2.java [FILE ?]");
      return;
    }
    
    var occurencies = lines.stream()
        .map(tuple -> Long.parseLong(tuple[1]))
        .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
    
    System.out.println(lines.stream()
        .mapToLong(tuple -> Long.parseLong(tuple[0]))
        .map(x -> x*occurencies.getOrDefault(x, 0L))
        .sum());
  }
}
