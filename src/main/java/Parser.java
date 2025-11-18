import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Ast.Stmt> parse() {
        List<Ast.Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }
        return statements;
    }

    private Ast.Stmt statement() {
        if (match(Token.Type.ASSIGN)) {
            Token name = consume(Token.Type.IDENTIFIER, "Expected identifier after 📦");
            consume(Token.Type.EQUAL_EQUAL, "Expected '=' transformed to '==' after identifier (simplified parser)");
            Ast.Expr value = expression();
            return new Ast.VarAssign(name.lexeme, value);
        }
        if (match(Token.Type.PRINT)) {
            Ast.Expr value = expression();
            return new Ast.Print(value);
        }
        if (match(Token.Type.IF)) {
            Ast.Expr cond = expression();
            Ast.Stmt thenBranch = block();
            return new Ast.If(cond, thenBranch);
        }
        if (match(Token.Type.WHILE)) {
            Ast.Expr cond = expression();
            Ast.Stmt body = block();
            return new Ast.While(cond, body);
        }
        return block();
    }

    private Ast.Stmt block() {
        consume(Token.Type.LBRACE, "Expected '{'");
        List<Ast.Stmt> stmts = new ArrayList<>();
        while (!check(Token.Type.RBRACE) && !isAtEnd()) {
            stmts.add(statement());
        }
        consume(Token.Type.RBRACE, "Expected '}'");
        return new Ast.Block(stmts);
    }

    private Ast.Expr expression() {
        return term();
    }

    private Ast.Expr term() {
        Ast.Expr expr = factor();
        while (match(Token.Type.PLUS, Token.Type.MINUS)) {
            Token.Type op = previous().type;
            Ast.Expr right = factor();
            expr = new Ast.Binary(expr, op, right);
        }
        return expr;
    }

    private Ast.Expr factor() {
        Ast.Expr expr = primary();
        while (match(Token.Type.STAR, Token.Type.SLASH)) {
            Token.Type op = previous().type;
            Ast.Expr right = primary();
            expr = new Ast.Binary(expr, op, right);
        }
        return expr;
    }

    private Ast.Expr primary() {
        if (match(Token.Type.NUMBER)) {
            return new Ast.NumberLiteral(Double.parseDouble(previous().lexeme));
        }
        if (match(Token.Type.IDENTIFIER)) {
            return new Ast.Variable(previous().lexeme);
        }
        throw new RuntimeException("Unexpected token: " + peek());
    }

    private boolean match(Token.Type... types) {
        for (Token.Type type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(Token.Type type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == Token.Type.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(Token.Type type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException(message + ", found " + peek());
    }
}
