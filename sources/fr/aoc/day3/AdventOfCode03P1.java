package fr.aoc.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public final class AdventOfCode03P1 {

  private static final String EXAMPLE = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))";
  
  public static void main(String[] args) throws IOException {
    String line;
    
    if (args.length == 0) {
      line = EXAMPLE;
    } else if (args.length == 1) {
      line = Files.lines(Path.of(args[0])).findFirst().get();
    } else {
      System.err.println("Usage: java AdventOfCode03P1.java [FILE ?]");
      return;
    }
    var pattern = Pattern.compile("mul\\((\\d+)\\,(\\d+)\\)");
    var matcher = pattern.matcher(line);
    System.out.println(matcher.results().mapToInt(r -> Integer.parseInt(r.group(1))*Integer.parseInt(r.group(2))).sum());
  }
}
