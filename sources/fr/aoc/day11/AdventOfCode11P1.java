package fr.aoc.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public final class AdventOfCode11P1 {

  private static final String EXAMPLE = "125 17";
  private static final int BLINK_LIMIT = 25;
  
  public static void main(String[] args) throws IOException {
    String line;
    
    if (args.length == 0) {
      line = EXAMPLE;
    } else if (args.length == 1) {
      line = Files.lines(Path.of(args[0])).findFirst().get();
    } else {
      System.err.println("Usage: java AdventOfCode11P1.java [FILE ?]");
      return;
    }
    var stones = Arrays.stream(line.split(" ")).map(Long::parseLong).toList();
    for (int i = 0; i < BLINK_LIMIT; i++) {
      var newStones = new ArrayList<Long>();
      for (var stone: stones) {
        if (stone == 0) {
          newStones.add(1L);
        } else if (("" + stone).length() % 2 == 1) {
          newStones.add(stone*2024);
        } else {
          var n = "" + stone;
          newStones.add(Long.parseLong(n.subSequence(0, n.length() / 2).toString()));
          newStones.add(Long.parseLong(n.subSequence(n.length() / 2, n.length()).toString()));
        }
        stones = newStones;
      }
    }
    System.out.println(stones.size());
  }
  
  
}