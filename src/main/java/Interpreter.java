import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
    private final Map<String, Double> env = new HashMap<>();

    public void execute(List<Ast.Stmt> statements) {
        for (Ast.Stmt stmt : statements) {
            exec(stmt);
        }
    }

    private void exec(Ast.Stmt stmt) {
        if (stmt instanceof Ast.VarAssign s) {
            double value = eval(s.value);
            env.put(s.name, value);
        } else if (stmt instanceof Ast.Print s) {
            double value = eval(s.value);
            System.out.println(value);
        } else if (stmt instanceof Ast.Block b) {
            for (Ast.Stmt inner : b.statements) exec(inner);
        } else if (stmt instanceof Ast.If i) {
            if (eval(i.condition) != 0) {
                exec(i.thenBranch);
            } else if (i.elseBranch != null) {
                exec(i.elseBranch);
            }
        } else if (stmt instanceof Ast.While w) {
            while (eval(w.condition) != 0) exec(w.body);
        } else {
            throw new RuntimeException("Unknown statement type: " + stmt.getClass());
        }
    }

    private double eval(Ast.Expr expr) {
        if (expr instanceof Ast.NumberLiteral n) {
            return n.value;
        }
        if (expr instanceof Ast.Variable v) {
            Double value = env.get(v.name);
            if (value == null) throw new RuntimeException("Undefined variable: " + v.name);
            return value;
        }
        if (expr instanceof Ast.Binary b) {
            double left = eval(b.left);
            double right = eval(b.right);
            return switch (b.operator) {
                case PLUS -> left + right;
                case MINUS -> left - right;
                case STAR -> left * right;
                case SLASH -> left / right;
                case GREATER -> left > right ? 1.0 : 0.0;
                case LESS -> left < right ? 1.0 : 0.0;
                case EQUAL_EQUAL -> left == right ? 1.0 : 0.0;
                case BANG_EQUAL -> left != right ? 1.0 : 0.0;
                default -> throw new RuntimeException("Unsupported operator: " + b.operator);
            };
        }
        throw new RuntimeException("Unknown expression type: " + expr.getClass());
    }
}
