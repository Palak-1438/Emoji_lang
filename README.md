# Emoji Programming Language (Java)

This project is a small emoji-based programming language implemented in Java.

## Syntax

Concept | Emoji | Example
------- | ----- | -------
Variable assign | 📦 | `📦 x == 5` (sets `x` to `5`)
Print | 📢 / 🖨️ | `📢 x`
Plus | ➕ | `5 ➕ 3`
Minus | ➖ | `9 ➖ 4`
Multiply | ✖️ | `2 ✖️ 3`
Divide | ➗ | `8 ➗ 4`
If | ❓ | `❓ x > 0 { ... }`
While | 🔁 | `🔁 x > 0 { ... }`

## Running

```bash
mvn package
java -cp target/emoji-lang-0.1.0-SNAPSHOT.jar Main example.emj
```
mvn package
java -cp target/emoji-lang-0.1.0-SNAPSHOT.jar EmojiStudio
## Roadmap

- Extend control flow (else, comparisons, etc.)
- Add better error messages
- Support more data types
