package fr.aoc.day25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public final class AdventOfCode25P1 {
  private static final String EXAMPLE = """
      #####
      .####
      .####
      .####
      .#.#.
      .#...
      .....
      
      #####
      ##.##
      .#.##
      ...##
      ...#.
      ...#.
      .....
      
      .....
      #....
      #....
      #...#
      #.#.#
      #.###
      #####
      
      .....
      .....
      #.#..
      ###..
      ###.#
      ###.#
      #####
      
      .....
      .....
      .....
      #....
      #.#..
      #.#.#
      #####
      """;
  
  
  private static int[] transform(List<char[]> input) {
    var res = new int[input.get(0).length];
    for (int x = 0; x < res.length; x++) {
      for (int y = 0; y < input.size(); y++) {
        if (input.get(y)[x] == '#') {
          res[x]++;          
        }
      }
      res[x]--;
    }
    return res;
  }
  
  
  private static boolean fit(int[] key, int[] lock) {
    for (int i = 0; i < key.length; i++) {
      if (key[i] + lock[i] > 5) {
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
      System.err.println("Usage: java AdventOfCode25P1.java [FILE ?]");
      return;
    }
    
    var keys = new ArrayList<int[]>();
    var locks = new ArrayList<int[]>();
    
    lines.filter(line -> !line.isEmpty())
    .map(String::toCharArray)
    .gather(Gatherers.windowFixed(7))
    .forEach(grid -> {
      if (grid.get(0)[0] == '.') {
        keys.add(transform(grid));
      } else {
        locks.add(transform(grid));
      }
    });
    
    
    System.out.println(locks.stream().mapToLong(lock -> keys.stream().filter(key -> fit(key, lock)).count()).sum());
  }
}
