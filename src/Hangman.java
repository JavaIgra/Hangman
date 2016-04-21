import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
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
    private int stage = 0; // It tracks the stage of the game
    private JButton nextButton;    // A button the user can click after one game ends to go on to the next word.
    private JButton giveUpButton;  // A button that the user can click during a game to give up and end the game.
    private String message;     // A message that is drawn in the Display.
    private String message2;     // A message that is drawn in the Display.
    private WordList wordlist;  // An object holding the list of possible words that can be used in the game.
    private String word;        // The current secret word.
    private String matches;     // This is the string with empty positions that is filled through the gsme
    private String guesses;     // A string containing all the letters that the user has guessed so far.
    private boolean gameOver;   // False when a game is in progress, true when a game has ended and a new one not yet begun.
    private int badGuesses;     // The number of incorrect letters that the user has guessed in the current game.

    /**
     * This class defines a listener that will respond to the events that occur
     * when the user clicks any of the buttons in the button.  The buttons are
     * labeled "Next word", "Give up", "Quit", "A", "B", "C", ..., "Z".
     */
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
                giveUpButton.setEnabled(false);
                alphabetButtons.stream().forEach(b -> b.setEnabled(false));
            } else if(whichButton == nextButton) {
                stage = 0;
                message2 = "Bad guesses remaining " + (7 - badGuesses);
                startGame();
            } else if (alphabetButtons.contains(whichButton)) {
                whichButton.setEnabled(false);
                if (word.indexOf(whichButton.getText()) != -1) {
                    guesses += cmd;
                    message = "Yes, " + whichButton.getText() + " is in the word. Pick your next letter.";
                    matches = secretWordBuilder(cmd, word, matches);
                } else {
                    message = "Sorry, " + whichButton.getText() + " is not in the word. Pick your next letter.";
                    badGuesses++;
                    message2 = "Bad guesses remaining: " + (7 - badGuesses);
                    stage++;
                }
            }

            if (stage == 7) {
                message = "Sorry, you are hung! The word is " + word;
                alphabetButtons.stream().forEach(b -> b.setEnabled(false));
                message2 = "Click \"Next word\" to play again.";
                nextButton.setEnabled(true);
                giveUpButton.setEnabled(false);
            }

            if (wordIsComplete()) {
                message = "CONGRATULATIONS, YOU WIN!!!";
                alphabetButtons.stream().forEach(b -> b.setEnabled(false));
                message2 = "Click \"Next word\" to play again.";
                giveUpButton.setEnabled(false);
                nextButton.setEnabled(true);
            }

            display.repaint();  // Causes the display to be redrawn, to show any changes made in this method.
        }
    }

    // this method builds the string with empty positions that is filled through the gsme
    public String secretWordBuilder(String letter, String word, String word2) {
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
     * This class defines the panel that occupies the large central area in the
     * main panel.  The paintComponent() method in this class is responsible for
     * drawing the content of that panel.  It shows everything that the user
     * is supposed to see, based on the current values of all the instance variables.
     */
    private class Display extends JPanel {
        Display() {
            setPreferredSize(new Dimension(620, 420));
            setBackground(new Color(200, 200, 255));
            setFont(new Font("Serif", Font.BOLD, 20));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            if (message != null && !gameOver) {
                g.setColor(Color.BLACK);
                g.drawString(message, 30, 40);
            }

            if (message2 != null && !gameOver) {
                g.setColor(Color.BLACK);
                g.drawString(message2, 30, 70);
            }

            g.drawString(matches, 30, 200);
            this.drawMan(g2, 400, 210, 200, stage);
        }

        // this method draws the body of the man
        private void drawMan(Graphics2D g2, int cx, int cy, int height, int stage) {
            int h2 = height / 2;  // h2 = half height
            int w2 = height / 4;  // w2 = half width
            g2.setStroke(new BasicStroke(4));
            g2.drawLine(cx - w2, cy + h2, cx - w2, cy - h2);
            g2.drawLine(cx - w2, cy - h2, cx + w2, cy - h2);
            if (stage < 1) return;
            g2.setStroke(new BasicStroke(2));
            int xm = cx + w2 / 2;  // x-position of the man
            // Draw the rope
            g2.drawLine(xm, cy - h2, xm, cy - height / 3);
            if (stage < 2) return;
            int head_r = height / 12;  // radius of the head
            // Head
            g2.draw(new Ellipse2D.Double(xm - head_r, cy - height / 3, 2 * head_r, 2 * head_r));
            if (stage < 3) return;
            int neck_y = cy - height / 3 + 2 * head_r;
            // Body
            g2.drawLine(xm, neck_y, xm, cy + height / 8);
            if (stage < 4) return;
            // Right hand
            g2.drawLine(xm, neck_y, xm - height / 5, neck_y + height / 5);
            if (stage < 5) return;
            // Left hand
            g2.drawLine(xm, neck_y, xm + height / 5, neck_y + height / 5);
            if (stage < 6) return;
            // Right leg
            g2.drawLine(xm, cy + height / 8, xm + height / 5, cy + height / 8 + height / 5);
            if (stage < 7) return;
            // Left leg
            g2.drawLine(xm, cy + height / 8, xm - height / 5, cy + height / 8 + height / 5);
        }
    }

    /**
     * The constructor that creates the main panel, which is represented
     * by this class.  It makes all the buttons and subpanels and adds
     * them to the main panel.
     */
    public Hangman() {
        ButtonHandler buttonHandler = new ButtonHandler(); // The ActionListener that will respond to button clicks.
        setLayout(new BorderLayout(3, 3));  // Use a BorderLayout layout manager on the main panel.

        // Creat Top Panel
        JPanel top = new JPanel(); // The small panel on the top edge of the main panel.
        top.setLayout(new GridLayout(2, 13));// Use a GridLayout layout manager on for the top panel.

        // Create 26 buttons, register the ActionListener to respond to clicks on the
        // buttons, and add them to the top panel.
        for (char i = 'A'; i <= 'Z'; i++) {
            JButton button = new JButton(i + "");
            button.addActionListener(buttonHandler);
            top.add(button);
            alphabetButtons.add(button);
        }

        // Create Display Panel that fills the large central area of the main panel
        display = new Display();

        // Create Bottom Panel
        JPanel bottom = new JPanel();  // The small panel on the bottom edge of the main panel.

        /* Create three buttons, register the ActionListener to respond to clicks on the
		 * buttons, and add them to the bottom panel.
		 */
        nextButton = new JButton("Next word");
        nextButton.addActionListener(buttonHandler);
        bottom.add(nextButton);

        giveUpButton = new JButton("Give up");
        giveUpButton.addActionListener(buttonHandler);
        bottom.add(giveUpButton);

        JButton quit = new JButton("Quit");
        quit.addActionListener(buttonHandler);
        bottom.add(quit);

        // Add subpanels to the main panel.
        add(top, BorderLayout.NORTH);   // Put top in the "NORTH" position of the layout.
        add(display, BorderLayout.CENTER); // Put display in the central position in the "CENTER" position.
        add(bottom, BorderLayout.SOUTH);   // Put bottom in the "SOUTH" position of the layout.

        // Set background color, border color and size
        setBackground(new Color(100, 0, 0));
        setBorder(BorderFactory.createLineBorder(new Color(100, 0, 0), 3));

		// Get the list of possible secret words from the resource file named "words.txt".
        File txt = new File("src/words.txt");
        wordlist = new WordList(txt);

		// Start the first game.
        startGame();
    } // end constructor

    /**
     * This method should be called any time a new game starts. It picks a new
     * secret word, initializes all the variables that record the state of the
     * game, and sets the enabled/disabled state of all the buttons.
     */
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
        word = wordlist.removeWord(index).toUpperCase();
        matches = word.replaceAll("[\\w+]", "_ ");
        message = "The word has " + word.length() + " letters.  Let's play Hangman!";
        message2 = "Bad guesses remaining: " + (7 - badGuesses);
    }

    /**
     * This method can be called to test whether the user has guessed all the letters
     * in the current secret word.  That would mean the user has won the game.
     */
    private boolean wordIsComplete() {
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (guesses.indexOf(ch) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * This main program makes it possible to run this class as an application.  The main routine
     * creates a window, sets it to contain a panel of type Hangman, and shows the window in the
     * center of the screen.
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Team Bali presents Hangman"); // The window, with "Hangman" in the title bar.
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
} // end class Hangman