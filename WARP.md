# Lang.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Core commands

### Build
- Build the project and produce the JAR (default from `pom.xml`):
  - `mvn package`
- The main artifact is written to:
  - `target/emoji-lang-0.1.0-SNAPSHOT.jar`

### Run the emoji language interpreter
- After building, run a program (e.g. `example.emj`):
  - `java -cp target/emoji-lang-0.1.0-SNAPSHOT.jar Main example.emj`
- Replace `example.emj` with the path to your source file when testing changes to the language.

### Compile / clean without packaging
- Compile only:
  - `mvn compile`
- Clean build outputs:
  - `mvn clean`

### Tests
- There are currently no test sources in this project, but Maven is configured in the standard way.
- When tests are added under `src/test/java`, use:
  - Run all tests: `mvn test`
  - Run a single test class: `mvn -Dtest=MyTestClass test`

## High-level architecture

This project implements a tiny emoji-based programming language in Java using a classic interpreter pipeline.

### Overall flow
- **Entry point (`Main`)**
  - Reads the source file from disk.
  - Constructs a `Lexer` from the raw source string.
  - Lexes to a `List<Token>`.
  - Constructs a `Parser` from the tokens.
  - Parses to a `List<Ast.Stmt>` representing the program.
  - Creates an `Interpreter` and executes the program AST.

- **Execution pipeline**
  1. **Lexing**: `Lexer` converts raw characters (including emoji) into `Token` instances.
  2. **Parsing**: `Parser` turns the token stream into an AST (`Ast` node types), handling statements and expressions.
  3. **Interpreting**: `Interpreter` walks the AST and performs the program’s side effects (currently numeric computation and printing).

### Core components
- **`Token`**
  - Enumerates all token types for the language (identifiers, numbers, control-flow emojis, arithmetic operators, braces, comparison operators, etc.).
  - Each token has a `Type` and a `lexeme` string; `toString()` is useful when debugging the lexer and parser.

- **`Lexer`**
  - Responsible for recognizing individual emoji and ASCII characters and turning them into tokens.
  - Handles:
    - Variable assign: `📦` → `ASSIGN`.
    - Print: `📢` / `🖨` → `PRINT`.
    - Arithmetic: `➕`, `➖`, `✖`, `➗` → `PLUS`, `MINUS`, `STAR`, `SLASH`.
    - Control flow: `❓`, `🔁` → `IF`, `WHILE`.
    - Braces and parentheses: `{`, `}`, `(`, `)`.
    - Comparisons and equality operators: `>`, `<`, `==`, `!=`.
    - Numbers (continuous digits) and identifiers (letters/digits).
  - Skips whitespace and appends an `EOF` token at the end.

- **`Ast` (Abstract Syntax Tree)**
  - Defines the node types for both statements and expressions:
    - Statements: variable assignment, print, block, `if`, and `while`.
    - Expressions: binary arithmetic expressions, variables, and numeric literals.
  - This is the central representation shared between parsing and interpreting.

- **`Parser`**
  - Consumes `Token` sequences and builds `Ast` nodes.
  - Statement-level constructs:
    - Variable assignment: `📦 x == expr` → `Ast.VarAssign`.
    - Print: `📢 expr` → `Ast.Print`.
    - `if`: `❓ condition { ... }` → `Ast.If`.
    - `while`: `🔁 condition { ... }` → `Ast.While`.
    - Blocks: `{ ... }` → `Ast.Block` (used for the bodies of `if` and `while`, and as a fallback statement form).
  - Expression grammar:
    - `expression` ⇒ `term`.
    - `term` handles `+` and `-` with left associativity.
    - `factor` handles `*` and `/` with left associativity.
    - `primary` handles numbers and identifiers.
  - Uses helper methods (`match`, `check`, `consume`, `advance`, etc.) in a straightforward recursive-descent style.

- **`Interpreter`**
  - Holds a simple environment `Map<String, Double>` mapping variable names to numeric values.
  - Statement execution:
    - `VarAssign`: evaluates the right-hand expression and stores the result in the environment.
    - `Print`: evaluates the expression and prints the numeric result to stdout.
    - `Block`: executes child statements in sequence with the same environment.
    - `If`: evaluates the condition; executes the `then` branch if the condition is non-zero.
    - `While`: repeatedly evaluates the condition and executes the body while the condition is non-zero.
  - Expression evaluation:
    - `NumberLiteral`: returns the stored `double` value.
    - `Variable`: looks up the name in the environment (error on undefined variables).
    - `Binary`: evaluates `left` and `right` then applies the operator (`PLUS`, `MINUS`, `STAR`, `SLASH`).
  - Comparison-related tokens (`GREATER`, `LESS`, `EQUAL_EQUAL`, `BANG_EQUAL`) are currently lexed and parsed structurally but not interpreted; adding semantics for them would extend the language’s control-flow capabilities.

## How to extend the language (for agents)

When implementing new language features, follow the existing pipeline so behavior stays consistent:
- **New operator or expression type**
  1. Add a new `Token.Type` in `Token` if needed.
  2. Teach `Lexer` to produce that token from the appropriate emoji/character.
  3. Extend `Ast` with a new node type if the feature is structurally different from existing ones.
  4. Update `Parser` to recognize the new syntax and build the appropriate AST node, respecting precedence.
  5. Extend `Interpreter` to evaluate the new expression or statement.

- **New statement form (e.g., `else`, additional control flow)**
  - Mirror the current design of `If`/`While`/`Block`: add new `Ast` nodes, parse them in `Parser`, and execute them in `Interpreter`.

## Notes from README

- The language is emoji-based; key constructs include assignment (`📦`), printing (`📢` / `🖨️`), arithmetic operators (`➕`, `➖`, `✖️`, `➗`), and control flow (`❓` for `if`, `🔁` for `while`).
- The README’s `Running` section corresponds directly to the build/run commands above and should stay in sync if artifact coordinates (groupId/artifactId/version) change in `pom.xml`.
