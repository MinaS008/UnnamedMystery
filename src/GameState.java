import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GameState extends JFrame implements NextStatementIsALie.gameListener {

    // Color scheme - Dark Mystery Theme
    private static final Color bgDark = new Color(15, 15, 20);
    private static final Color bgPanel = new Color(25, 25, 35);
    private static final Color bgHover = new Color(40, 40, 55);
    private static final Color txtPrimary = new Color(220, 215, 200);
    private static final Color txtSecondary = new Color(150, 145, 135);
    private static final Color txtDanger = new Color(180, 60, 60);
    private static final Color txtSuccess = new Color(80, 160, 80);
    private static final Color accGold = new Color(180, 150, 90);
    private static final Color accRed = new Color(140, 50, 50);
    private static final Color border = new Color(60, 55, 50);

    // Typewriter settings
    private static final int TYPEWRITER_DELAY_SLOW = 45;
    private static final int TYPEWRITER_DELAY_NORMAL = 25;

    // Game reference
    private final NextStatementIsALie game;

    // Main panels
    private JPanel mainContainer;
    private JPanel leftPanel;
    private JPanel narrativePanel;
    private JPanel choicePanel;
    private JPanel characterSelectPanel;
    private JPanel endingPanel;

    // Components
    private JTextArea narrativeText;
    private JLabel sceneTitle;
    private final List<JButton> choiceButtons;
    private JButton sisterButton;
    private JButton friendButton;
    private JPanel glassOverlay;

    //Animation state
    private javax.swing.Timer typewriterTimer;
    private String fullNarrativeText;
    private int typewriterIndex;
    private boolean isTypewriting;
    private javax.swing.Timer transitionTimer;
    private float transitionAlpha;
    private boolean isTransitioning;

    //Track special scemes
    private boolean isOpeningGathering;
    private boolean isFinalGathering;

    public GameState(NextStatementIsALie game) {
        this.game = game;
        this.choiceButtons = new ArrayList<>();
        this.isTypewriting = false;
        this.isTransitioning = false;

        game.addListener(this);
        initializeFrame();
        buildUI();
        showCharacterSelect();
    }

    private void initializeFrame() {
        setTitle("The Next Statement Is A Lie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(bgDark);

        //Custom cursor for mystery feel
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } catch (Exception e) {
            //Fallback to default
        }
    }

    private void buildUI() {
        mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(bgDark);

        //Create glass overlay for transition
        glassOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(isTransitioning) {
                    Graphics g2d = (Graphics2D) g.create();
                    g2d.setColor(new Color(0,0,0, (int)(transitionAlpha * 255)));
                    g2d.fillRect(0,0, getWidth(), getHeight());
                    g2d.dispose();
                }
            }
        };
        glassOverlay.setOpaque(false);

        buildLeftPanel();
        buildCharacterSelectPanel();
        buildEndingPanel();

        mainContainer.add(leftPanel, BorderLayout.CENTER);

        // Layerd pane for overlay effect
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(mainContainer, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(glassOverlay, JLayeredPane.PALETTE_LAYER);

        setContentPane((layeredPane));
    }

    private void buildLeftPanel() {
        leftPanel = new JPanel(new BorderLayout(0, 0));
        leftPanel.setBackground(bgDark);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Scene title at top
        sceneTitle = new JLabel("");
        sceneTitle.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 18));
        sceneTitle.setForeground(accGold);
        sceneTitle.setHorizontalAlignment(SwingConstants.CENTER);
        sceneTitle.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));

        // Narrative panel
        buildNarrativePanel();

        //Choicce pamel
        buildChoicePanel();

        leftPanel.add(sceneTitle, BorderLayout.NORTH);
        leftPanel.add(narrativePanel, BorderLayout.CENTER);
        leftPanel.add(choicePanel, BorderLayout.SOUTH);
    }

    private void buildNarrativePanel() {
        narrativePanel = new JPanel(new BorderLayout());
        narrativePanel.setBackground(bgPanel);
        narrativePanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(border, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));
        narrativeText = new JTextArea();
        narrativeText.setFont(new Font("Georgia", Font.PLAIN, 17));
        narrativeText.setForeground(txtPrimary);
        narrativeText.setBackground(bgPanel);
        narrativeText.setLineWrap(true);
        narrativeText.setWrapStyleWord(true);
        narrativeText.setEditable(false);
        narrativeText.setCaretColor(bgPanel);
        narrativeText.setBorder(null);

        narrativeText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isTypewriting) {
                    skipTypewriter();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(narrativeText);
        scrollPane.setBackground(bgPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(bgPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUI(new DarkScrollBarUI());

        narrativePanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void buildChoicePanel() {
        choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
        choicePanel.setBackground(bgDark);
        choicePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        // Pre-create choice buttons
        for(int i = 0; i < 5; i++) {
            JButton btn = createChoiceButton("");
            btn.setVisible(false);
            choiceButtons.add(btn);
            choicePanel.add(btn);
            choicePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    private JButton createChoiceButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)  g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if(getModel().isPressed()) {
                    g2d.setColor(accRed);
                } else if(getModel().isRollover()) {
                    g2d.setColor(bgHover);
                } else {
                    g2d.setColor(bgPanel);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border
                g2d.setColor(getModel().isRollover() ? accGold : border);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Georgia", Font.PLAIN, 15));
        btn.setForeground(txtPrimary);
        btn.setBackground(bgPanel);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(700, 50));
        btn.setPreferredSize(new Dimension(700, 50));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(accGold);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(txtPrimary);
                btn.repaint();
            }
        });

        return btn;
    }

    private void buildCharacterSelectPanel() {
        characterSelectPanel = new JPanel();
        characterSelectPanel.setLayout(new BoxLayout(characterSelectPanel, BoxLayout.Y_AXIS));
        characterSelectPanel.setBackground(bgDark);
        characterSelectPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 60, 80));

        // Titke
        JLabel title = new JLabel("THE NEXT STATEMENT IS A LIE");
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(txtPrimary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("A Murder Mystery");
        subtitle.setFont(new Font("Serif", Font.ITALIC, 22));
        subtitle.setForeground(accGold);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Flavor text panel
        JTextArea flavorText = new JTextArea();
        flavorText.setText(
                "The countryside estate is silent except for the rain against the windows.\n\n" +
                        "Downstairs, the Grandfather lies dead.\n\n" +
                        "No one has called anyone yet. No one has moved toward the door.\n" +
                        "The storm outside sees to that.\n\n" +
                        "Someone in this room did this.\n\n" +
                        "The question is who - and whether you will find out before they\n" +
                        "decide that you know too much.\n\n" +
                        "Choose your perspective wisely."
        );
        flavorText.setFont(new Font("Georgia", Font.ITALIC, 16));
        flavorText.setForeground(txtSecondary);
        flavorText.setBackground(bgDark);
        flavorText.setEditable(false);
        flavorText.setLineWrap(true);
        flavorText.setWrapStyleWord(true);
        flavorText.setAlignmentX(Component.CENTER_ALIGNMENT);
        flavorText.setMaximumSize(new Dimension(600, 300));
        flavorText.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        //Character Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setBackground(bgDark);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sisterButton = createCharacterButton("Older Sister",
                "Observant. Protective. You've always watched over this family.");
        friendButton = createCharacterButton("Family Friend",
                "Outsider. Observant. You see what others choose to ignore. ");

        sisterButton.addActionListener(e -> {
            if(!isTransitioning) {
                performTransition(() -> game.selectCharacter(NextStatementIsALie.playableCharacter.olderSister), false);
            }
        });

        friendButton.addActionListener( e -> {
            if(!isTransitioning) {
                performTransition(() -> game.selectCharacter(NextStatementIsALie.playableCharacter.familyFriend), false);
            }
        });

        buttonPanel.add(sisterButton);
        buttonPanel.add(friendButton);

        characterSelectPanel.add(Box.createVerticalGlue());
        characterSelectPanel.add(title);
        characterSelectPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        characterSelectPanel.add(subtitle);
        characterSelectPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        characterSelectPanel.add(flavorText);
        characterSelectPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        characterSelectPanel.add(buttonPanel);
        characterSelectPanel.add(Box.createVerticalGlue());
    }

    private JButton createCharacterButton(String name, String description) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // background
                if (getModel().isPressed()) {
                    g2d.setColor(accRed);
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgHover);
                } else {
                    g2d.setColor(bgPanel);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                //Border with glow effect on hover
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(accGold.getRed(), accGold.getGreen(), accGold.getBlue(), 100));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
                }
                g2d.setColor(getModel().isRollover() ? accGold : border);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2d.dispose();

                // Draw text manually
                g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                //Name
                g2d.setFont(new Font("Serif", Font.BOLD, 20));
                g2d.setColor(getModel().isRollover() ? accGold : txtPrimary);
                FontMetrics fm = g2d.getFontMetrics();
                int nameWidth = fm.stringWidth(name);
                g2d.drawString(name, (getWidth() - nameWidth) / 2, 45);

                //Description
                g2d.setFont(new Font("Georgia", Font.ITALIC, 12));
                g2d.setColor(txtSecondary);
                fm = g2d.getFontMetrics();
                int descWidth = fm.stringWidth(description);
                if(descWidth > getWidth() - 40) {
                    //Wrap text
                    String[] words = description.split(" ");
                    StringBuilder line1 = new StringBuilder();
                    StringBuilder line2 = new StringBuilder();
                    boolean firstLine = true;
                    for (String word : words) {
                        if (firstLine && fm.stringWidth(line1.toString() + word) < getWidth() - 40) {
                            line1.append(word).append(" ");
                        } else {
                            firstLine = false;
                            line2.append(word).append(" ");
                        }
                    }
                    g2d.drawString(line1.toString().trim(), (getWidth() - fm.stringWidth(line1.toString().trim())) / 2, 70);
                    g2d.drawString(line2.toString().trim(), (getWidth() - fm.stringWidth(line2.toString().trim())) / 2, 85);
                } else {
                    g2d.drawString(description, (getWidth() - descWidth) / 2, 75);
                }

                g2d.dispose();
                }
            };

        btn.setPreferredSize(new Dimension(280, 120));
        btn.setMaximumSize(new Dimension(280, 120));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return  btn;
    }

        private void buildEndingPanel() {
        endingPanel = new JPanel();
        endingPanel.setLayout(new BoxLayout(endingPanel, BoxLayout.Y_AXIS));
        endingPanel.setBackground(bgDark);
        endingPanel.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
    }

    private void showCharacterSelect() {
        mainContainer.removeAll();
        mainContainer.add(characterSelectPanel, BorderLayout.CENTER);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private void showGamePanel() {
        mainContainer.removeAll();
        mainContainer.add(leftPanel, BorderLayout.CENTER);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private void renderScene(Scene scene) {
        if (scene == null) return;

        String sceneID = scene.getSceneID();
        isOpeningGathering = sceneID.equals("OpeningGathering");
        isFinalGathering = sceneID.contains("Final Gathering") || scene.getTriggersFinalGathering();

        // Update scene title
        sceneTitle.setText(formatSceneTitle(sceneID));

        // Get narrative text
        fullNarrativeText = scene.getNarrativeText();

        // Determine if typewriter should be used
        if (isOpeningGathering || isFinalGathering) {
            startTypewriterAnimation(TYPEWRITER_DELAY_SLOW);
        } else {
            startTypewriterAnimation(TYPEWRITER_DELAY_NORMAL);
        }

        // Render choices (but keep them disabled during typewriter)
        renderChoices(game.getAvailableChoices());
    }

    private String formatSceneTitle(String sceneID) {
        // Convert camelCase to Title Case with spaces
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sceneID.length(); i++) {
            char c = sceneID.charAt(i);
            if (java.lang.Character.isUpperCase(c) && i > 0) {
                result.append(" ");
            }
            result.append(c);
        }
        return result.toString().toUpperCase();
    }

    private void renderChoices(List<Choice> choices) {
        //Hide all buttons first
        for(JButton btn: choiceButtons) {
            btn.setVisible(false);
            for(ActionListener al: btn.getActionListeners()) {
                btn.removeActionListener(al);
            }
        }

        //Show and configure available chocies
        for(int i = 0; i < choices.size() && i < choiceButtons.size(); i++) {
            Choice choice = choices.get(i);
            JButton btn = choiceButtons.get(i);

            btn.setText("> " + choice.getText());
            btn.setVisible(true);
            btn.setEnabled(!isTypewriting);

            final int index = i;
            btn.addActionListener(e -> {
                if (!isTransitioning && !isTypewriting) {
                    Choice selectedChoice = choices.get(index);
                    boolean shouldFlash = selectedChoice.getText().toLowerCase().contains("accuse") ||
                            selectedChoice.getText().toLowerCase().contains("confront") ||
                            selectedChoice.getText().toLowerCase().contains("final");
                    performTransition(() -> game.makeChoice(selectedChoice), shouldFlash);
                }
            });
        }

        choicePanel.revalidate();
        choicePanel.repaint();
    }

    private void enableChoices() {
        for(JButton btn: choiceButtons) {
            if(btn.isVisible()) {
                btn.setEnabled(true);
            }
        }
    }

    //Typewriter animation
    private void startTypewriterAnimation(int delay) {
        if(typewriterTimer != null ) {
            typewriterTimer.stop();
        }

        narrativeText.setText("");
        typewriterIndex = 0;
        isTypewriting = true;

        //Disable choices while typewriter
        for(JButton btn : choiceButtons) {
            btn.setEnabled(false);
        }

        typewriterTimer = new javax.swing.Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(typewriterIndex < fullNarrativeText.length()) {
                    char c = fullNarrativeText.charAt(typewriterIndex);
                    narrativeText.append(String.valueOf(c));
                    typewriterIndex++;

                    //Add sloght pause fro drama
                    if(c == '.' || c =='?' || c == '!') {
                        typewriterTimer.setDelay(delay * 4);
                    } else if ( c == ',') {
                            typewriterTimer.setDelay(delay*2);
                    } else if( c == '\n') {
                        typewriterTimer.setDelay(delay*3);
                    } else {
                        typewriterTimer.setDelay(delay);
                    }

                    //Auto-scroll
                    narrativeText.setCaretPosition(narrativeText.getDocument().getLength());
                } else {
                    typewriterTimer.stop();
                    isTypewriting = false;
                    enableChoices();
                }
            }
        });
        typewriterTimer.start();
    }

    private void skipTypewriter() {
        if (typewriterTimer != null) {
            typewriterTimer.stop();
        }
        narrativeText.setText(fullNarrativeText);
        isTypewriting = false;
        enableChoices();
    }

    //Transition effect
    private void performTransition(Runnable action, boolean useFlash) {
        if(isTransitioning) return;;
        isTransitioning = true;

        if(useFlash) {
            performFlashTransition(action);
        } else {
            performFadeTransition(action);
        }
    }

    private void performFadeTransition(Runnable action) {
        transitionAlpha = 0f;

        javax.swing.Timer fadeOut = new javax.swing.Timer(20, null);
        fadeOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionAlpha += 0.08f;
                glassOverlay.repaint();

                if (transitionAlpha >= 1f) {
                    fadeOut.stop();
                    transitionAlpha = 1f;

                    // Execute action at peak of fade
                    action.run();

                    // Fade in
                    javax.swing.Timer fadeIn = new javax.swing.Timer(20, null);
                    fadeIn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e2) {
                            transitionAlpha -= 0.06f;
                            glassOverlay.repaint();

                            if (transitionAlpha <= 0f) {
                                fadeIn.stop();
                                transitionAlpha = 0f;
                                isTransitioning = false;
                                glassOverlay.repaint();
                            }
                        }
                    });
                    fadeIn.start();
                }
            }
        });
        fadeOut.start();
    }

    private void performFlashTransition(Runnable action) {
        // Quick flash to white then fade to black
        transitionAlpha = 0f;

        // Store original overlay paint
        JPanel flashOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Flash white briefly
                g2d.setColor(new Color(255, 255, 255, (int)(transitionAlpha * 200)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        flashOverlay.setOpaque(false);

        javax.swing.Timer flash = new javax.swing.Timer(15, null);
        final int[] phase = {0}; // 0=flash up, 1=flash down, 2=fade to black, 3=fade in

        flash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (phase[0]) {
                    case 0: // Flash up (white)
                        transitionAlpha += 0.25f;
                        if (transitionAlpha >= 1f) {
                            transitionAlpha = 1f;
                            phase[0] = 1;
                        }
                        break;
                    case 1: // Flash down
                        transitionAlpha -= 0.15f;
                        if (transitionAlpha <= 0f) {
                            transitionAlpha = 0f;
                            phase[0] = 2;
                            action.run();
                        }
                        break;
                    case 2: // Fade to black (using regular overlay)
                        transitionAlpha += 0.1f;
                        if (transitionAlpha >= 0.8f) {
                            transitionAlpha = 0.8f;
                            phase[0] = 3;
                        }
                        break;
                    case 3: // Fade in
                        transitionAlpha -= 0.05f;
                        if (transitionAlpha <= 0f) {
                            flash.stop();
                            transitionAlpha = 0f;
                            isTransitioning = false;
                        }
                        break;
                }
                glassOverlay.repaint();
            }
        });
        flash.start();
    }

    //Ending scenes
    private void showEndingScreen(NextStatementIsALie.endingType ending) {
        endingPanel.removeAll();

        String title;
        String message;
        Color titleColor;

        switch (ending) {
            case correctGuessEscape:
                title = "Justice Prevails";
                message = "You identified the killer: " + game.getKillerReveal() + ".\n\n" +
                        "The truth is out. The guilty will face consequences. \n" +
                        "You survived the night.";
                titleColor = txtSuccess;
                break;
            case correctGuessTooLate:
                title = "Pyrrhic Victory";
                message = "You were right about " + game.getKillerReveal() + ".\n\n" +
                        "But you waited too long. The killer struck first. \n" +
                        "The truth dies with you.";
                titleColor = accGold;
                break;
            case wrongGuess:
                title = "Fatal Mistake";
                message = "You accused the wrong person.\n\n" +
                "The real killer was " + game.getKillerReveal() + ".\n" +
                        "They watched you condemn an innocent.\n" +
                        "Then they made sure you wouldn't make that mistake again.";
                titleColor = txtDanger;
                break;
            case escapedUnsolved:
                title = "Escaped... But at what cost?";
                message = "You fled into the night.\n\n" +
                        "The killer was " + game.getKillerReveal() + ".\n" +
                        "They remain free. The mystery unsolved.\n" +
                        "You'll always wonder if you could have stopped them.";
                titleColor = txtSecondary;
                break;
            case everyoneDead:
                title = "Silence Falls";
                message = "The house is quiet now.\n\n" +
                        "The killer was " + game.getKillerReveal() + ".\n" +
                        "No one remains to tell the tale.\n" +
                        "Some secrets die with their keepers.";
                titleColor = txtDanger;
                break;
            default:
                title = "Game Over";
                message = "The mystery ends here.";
                titleColor = txtPrimary;
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 38));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea messageText = new JTextArea(message);
        messageText.setFont(new Font("Georgia", Font.PLAIN, 18));
        messageText.setForeground(txtSecondary);
        messageText.setBackground(bgDark);
        messageText.setEditable(false);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageText.setMaximumSize(new Dimension(600, 300));
        messageText.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JButton restartBtn = createChoiceButton("Play Again");
        restartBtn.addActionListener(e -> {
            dispose();
        });
        restartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        endingPanel.add(Box.createVerticalGlue());
        endingPanel.add(titleLabel);
        endingPanel.add(messageText);
        endingPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        endingPanel.add(restartBtn);
        endingPanel.add(Box.createVerticalGlue());

        mainContainer.removeAll();
        mainContainer.add(endingPanel, BorderLayout.CENTER);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    //Game Listenere implementation
    @Override
    public void onGameEvent(NextStatementIsALie.gameEvent event, NextStatementIsALie game) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case gameStarted:
                    showCharacterSelect();
                    break;

                case characterSelected:
                    showGamePanel();
                    renderScene(game.getCurrentScene());
                    break;

                case sceneChanged:
                    renderScene(game.getCurrentScene());
                    break;

                case gameEnded:
                    performTransition(() -> showEndingScreen(game.getEndingType()), true);
                    break;

                case inventoryChanged:
                case suspicionChanged:
                case dangerChanged:
                    // Could update UI indicators here
                    break;

                case characterDied:
                    // Could show death notification
                    break;

                case choiceUnavailable:
                    // Flash the unavailable choice red briefly
                    break;
            }
        });
    }

    //Game Scrollbar
    private class DarkScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = border;
            this.trackColor = bgPanel;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(thumbColor);
            g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 8, 8);
            g2d.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }

    public static void main(String[] args) {
        
    }
}
