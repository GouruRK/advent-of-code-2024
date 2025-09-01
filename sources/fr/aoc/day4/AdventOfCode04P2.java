package fr.aoc.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AdventOfCode04P2 {

  private static final String EXAMPLE = """
      MMMSXXMASM
      MSAMXMSMSA
      AMXSXMAAMM
      MSAMASMSMX
      XMASAMXAMM
      XXAMMXXAMA
      SMSMSASXSS
      SAXAMASAAA
      MAMMMXMMMM
      MXMXAXMASX
      """;

  private static List<String> PATTERNS = List.of("MMSS", "SSMM", "SMSM", "MSMS");
  
  private static int[][] COORDS = {
      {-1, -1},
      {1, -1},
      {-1, 1},
      {+1, +1}
  };
  
  private static boolean isPatternPresent(int x, int y, List<String> lines) {
    var motif = IntStream.range(0, COORDS.length)
        .mapToObj(i -> "" + lines.get(y + COORDS[i][1])
        .charAt(x + COORDS[i][0])).collect(Collectors.joining(""));
    return PATTERNS.contains(motif);
  }
  
  public static void main(String[] args) throws IOException {
    List<String> lines;
    
    if (args.length == 0) {
      lines = EXAMPLE.strip().lines().toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode04P2.java [FILE ?]");
      return;
    }
    
    int height = lines.size();
    int width = lines.getFirst().length();
    
    int res = 0;
    for (int y = 1; y < height - 1; y++) {
      for (int x = 1; x < width - 1; x++) {
        if (lines.get(y).charAt(x) == 'A' && isPatternPresent(x, y, lines)) {
          res++;
        }
      }
    }
    System.out.println(res);
  }
  
}
