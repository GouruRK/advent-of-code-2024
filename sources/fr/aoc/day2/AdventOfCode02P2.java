package fr.aoc.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class AdventOfCode02P2 {

  private static final String EXAMPLE = """
      7 6 4 2 1
      1 2 7 8 9
      9 7 6 2 1
      1 3 2 4 5
      8 6 4 4 1
      1 3 6 7 9
      1 2 3 4 3
      4 5 4 3 2
      """;
  
  private static final boolean isLineSafe(List<Integer> line) {
    return isLineSafe(line, true);
  }
  
  private static final boolean isLineSafe(List<Integer> line, boolean firstTime) {
    boolean isDecreasing = line.getFirst() - line.get(1) > 0;
    for (int i = 0; i < line.size() - 1; i++) {
      int r = line.get(i) - line.get(i + 1);
      if (((r >= 0 && !isDecreasing) || (r <= 0 && isDecreasing)) || Math.abs(r) > 3) {
        if (firstTime) {
          var tempA = new ArrayList<Integer>();
          tempA.addAll(line.subList(0, i));
          tempA.addAll(line.subList(i + 1, line.size()));

          var tempB = new ArrayList<Integer>();
          tempB.addAll(line.subList(0, i + 1));
          tempB.addAll(line.subList(i + 2, line.size()));
          return isLineSafe(tempA, false) || isLineSafe(tempB, false);
        }
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
    
    System.out.println(lines.map(line -> List.of(line.split(" +")).stream().map(Integer::parseInt).toList())
        .filter(line -> isLineSafe(line) || isLineSafe(line.subList(1, line.size()), false) || isLineSafe(line.subList(0, line.size() - 1), false)).count());
  }
  
}
