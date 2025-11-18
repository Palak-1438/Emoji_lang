import java.util.List;

public class Ast {
    public interface Stmt {}
    public interface Expr {}

    public static class VarAssign implements Stmt {
        public final String name;
        public final Expr value;
        public VarAssign(String name, Expr value) { this.name = name; this.value = value; }
    }

    public static class Print implements Stmt {
        public final Expr value;
        public Print(Expr value) { this.value = value; }
    }

    public static class Block implements Stmt {
        public final List<Stmt> statements;
        public Block(List<Stmt> statements) { this.statements = statements; }
    }

    public static class If implements Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch; // may be null
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
    }

    public static class While implements Stmt {
        public final Expr condition;
        public final Stmt body;
        public While(Expr condition, Stmt body) { this.condition = condition; this.body = body; }
    }

    public static class Binary implements Expr {
        public final Expr left;
        public final Token.Type operator;
        public final Expr right;
        public Binary(Expr left, Token.Type operator, Expr right) {
            this.left = left; this.operator = operator; this.right = right;
        }
    }

    public static class Variable implements Expr {
        public final String name;
        public Variable(String name) { this.name = name; }
    }

    public static class NumberLiteral implements Expr {
        public final double value;
        public NumberLiteral(double value) { this.value = value; }
    }
}
