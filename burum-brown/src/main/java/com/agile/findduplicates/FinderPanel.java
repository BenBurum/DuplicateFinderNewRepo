package com.agile.findduplicates;

import javax.swing.*;

/**
 * Created by Ben on 10/4/2014.
 */
public class FinderPanel {
    private JPanel panel1;
    private JTextField dirPathTextField;
    private JButton findDirectoryButton;
    private JTextArea textArea1;
    private JButton analyzeButton;
    private JTree duplicateFileTree;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FinderPanel");
        frame.setContentPane(new FinderPanel().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
