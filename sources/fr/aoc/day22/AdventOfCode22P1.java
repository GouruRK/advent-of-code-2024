package fr.aoc.day22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;


public final class AdventOfCode22P1 {
  private static final String EXAMPLE = """
      1
      10
      100
      2024
      """;

  private static long prune(long n) {
    return n % 16777216;
  }
  
  private static long mix(long n, long other) {
    return n ^ other;
  }
  
  private static long transform(long n) {
    for (int i = 0; i < 2000; i++) {
      n = prune(mix(n*64, n));
      n = prune(mix(n/32, n));
      n = prune(mix(n*2048, n));
    }
    return n;
  }
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;
    
    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n"));
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode22P1.java [FILE ?]");
      return;
    }
    
    System.out.println(lines.mapToLong(Long::parseLong).map(AdventOfCode22P1::transform).sum());
  }
}
