package com.agile.findduplicates;

import java.util.ArrayList;

/**
 * A mock map class.  HashMap and other Map subclasses were not practical for FileUtility, so this serves in their place.  It maintains two distinct ArrayLists, and any value from one list can be mapped to the other.  However, since we do not prohibit duplicate values in either list, this class will not function well with duplicates, especially multiple duplicates.  As FileUtility expects to work with duplicate values, this is something of a problem.  TODO: improve functionality with duplicates, find an appropriate Java class and scrap this whole thing, low priority: add additional map functionality, like removal
 *
 * */
public class DoubleMap<K,V> {

    private ArrayList<K> keys;
    private ArrayList<V> values;

    public DoubleMap () {
        keys = new ArrayList<K>();
        values = new ArrayList<V>();
    }

    /**
     * Get the list of all keys in the DoubleMap.
     *
     * @return An ArrayList of the map's keys.
     * */
    public ArrayList<K> keyList () {
        ArrayList<K> copy = keys;
        return copy;
    }

    /**
     * Get the list of all values in the DoubleMap.
     *
     * @return An ArrayList of the map's values.
     * */
    public ArrayList<V> valueList () {
        ArrayList<V> copy = values;
        return copy;
    }

    /**
     * Add a new key/value pair to the DoubleMap.
     *
     * */
    public void put (K key, V value) {
        keys.add(key);
        values.add(value);
    }

    /**
     * Given a key, returns the corresponding value.
     *
     * @param key The key whose value will be returned.
     * @return The value of type V, or null if the pair is not in the map.
     * */
    public V getValue (K key) {
        if (keys.contains(key)) {
            int index = keys.indexOf(key);
            return values.get(index);
        } else {
            return null;
        }
    }

    /**
     * Given a value, returns the corresponding key.
     *
     * @param value The value whose key will be returned.
     * @return The key of type K, or null if the pair is not in the map.
     * */
    public K getKey (V value) {
        if (values.contains(value)) {
            int index = values.indexOf(value);
            return keys.get(index);
        } else {
            return null;
        }
    }

}
