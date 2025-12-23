import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static final String DEFAULT_OUTPUT_NAME = "out.asm";
    static final int MAX_NEXTED_STATEMENTS = 30;
    static final String AS_IS_KEYWORDS = "inc,dec,db,dw,dd,dq,section,align,alignb,struc,endstruc,istruc,at,iend,dt,ddq,do,resb," +
            "resw,resd,resq,rest,resdq,reso,times,equ,global,extern,common,cpu,bits,org,%define,%macro,%endmacro,%ifdef,%ifndef" +
            "%else,%endif,%include,%assign,%strlen,%substr,byte,word,dword,ptr,strict,short,near,far,nosplit,rel,abs,seg,wrt$$";

    static boolean DISABLE_WARNINGS = false;
    static boolean DISABLE_UNSAFE = false;

    static Dictionary<String, String[]> functions_dict = new Dictionary<>();
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

    public static String handle_unsafe(String line){
        if(DISABLE_UNSAFE){
            System.out.printf("Unsafe lines are not allowed.\nUnsafe line detected at %d:\n%s\n", line_count, line);
            System.exit(1);
        }
        if(!DISABLE_WARNINGS)
            System.out.println("Unsafe Line at " + line_count + ":\n" + line+"\n");
        return line.trim();
    }

    public static void handle_push_err(String push_str, String line){
        try{statement_stack.push(push_str);}catch(Exception e){
            System.out.printf("Error at line %d:\n%s\nExceeded Maximum Nested Statements\n", line_count, line);
            System.exit(1);
        }
    }

    public static String handle_opposite_err(String original, String line){
        String opposite = null;
        try{opposite = negative_comparator(original);}catch(Exception e){
            System.out.printf("Error at line %d:\n%s\nInvalid Comparator\n", line_count, line);
            System.exit(1);
        }
        return opposite;
    }

    public static String handle_new_if(String[] array, String line){
        if_count++;
        handle_push_err("if_" + if_count, line);
        String opposite = handle_opposite_err(array[2], line);
        return String.format("cmp %s, %s\nj%s if_%d", array[1], array[3], opposite, if_count);
    }

    public static String handle_new_loop(String[] array, String line){
        loop_count++;
        handle_push_err("loop_end_" + loop_count, line);
        String opposite = handle_opposite_err(array[2], line);
        return String.format("loop_%d:\ncmp %s, %s\nj%s loop_end_%d", loop_count, array[1], array[3], opposite, loop_count);
    }

    public static String handle_function_def(String[] array, String line){
        String name = array[1];
        String[] args = Arrays.copyOfRange(array, 2, array.length);
        functions_dict.add(name, args);
        handle_push_err(String.format("ret\n%s_exit", name), line);
        return String.format("jmp %s_exit\n%s:", name, name);
    }

    public static String handle_push_pop(String[] array, String line){
        StringBuilder new_line = new StringBuilder();
        String operation = array[0];
        for (int i = 1; i < array.length; i++){
            if(!array[i].matches(".*[a-z][ipx]") && !array[i].equals("all")){
                System.out.printf("Encountered an invalid register at %d:\n %s%n\n", line_count, line);
                System.exit(1);
            }
            if(array[i].equals("all")){
                new_line.append(operation).append("a\n");
                continue;
            }
            new_line.append(String.format("%s %s\n", operation, array[i]));
        }
        return new_line.toString().replaceAll("\n$", "");
    }

    public static String call_function(String[] array, String line){
        String name = array[0];
        String[] line_args = Arrays.copyOfRange(array, 1, array.length);
        String[] args = functions_dict.get(name);
        if(args == null)
            return handle_unsafe(line);
        else if(args.length != line_args.length){
            System.out.println("Tried to call non-existent function at " + line_count + ":\n" + line);
            System.exit(1);
        }
        String final_line = "";
        for(int i = 0; i < line_args.length; i++){
            final_line = String.format("%smov %s, %s\n", final_line, args[i], line_args[i]);
        }
        final_line += "call " + name;
        return final_line;
    }

    public static String handle_statement_end(){
        String new_line;
        String statement = statement_stack.pop();
        new_line = statement + ":";
        String[] split = statement.split("_");
        if(split[0].equals("loop"))
            new_line = "jmp loop_" + split[2] + "\n" + new_line;
        return new_line;
    }

    public static String handle_continue_break(String mode, String line){
        String stack_top = statement_stack.top();
        if(!stack_top.contains("loop")){
            System.out.println("Encountered an invalid "+ mode +" at " + line_count + ":\n" + line);
            System.exit(1);
        }
        String[] top_parts = stack_top.split("_");
        if(mode.equals("continue")) return String.format("jmp loop_%s:", top_parts[2]);
        return String.format("jmp loop_end_%s:", top_parts[2]);
    }

    public static String determine_line(String line){
        String[] split_line = strip_whitespace(line.trim().split("[ (){},]"));
        char[] chars = line.trim().toCharArray();
        String new_line = "";
        boolean add_statement_end = false;
        if(line.trim().isEmpty())
            return "";
        if(chars[0] == '#')
            new_line = "; "+line;
        else if(line.contains("}"))
            add_statement_end = true;
        else new_line = switch (split_line[0]) {
                case "if" -> handle_new_if(split_line, line);
                case "loop" -> handle_new_loop(split_line, line);
                case "/" -> String.format("div %s", split_line[1]);
                case "*" -> String.format("mul %s", split_line[1]);
                case "segment" -> String.format("%s:", split_line[1]);
                case "goto" -> "jmp " + split_line[1];
                case "fn" -> handle_function_def(split_line, line);
                case "push", "pop" -> handle_push_pop(split_line, line);
                case String x when AS_IS_KEYWORDS.contains(x.replaceAll("[\\[\\]]","")) -> line;
                case "break", "continue" -> handle_continue_break(split_line[0], line);
                default -> split_line.length > 1 ? switch(split_line[1]){
                    case "=" -> String.format("mov %s, %s", split_line[0], split_line[2]);
                    case "+" -> String.format("add %s, %s", split_line[0], split_line[2]);
                    case "-" -> String.format("sub %s, %s", split_line[0], split_line[2]);
                    default -> call_function(split_line, line);
                } : call_function(split_line, line);
            };
        if(add_statement_end)
            new_line += handle_statement_end();
        new_line += "\n";
        return new_line;
    }

    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            System.out.println("Need at least 1 argument.\n");
            System.out.println("Example usage:\nsimpasm input_file -dw output_file\nsimpasm -du input\nsimpasm input\n");
            System.out.println("Flags:\n--disable-unsafe\tDisables the usage of unsafe lines.\n-du");
            System.out.println("\n--disable-warnings\tDisables the warnings of unsafe lines.\n-dw");
            System.exit(1);
        }
        String[] arguments = Arrays.stream(args).filter(x -> !x.matches("^-.*")).toArray(String[]::new);
        String[] parameters = Arrays.stream(args).filter(x -> !x.matches("^[a-zA-Z0-9.]+$")).toArray(String[]::new);
        StringBuilder file_contents = new StringBuilder();

        for (String parameter : parameters) {
            if(parameter.equals("--disable-warnings") || parameter.equals("-dw")) DISABLE_WARNINGS = true;
            else if(parameter.equals("--disable-unsafe") || parameter.equals("-du")) DISABLE_UNSAFE = true;
            else{
                System.out.println("Unknown parameter: " + parameter);
                System.exit(1);
            }
        }

        String output_name = DEFAULT_OUTPUT_NAME;
        if(arguments.length > 1) output_name = arguments[1];
        File input_file = new File(arguments[0]);
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
