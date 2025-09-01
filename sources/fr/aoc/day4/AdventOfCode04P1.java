package fr.aoc.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class AdventOfCode04P1 {

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
  
  private static final int[][] DIRECTIONS = {
      {  1,  0 },  // to the right
      { -1,  0 },  // to the left
      {  0,  1 },  // upward
      {  0, -1 },  // downard
      { -1, -1 },  // upleft diagonal
      { -1,  1 },  // downleft diagonal
      {  1, -1 },  // upright diagonal
      {  1,  1 }   // down right diagonal
  };
  
  private static final String TARGET = "XMAS";

  private static boolean isInGrid(int x, int y, int width, int height) {
    return (0 <= x && x < width) && (0 <= y && y < height); 
  }
  
  private static int countPatternPresence(int sx, int sy, List<String> lines, int width, int height) {
    int res = 0;
    for (int i = 0; i < DIRECTIONS.length; i++) {
      int x = sx,
          y = sy,
          j = 0;
      while (isInGrid(x, y, width, height) && j < TARGET.length() && TARGET.charAt(j) == lines.get(y).charAt(x)) {
        x += DIRECTIONS[i][0];
        y += DIRECTIONS[i][1];
        j++;
      }
      if (j == TARGET.length()) {
        res++;
      }
    }
    return res;
  }
  
  public static void main(String[] args) throws IOException {
    List<String> lines;
    
    if (args.length == 0) {
      lines = EXAMPLE.strip().lines().toList();
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0])).toList();
    } else {
      System.err.println("Usage: java AdventOfCode04P1.java [FILE ?]");
      return;
    }
    
    int height = lines.size();
    int width = lines.getFirst().length();
    
    int res = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (lines.get(y).charAt(x) == TARGET.charAt(0)) {
          res += countPatternPresence(x, y, lines, width, height);
        }
      }
    }
    System.out.println(res);
  }
  
}
