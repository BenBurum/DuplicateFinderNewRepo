package com.agile.findduplicates;

import javax.swing.*;

/**
 * Created by Ben on 10/4/2014.
 */
public class FinderPanel {
    private JPanel panel1;
    private JTree tree1;
    private JTextArea textArea1;
    private JButton analyzeButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FinderPanel");
        frame.setContentPane(new FinderPanel().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
