package fr.aoc.day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AdventOfCode19P2 {
    
  private static final String EXAMPLE = """
            r, wr, b, g, bwu, rb, gb, br

            brwrr
            bggr
            gbbr
            rrbgbr
            ubwu
            bwurrg
            brgr
            bbrgwb
            """;
    
    public static void main(String[] args) throws IOException {
      List<String> lines;
  
      if (args.length == 0) {
        lines = EXAMPLE.strip().lines().toList();
      } else if (args.length == 1) {
        lines = Files.lines(Path.of(args[0])).toList();
      } else {
        System.err.println("Usage: java AdventOfCode19P2.java [FILE ?]");
        return;
      }
     
      Map<Character, List<String>> patterns = Arrays.stream(lines.getFirst().split(", "))
          .collect(Collectors.groupingBy(s -> s.charAt(0)));
      List<String> words = lines.subList(2, lines.size());
      long acc = 0;
     
      for (var word : words) {
        var dp = new long[word.length() + 1];
        dp[0] = 1;
        for (int i = 0; i < word.length(); i++) {
          for (var pattern : patterns.getOrDefault(word.charAt(i), List.of())) {
            int patternLength = pattern.length();
            if (i + patternLength <= word.length() && word.startsWith(pattern, i)) {
              dp[i + patternLength] += dp[i];
            }
          }
        }
        acc += dp[word.length()];
      }
      System.out.println(acc);
    }
}

