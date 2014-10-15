package com.agile.findduplicates;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.Assert.*;

/**
 * Unit test for simple App.
 */
public class FileTest {

    private static final String DIR_NAME = "NavigationalFileManager";
    private static final String DIR1 = "dir1";
    private static final String DIR2 = "dir2";
    private static final String DIR3 = "dir3";
    private static final String FILE1 = "test.txt";
    private static final String FILE2 = "duplicate.txt";

    private File directory;

    public boolean rm (File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!rm(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    @Before
    public void setup () {

        try {
            Path p = Paths.get(System.getProperty("user.dir"), DIR_NAME);
            directory = Files.createDirectory(p).toFile();
            String directoryPath = directory.getAbsolutePath();

            Files.createDirectory(Paths.get(directoryPath, DIR1));

            String dir1Path = Paths.get(directoryPath, DIR1).toString();

            Files.createFile(Paths.get(dir1Path, FILE1));
            Files.createFile(Paths.get(dir1Path, FILE2));
            Files.createDirectory(Paths.get(directoryPath, DIR2));
            Files.createFile(Paths.get(directoryPath, FILE1));
            Files.createDirectory(Paths.get(directoryPath, DIR3));
        } catch (IOException e) {
            System.out.println("Exception");
            System.exit(1);
        }
    }

    @After
    public void tearDown () {
        rm(directory);
        directory = null;
    }

    @Test
    public void testLs () {
        String[] files = {
            DIR1, DIR2, DIR3, FILE1
        };
        FileManager fm = new NavigationalFileManager(directory.getAbsolutePath());
        String[] list = fm.ls();
        for (int i = 0; i < list.length; i++) {
            assertEquals(list[i],files[i]);
        }
    }

    @Test
    public void testCd () {
        FileManager fm = new NavigationalFileManager(directory.getAbsolutePath());
        fm.cd(DIR1);
        assertEquals(DIR1, fm.currentDir());
        fm.cd();
        assertEquals(DIR_NAME, fm.currentDir());
    }

    @Test
    public void testDuplicates () {
        FileManager fm = new NavigationalFileManager(directory.getAbsolutePath());
        String[] duplicates = fm.findDuplicates();
        String[] files = {
            FILE1, FILE2
        };
        for (int i = 0; i < duplicates.length; i++) {
            assertEquals(duplicates[i],files[i]);
        }
    }

    @Test
    public void testRm () {
        FileManager fm = new NavigationalFileManager(directory.getAbsolutePath());

        assertTrue(fm.rm(FILE1));
        boolean remains = false;
        for (String s : fm.ls()) {
            if (s.equals(FILE1)) {
                remains = true;
            }
        }
        assertFalse(remains);
    }

    @Test
    public void testPwd () {
        FileManager fm = new NavigationalFileManager(directory.getAbsolutePath());
        assertEquals(directory.getAbsolutePath(), fm.pwd());
    }

    @Test
    public void testCurrentDir () {
        FileManager fm = new NavigationalFileManager(directory.getAbsolutePath());
        assertEquals(directory.getName(), fm.currentDir());
    }

    @Test
    public void testRmdir () {
        String name = directory.getName();
        FileManager fm = new NavigationalFileManager(directory.getParentFile().getAbsolutePath());
        assertTrue(fm.rmdir(name));
    }

}
