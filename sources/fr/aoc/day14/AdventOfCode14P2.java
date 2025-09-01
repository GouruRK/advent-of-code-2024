package fr.aoc.day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public final class AdventOfCode14P2 {

  private static int WIDTH = 11;
  private static int HEIGHT = 7;
  
  private static final String EXAMPLE = """
          p=0,4 v=3,-3
          p=6,3 v=-1,-3
          p=10,3 v=-1,2
          p=2,0 v=2,-1
          p=0,0 v=1,3
          p=3,0 v=-2,-2
          p=7,6 v=-1,-3
          p=3,0 v=-1,-2
          p=9,3 v=2,3
          p=7,3 v=-1,2
          p=2,4 v=2,-3
          p=9,5 v=-3,-3
          """;
  
  static record Coord(int x, int y) {}
  
  static class Robot {
    private Coord position;
    private final Coord velocity;
    
    public Robot(Coord position, Coord velocity) {
      this.position = position;
      this.velocity = velocity;
    }
    
    public Robot(int ax, int ay, int bx, int by) {
      this(new Coord(ax, ay), new Coord(bx, by));
    }
    
    public void move() {
      this.position = new Coord(truemod(position.x + velocity.x, WIDTH),
                                truemod(position.y + velocity.y, HEIGHT));
    }
  }
  
  private static int truemod(int n, int m) {
    return (((n % m) + m) % m);
  }
  
  
  public static void main(String[] args) throws IOException {
    Stream<String> lines;

    if (args.length == 0) {
      lines = Arrays.stream(EXAMPLE.strip().split("\n"));
    } else if (args.length == 1) {
      lines = Files.lines(Path.of(args[0]));
      WIDTH = 101;
      HEIGHT = 103;
    } else {
      System.err.println("Usage: java AdventOfCode14P2.java [FILE ?]");
      return;
    }
    
    var pattern = Pattern.compile("([-]?\\d+)");
    var robots = lines.flatMap(line -> pattern.matcher(line).results().map(r -> Integer.parseInt(r.group())))
       .gather(Gatherers.windowFixed(4))
       .map(nums -> new Robot(nums.get(0), nums.get(1), nums.get(2), nums.get(3)))
       .toList();
    
    var coords = new HashSet<Coord>();
    var acc = 0;
    do {
      coords.clear();
      robots.stream().peek(Robot::move).forEach(r -> coords.add(r.position));
      acc++;
    } while(coords.size() != robots.size());
    System.out.println(acc);
  }
  
}
