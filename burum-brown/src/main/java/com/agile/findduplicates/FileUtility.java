package com.agile.findduplicates;

import com.sun.javafx.tools.packager.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.zip.CRC32;

/**
 * This class is used to perform file operations.  It is currently designed as a singleton class, since file operations ought not to run concurrently, multiple instances running might confuse each other, etc.  Its working directory defaults to the home directory specified by environment variables.  Hasn't been tested on Windows yet, I don't expect it to run until I learn what Windows uses for env variables.  Currently, this class can list files in its directory, and identify duplicate files in the current directory.  Todo: add the ability to change directories, remove files, hook up with GUI.
 *
 * */
public class FileUtility {

    private File directory;
    private static FileUtility INSTANCE;

    private FileUtility () throws Exception {
        Map<String,String> env = System.getenv();
        if (env.containsKey("HOME")) {
            String homeDir = env.get("HOME");
            if (!homeDir.isEmpty()) {
                directory = new File(homeDir);
                if (!directory.isDirectory()) {
                    throw new Exception();
                }
            }
            else {
                throw new Exception();
            }
        } else {
            throw new Exception();
        }
    }

    /**
     * Accessor method for the class.
     *
     * @return The singleton instance of FileUtility.
     * */
    public static FileUtility getInstance () {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            try {
                INSTANCE = new FileUtility();
                return INSTANCE;
            } catch (Exception e) {
                Log.debug("Home directory not defined");
                return null;
            }
        }
    }

    /**
     * Lists the names of all files in the current directory.
     *
     * @return A string array of the names of the files in the current directory.
     * */
    public String[] ls () {
        return directory.list();
    }

    /**
     * Calculates checksums (CRC32) for all file in the current directory.
     *
     * @return A DoubleMap containing all the checksums, as well as their corresponding files.
     * */
    private DoubleMap<Long,String> getChecksums () {

        DoubleMap<Long,String> checksums = new DoubleMap<Long,String>();

        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    CRC32 crc = new CRC32();
                    byte[] buffer = new byte[256];
                    int bytesRead;
                    //CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32());
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        crc.update(buffer, 0, bytesRead);
                    }

                    checksums.put(crc.getValue(), file.getName());
                } catch (FileNotFoundException e) {
                    System.err.println("FileNotFoundException");
                } catch (IOException e) {
                    System.err.println("IOException");
                    System.exit(1);
                }
            }
        }

        return checksums;
    }

    /**
     * Find all duplicate files in the current directory using checksums (CRC32).
     *
     * @return A string array containing the file names of all duplicate files.
     * */
    public String[] findDuplicates () {
        DoubleMap<Long,String> checksums = getChecksums();
        HashSet<Long> hashSet = new HashSet<Long>();
        ArrayList<String> duplicates = new ArrayList<String>();

        for (Long key : checksums.keyList()) {

            // If the hashset already contains the key, we have a duplicate
            if (hashSet.contains(key)) {
                // add the duplicate to our list of duplicates
                duplicates.add(checksums.getValue(key));
            } else {
                // If it doesn't contain the key, add it for the next loop
                hashSet.add(key);
            }
        }

        // complex return statement converts the variable duplicates from an ArrayList to a String[]
        // TODO: low priority, clean this statement up
        return Arrays.copyOf(duplicates.toArray(), duplicates.toArray().length, String[].class);
    }



}
