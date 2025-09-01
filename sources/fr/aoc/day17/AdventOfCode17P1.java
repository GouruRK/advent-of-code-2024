package fr.aoc.day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class AdventOfCode17P1 {

  private static final String EXAMPLE = """
          Register A: 729
          Register B: 0
          Register C: 0
          
          Program: 0,1,5,4,3,0
          """;
  
//  private static final String EXAMPLE = """
//      Register A: 2024
//      Register B: 0
//      Register C: 0
//      
//      Program: 0,1,5,4,3,0
//      """;
//  
  
  
  private static int getComboValue(int[] registers, int litteral) {
    return 3 < litteral ? registers[litteral - 4] : litteral;
  }
  
  
  private static int update(int[] registers, int[] program, ArrayList<Integer> res, int pointer) {
    return switch (program[pointer]) {
      case 0 -> { // adv
        registers[0] /= 1 << getComboValue(registers, program[pointer + 1]);
        yield pointer + 2;
      }
      case 1 -> { // bxl
        registers[1] ^= program[pointer + 1];
        yield pointer + 2;
      }
      case 2 -> { // bst
        registers[1] = getComboValue(registers, program[pointer + 1]) % 8;
        yield pointer + 2;
      }
      case 3 -> { // jnz
        if (registers[0] != 0) {
          yield program[pointer + 1];
        }
        yield pointer + 2;
      }
      case 4 -> { // bxc
        registers[1] = registers[1] ^ registers[2];
        yield pointer + 2;
      }
      case 5 -> { // out
        res.add(getComboValue(registers, program[pointer + 1]) % 8);
        yield pointer + 2;
      }
      case 6 -> { // bdv
        registers[1] = registers[0] / (1 << getComboValue(registers, program[pointer + 1]));
        yield pointer + 2;
      }
      case 7 -> { // cdv
        registers[2] = registers[0] / (1 << getComboValue(registers, program[pointer + 1]));
        yield pointer + 2;
      }
      default -> throw new IllegalStateException(); 
    };
  }
  
  private static List<Integer> run(int[] registers, int[] program) {
    int pointer = 0;
    var res = new ArrayList<Integer>();
    
    while (pointer < program.length) {
      pointer = update(registers, program, res, pointer);
      System.out.println(pointer);
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
      System.err.println("Usage: java AdventOfCode17P1.java [FILE ?]");
      return;
    }
    
    var pattern = Pattern.compile("(\\d+)");
    var registers = lines.stream().limit(3).flatMapToInt(line -> pattern.matcher(line).results().mapToInt(r -> Integer.parseInt(r.group()))).toArray();
    var program = lines.getLast().chars().filter(i -> '0' <= i && i < '8').map(i -> i - '0').toArray();
    System.out.println(Arrays.toString(registers));
    System.out.println(run(registers, program).stream().map(String::valueOf).collect(Collectors.joining(",")));
    System.out.println(Arrays.toString(registers));
  }
  
}
