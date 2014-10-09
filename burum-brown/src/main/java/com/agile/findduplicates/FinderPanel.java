package com.agile.findduplicates;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private JTextField dirPathTextField;
    private JCheckBox fullNameCheckBox;
    private JCheckBox checksumCheckBox;
    private JCheckBox findRecursivelyCheckBox;
    private Map duplicatesMap;


    /**
     * Main panel where the action happens!
     */
    public FinderPanel() {

        analyzeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    ArrayList<File> filesList = new ArrayList<File>();
                    File dirFile = new File(dirPathTextField.getText());
                    filesList = listFilesRecursive(dirFile, filesList);
                    System.out.println(filesList.toString());
//                    if (fullNameCheckBox.isSelected()) {
//                        try {
//                            findDuplicatesFullName(dirPathTextField.getText());
//                        } catch (Exception ex) {
//                            System.out.println("Error matching by full File Name: " + ex);
//                        }
//                    }
                    System.out.println("You pressed the button!");
                }catch(Exception ex){
                    System.err.println("Error analyzing directory: " + e);
                }
            }
        });
    }

//    private Map<File, String> findDuplicatesFullName(String dirPath) {
//        try {
//            duplicatesMap = new HashMap<File, String>();
//            File path = new File(dirPath);
//            File[] files = path.listFiles();
//            ArrayList<File> filesList;
//            ArrayList<File> filesCopy = new ArrayList<File>();
//
//            try {
//                filesList = listFilesRecursive(path);
//                System.out.println(filesList);
//            }catch (Exception ex){
//                System.out.println("Error creating filesCopy: " + ex);
//            }
////            while (filesCopy.size() != 0){
////            for (File file : files) {
////                Map filesMap = new HashMap<File, Path>();
////                for (File copyFile : filesCopy) {
////
////                        if (file.getName().equals(copyFile.getName()) && (file.length() == (copyFile.length()))) {
////                            filesMap.put(file, file.getAbsolutePath());
////                            filesCopy.remove(copyFile);
////                            duplicatesMap.add(filesMap);
////                        }
////                    }
////
////                }
////            }
//            System.out.println("findDuplicatesFullName run successfully: duplicatesMap = " + duplicatesMap);
//            return duplicatesMap;
//
//        }catch (Exception ex){
//            System.out.println("Error matching by full File Name from within Method: " + ex);
//            return duplicatesMap;
//        }
//
//
//    }

    /**
     * Takes a directory and an ArrayList (initially should be empty) as input and returns an ArrayList of every file in the directory, recursively.
     *
     * @param dir
     * @return
     */
    public ArrayList<File> listFilesRecursive (File dir, ArrayList<File> inList){
        ArrayList<File> recursiveList = inList;
        if (dir.isDirectory()){
            File[] dirFiles = dir.listFiles();
            for (File file : dirFiles) {
                if (!file.isDirectory()) {
                    recursiveList.add(file);

                } else {

                    listFilesRecursive(file, recursiveList);
                }
            }
        }
        else {
            System.err.println("Error: listFilesRecursive called on invalid target: " + dir);
        }


        return recursiveList;
    }




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
