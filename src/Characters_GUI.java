import javax.swing.*;
import java.awt.*;

public class Characters_GUI extends JPanel{
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        drawMother(g, 50, 200);
        drawFather(g, 150, 200);
        drawOlderSister(g, 250, 200);
        drawLittleBrother(g, 350, 220);
        drawUncle(g, 450, 200);
        drawCousin(g, 550, 210);
        drawFamilyFriend(g, 650, 200);
    }

    void drawPerson(Graphics g, int x, int y, Color color, int height){
        g.setColor(new Color(255, 224, 189));
        g.fillOval(x, y - height - 40, 30, 30); //head

        g.setColor(color);
        g.fillRect(x + 10, y - height, 10, height); //body

        g.drawLine(x + 10, y - height + 20, x - 10, y - height + 40); //Left Arm
        g.drawLine(x+20, y - height + 20, x+40, y-height+40); //Right Arm

        g.drawLine(x + 10, y, x, y + 30); //left leg
        g.drawLine(x + 20, y, x + 30, y + 30); //right lef
    }

    void drawMother(Graphics g, int x, int y){
        drawPerson(g, x, y, Color.PINK, 60);
    }
    void drawFather(Graphics g, int x, int y){
        drawPerson(g, x, y, Color.BLUE, 70);
    }
    void drawOlderSister(Graphics g, int x, int y){
        drawPerson(g, x, y, Color.MAGENTA, 65);
    }
    void drawLittleBrother(Graphics g, int x, int y){
        drawPerson(g, x, y, Color.GREEN, 50);
    }
    void drawUncle(Graphics g, int x, int y){
        drawPerson(g, x, y, Color.ORANGE, 70);
    }
    void drawCousin(Graphics g, int x, int y){
        drawPerson(g, x, y, Color.CYAN, 55);
    }
    void drawFamilyFriend(Graphics g, int x, int y){
        drawPerson(g, x, y, Color.GRAY, 65);
    }
}
