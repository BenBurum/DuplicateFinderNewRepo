package com.agile.findduplicates;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;

public class DuplicateFinder {

    public static final int DUPLICATE_BY_CHECKSUM = 1;
    public static final int DUPLICATE_BY_FILENAME = 2;
    public static final int DUPLICATE_BY_SIZE = 3;

    private static final String ERROR_DIRECTORY_ARGUMENT = "com.agile.findduplicates.DuplicateFinder.getChecksums: argument was not a directory";

    /**
     * Returns a Multimap matching each file to the set of its duplicate files.
     *
     * @throws java.lang.IllegalArgumentException If the File provided is not a directory, an IllegalArgumentException will be thrown.
     * @param directory The directory to check for duplicate files.
     * @param param Specifies which method should be used to check for duplicate files.
     * @param recursive If true, will run recursively.  If false, will only check one level deep.
     * @return A Multimap containing all files that have duplicates as keys.  For each key, the files that are duplicates of that key are its values.  If there are no files in the directory, or if an invalid parameter is passed in, an empty Multimap will be returned.
     */
    public static Multimap<File,File> findDuplicates (File directory, int param, boolean recursive) {

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(ERROR_DIRECTORY_ARGUMENT);
        }

        Multimap<File,File> duplicateList = HashMultimap.create();

        if (directory.listFiles() == null) {
            return duplicateList;
        }

        switch (param) {
            case DUPLICATE_BY_CHECKSUM:
                Multimap<Long,File> checksum = HashMultimap.create();
                if (recursive == true) {
                    checksum = getChecksums(directory, recursive);
                } else {
                    checksum = getChecksums(directory);
                }
                duplicateList = translateMultimap(checksum);
                break;
            case DUPLICATE_BY_FILENAME:
                Multimap<String,File> names = HashMultimap.create();
                if (recursive == true) {
                    names = getNames(directory, recursive);
                } else {
                    names = getNames(directory);
                }
                duplicateList = translateMultimap(names);
                break;
            case DUPLICATE_BY_SIZE:
                Multimap<Long,File> sizes = HashMultimap.create();
                if (recursive == true) {
                    sizes = getSizes(directory, recursive);
                } else {
                    sizes = getSizes(directory);
                }
                duplicateList = translateMultimap(sizes);
                break;
            default:
                return duplicateList;

        }

