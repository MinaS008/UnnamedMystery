import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.*;
import java.awt.event.ActionEvent;

public class NextStatementIsALie extends JFrame {
    private JTextArea narrativeArea;
    private JButton[] choiceButtons;
    private Scene currentScene;

    public NextStatementIsALie() {
        setTitle("Murder Mystery");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        narrativeArea = new JTextArea();
        narrativeArea.setEditable(false);
        narrativeArea.setLineWrap(true);
        narrativeArea.setWrapStyleWord(true);
        add(new JScrollPane(narrativeArea), BorderLayout.CENTER);

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new GridLayout(1, 3));

        choiceButtons = new JButton[3];
        for (int i = 0; i < 3; i++) {
            choiceButtons[i] = new JButton();
            choiceButtons[i].setVisible(false);
            final int index = 1;

            choiceButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleChoice(index);
                }
            });

            choicePanel.add(choiceButtons[i]);
        }
        add(choicePanel, BorderLayout.SOUTH);
        createSampleStory();
        setVisible(true);
    }

    private void handleChoice(int index) {
        ArrayList<Choice> choices = currentScene.getChoices();
        if (index < choices.size()) {
            currentScene = choices.get(index).getNextScene();
            updateScene();
        }
    }

    private void updateScene(){
        narrativeArea.setText(currentScene.getText());
        ArrayList<Choice> choices = currentScene.getChoices();

        for(int i = 0; i<choiceButtons.length; i++){
            if(i < choices.size()){
                choiceButtons[i].setText(choices.get(i).getText());
                choiceButtons[i].setVisible(true);
            } else {
                choiceButtons[i].setVisible(false);
            }
        }
    }

    private void createSampleStory(){

    }

    public static void main(String[] args) {
        new NextStatementIsALie();
    }
}
