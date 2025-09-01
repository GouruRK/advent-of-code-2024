package fr.aoc.day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

public final class AdventOfCode07P1 {

  private static final String EXAMPLE = """
      190: 10 19
      3267: 81 40 27
      83: 17 5
      156: 15 6
      7290: 6 8 6 15
      161011: 16 10 13
      192: 17 8 14
      21037: 9 7 18 13
      292: 11 6 16 20
      """;
    
  private record Pair (long target, long[] ints) {}
  
  private static boolean isTargetReachable(Pair pair, long current, int i) {
    if (i == pair.ints.length) {
      return current == pair.target;
    }
    if (current > pair.target) {
      return false;
    }
    return isTargetReachable(pair, current*pair.ints[i], i + 1) || isTargetReachable(pair, current + pair.ints[i], i + 1); 
  }
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;
    
    if (args.length == 0) {
      lines = EXAMPLE.strip().lines();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode07P1.java [FILE ?]");
      return;
    }
    
    System.out.println(lines.map(line -> line.split(": "))
                 .map(t -> new Pair(Long.parseLong(t[0]), Arrays.stream(t[1].split(" "))
                     .mapToLong(Long::parseLong)
                     .toArray()))
                 .filter(p -> isTargetReachable(p, 0, 0))
                 .mapToLong(Pair::target)
                 .sum());
  }
  
}
