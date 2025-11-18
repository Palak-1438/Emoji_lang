import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int current = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> lex() {
        while (!isAtEnd()) {
            char c = advance();
            switch (c) {
                case '📦':
                    add(Token.Type.ASSIGN, "📦");
                    break;
                case '📢':
                case '🖨':
                    add(Token.Type.PRINT, String.valueOf(c));
                    break;
                case '➕':
                    add(Token.Type.PLUS, "➕");
                    break;
                case '➖':
                    add(Token.Type.MINUS, "➖");
                    break;
                case '✖':
                    add(Token.Type.STAR, "✖️");
                    break;
                case '➗':
                    add(Token.Type.SLASH, "➗");
                    break;
                case '❓':
                    add(Token.Type.IF, "❓");
                    break;
                case '🔁':
                    add(Token.Type.WHILE, "🔁");
                    break;
                case '{':
                    add(Token.Type.LBRACE, "{");
                    break;
                case '}':
                    add(Token.Type.RBRACE, "}");
                    break;
                case '(':
                    add(Token.Type.LPAREN, "(");
                    break;
                case ')':
                    add(Token.Type.RPAREN, ")");
                    break;
                case '>':
                    add(Token.Type.GREATER, ">");
                    break;
                case '<':
                    add(Token.Type.LESS, "<");
                    break;
                case '=':
                    if (match('=')) {
                        add(Token.Type.EQUAL_EQUAL, "==");
                    }
                    break;
                case '!':
                    if (match('=')) {
                        add(Token.Type.BANG_EQUAL, "!=");
                    }
                    break;
                case ';':
                    add(Token.Type.SEMICOLON, ";");
                    break;
                case ' ': case '\r': case '\t': case '\n':
                    break; // skip whitespace
                default:
                    if (Character.isDigit(c)) {
                        number(c);
                    } else if (Character.isLetter(c)) {
                        identifier(c);
                    }
            }
        }
        tokens.add(new Token(Token.Type.EOF, ""));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private void add(Token.Type type, String lexeme) {
        tokens.add(new Token(type, lexeme));
    }

    private void number(char first) {
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        while (!isAtEnd() && Character.isDigit(source.charAt(current))) {
            sb.append(advance());
        }
        add(Token.Type.NUMBER, sb.toString());
    }

    private void identifier(char first) {
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        while (!isAtEnd() && Character.isLetterOrDigit(source.charAt(current))) {
            sb.append(advance());
        }
        add(Token.Type.IDENTIFIER, sb.toString());
    }
}
