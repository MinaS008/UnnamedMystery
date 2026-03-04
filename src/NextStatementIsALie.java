import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class NextStatementIsALie {
    public static void main(String[] args) {
        JFrame frame = new JFrame("The Next Statement is a Lie");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.gray);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }


}

