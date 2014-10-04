package com.agile.findduplicates;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by Ben on 10/4/2014.
 */
public class FinderPanel extends JPanel {
    private JPanel panel1;
    private JTree duplicatesTree;
    private JTextArea outputTextArea;
    private JButton analyzeButton;
    private JButton ignoreButton;
    private JButton deleteButtion;
    private JScrollPane scrollPane1;
    private JTextField textField1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FinderPanel");
        frame.setContentPane(new FinderPanel().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates UI components
     */
    private void createUIComponents() {

        populateFileTree();


        //duplicatesTree = new DuplicatesFileTree();
        //duplicateFileTree = new javax.swing.JTree(populateJTree.addNodes(null, null));
    }

    /**
     * Default constuctor showing an example of how to populate the JTree.  In practice the analyze button will
     * call an overloaded constructor passing the duplicate files as an argument.
     */
    public void populateFileTree(){
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Checksum1");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("TestFile1");
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("TestFile2");

        //add the child nodes to the root node
        root.add(vegetableNode);
        root.add(fruitNode);

        //create the tree by passing in the root node
        duplicatesTree = new JTree(root);
        add(duplicatesTree);
    }
}
