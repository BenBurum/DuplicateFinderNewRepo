package test.com.agile.findduplicates;

import com.agile.findduplicates.DuplicateFinder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;

public class DuplicateFinderTest {

    private static final String DIR_NAME = "test";
    private static final String DIR1 = "dir1";
    private static final String DIR2 = "dir2";
    private static final String DIR3 = "dir3";
    private static final String FILE1 = "file1";
    private static final String FILE2 = "file2";
    private static final String FILE3 = "file3";
    private static final String FILE4 = "file4";
    private static final String FILE_CONTENT1 = UUID.randomUUID().toString();
    private static final String FILE_CONTENT2 = UUID.randomUUID().toString();
    private static final String FILE_CONTENT3 = UUID.randomUUID().toString() + UUID.randomUUID().toString();
    private static final String FILE_CONTENT4 = UUID.randomUUID().toString() + UUID.randomUUID().toString();

    private File directory;

    private File dir1File1;
    private File dir1File2;
    private File dir1File3;
    private File dir1Sub1File4;
    private File dir1Sub2File1;
    private File dir2File1;
    private File dir2File2;
    private File dir2File4;
    private File dir3File1;
    private File dir3File2;
    private File dir3File3;
    private File dir3File4;

    private boolean rm (File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!rm(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    private Path createDirectory (String dirPath, String dirName) throws Exception {
        return Files.createDirectory(Paths.get(dirPath, dirName));
    }

    private Path createFile (String dirPath, String fileName) throws Exception {
        return Files.createFile(Paths.get(dirPath, fileName));
    }

    private File createFile (String dirPath, String fileName, String fileContent) throws Exception {
        Path p = Paths.get(dirPath, fileName);
        Files.createFile(p);
        PrintWriter writer = new PrintWriter(p.toFile());
        writer.println(fileContent);
        writer.flush();
        return p.toFile();
    }

    private <K,V> boolean checkMultimapEquals (Multimap<K,V> a, Multimap<K,V> b) {
        boolean equals = true;
        for (K key : a.keySet()) {
            for (V value : b.get(key)) {
                if (!a.get(key).contains(value)) {
                    return false;
                }
            }
        }
        for (K key : b.keySet()) {
            for (V value : a.get(key)) {
                if (!b.get(key).contains(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets up a miniature file system in our working directory for testing purposes.
     */
    @Before
    public void setUp() throws Exception {
        Path p = Paths.get(System.getProperty("user.dir"), DIR_NAME);
        directory = Files.createDirectory(p).toFile();
        String directoryPath = directory.getAbsolutePath();

        createDirectory(directoryPath, DIR1);
        createDirectory(directoryPath, DIR2);
        createDirectory(directoryPath, DIR3);

        String dir1Path = Paths.get(directoryPath, DIR1).toString();
        createDirectory(dir1Path, DIR1);
        createDirectory(dir1Path, DIR2);
        dir1File1 = createFile(dir1Path, FILE1, FILE_CONTENT1);
        dir1File2 = createFile(dir1Path, FILE2, FILE_CONTENT1);
        dir1File3 = createFile(dir1Path, FILE3, FILE_CONTENT3);

        String dir1Subdirpath = Paths.get(dir1Path, DIR1).toString();
        createDirectory(dir1Subdirpath, DIR1);
        createDirectory(dir1Subdirpath, DIR2);
        dir1Sub1File4 = createFile(dir1Subdirpath, FILE4, FILE_CONTENT2);
        dir1Sub2File1 = createFile(Paths.get(dir1Subdirpath, DIR1).toString(), FILE1, FILE_CONTENT1);

        String dir2Path = Paths.get(directoryPath, DIR2).toString();
        dir2File4 = createFile(dir2Path, FILE4, FILE_CONTENT4);
        dir2File1 = createFile(dir2Path, FILE1, FILE_CONTENT3);
        dir2File2 = createFile(dir2Path, FILE2, FILE_CONTENT2);

        String dir3Path = Paths.get(directoryPath, DIR3).toString();
        dir3File1 = createFile(dir3Path, FILE1, FILE_CONTENT4);
        dir3File2 = createFile(dir3Path, FILE2, FILE_CONTENT4);
        dir3File3 = createFile(dir3Path, FILE3, FILE_CONTENT1);
        dir3File4 = createFile(dir3Path, FILE4, FILE_CONTENT2);
    }

    /**
     * Removes the temporary folders.
     */
    @After
    public void tearDown() throws Exception {
        rm(directory);
        directory = null;
    }

    /**
     * Tests DuplicateFinder.findDuplicates for correctness.  Files are considered duplicates with the same checksum, and recursion is on.
     */
    @Test
    public void testFindDuplicates1() {
        Multimap<File,File> testMap = DuplicateFinder.findDuplicates(directory, DuplicateFinder.DUPLICATE_BY_CHECKSUM, true);

        Multimap<File,File> knownMap = HashMultimap.create();
        knownMap.put(dir1File1, dir1File2);
        knownMap.put(dir1File1, dir1Sub2File1);
        knownMap.put(dir1File1, dir3File3);
        knownMap.put(dir1File2, dir1File1);
        knownMap.put(dir1File2, dir1Sub2File1);
        knownMap.put(dir1File2, dir3File3);
        knownMap.put(dir1File3, dir2File1);
        knownMap.put(dir1Sub1File4, dir2File2);
        knownMap.put(dir1Sub1File4, dir3File4);
        knownMap.put(dir1Sub2File1, dir1File1);
        knownMap.put(dir1Sub2File1, dir1File2);
        knownMap.put(dir1Sub2File1, dir3File3);
        knownMap.put(dir2File1, dir1File3);
        knownMap.put(dir2File2, dir1Sub1File4);
        knownMap.put(dir2File2, dir3File4);
        knownMap.put(dir2File4, dir3File1);
        knownMap.put(dir2File4, dir3File2);
        knownMap.put(dir3File1, dir2File4);
        knownMap.put(dir3File1, dir3File2);
        knownMap.put(dir3File2, dir2File4);
        knownMap.put(dir3File2, dir3File1);
        knownMap.put(dir3File3, dir1File1);
        knownMap.put(dir3File3, dir1File2);
        knownMap.put(dir3File3, dir1Sub2File1);
        knownMap.put(dir3File4, dir1Sub1File4);
        knownMap.put(dir3File4, dir2File2);

        assertTrue(checkMultimapEquals(testMap, knownMap));
    }

    /**
     * Tests DuplicateFinder.findDuplicates for correctness.  Files are considered duplicates with the same filename, and recursion is on.
     */
    @Test
    public void testFindDuplicates2 () {
        Multimap<File,File> testMap = DuplicateFinder.findDuplicates(directory, DuplicateFinder.DUPLICATE_BY_FILENAME, true);

        Multimap<File,File> knownMap = HashMultimap.create();

        knownMap.put(dir1File1, dir1Sub2File1);
        knownMap.put(dir1File1, dir2File1);
        knownMap.put(dir1File1, dir3File1);
        knownMap.put(dir1File2, dir2File2);
        knownMap.put(dir1File2, dir3File2);
        knownMap.put(dir1File3, dir3File3);
        knownMap.put(dir1Sub1File4, dir2File4);
        knownMap.put(dir1Sub1File4, dir3File4);
        knownMap.put(dir1Sub2File1, dir1File1);
        knownMap.put(dir1Sub2File1, dir2File1);
        knownMap.put(dir1Sub2File1, dir3File1);
        knownMap.put(dir2File1, dir1File1);
        knownMap.put(dir2File1, dir1Sub2File1);
        knownMap.put(dir2File1, dir3File1);
        knownMap.put(dir2File2, dir1File2);
        knownMap.put(dir2File2, dir3File2);
        knownMap.put(dir2File4, dir1Sub1File4);
        knownMap.put(dir2File4, dir3File4);
        knownMap.put(dir3File1, dir1File1);
        knownMap.put(dir3File1, dir1Sub2File1);
        knownMap.put(dir3File1, dir2File1);
        knownMap.put(dir3File2, dir1File2);
        knownMap.put(dir3File2, dir2File2);
        knownMap.put(dir3File3, dir1File3);
        knownMap.put(dir3File4, dir1Sub1File4);
        knownMap.put(dir3File4, dir2File4);

        assertTrue(checkMultimapEquals(testMap, knownMap));
    }

    /**
     * Tests DuplicateFinder.findDuplicates for correctness.  Files are considered duplicates with the same size, and recursion is on.
     */
    @Test
    public void testFindDuplicates3() {
        Multimap<File,File> testMap = DuplicateFinder.findDuplicates(directory, DuplicateFinder.DUPLICATE_BY_SIZE, true);

        Multimap<File,File> knownMap = HashMultimap.create();

        knownMap.put(dir1File1, dir1File2);
        knownMap.put(dir1File1, dir1Sub1File4);
        knownMap.put(dir1File1, dir1Sub2File1);
        knownMap.put(dir1File1, dir2File2);
        knownMap.put(dir1File1, dir3File3);
        knownMap.put(dir1File1, dir3File4);
        knownMap.put(dir1File2, dir1File1);
        knownMap.put(dir1File2, dir1Sub2File1);
        knownMap.put(dir1File2, dir1Sub1File4);
        knownMap.put(dir1File2, dir2File2);
        knownMap.put(dir1File2, dir3File3);
        knownMap.put(dir1File2, dir3File4);
        knownMap.put(dir1File3, dir2File4);
        knownMap.put(dir1File3, dir2File1);
        knownMap.put(dir1File3, dir3File1);
        knownMap.put(dir1File3, dir3File2);
        knownMap.put(dir1Sub1File4, dir1File1);
        knownMap.put(dir1Sub1File4, dir1File2);
        knownMap.put(dir1Sub1File4, dir1Sub2File1);
        knownMap.put(dir1Sub1File4, dir2File2);
        knownMap.put(dir1Sub1File4, dir3File3);
        knownMap.put(dir1Sub1File4, dir3File4);
        knownMap.put(dir2File1, dir2File4);
        knownMap.put(dir2File1, dir1File3);
        knownMap.put(dir2File1, dir3File1);
        knownMap.put(dir2File1, dir3File2);
        knownMap.put(dir2File2, dir1File1);
        knownMap.put(dir2File2, dir1File2);
        knownMap.put(dir2File2, dir1Sub1File4);
        knownMap.put(dir2File2, dir1Sub2File1);
        knownMap.put(dir2File2, dir3File3);
        knownMap.put(dir2File2, dir3File4);
        knownMap.put(dir2File4, dir2File1);
        knownMap.put(dir2File4, dir1File3);
        knownMap.put(dir2File4, dir3File1);
        knownMap.put(dir2File4, dir3File2);
        knownMap.put(dir3File1, dir1File3);
        knownMap.put(dir3File1, dir2File4);
        knownMap.put(dir3File1, dir2File1);
        knownMap.put(dir3File1, dir3File2);
        knownMap.put(dir3File2, dir1File3);
        knownMap.put(dir3File2, dir2File1);
        knownMap.put(dir3File2, dir2File4);
        knownMap.put(dir3File2, dir3File1);
        knownMap.put(dir3File3, dir1File1);
        knownMap.put(dir3File3, dir1File2);
        knownMap.put(dir3File3, dir1Sub1File4);
        knownMap.put(dir3File3, dir1Sub2File1);
        knownMap.put(dir3File3, dir2File2);
        knownMap.put(dir3File3, dir3File4);
        knownMap.put(dir3File4, dir1File1);
        knownMap.put(dir3File4, dir1File2);
        knownMap.put(dir3File4, dir1Sub1File4);
        knownMap.put(dir3File4, dir1Sub2File1);
        knownMap.put(dir3File4, dir2File2);
        knownMap.put(dir3File4, dir3File3);

        assertTrue(checkMultimapEquals(testMap, knownMap));
    }

    /**
     * Tests DuplicateFinder.findDuplicates for correctness.  The specified directory contains no files, and recursion is off.  This is a test to make sure findDuplicates works properly with no files.
     */
    @Test
    public void testFindDuplicates4 () {
        Multimap<File,File> testMap = DuplicateFinder.findDuplicates(directory, DuplicateFinder.DUPLICATE_BY_SIZE, false);

        Multimap<File,File> knownMap = HashMultimap.create();

        assertTrue(checkMultimapEquals(testMap, knownMap));
    }

    /**
     * Tests DuplicateFinder.findDuplicates for correctness.  Files are considered duplicates with the same size, and recursion is off.
     */
    @Test
    public void testFindDuplicates5 () {
        File dir = Paths.get(directory.getAbsolutePath(), DIR3).toFile();
        Multimap<File,File> testMap = DuplicateFinder.findDuplicates(dir, DuplicateFinder.DUPLICATE_BY_SIZE, false);

        Multimap<File,File> knownMap = HashMultimap.create();

        knownMap.put(dir3File1, dir3File2);
        knownMap.put(dir3File2, dir3File1);
        knownMap.put(dir3File3, dir3File4);
        knownMap.put(dir3File4, dir3File3);

        assertTrue(checkMultimapEquals(testMap, knownMap));
    }
}