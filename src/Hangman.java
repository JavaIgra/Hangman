import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * A GUI version of the game of Hangman.  The user tries to guess letters in
 * a secret word, and loses after 7 guesses that are not in the word.  The
 * user guesses a letter by clicking a button whose text is that letter.
 */
public class Hangman extends JPanel {

    private Display display; // The central panel of the GUI, where things are drawn

    private ArrayList<JButton> alphabetButtons = new ArrayList<JButton>(); // 26 buttons, with lables "A", "B", ..., "Z"
    private JButton nextButton;    // A button the user can click after one game ends to go on to the next word.
    private JButton giveUpButton;  // A button that the user can click during a game to give up and end the game.
    private String message2;     // A message that is drawn in the Display.
    private String message;     // A message that is drawn in the Display.
    private WordList wordlist;  // An object holding the list of possible words that can be used in the game.
    private String word;        // The current secret word.
    private String word2;
    private String guesses;     // A string containing all the letters that the user has guessed so far.
    private boolean gameOver;   // False when a game is in progress, true when a game has ended and a new one not yet begun.
    private int badGuesses;     // The number of incorrect letters that the user has guessed in the current game.}
    int stage = 0;

    /**
     * This class defines the panel that occupies the large central area in the
     * main panel.  The paintComponent() method in this class is responsible for
     * drawing the content of that panel.  It shows everything that that the user
     * is supposed to see, based on the current values of all the instance variables.
     */

    private class Display extends JPanel {
        Display() {
            setPreferredSize(new Dimension(620, 420));
            //setBackground(new Color(250, 230, 180));
            setBackground(Color.CYAN);
            setFont(new Font("Serif", Font.BOLD, 20));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            ((Graphics2D) g).setStroke(new BasicStroke(3));
            if (message != null) {
                g.setColor(Color.BLACK);
                g.drawString(message, 30, 40);
            }
        }
    }

    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JButton whichButton = (JButton) evt.getSource();  // The button that the user clicked.
            String cmd = evt.getActionCommand();  // The test from the button that the user clicked.

            if (cmd.equals("Quit")) { // Respond to Quit button by ending the program.
                System.exit(0);
            } else if (cmd.equals("Give up")) {
                message = "You loose, because you gave up! The word is: " + word;
                message2 = "Click \"Next word\" to play again.";
                nextButton.setEnabled(true);
                alphabetButtons.stream().forEach(b -> b.setEnabled(false));
            } else if (whichButton == nextButton) {
                stage = 0;
                message2 = "Bad guesses remaining " + (7 - badGuesses);
                startGame();
            } else if (alphabetButtons.contains(whichButton)) {
                whichButton.setEnabled(false);
                if (word.indexOf(whichButton.getText()) != -1) {
                    guesses += cmd;
                    message = "Yes, " + whichButton.getText() + " is in the word. Pick your next letter.";
                    word2 = stringBuilder(cmd, word, word2);
                } else {
                    message = "Sorry, " + whichButton.getText() + " is not in the word. Pick your next letter.";
                    badGuesses++;
                    message2 = "Bag guesses remaining: " + (7 - badGuesses);
                    stage++;
                }
            }

            display.repaint();  // Causes the display to be redrawn, to show any changes made in this method.
        }
    }

    public String stringBuilder(String letter, String word, String word2) {
        int index = word.indexOf(letter);
        StringBuilder result = new StringBuilder();
        for (int i = 0, j = 0; i < word.length(); i++, j += 2) {
            if (word.charAt(i) == letter.charAt(0)) {
                result.append(letter + " ");
            } else {
                result.append(word2.substring(j, j + 2));
            }
        }
        return result.toString();
    }

    /**
     * The constructor that creates the main panel, which is represented
     * by this class.  It makes all the buttons and subpanels and adds
     * them to the main panel.
     */
    public Hangman() {
        ButtonHandler buttonHandler = new ButtonHandler(); // The ActionListener that will respond to button clicks.

		/* Create the subpanels and add them to the main panel.
         */

        display = new Display();  // The display panel that fills the large central area of the main panel.

        JPanel bottom = new JPanel();  // The small panel on the bottom edge of the main panel.

        nextButton = new JButton("Next word");
        nextButton.addActionListener(buttonHandler);
        bottom.add(nextButton);

        giveUpButton = new JButton("Give up");
        giveUpButton.addActionListener(buttonHandler);
        bottom.add(giveUpButton);

        JButton quit = new JButton("Quit");
        quit.addActionListener(buttonHandler);
        bottom.add(quit);
        JPanel top = new JPanel(); // The small panel on the top edge of the main panel.
        top.setLayout(new GridLayout(2, 13));// Use a GridLayout layout manager on the main panel.

        setLayout(new BorderLayout(3, 3));  // Use a BorderLayout layout manager on the main panel.
        add(display, BorderLayout.CENTER); // Put display in the central position in the "CENTER" position.
        add(bottom, BorderLayout.SOUTH);   // Put bottom in the "SOUTH" position of the layout.

        add(top, BorderLayout.NORTH);   // Put top in the "NORTH" position of the layout.
        /* Make the main panel a little prettier
         */
        for (char i = 'A'; i <= 'Z'; i++) {
            JButton button = new JButton(i + "");
            button.addActionListener(buttonHandler);
            top.add(button);
            alphabetButtons.add(button);
        }

        setBackground(new Color(100, 0, 0));
        setBorder(BorderFactory.createLineBorder(new Color(100, 0, 0), 3));

        File txt = new File("src/words.txt");
        wordlist = new WordList(txt);

        startGame();
    }

    private void startGame() {
        gameOver = false;
        guesses = "";
        badGuesses = 0;
        nextButton.setEnabled(false);
        for (int i = 0; i < alphabetButtons.size(); i++) {
            alphabetButtons.get(i).setEnabled(true);
        }
        giveUpButton.setEnabled(true);
        int index = (int) (Math.random() * wordlist.getWordCount());
        word = wordlist.removeWord(index);
        word = word.toUpperCase();
        word2 = word.replaceAll("[\\w+]", "_ ");
        message = "The word has " + word.length() + " letters.  Let's play Hangman!";
        message2 = "Bag guesses remaining: " + (7 - badGuesses);


    }

    /**
     * This main program makes it possible to run this class as an application.  The main routine
     * creates a window, sets it to contain a panel of type Hangman, and shows the window in the
     * center of the screen.
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("TEAM BALI presents HANGMAN"); // The window, with "Hangman" in the title bar.
        Hangman panel = new Hangman();  // The main panel for the window.
        window.setContentPane(panel);   // Set the main panel to be the content of the window
        window.pack();  // Set the size of the window based on the preferred sizes of what it contains.
        window.setResizable(false);  // Don't let the user resize the window.
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // End the program if the user closes the window.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();  // The width/height of the screen.
        window.setLocation((screen.width - window.getWidth()) / 2,
                (screen.height - window.getHeight()) / 2);  // Position window in the center of screen.
        window.setVisible(true);  // Make the window visible on the screen.
    }
}