package com.agile.findduplicates;

public interface FileManager {
    /**
     * Finds all duplicate files in the scope of the FileManager.
     *
     * @return A string array containing the file names of all duplicate files.
     */
    public String[] findDuplicates ();

    /**
     * Lists the names of all files in the current directory.
     *
     * @return A string array of the names of the files in the current directory.
     */
    public String[] ls ();

    /**
     * Changes the active directory to the parent of the current directory.  If there is no parent directory, no change will be made.
     */
    public void cd ();

    /**
     * Changes the active directory to the specified directory.
     *
     * @param dir The name of the new directory.
     */
    public void cd (String dir);

    /**
     * Deletes the specified file.
     *
     * @param file The name of the file to be deleted.
     * @return Returns true if the file is deleted, false if not.
     */
    public boolean rm (String file);

    /**
     * Removes the specified directory/file and all its contents.  Deletes subfolders/files recursively.
     *
     * @param dir The name of the folder/file to be deleted.
     * @return Returns true if the delete is successful, false if not.
     */
    public boolean rmdir (String dir);

    /**
     * Returns the absolute path of the current directory.
     *
     * @return A String containing the absolute path of the current directory.
     */
    public String pwd ();

    /**
     * Returns the name of the current directory.
     *
     * @return A String containing the name of the current directory.
     */
    public String currentDir();
}
