package com.example.nioto.emojigame.utils;

public class Constants {

    //PLAY ACTIVITY
    // TOOLBAR TITLES
    public static final String PLAY_ACTIVITY_TITLE = "EmojiGame";
    public static final String TOOLBAR_TITLE_IN_PLAY = "Enigmes en Jeu";
    public static final String TOOLBAR_TITLE_HISTORIC = "Enigmes Jouées";
    public static final String TOOLBAR_TITLE_OWN = "Enigmes Crées";
    // SORT / FILTER OPTIONS
    public static final String FILTER_CATEGORY_ALL = "CATEGORIE";
    public static final String FILTER_CATEGORY_PERSONAGE = "PERSONNAGE";
    public static final String FILTER_CATEGORY_CINEMA = "CINEMA";
    public static final String FILTER_CATEGORY_MUSIC = "MUSIQUE";
    public static final String FILTER_CATEGORY_EXPRESSION = "EXPRESSIONS";
    public static final String FILTER_CATEGORY_OBJECT = "OBJET";
    public static final String FILTER_CATEGORY_WORD = "NOM COMMUN";
    public static final String FILTER_CATEGORY_OTHER = "AUTRE";
    public static final String SORT_DIFICULTY_ASC = "Dificulty Asc.";
    public static final String SORT_DIFICULTY_DESC = "Dificulty Desc.";
    public static final String SORT_DATE_ASC = "Date Asc.";
    public static final String SORT_DATE_DESC = "Date Desc.";
    public static final String SORT_PLAYER_ASC = "Joueur A-Z";
    public static final String SORT_PlAYER_DESC = "Joueur Z-A";
    // BUTTON NAMES
    public static final String FIREBASE_CATEGORY_PERSONAGE_TEXT = "Personnage";
    public static final String FIREBASE_CATEGORY_CINEMA_TEXT = "Cinéma";
    public static final String FIREBASE_CATEGORY_MUSIC_TEXT = "Musique";
    public static final String FIREBASE_CATEGORY_EXPRESSION_TEXT = "Expressions";
    public static final String FIREBASE_CATEGORY_OBJECT_TEXT = "Objet";
    public static final String FIREBASE_CATEGORY_WORD_TEXT = "Nom Commun";
    public static final String FIREBASE_CATEGORY_OTHER_TEXT = "Autres";

    // SOLVE ACTIVITY
    public static final String SOLVE_ACTIVITY_TITLE = "Résoudre l\'énigme";
    // EXTRA_BUNDLE
    public static final String EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY = "EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY";
    public static final int INTENT_UPDATE_ACTIVITY_KEY = 14;

    // CREATE ACTIVITY
    public static final String CREATE_ACTIVITY_TITLE = "Créer votre énigme";
    // PROFILE ACTIVITY
    public static final String PROFILE_ACTIVITY_TITLE = "Votre Profil";

    // SHARED PREFERENCES
    public static final String SHARED_PREF_NAME = "SHARED_PREF_NAME";
    public static final String SHARED_PREF_LIFE_LEFT_TIME = "SHARED_PREF_LIFE_LEFT_TIME";
    public static final String SHARED_PREF_LIFE_END_TIME = "SHARED_PREF_LIFE_END_TIME";
    public static final String SHARED_PREF_LIFE_IS_TIMER_RUNNING = "SHARED_PREF_LIFE_IS_TIMER_RUNNING";

    //TIMER
    public static final long TIMES_UP_UNTIL_NEW_LIFE = 10800000;
    public static final long TIMES_UP_FOR_FIVE_NEW_LIFE = 54000000;

    // DIALOG FRAGMENT
    public static final String PODIUM_DIALOG_FRAGMENT_TAG = "podiumDialog";
    public static final String SMILEYS_DIALOG_FRAGMENT_TAG = "smileysDialog";
    public static final String PODIUM_DIALOG_ARG_ENIGMA = "enigma";
    public static final String LIFE_DIALOG_ARG_USER = "user";
    public static final String LIFE_DIALOG_ARG_TIME_LEFT = "timeLeft";

}
