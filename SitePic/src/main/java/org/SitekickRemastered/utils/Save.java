package org.SitekickRemastered.utils;

import java.io.*;
import java.util.ArrayList;

/** This class is no longer used. It's here in case we want to get the user count at a later date **/
public class Save {

    /** Function that saves everything to a log so that you can tally userCount and who's used it */
    public static void save(String n) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        File logs = new File("src/log.txt");
        File userCount = new File("src/userCount.txt");
        if (logs.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(logs));
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line);
            }
            names.add(n);
            br.close();
        }
        names = removeDuplicates(names);
        BufferedWriter log = new BufferedWriter(new FileWriter(logs));
        BufferedWriter count = new BufferedWriter(new FileWriter(userCount));
        for (String name : names) {
            log.write(name + "\n");
        }
        if (userCount.exists())
            count.write(Integer.toString(names.size()));

        log.close();
        count.close();
    }


    /**
     * function that goes through an arrayList and removes duplicates in it
     *
     * @param list - the given list you want to remove duplicates from
     * @return - returns the list.
     */
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<>();
        for (T element : list) {
            if (!newList.contains(element))
                newList.add(element);
        }
        return newList;
    }
}
