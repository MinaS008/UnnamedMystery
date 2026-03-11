import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


//Why am I using float? - Float can be used for cases that require memory efficiency, such as real-time graphics
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
        fontBody = new Font("Serif", Font.PLAIN, 11);
        fontMono = new Font("Monospaced", Font.PLAIN, 10);
    }

    private final dangerPanel dangerPanel;
    private final suspicionPanel suspicionPanel;
    private final inventoryPanel inventoryPanel;
    private final ambushWarning ambushWarning;

    private NextStatementIsALie game;
    private boolean visible = false;

    public GameSidebarPanel(){
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
        inventoryPanel.setAlignmnetX(LEFT_ALIGNMENT);
        inner.add(inventoryPanel);

        //Putting in a JScrollPane so longer inventories don't clip
        JScrollPane scroll = new JScrollPane(inner, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
    public void onGameEvent(NextStatementIsALie.gameEvent event, NextStatementIsALie g){
        this.game = g;

        SwingUtilities.invokeLater(() ->{
            switch(event){
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
                    suspicionPanel.update(g.getAllSuspicionScores());
                    break;

                default:
                    break;
            }
            repaint();
        });
    }
    private void showSidebar(){
        setVisible(true);
        visible = true;
        setOpaque(false);
        Timer t = new Timer();
        final float[] alpha = {0f};
        t.scheduleAtFixedRate(new TimerTask() {
            @Override public void run(){
                alpha[0] = Math.min(1f, alpha[0] + 0.05f);
                SwingUtilities.invokeLater(() -> repaint());
                if(alpha[0] >= 1f) t.cancel();
            }
        }, 0, 30);
    }

    private void hideSidebar(){
        setVisible(false);
        visible = false;
        ambushWarning.hide();
    }

    private void checkAmbushWarning(NextStatementIsALie g){
        if(g.getDangerLevel() >= 8){
            ambushWarning.show();
        } else{
            ambushWarning.hide();
        }
    }

    protected void paintComponent(Graphics g){
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

    private void paintGrain(Graphics2D g2, int w, int h){
        Random rng = new Random(42); //fixed seed - consistent texture
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.025f)); //Sets opacity to 2.5% opacity so very subtle
        g2.setColor(Color.WHITE);
        for (int i = 0; i < w * h / 8; i++) { //how many pixels of noise to draw
            int x = rng.nextInt(w); //Random position
            int y = rng.nextInt(h);
            g2.fillRect(x, y, 1, 1); //1x1 pixel square
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); //opacity is reset so that further drawing isn't transparent
    }

    static class DangerPanel extends JPanel{
        private int currentLevel = 0;
        private int targetLevel = 0;
        private float animLevel = 0f;
        private Timer animTimer;
        private boolean pulsing = false;

        DangerPanel(){
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            setPreferredSize(new Dimension(236, 70));
        }

        void update(int newLevel){
            targetLevel = newLevel;
            startAnimation();
            if(newLevel >= 8) startPulse();
            else stopPulse();
        }

        private void startAnimation(){
            if(animTimer != null) animTimer.cancel();
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
        private float pulseAlpha = 0f; //opacity of the pulse
        private float pulseDir   = 1f; //direction of the pulse - increasing or decreasing
        private Timer pulseTimer;

        private void startPulse(){
            if(pulsing) return;
            pulsing = true;
            pulseTimer = new Timer();
            pulseTimer.scheduleAtFixedRate(new TimerTask() {
                @Override public void run() {
                    pulseAlpha += 0.04f * pulseDir; //increase or decrease by 0.04
                    if (pulseAlpha >= 1f) { pulseAlpha = 1f; pulseDir = -1f; } //clamp to 1 if it ever reaches, reverse direction
                    if (pulseAlpha <= 0f) { pulseAlpha = 0f; pulseDir =  1f; } //clamp to -1 if it ever reaches
                    SwingUtilities.invokeLater(() -> repaint());
                }
            }, 0, 30);
        }

        private void stopPulse(){
            pulsing = false;
            if(pulseTimer != null) pulseTimer.cancel();
            pulseAlpha = 0f;
        }

        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();

            g2.setFont(fontTitle.deriveFont(Font.BOLD, 10f));
            g2.setColor(textDim);
            g2.drawString("DANGER", 0, 12);



        }



    }



}
