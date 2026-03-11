import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// Why am I using float? - Float can be used for cases that require memory
// efficiency, such as real-time graphics
public class GameSidebarPanel extends JPanel implements NextStatementIsALie.gameListener {

    static final Color bgDeep = new Color(10, 8, 6);
    static final Color bgPanel = new Color(18, 14, 10);
    static final Color bgCard = new Color(26, 20, 14);
    static final Color accentGold = new Color(180, 140, 60);
    static final Color accentCrimson = new Color(140, 30, 30);
    static final Color textPrimary = new Color(220, 210, 190);
    static final Color textDim = new Color(120, 110, 95);
    static final Color dangerLow = new Color(60, 100, 60);
    static final Color dangerMid = new Color(160, 100, 20);
    static final Color dangerHigh = new Color(160, 30, 30);

    static final Font fontTitle;
    static final Font fontBody;
    static final Font fontMono;

    static {
        fontTitle = new Font("Serif", Font.BOLD, 13);
        fontBody  = new Font("Serif", Font.PLAIN, 11);
        fontMono  = new Font("Monospaced", Font.PLAIN, 10);
    }

    private final DangerPanel    dangerPanel;
    private final SuspicionPanel suspicionPanel;
    private final InventoryPanel inventoryPanel;
    private final AmbushWarning  ambushWarning;

    private NextStatementIsALie game;
    private boolean visible = false;

    public GameSidebarPanel() {
        setPreferredSize(new Dimension(260, 0));
        setBackground(bgDeep);
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        JLabel header = new JLabel("Case Board");
        header.setFont(fontTitle.deriveFont(Font.BOLD, 11f));
        header.setForeground(textDim);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(0, 2, 10, 0));
        inner.add(header);

        dangerPanel = new DangerPanel();
        dangerPanel.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(dangerPanel);
        inner.add(Box.createVerticalStrut(14));

        suspicionPanel = new SuspicionPanel();
        suspicionPanel.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(suspicionPanel);
        inner.add(Box.createVerticalStrut(14));

        inventoryPanel = new InventoryPanel();
        inventoryPanel.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(inventoryPanel);

        // Putting in a JScrollPane so longer inventories don't clip
        JScrollPane scroll = new JScrollPane(
                inner,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new ThinScrollBarUI());

