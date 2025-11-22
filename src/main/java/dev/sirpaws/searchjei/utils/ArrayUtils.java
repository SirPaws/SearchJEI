package dev.sirpaws.searchjei.utils;

import java.util.ArrayList;

public class ArrayUtils {
    public static<T> ArrayList<T> copyArray(ArrayList<T> list) {
        return (ArrayList<T>)list.clone();
    }
}
