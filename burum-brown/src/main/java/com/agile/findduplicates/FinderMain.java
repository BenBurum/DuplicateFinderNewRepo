package com.agile.findduplicates;

import javax.swing.*;


/**
 * Created by Ben on 10/26/2014.
 */
public final class FinderMain {


    /**
     * Creates the JFrame and puts the panel in it with the GUI.
     * @param panel
     */
    public static void createJFrame(FinderPanel panel){
        JFrame frame = new JFrame();
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }




    public static void main(String[] args){
        FinderPanel panel = new FinderPanel();
        panel.run();
        createJFrame(panel);

    }
}
