package fr.aoc.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class AdventOfCode02P1 {

  private static final String EXAMPLE = """
      7 6 4 2 1
      1 2 7 8 9
      9 7 6 2 1
      1 3 2 4 5
      8 6 4 4 1
      1 3 6 7 9
      """;

  private static final boolean isLineSafe(List<Integer> line) {
    boolean isDecreasing = line.getFirst() - line.get(1) > 0;
    for (int i = 0; i < line.size() - 1; i++) {
      int r = line.get(i) - line.get(i + 1);
      if (((r >= 0 && !isDecreasing) || (r <= 0 && isDecreasing)) || Math.abs(r) > 3) {
        return false;
      }
    }
    return true;
  }
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;
    
    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n"));
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode02P1.java [FILE ?]");
      return;
    }
    System.out.println(lines.map(line -> List.of(line.split(" +")).stream().map(Integer::parseInt).toList()).filter(AdventOfCode02P1::isLineSafe).count());
  }
  
}
