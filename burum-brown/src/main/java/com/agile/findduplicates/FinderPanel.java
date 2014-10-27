package com.agile.findduplicates;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.MultiMap;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ContainerAdapter;
import java.io.File;
import java.util.*;

/**
 * Created by Ben Burum and Eric Brown as a group project for Java308 with Spencer Marks at Umass Lowell.
 */
public class FinderPanel extends JPanel implements Runnable {
    private JPanel panel1;
    private JTree duplicatesTree;
    private JTextArea outputTextArea;
    private JButton listFilesButton;
    private JButton ignoreSelectedButton;
    private JButton deleteButtion;
    private JScrollPane scrollPane1;
    private JTextField dirPathTextField;
    private JCheckBox fullNameCheckBox;
    private JCheckBox checksumCheckBox;
    private JCheckBox recursiveCheckBox;
    private JButton analyzeButton;
    private JList matchesList;
    private JList dupesList;
    private JButton testDataButton;
    private JButton dupesListButton;
    private JButton ignoreSelectedButton2;
    private JButton deleteSelectedButton;
    private Map fullNameDupesMap;
    private ArrayListMultimap<File, File> dupesMap;


    /**
     * Main panel where the action happens!
     */
    public FinderPanel() {

        /**
         * Lists files in the directory, either recursively or not.  Doesn't really belong here in the gui panel but it's pretty useful code either way.
         */
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

        /**
         *  Creates a dummy Multimap to feed into the JLists when the "use test data" button is pressed.
         */
        testDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayListMultimap<File, File> dupeMap = ArrayListMultimap.create();
                dupeMap.asMap();
                dupeMap.put(new File("New Text Document.txt"), new File("C:\\Users\\Ben\\TestDir\\New Text Document.txt"));
                dupeMap.put(new File("New Text Document(1).txt"), new File("C:\\Users\\Ben\\TestDir\\New Text Document(1).txt"));
                dupeMap.put(new File("New Text Document(1).txt"), new File("C:\\Users\\Ben\\TestDir\\New Folder\\New Text Document(1).txt"));

                dupeMap.put(new File("New Text Document.txt"), new File("C:\\Users\\Ben\\TestDir\\New Folder\\New Text Document.txt"));
                dupesMap = dupeMap;
                populateMatchesList(dupeMap);
            }
        });
        matchesList.addComponentListener(new ComponentAdapter() {
        });
        matchesList.addContainerListener(new ContainerAdapter() {
        });
        dupesListButton.addActionListener(new ActionListener() {
            /**
             * Invoked when Show Duplicates button is pressed.  Populates the JList on the right with the values corresponding to the key which is currently selected.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel dupesModel = new DefaultListModel();
                try {

                    File key = new File(matchesList.getSelectedValue().toString());
                    List<File> duplicates = dupesMap.get(key);
                    for (File dupeFile : duplicates) {
                        dupesModel.addElement(dupeFile);
                    }
                    dupesList.setModel(dupesModel);
                }catch(Exception ex){
                    outputTextArea.append("Error: invalid selection." + ex);
                }
            }
        });
    }

    /**
     *Method to populate the List on the left with the keys from the MultiMap.
     */
       public void populateMatchesList(Multimap<File, File> map){

           DefaultListModel matchesModel = new DefaultListModel();


           for (File key : map.keySet()){
               matchesModel.addElement(key);


           }
           matchesList.setModel(matchesModel);

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
     * file in the directory, recursively.  Doesn't belong in the GUI panel, but useful code nonetheless I think.
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

    /**
     * Allows the panel to be created from FinderMain.java.
     */
    public void run(){
        JFrame frame = new JFrame("FinderPanel");
        frame.setContentPane(new FinderPanel().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Not sure if it still needs a main method in this class.
     * @param args
     */
    public static void main(String[] args) {

        JFrame frame = new JFrame("FinderPanel");
        frame.setContentPane(new FinderPanel().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates UI components.  Not being used currently since I'm not using a FileTree.
     */
    private void createUIComponents() {


        populateFileTree();


        //duplicatesTree = new DuplicatesFileTree();
        //duplicateFileTree = new javax.swing.JTree(populateJTree.addNodes(null, null));
    }

    /**
     * overloaded constructor.  See comment above.
     * @param map
     */
    private void createUIComponents(HashMap map) {


        populateFileTree(map);



        //duplicatesTree = new DuplicatesFileTree();
        //duplicateFileTree = new javax.swing.JTree(populateJTree.addNodes(null, null));
    }

    /**
     * Not sure.  Looks like this method is never called.
     * @param map
     */
    private void createUIComponents(MultiMap map){
        populateMatchesList((Multimap<File, File>) map);
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
