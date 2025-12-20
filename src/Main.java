import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static final String DEFAULT_OUTPUT_NAME = "out.asm";
    static final int MAX_NEXTED_STATEMENTS = 30;

    static Stack<String> statement_stack = new Stack<>(MAX_NEXTED_STATEMENTS);
    static int if_count = 0;
    static int loop_count = 0;
    static int line_count = 0;

    public static String negative_comparator(String comparator) throws Exception{
        return switch (comparator) {
            case "<=" -> "g";
            case ">=" -> "l";
            case "<" -> "ge";
            case ">" -> "le";
            case "==" -> "ne";
            case "!=" -> "e";
            default -> throw new Exception("Invalid comparator");
        };
    }

    public static String[] strip_whitespace(String[] str){
        return Arrays.stream(str).filter(x -> !x.isEmpty()).toArray(String[]::new);
    }

    public static void handle_push_err(String push_str, int num, String line){
        try{statement_stack.push(push_str + num);}catch(Exception e){
            System.out.println("Error at line " + line_count + ":\n" + line+"\nExceeded Maximum Nested Statements");
            System.exit(-1);
        }
    }

    public static String handle_opposite_err(String original, String line){
        String opposite = null;
        try{opposite = negative_comparator(original);}catch(Exception e){
            System.out.println("Error at line " + line_count + ":\n" + line+"\nInvalid Comparator");
            System.exit(-1);
        }
        return opposite;
    }

    public static String handle_new_if(String[] array, String line){
        handle_push_err("if_", if_count, line);
        String opposite = handle_opposite_err(array[2], line);
        return String.format("cmp %s, %s\nj%s if_%d", array[1], array[3], opposite, if_count);
    }

    public static String handle_new_loop(String[] array, String line){
        handle_push_err("loop_end_", loop_count, line);
        String opposite = handle_opposite_err(array[2], line);
        return String.format("loop_%d:\ncmp %s, %s\nj%s loop_end_%d", loop_count, array[1], array[3], opposite, loop_count);
    }

    public static String determine_line(String line){
        String[] split_line = line.trim().split("[ (),]");
        split_line = strip_whitespace(split_line);
        String new_line;
        if(line.trim().isEmpty()) return "\n";
        if(line.trim().toCharArray()[0] == '#')
            new_line = "; "+split_line[1];
        else if(split_line[0].equals("if")) {
            new_line = handle_new_if(split_line, line);
            if_count++;
        }
        else if(split_line[0].equals("loop")) {
            new_line = handle_new_loop(split_line, line);
            loop_count++;
        }
        else if(split_line[0].equals("}")){
            String statement = statement_stack.pop();
            new_line = statement + ":";
            String[] split = statement.split("_");
            if(split[0].equals("loop"))
                new_line = "jmp loop_" + split[2] + "\n" + new_line;
        }
        else if(split_line[0].equals("goto"))
            new_line = "jmp " + split_line[1];
        else if(split_line[1].equals("="))
            new_line = String.format("mov %s, %s", split_line[0], split_line[2]);
        else if(split_line[0].equals("segment"))
            new_line = String.format("%s:", split_line[1]);
        else if(split_line[1].equals("+"))
            new_line = String.format("add %s, %s", split_line[0], split_line[2]);
        else if(split_line[1].equals("-"))
            new_line = String.format("sub %s, %s", split_line[0], split_line[2]);
        else if(split_line[0].equals("*"))
            new_line = String.format("mul %s", split_line[1]);
        else if(split_line[0].equals("/"))
            new_line = String.format("div %s", split_line[1]);
        else{
            new_line = line.trim();
            System.out.println("Unsafe Line at " + line_count + ":\n" + line+"\n");
        }
        new_line += "\n";
        return new_line;
    }

    public static void main(String[] args) throws Exception {
        StringBuilder file_contents = new StringBuilder();
        if(args.length < 1){
            System.out.println("Need at least 1 argument.");
            System.exit(-1);
        }
        String output_name = DEFAULT_OUTPUT_NAME;
        if(args.length > 1) output_name = args[1];
        File input_file = new File(args[0]);
        File output_file = new File(output_name);
        if(input_file.exists()) output_file.delete();
        output_file.createNewFile();
        try(Scanner input = new Scanner(input_file)){
            while(input.hasNextLine()){
                String line = input.nextLine();
                line_count++;
                file_contents.append(determine_line(line));
            }
        }
        FileWriter file_writer = new FileWriter(output_file);
        file_writer.write(file_contents.toString());
        file_writer.close();
    }

}
