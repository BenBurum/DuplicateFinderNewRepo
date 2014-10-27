package com.agile.findduplicates;

import com.google.common.collect.Multimap;

import java.io.File;

/**
 * Created by ericbrown on 10/22/14.
 */
public interface SortFile {
    /**
     * Returns a Multimap matching each file to the set of its duplicate files.  Files with the same size are considered duplicates.
     *
     * @param recursive If true, will run recursively.  If false, will only check one level deep.
     * @return A Multimap matching each file to the set of its duplicate files.
     */
    public Multimap<File,File> findDuplicatesBySize (boolean recursive);

    /**
     * Returns a Multimap matching each file to the set of its duplicate files.  Files with the same filename are considered duplicates.
     *
     * @param recursive If true, will run recursively.  If false, will only check one level deep.
     * @return A Multimap containing all files that have duplicates as keys.
     */
    public Multimap<File,File> findDuplicatesByFilename (boolean recursive);

    /**
     * Returns a Multimap matching each file to the set of its duplicate files.  Files with the same checksum are considered duplicates.
     *
     * @param recursive If true, will run recursively.  If false, will only check one level deep.
     * @return A Multimap containing all files that have duplicates as keys.
     */
    public Multimap<File,File> findDuplicatesByChecksum (boolean recursive);
}
