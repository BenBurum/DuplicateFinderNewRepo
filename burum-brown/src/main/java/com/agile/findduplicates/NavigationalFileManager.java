package com.agile.findduplicates;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * This class is used to perform file operations.  It implements the FileManager interface.  Its working directory defaults to the home directory specified by System.getProperty("user.home"), which will vary based on operating system and JVM implementation.  Currently, this class can list files in its directory, change directories, remove files, remove directories, print the current directory's name and absolute path, and identify duplicate files in the current directory.  Todo: hook up with GUI.
 *
 */
public class NavigationalFileManager implements FileManager {

    private static FileManager FM_instance;

    private File directory;

    /**
     * Default constructor. Its default directory is the user's home directory, which may not be static.
     *
     * @throws java.lang.IllegalStateException Thrown if the user's home directory is not defined according to System.getProperty("user.home"), or if the object Java defines as home directory is not actually a directory.
     */

    public NavigationalFileManager () {
        String homeDir = System.getProperty("user.home");
        if (!homeDir.isEmpty()) {
            directory = new File(homeDir);
            if (!directory.isDirectory()) {
                // System.getProperty("user.home") should return a directory, if it did not something is wrong and we throw an exception
                throw new IllegalStateException();
            }
        } else {
            // if System.getProperty("user.home") is empty, something is wrong and we throw an exception
            throw new IllegalStateException();
        }
    }

    /**
     * Overloaded Constructor to be called by the Swing panel.  Sets directory variable to the directory
     * specified by the user.
     *
     * @param dirPath The absolute path of the directory to be used.
     * @throws java.lang.IllegalStateException If the path specified does not lead to a directory, this will throw an IllegalStateException.
     * @throws java.lang.IllegalArgumentException If the path specified is empty, this will throw an IllegalArgumentException.
     * @throws java.lang.NullPointerException If the path specified does not resolve to a file, this will throw a NullPointerException.
     */
    public NavigationalFileManager (String dirPath) {
        if (!dirPath.isEmpty()) {
            directory = new File(dirPath); // If the user did not provide a valid file path, this statement will throw a NullPointerException
            if (!directory.isDirectory()) {
                // If the file specified wasn't a directory, throw an IllegalStateException
                throw new IllegalStateException();
            }
        } else {
            // If the path provided was empty, throw an IllegalArgumentException
            throw new IllegalArgumentException();
        }
    }

    /**
     * Lists the names of all files in the current directory.
     *
     * @return A string array of the names of the files in the current directory.
     */
    public synchronized String[] ls () {
        return directory.list();
    }

    /**
     * Changes the active directory to the specified directory, if a directory is specified.
     *
     * @param dir The File that will become the new active directory. If the file is not a directory, no changes will be made.
     */
    public synchronized void cd (File dir) {
        if (dir.isDirectory()) {
            directory = dir;
        }
    }

    /**
     * Changes the active directory to the specified directory.
     *
     * @param dirName The name of the new directory. Must be inside the currently active directory; that is, a member of ls().
     */
    public synchronized void cd (String dirName) {
        cd(getFileFromString(dirName));
    }

    /**
     * Changes the active directory to the parent of the current directory.  If there is no parent directory, no change will be made.
     */
    public synchronized void cd () {
        if (directory.getParentFile() != null) {
            directory = directory.getParentFile();
        }
    }

    /**
     * Deletes the specified file.  Will delete folders, but only if they are empty.  Wrapper for File.delete().
     *
     * @param file The File to be deleted.
     * @return Returns true if the file is deleted, false if not. Attempting to delete a non-empty folder will always return false.
     */
    public synchronized boolean rm (File file) {
        return file.delete();
    }

    /**
     * Deletes the specified file in the current directory.  Will delete folders, but only if they are empty.
     *
     * @param fileName The name of the file to be deleted.  Must be inside the current directory.
     * @return Returns true if the file is deleted, false if not.  Attempting to delete a non-empty folder will always return false.
     */
    public synchronized boolean rm (String fileName) {
        return rm(getFileFromString(fileName));
    }

    /**
     * Removes the specified directory/file and all its contents.  Deletes subfolders/files recursively.
     *
     * @param fileName The name of the folder/file to be deleted.  Must be inside the current directory.
     * @return Returns true if the delete is successful, false if not.  Warning: even if it returns false, some files/folders may still have been deleted.
     */
    public synchronized boolean rmdir (String fileName) {
        return rmdir(getFileFromString(fileName));
    }

    /**
     * Removes the specified directory/file and all its contents.  Deletes subfolders/files recursively.
     *
     * @param file The File to be deleted.
     * @return Returns true if the delete is successful, false if not.  Warning: even if it returns false, some files/folders may still have been deleted.
     */
    private synchronized boolean rmdir (File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!rmdir(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /**
     * Returns the absolute path of the current directory.
     *
     * @return A String containing the absolute path of the current directory.
     */
    public synchronized String pwd () {
        return directory.getAbsolutePath();
    }

    /**
     * Returns the name of the current directory.
     * @return A String containing the name of the current directory.
     */
    public synchronized String currentDir () {
        return directory.getName();
    }


    /**
     * Find all duplicate files in the current directory using checksums (CRC32).
     *
     * @return A string array containing the file names of all duplicate files.
     */
    public synchronized String[] findDuplicates () {
        Multimap<Long,String> checksums = getChecksums();
        ArrayList<String> duplicates = new ArrayList<String>();

        for (Long key : checksums.keySet()) {
            if (checksums.get(key).size() > 1) {
                for (String s : checksums.get(key)) {
                    duplicates.add(s);
                }
            }
        }

        // complex return statement converts the variable duplicates from an ArrayList to a String[]
        // TODO: low priority, clean this statement up
        return Arrays.copyOf(duplicates.toArray(), duplicates.toArray().length, String[].class);
    }

    /**
     * Helper method for this class. We only expose methods that take Strings as arguments, but it's easier internally to take Files as arguments.  This function serves as the conversion factor. Given a string, it will return the file in the current directory that shares that name.
     *
     * @param fileName The name of the file.
     * @return The File object specified by the parameter.
     */
    private synchronized File getFileFromString (String fileName) {
        for (File f : directory.listFiles()) {
            if (f.getName().equals(fileName)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Calculates checksums (CRC32) for all file in the current directory.
     *
     * @return A Multimap containing all the checksums, as well as their corresponding files.
     */
    private synchronized Multimap<Long,String> getChecksums () {

        Multimap<Long,String> checksums = HashMultimap.create();

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




}