        add(scroll, BorderLayout.CENTER);
        ambushWarning = new AmbushWarning();
        add(ambushWarning, BorderLayout.NORTH);
        setVisible(false);
    }

    @Override
    public void onGameEvent(NextStatementIsALie.gameEvent event, NextStatementIsALie g) {
        this.game = g;

        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case characterSelected:
                    showSidebar();
                    break;

                case gameEnded:
                    hideSidebar();
                    break;

                case suspicionChanged:
                    suspicionPanel.update(g.getAllSuspicionScores(), g);
                    break;

                case dangerChanged:
                case sceneChanged:
                    dangerPanel.update(g.getDangerLevel());
                    suspicionPanel.update(g.getAllSuspicionScores(), g);
                    checkAmbushWarning(g);
                    break;

                case inventoryChanged:
                    inventoryPanel.update(g.getInventory());
                    break;

                case characterDied:
                    // FIX 3: was calling update(scores) with one arg — method requires two
                    suspicionPanel.update(g.getAllSuspicionScores(), g);
                    break;

                default:
                    break;
            }
            repaint();
        });
    }

    private void showSidebar() {
        setVisible(true);
        visible = true;
        setOpaque(false);
        Timer t = new Timer();
        final float[] alpha = {0f};
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                alpha[0] = Math.min(1f, alpha[0] + 0.05f);
                SwingUtilities.invokeLater(() -> repaint());
                if (alpha[0] >= 1f) t.cancel();
            }
        }, 0, 30);
    }

    private void hideSidebar() {
        setVisible(false);
        visible = false;
        ambushWarning.hide();
    }

    private void checkAmbushWarning(NextStatementIsALie g) {
        if (g.getDangerLevel() >= 8) {
            ambushWarning.show();
        } else {
            ambushWarning.hide();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(bgDeep);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(accentGold);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(0, 20, 0, getHeight() - 20);

        paintGrain(g2, getWidth(), getHeight());
        g2.dispose();
    }

    private void paintGrain(Graphics2D g2, int w, int h) {
        Random rng = new Random(42); // fixed seed — consistent texture
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.025f)); // 2.5% opacity, very subtle
        g2.setColor(Color.WHITE);
        for (int i = 0; i < w * h / 8; i++) { // how many pixels of noise to draw
            int x = rng.nextInt(w); // random position
            int y = rng.nextInt(h);
            g2.fillRect(x, y, 1, 1); // 1x1 pixel square
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset opacity
    }

    static class DangerPanel extends JPanel {
        private int   targetLevel = 0;
        private float animLevel = 0f;
        private Timer animTimer;
        private boolean pulsing = false;

        private float pulseAlpha = 0f; // opacity of the pulse
        private float pulseDir = 1f; // direction: increasing or decreasing
        private Timer pulseTimer;

        DangerPanel() {
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            setPreferredSize(new Dimension(236, 70));
        }

        void update(int newLevel) {
            targetLevel = newLevel;
            startAnimation();
            if (newLevel >= 8) startPulse();
            else               stopPulse();
        }

        private void startAnimation() {
            if (animTimer != null) animTimer.cancel();
            animTimer = new Timer();
            animTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    float diff = targetLevel - animLevel;
                    animLevel += diff * 0.15f;
                    if (Math.abs(diff) < 0.01f) {
                        animLevel = targetLevel;
                        animTimer.cancel();
                    }
                    SwingUtilities.invokeLater(() -> repaint());
                }
            }, 0, 16);
        }

        private void startPulse() {
            if (pulsing) return;
            pulsing = true;
            pulseTimer = new Timer();
            pulseTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    pulseAlpha += 0.04f * pulseDir; // increase or decrease by 0.04
                    if (pulseAlpha >= 1f) { pulseAlpha = 1f; pulseDir = -1f; } // clamp, reverse
                    if (pulseAlpha <= 0f) { pulseAlpha = 0f; pulseDir =  1f; } // clamp, reverse
                    SwingUtilities.invokeLater(() -> repaint());
                }
            }, 0, 30);
        }

        private void stopPulse() {
            pulsing = false;
            if (pulseTimer != null) pulseTimer.cancel();
            pulseAlpha = 0f;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();

            g2.setFont(fontTitle.deriveFont(Font.BOLD, 10f));
            g2.setColor(textDim);
            g2.drawString("DANGER", 0, 12);

            int barY = 22;
            int barH = 14;
            int barW = w - 4;

            g2.setColor(new Color(30, 20, 15));
            g2.fillRoundRect(0, barY, barW, barH, 4, 4);

            float ratio  = animLevel / 10f;
            int   fillW  = (int) (barW * ratio);
            Color barColor = ratio < 0.4f ? dangerLow : ratio < 0.75f ? dangerMid : dangerHigh;
            g2.setColor(barColor);
            if (fillW > 4) g2.fillRoundRect(0, barY, fillW, barH, 4, 4);

            if (pulsing && pulseAlpha > 0) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha * 0.35f));
                g2.setColor(dangerHigh);
                g2.fillRoundRect(-4, barY, barW + 8, barH + 8, 8, 8);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            // Tick marks
            g2.setColor(new Color(0, 0, 0, 80));
            for (int i = 1; i < 10; i++) {
                int tx = (int) (barW * i / 10f);
                g2.drawLine(tx, barY, tx, barY + barH); // vertical line
            }

            // Level label
            g2.setFont(fontMono.deriveFont(10f));
            g2.setColor(textPrimary);
            String label = (int) animLevel + " /10";
            g2.drawString(label, 0, barY + barH + 16);

            if (animLevel >= 8) {
                g2.setFont(fontBody.deriveFont(Font.ITALIC, 9f));
                g2.setColor(new Color(
                        dangerHigh.getRed(), dangerHigh.getGreen(), dangerHigh.getBlue(),
                        (int) (180 * pulseAlpha + 80)
                ));
                g2.drawString("You are being watched.", 60, barY + barH + 16);
            }

            g2.dispose();
        }
    }

    static class SuspicionPanel extends JPanel {
        // Preserves insertion order
        private final Map<NextStatementIsALie.characterNames, SuspicionBar> bars = new LinkedHashMap<>();

        SuspicionPanel() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        void update(Map<NextStatementIsALie.characterNames, Integer> scores, NextStatementIsALie game) {
            removeAll();

            JLabel label = new JLabel("Suspicion");
            label.setFont(fontTitle.deriveFont(Font.BOLD, 10f));
            label.setForeground(textDim);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
            add(label);

            for (Map.Entry<NextStatementIsALie.characterNames, Integer> entry : scores.entrySet()) {
                NextStatementIsALie.characterNames name = entry.getKey();
                boolean alive       = game.isAlive(name);
                String  displayName = game.getCharacterDisplayName(name);

                SuspicionBar bar = bars.computeIfAbsent(name, k -> new SuspicionBar());
                bar.update(displayName, entry.getValue(), alive);
                bar.setAlignmentX(LEFT_ALIGNMENT);
                add(bar);
                add(Box.createVerticalStrut(5));
            }
            revalidate();
            repaint();
        }
    }

    static class SuspicionBar extends JPanel {
        private String charName = "";
        private int target = 0;
        private float animated = 0f;
        private boolean dead = false;
        private Timer anim;

        SuspicionBar() {
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            setPreferredSize(new Dimension(236, 36));
        }

        void update(String name, int level, boolean alive) {
            this.charName = name;
            this.target   = level;
            this.dead     = !alive;
            startAnim();
        }

        private void startAnim() {
            if (anim != null) anim.cancel();
            anim = new Timer();
            anim.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    float diff = target - animated;
                    animated += diff * 0.18f;
                    if (Math.abs(diff) < 0.05f) {
                        animated = target;
                        anim.cancel();
                    }
                    SwingUtilities.invokeLater(() -> repaint());
                }
            }, 0, 16);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int   w     = getWidth();
            float ratio = Math.min(1f, animated / 10f);

            // Dead character: greyed-out
            if (dead) {
                g2.setFont(fontBody.deriveFont(9f));
                g2.setColor(new Color(60, 55, 50));
                g2.drawString("✝  " + charName, 0, 12);
                g2.dispose();
                return;
            }

            // Character name — turns red at high suspicion
            g2.setFont(fontBody.deriveFont(10f));
            g2.setColor(animated >= 7 ? new Color(200, 80, 80) : textPrimary);
            g2.drawString(charName, 0, 12);

            // Suspicion number (right-aligned)
            g2.setFont(fontMono.deriveFont(9f));
            g2.setColor(textDim);
            String     num = (int) animated + "";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(num, w - fm.stringWidth(num) - 2, 12);

            // Bar background
            int barY = 17;
            int barH = 8;
            int barW = w - 4;
            g2.setColor(new Color(30, 22, 14));
            g2.fillRoundRect(0, barY, barW, barH, 3, 3);

            // Bar fill — colour shifts from amber to crimson as suspicion rises
            Color fillColor = interpolateColor(new Color(130, 100, 40), accentCrimson, ratio);
            if (ratio > 0.01f) {
                g2.setColor(fillColor);
                g2.fillRoundRect(0, barY, (int) (barW * ratio), barH, 3, 3);
            }

            // High-suspicion glow
            if (ratio > 0.65f) {
                float glowAlpha = (ratio - 0.65f) / 0.35f * 0.3f;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
                g2.setColor(accentCrimson);
                g2.fillRoundRect(-2, barY - 2, barW + 4, barH + 4, 5, 5);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            g2.dispose();
        }

        private Color interpolateColor(Color a, Color b, float t) {
            t = Math.max(0f, Math.min(1f, t));
            return new Color(
                    (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t),
                    (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                    (int) (a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)
            );
        }
    }


    static class InventoryPanel extends JPanel {
        private final List<ClueCard> cards = new ArrayList<>();

        InventoryPanel() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        void update(List<String> itemIDs) {
            removeAll();
            cards.clear();

            JLabel label = new JLabel("Evidence");
            label.setFont(fontTitle.deriveFont(Font.BOLD, 10f));
            label.setForeground(textDim);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
            add(label);

            if (itemIDs.isEmpty()) {
                JLabel empty = new JLabel("No evidence collected.");
                empty.setFont(fontBody.deriveFont(Font.ITALIC, 10f));
                empty.setForeground(textDim);
                add(empty);
            } else {
                for (String id : itemIDs) {
                    ClueCard card = new ClueCard(id);
                    card.setAlignmentX(LEFT_ALIGNMENT);
                    cards.add(card);
                    add(card);
                    add(Box.createVerticalStrut(4));
                }
            }

            revalidate();
            repaint();
        }
    }

    static class ClueCard extends JPanel {
        private final String clueID;
        private final String displayName;
        private final String description;
        private boolean expanded = false;

        ClueCard(String clueID) {
            this.clueID      = clueID;
            Clue clue        = Clue.getByClueID(clueID);
            this.displayName = formatID(clueID);
            this.description = (clue != null) ? clue.getDescription() : "Unknown item.";

            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

            // Make the card focusable so it can receive key events
            setFocusable(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Name row
            JLabel nameLabel = new JLabel("▸  " + displayName);
            nameLabel.setFont(fontBody.deriveFont(10f));
            nameLabel.setForeground(accentGold);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            add(nameLabel);

            JTextArea descArea = new JTextArea(description);
            descArea.setFont(fontBody.deriveFont(Font.ITALIC, 9f));
            descArea.setForeground(textPrimary);
            descArea.setBackground(new Color(30, 22, 14));
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 6));
            descArea.setVisible(false);
            add(descArea);

            // FIX 6: changed MouseListener to KeyListener (Enter or Space to toggle)
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                        expanded = !expanded;
                        nameLabel.setText((expanded ? "▾  " : "▸  ") + displayName);
                        descArea.setVisible(expanded);
                        revalidate();
                        repaint();

                        // Bubble up repaint to scroll container
                        SwingUtilities.invokeLater(() -> {
                            Container p = getParent();
                            while (p != null) {
                                p.revalidate();
                                p = p.getParent();
                            }
                        });
                    }
                }
            });

            // Give the card focus on click so keyboard events work immediately
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    requestFocusInWindow();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    nameLabel.setForeground(textPrimary);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    nameLabel.setForeground(accentGold);
                    repaint();
                }
            });
        }

        private String formatID(String id) {
            if (id == null || id.isBlank()) return "???";
            String spaced = id.replaceAll("([A-Z])", " $1").trim();
            return java.lang.Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(bgCard);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
            // Highlight border when focused
            boolean focused = isFocusOwner();
            g2.setColor(expanded || focused ? accentGold : new Color(50, 38, 22));
            g2.setStroke(new BasicStroke(expanded || focused ? 1f : 0.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class AmbushWarning extends JPanel {
        private float alpha = 0f;
        private boolean showing = false;
        private float blinkVal = 0f;
        private float blinkDir = 1f;
        private Timer blinkTimer;

        AmbushWarning() {
            setOpaque(false);
            setPreferredSize(new Dimension(260, 0));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 0));
        }

        void showWarning() {
            if (showing) return;
            showing = true;
            setPreferredSize(new Dimension(260, 36));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            alpha = 1f;
            startBlink();
            revalidate();
        }

        void hideWarning() {
            showing = false;
            stopBlink();
            setPreferredSize(new Dimension(260, 0));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 0));
            revalidate();
        }

        private void startBlink() {
            if (blinkTimer != null) blinkTimer.cancel();
            blinkTimer = new Timer();
            blinkTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    blinkVal += 0.05f * blinkDir;
                    if (blinkVal >= 1f) { blinkVal = 1f; blinkDir = -1f; }
                    if (blinkVal <= 0f) { blinkVal = 0f; blinkDir =  1f; }
                    SwingUtilities.invokeLater(() -> repaint());
                }
            }, 0, 30);
        }

        private void stopBlink() {
            if (blinkTimer != null) blinkTimer.cancel();
            blinkVal = 0f;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!showing) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Pulsing red background
            float bgAlpha = 0.55f + blinkVal * 0.3f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bgAlpha));
            g2.setColor(dangerHigh);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Warning text — pulses with blink
            g2.setFont(fontTitle.deriveFont(Font.BOLD, 10f));
            int textAlpha = (int) (180 + blinkVal * 75);
            g2.setColor(new Color(255, 220, 180, textAlpha));
            String msg = "⚠  DANGER — Someone is close.";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(msg)) / 2;
            g2.drawString(msg, tx, getHeight() / 2 + fm.getAscent() / 2 - 2);

            g2.dispose();
        }
    }

    static class ThinScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(80, 60, 30);
            trackColor = new Color(20, 15, 10);
            thumbHighlightColor = new Color(120, 90, 40);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return invisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return invisibleButton();
        }

        private JButton invisibleButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 4, thumbBounds.height - 4, 4, 4);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}