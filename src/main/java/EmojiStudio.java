import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.io.File;

/**
 * Simple desktop "studio" for the Emoji programming language.
 *
 * It provides a text editor, a Run button, and an output console.
 */
public class EmojiStudio {
    private final JFrame frame;
    private final JTextArea editor;
    private final JTextArea console;
    private File currentFile;

    public EmojiStudio() {
        frame = new JFrame("Emoji Lang Studio");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        editor = new JTextArea();
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        console = new JTextArea();
        console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        console.setEditable(false);

        JButton runButton = new JButton("Run ▶");
        runButton.addActionListener(this::runProgram);

        JButton clearButton = new JButton("Clear Console");
        clearButton.addActionListener(e -> console.setText(""));

        JButton openExampleButton = new JButton("Open example.emj");
        openExampleButton.addActionListener(e -> loadExample());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(runButton);
        topBar.add(clearButton);
        topBar.add(openExampleButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(editor), new JScrollPane(console));
        splitPane.setResizeWeight(0.7);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(createMenuBar(), BorderLayout.NORTH);
        frame.getContentPane().add(topBar, BorderLayout.SOUTH);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open...");
        openItem.addActionListener(e -> openFile());
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveFile(false));
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        saveAsItem.addActionListener(e -> saveFile(true));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        bar.add(fileMenu);
        return bar;
    }

    private void runProgram(ActionEvent e) {
        console.setText("");
        String source = editor.getText();
        try {
            Lexer lexer = new Lexer(source);
            java.util.List<Token> tokens = lexer.lex();
            Parser parser = new Parser(tokens);
            java.util.List<Ast.Stmt> program = parser.parse();

            // Capture output by temporarily redirecting System.out
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream originalOut = System.out;
            java.io.PrintStream ps = new java.io.PrintStream(baos, true);
            System.setOut(ps);
            try {
                Interpreter interpreter = new Interpreter();
                interpreter.execute(program);
            } finally {
                System.setOut(originalOut);
            }

            console.append(baos.toString());
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error: ").append(ex.getClass().getSimpleName())
              .append(" - ").append(ex.getMessage()).append("\n");
            console.append(sb.toString());
            // Also show a dialog for visibility
            JOptionPane.showMessageDialog(frame, sb.toString(),
                    "Runtime / Parse Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            try {
                String text = java.nio.file.Files.readString(currentFile.toPath());
                editor.setText(text);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to open file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile(boolean saveAs) {
        try {
            if (saveAs || currentFile == null) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                currentFile = chooser.getSelectedFile();
            }
            java.nio.file.Files.writeString(currentFile.toPath(), editor.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Failed to save file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExample() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("example.emj");
            String text = java.nio.file.Files.readString(path);
            editor.setText(text);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Failed to load example.emj: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmojiStudio studio = new EmojiStudio();
            studio.show();
        });
    }
}
