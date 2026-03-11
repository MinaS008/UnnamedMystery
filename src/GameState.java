import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GameState extends JFrame implements NextStatementIsALie.gameListener{
    //Colors
    //Fonts
    //UI
    private NextStatementIsALie game;
    private JTextArea narrativeArea;
    private JPanel choicesPanel;
    private JPanel suspicionPanel;
    private JPanel inventoryPanel;
    private JPanel  guessPanel;
    private JLabel dangerLabel;
    private JLabel dangerBar;
    private JLabel statusLabel;
    private JPanel rightPanel;
    private JButton tabSuspects;
    private JButton tabInventory;
    private JButton tabAccuse;

    private static final String tabSuspicion = "Suspicion";
    private static final String tabInventoryy = "Inventory";
    private static final String tabGuess = "Guess";

    public GameState(NextStatementIsALie game) {
        this.game = game;
        game.addListener(this);

        setTitle("The Next Statement Is A Lie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 740);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        setVisible(true);
        buildUI();

        game.startGame();
        showCharacterSelect();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildTopBar, BorderLayout.NORTH);
    }
}
