package com.agile.findduplicates;

public interface FileManager {
    public String[] findDuplicates ();
    public String[] ls ();
    public void cd ();
    public void cd (String dir);
    public boolean rm (String file);
    public boolean rmdir (String dir);
    public String pwd ();
    public String currentDir();
}
