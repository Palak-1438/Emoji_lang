import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java -jar emoji-lang.jar <source-file>");
            System.exit(1);
        }
        String source = Files.readString(Path.of(args[0]));
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        List<Ast.Stmt> program = parser.parse();
        Interpreter interpreter = new Interpreter();
        interpreter.execute(program);
    }
}
