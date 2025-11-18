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
            // Skip stray semicolons at the top level
            if (match(Token.Type.SEMICOLON)) continue;
            statements.add(statement());
        }
        return statements;
    }

    private Ast.Stmt statement() {
        if (match(Token.Type.ASSIGN)) {
            Token name = consume(Token.Type.IDENTIFIER, "Expected identifier after 📦");
            consume(Token.Type.EQUAL_EQUAL, "Expected '=' transformed to '==' after identifier (simplified parser)");
            Ast.Expr value = expression();
            // Optional semicolon after assignment
            match(Token.Type.SEMICOLON);
            return new Ast.VarAssign(name.lexeme, value);
        }
        if (match(Token.Type.PRINT)) {
            Ast.Expr value = expression();
            // Optional semicolon after print
            match(Token.Type.SEMICOLON);
            return new Ast.Print(value);
        }
        if (match(Token.Type.IF)) {
            Ast.Expr cond = expression();
            Ast.Stmt thenBranch = statementOrBlock();
            Ast.Stmt elseBranch = null;
            // Simple else: use keyword "else" in ASCII for now
            if (match(Token.Type.IDENTIFIER) && "else".equals(previous().lexeme)) {
                elseBranch = statementOrBlock();
            }
            return new Ast.If(cond, thenBranch, elseBranch);
        }
        if (match(Token.Type.WHILE)) {
            Ast.Expr cond = expression();
            Ast.Stmt body = statementOrBlock();
            return new Ast.While(cond, body);
        }
        // Fallback: either a block or an expression statement
        if (check(Token.Type.LBRACE)) {
            return block();
        }
        Ast.Expr expr = expression();
        match(Token.Type.SEMICOLON);
        // Currently, only assignments/prints are meaningful; treat bare expr as print for convenience
        return new Ast.Print(expr);
    }

    private Ast.Stmt statementOrBlock() {
        if (match(Token.Type.LBRACE)) {
            // We consumed the '{', parse a full block body
            List<Ast.Stmt> stmts = new ArrayList<>();
            while (!check(Token.Type.RBRACE) && !isAtEnd()) {
                stmts.add(statement());
            }
            consume(Token.Type.RBRACE, "Expected '}'");
            return new Ast.Block(stmts);
        }
        // Single statement body (no braces)
        return statement();
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
        return equality();
    }

    private Ast.Expr equality() {
        Ast.Expr expr = comparison();
        while (match(Token.Type.EQUAL_EQUAL, Token.Type.BANG_EQUAL)) {
            Token.Type op = previous().type;
            Ast.Expr right = comparison();
            expr = new Ast.Binary(expr, op, right);
        }
        return expr;
    }

    private Ast.Expr comparison() {
        Ast.Expr expr = term();
        while (match(Token.Type.GREATER, Token.Type.LESS)) {
            Token.Type op = previous().type;
            Ast.Expr right = term();
            expr = new Ast.Binary(expr, op, right);
        }
        return expr;
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
