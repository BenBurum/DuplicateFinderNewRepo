package com.agile.findduplicates;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Ben on 10/4/2014.
 */
public class FinderPanel extends JPanel {
    private JPanel panel1;
    private JTree duplicatesTree;
    private JTextArea outputTextArea;
    private JButton listFilesButton;
    private JButton ignoreButton;
    private JButton deleteButtion;
    private JScrollPane scrollPane1;
    private JTextField dirPathTextField;
    private JCheckBox fullNameCheckBox;
    private JCheckBox checksumCheckBox;
    private JCheckBox recursiveCheckBox;
    private JButton analyzeButton;
    private Map fullNameDupesMap;


    /**
     * Main panel where the action happens!
     */
    public FinderPanel() {

        listFilesButton.addActionListener(new ActionListener() {

            /**
             * Action performed when the List Files button is pressed.
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                outputTextArea.setText("");

                try {
                    ArrayList<File> filesList = new ArrayList<File>();
                    File dirFile = new File(dirPathTextField.getText());

                    //Checks whether to find files recursively or not, and then sets the ArrayList filesList to the result.
                    if (recursiveCheckBox.isSelected()){
                        filesList = listFilesRecursive(dirFile, filesList);
                    }
                    else {
                        File[] fileList = dirFile.listFiles();
                        for (File file : fileList){
                            filesList.add(file);
                        }
                    }

                    //Prints each file.toString() on its own line.
                    outputTextArea.append("List of files:\n\n" );
                    for (File file : filesList) {
                        outputTextArea.append(file.toString() + "\n");
                    }

//                    if (fullNameCheckBox.isSelected()) {
//                        try {
//                            findDuplicatesFullName(dirPathTextField.getText());
//                        } catch (Exception ex) {
//                            System.out.println("Error matching by full File Name: " + ex);
//                        }
//                    }
                    System.out.println("You pressed the button!");
                } catch (Exception ex) {
                    System.err.println("Error analyzing directory: " + e);
                }
            }
        });

        /**
         * Action performed when the Analyze button is pressed.
         */
        analyzeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {


                try {
                    ArrayList<File> filesList = new ArrayList<File>();
                    File dirFile = new File(dirPathTextField.getText());

                    //Checks whether to find files recursively or not, and then sets the ArrayList filesList to the result.
                    if (recursiveCheckBox.isSelected()){
                        filesList = listFilesRecursive(dirFile, filesList);
                    }
                    else {
                        File[] fileList = dirFile.listFiles();
                        for (File file : fileList){
                            filesList.add(file);
                        }
                    }

                        if (fullNameCheckBox.isSelected()){                                       //If the "Name w/ Extension box is checked.
                              HashMap fullNameDupes = findDuplicatesFullName(filesList);
                              System.out.println("Full Name Hashmap created.");
                            outputTextArea.append(fullNameDupes.toString());
                            createUIComponents(fullNameDupes);
                        }


                    System.out.println("You pressed the button!");
                } catch (Exception ex) {
                    System.err.println("Error analyzing directory: " + e);
                }
            }
        });
    }

    /**
     * Takes an ArrayList of Files as a parameter and returns a HashMap with (String) Filenames as the keys and ArrayLists of
     * Files as the values.  Each key-value pair corresponds to Filename -> List of files with that name.
     * @param filesList
     * @return
     */
    private HashMap<String, ArrayList<File>> findDuplicatesFullName(ArrayList<File> filesList){
        ArrayList<File> copyList = filesList;
        HashMap fileInstanceMap = new HashMap<String, ArrayList<File>>();
        for (File f : filesList){
            ArrayList<File> dupeList = new ArrayList<File>();
            String fName = f.getName();
            for (File copyFile : copyList){
                String copyName = copyFile.getName();
                if (copyName.equals(fName)){
                    dupeList.add(copyFile);
                }
            }
            fileInstanceMap.put(fName, dupeList);
        }

        return fileInstanceMap;

    }



    /**
     * Takes a directory and an ArrayList (initially should be empty) as input and returns an ArrayList of every
     * file in the directory, recursively.
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
            outputTextArea.append("\nError: listFilesRecursive called on invalid target:\n" + dir + "\nCheck the path entered into the field above.\n");
        }

        Collections.reverse(recursiveList);
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

    private void createUIComponents(HashMap map) {


        populateFileTree(map);


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

    /**
     * Failed attempt to repopulate the JTree when passed a HashMap.
     * @param dupesMap
     */
    public void populateFileTree(HashMap dupesMap){
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Checksum2");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("TestFile2");
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("TestFile3");

        //add the child nodes to the root node
        root.add(vegetableNode);
        root.add(fruitNode);

        //create the tree by passing in the root node
        duplicatesTree = new JTree(root);
        add(duplicatesTree);
    }
}
