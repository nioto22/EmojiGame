package com.example.nioto.emojigame.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    private static String title = "";

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> categories = new ArrayList<String>();
        categories.add("Personnage");
        categories.add("Cinéma");
        categories.add("Musique");
        categories.add("Expressions");
        categories.add("Objet");
        categories.add("Autres");


        expandableListDetail.put(title, categories);
        return expandableListDetail;
    }

    public static void setTitle(String title) {
        ExpandableListDataPump.title = title;
    }
}