        return duplicateList;
    }

    /**
     * Calculates checksums (CRC32) for all files in the specified directory.  If invoked recursively, will calculate checksums for subdirectories of the specified directory.
     *
     * @throws java.lang.IllegalArgumentException If the File provided is not a directory, an IllegalArgumentException will be thrown.
     * @param directory The directory whose checksums should be calculated.
     * @param recursive If true, will run recursively.  If false, will only check one level deep.
     * @return A Multimap containing all the checksums, as well as their corresponding files.  If there are no files in the directory, an empty Multimap will be returned.
     */
    private static Multimap<Long,File> getChecksums (File directory, boolean recursive) {

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(ERROR_DIRECTORY_ARGUMENT);
        }

        Multimap<Long,File> checksums = HashMultimap.create();

        if (directory.listFiles() == null) {
            return checksums;
        }

        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    CRC32 crc = new CRC32();
                    byte[] buffer = new byte[256];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        crc.update(buffer, 0, bytesRead);
                    }

                    checksums.put(crc.getValue(), file);
                } catch (FileNotFoundException e) {
                    System.err.println("FileNotFoundException");
                } catch (IOException e) {
                    System.err.println("IOException");
                }
            } else if (file.isDirectory() && recursive == true) {
                checksums.putAll(getChecksums(file, recursive));
            }
        }

        for (Long l : checksums.keySet()) {
            if (checksums.get(l).size() < 2) {
                checksums.remove(l, checksums.get(l));
            }
        }

        return pruneMultimap(checksums);
    }

    /**
     * Calculates checksums (CRC32) for all files in the specified directory.
     *
     * @throws java.lang.IllegalArgumentException If the File provided is not a directory, an IllegalArgumentException will be thrown.
     * @param directory The directory whose checksums should be calculated.
     * @return A Multimap containing all the checksums, as well as their corresponding files.  If there are no files in the directory, an empty Multimap will be returned.
     */
    private static Multimap<Long,File> getChecksums (File directory) {
        return getChecksums(directory, false);
    }

    /**
     * Gets the names of all files in the specified directory.  If invoked recursively, will contain names of files in subdirectories.
     *
     * @throws java.lang.IllegalArgumentException If the File provided is not a directory, an IllegalArgumentException will be thrown.
     * @param directory The directory whose file names should be listed.
     * @param recursive If true, will run recursively.  If false, will only check one level deep.
     * @return A Multimap containing all file names, as well as files with those names.  If there are no files in the directory, an empty Multimap will be returned.
     */
    private static Multimap<String,File> getNames (File directory, boolean recursive) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(ERROR_DIRECTORY_ARGUMENT);
        }

        Multimap<String,File> names = HashMultimap.create();

        if (directory.listFiles() == null) {
            return names;
        }

        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                names.put(file.getName(), file);
            } else if (file.isDirectory() && recursive == true) {
                names.putAll(getNames(file, recursive));
            }
        }

        return pruneMultimap(names);
    }

    /**
     * Gets the names of all files in the specified directory.
     *
     * @throws java.lang.IllegalArgumentException If the File provided is not a directory, an IllegalArgumentException will be thrown.
     * @param directory The directory whose file names should be listed.
     * @return A Multimap containing all file names, as well as files with those names.  If there are no files in the directory, an empty Multimap will be returned.
     */
    private static Multimap<String,File> getNames (File directory) {
        return getNames(directory, false);
    }

    /**
     * Gets the sizes of all files in the specified directory.  If invoked recursively, will contain sizes of files in subdirectories.
     *
     * @throws java.lang.IllegalArgumentException If the File provided is not a directory, an IllegalArgumentException will be thrown.
     * @param directory The directory whose file sizes should be listed.
     * @param recursive If true, will run recursively.  If false, will only check one level deep.
     * @return A Multimap containing all file sizes in bytes, as well as files with those sizes.  If there are no files in the directory, an empty Multimap will be returned.
     */
    private static Multimap<Long,File> getSizes (File directory, boolean recursive) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(ERROR_DIRECTORY_ARGUMENT);
        }

        Multimap<Long,File> sizes = HashMultimap.create();

        if (directory.listFiles() == null) {
            return sizes;
        }

        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                sizes.put(file.length(),file);
            } else if (file.isDirectory() && recursive == true) {
                sizes.putAll(getSizes(file, recursive));
            }
        }

        return pruneMultimap(sizes);
    }

    /**
     * Gets the sizes of all files in the specified directory.
     *
     * @throws java.lang.IllegalArgumentException If the File provided is not a directory, an IllegalArgumentException will be thrown.
     * @param directory The directory whose file sizes should be listed.
     * @return A Multimap containing all file sizes in bytes, as well as files with those sizes.  If there are no files in the directory, an empty Multimap will be returned.
     */
    private static Multimap<Long,File> getSizes (File directory) {
        return getSizes(directory, false);
    }

    /**
     * Removes unnecessary entries from a Multimap.  Given a Multimap, this function returns the same Multimap with all key-value pairs removed where one key corresponds to only one value.  The resulting Multimap guarantees that for any type K k, Multimap.get(k).size > 1.
     *
     * @param map A Multimap<K,V> to be reduced.
     * @return The same Multimap, with all keys of type K k removed if they only correspond to one value.
     */
    private static <K,V> Multimap<K,V> pruneMultimap (Multimap<K,V> map) {
        for (K k : map.keySet()) {
            if (map.get(k).size() == 2) {
                map.remove(k, map.get(k));
            }
        }
        return map;
    }

    /**
     * Gets a Multimap matching each file to the set of its duplicate files, given a Multimap whose keys are the parameter by which we are measuring duplication and whose values are the duplicate files that match that parameter.
     *
     * @param map A Multimap with keys of type K and values of type File.  Expected parameters are Long and String.  Each key has multiple Files as values, such that those Files are all duplicates of each other, and no other value set contains any of those Files.
     * @return A Multimap containing all files that have duplicates as keys.  For each key, the files that are duplicates of that key are its values.
     */
    private static <K> Multimap<File,File> translateMultimap (Multimap<K,File> map) {
        Multimap<File,File> duplicateMap = HashMultimap.create();

        if (map.isEmpty()) {
            return duplicateMap;
        }

        for (K key : map.keySet()) {
            for (File value : map.get(key)) {
                duplicateMap.putAll(value, map.get(key));
                duplicateMap.remove(value, value);
            }
        }

        return duplicateMap;
    }

}
