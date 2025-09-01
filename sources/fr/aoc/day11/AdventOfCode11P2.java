package fr.aoc.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class AdventOfCode11P2 {

  private static final int BLINK_LIMIT = 75;
  private static final String EXAMPLE = "125 17";
  
  public static void main(String[] args) throws IOException {
    String line;
    
    if (args.length == 0) {
      line = EXAMPLE;
    } else if (args.length == 1) {
      line = Files.lines(Path.of(args[0])).findFirst().get();
    } else {
      System.err.println("Usage: java AdventOfCode11P2.java [FILE ?]");
      return;
    }
    var stones = Arrays.stream(line.split(" ")).map(Long::parseLong).collect(Collectors.toMap(Function.identity(), _ -> 1L));
    for (int i = 0; i < BLINK_LIMIT; i++) {
      var newStones = new HashMap<Long, Long>();
      for (var set: stones.entrySet()) {
        var stone = set.getKey();
        long[] tempStoneCollection;
        if (stone == 0) {
          tempStoneCollection = new long[] { 1 };
        } else if (("" + stone).length() % 2 == 1) {
          tempStoneCollection = new long[] { stone*2024 };
        } else {
          var s = "" + stone;
          tempStoneCollection = new long[] { Long.parseLong(s.subSequence(0, s.length() / 2).toString()),
                                             Long.parseLong(s.subSequence(s.length() / 2, s.length()).toString()) 
                                           };
        }
        for (var tempStone: tempStoneCollection) {
          newStones.merge(tempStone, set.getValue(), Long::sum);
        }
      }
      stones = newStones;
    }
    System.out.println(stones.values().stream().mapToLong(x -> x).sum());
  }
  
  
}