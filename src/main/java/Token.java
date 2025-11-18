public class Token {
    public enum Type {
        IDENTIFIER,
        NUMBER,
        ASSIGN,      // 📦
        PRINT,       // 📢 or 🖨️
        PLUS,        // ➕
        MINUS,       // ➖
        STAR,        // ✖️
        SLASH,       // ➗
        IF,          // ❓
        WHILE,       // 🔁
        LBRACE,      // {
        RBRACE,      // }
        LPAREN,
        RPAREN,
        GREATER,
        LESS,
        EQUAL_EQUAL,
        BANG_EQUAL,
        SEMICOLON,
        EOF
    }

    public final Type type;
    public final String lexeme;

    public Token(Type type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return type + "(" + lexeme + ")";
    }
}
