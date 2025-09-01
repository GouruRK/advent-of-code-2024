package fr.aoc.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public final class AdventOfCode13P1 {

  private static final String EXAMPLE = """
      Button A: X+94, Y+34
      Button B: X+22, Y+67
      Prize: X=8400, Y=5400

      Button A: X+26, Y+66
      Button B: X+67, Y+21
      Prize: X=12748, Y=12176

      Button A: X+17, Y+86
      Button B: X+84, Y+37
      Prize: X=7870, Y=6450
      
      Button A: X+69, Y+23
      Button B: X+27, Y+71
      Prize: X=18641, Y=10279
            """;

  public static void main(String[] args) throws IOException {
    Stream<String> lines;

    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n"));
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
    } else {
      System.err.println("Usage: java AdventOfCode13P1.java [FILE ?]");
      return;
    }

    var intPattern = Pattern.compile("(\\d+)");
    
    var res = lines.filter(line -> !line.isEmpty())
      .flatMap(line -> intPattern.matcher(line).results().map(r -> Integer.parseInt(r.group())))
      .gather(Gatherers.windowFixed(6))
      .mapToInt(obj -> {
        int a1 = obj.get(0), b1 = obj.get(2), a2 = obj.get(1), b2 = obj.get(3), c1 = obj.get(4), c2 = obj.get(5);  
        
        var determinant = a1 * b2 - a2 * b1;
        if (determinant == 0) {
          return 0;
        }
        var x = (b2 * c1 - b1 * c2) / (double) determinant;
        var y = (a1 * c2 - a2 * c1) / (double) determinant;
        var ix = (int) x;
        var iy = (int) y;
        if (x == ix && y == iy && ix >= 0 && iy >=0) {
          return ix*3 + iy;
        }
        return 0;
      }).sum();
    System.out.println(res);
  }

}
