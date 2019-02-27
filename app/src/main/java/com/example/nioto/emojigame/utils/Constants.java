package com.example.nioto.emojigame.utils;

public class Constants {
    // APPLICATION
    public static final String APPLICATION_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=pro.cloudteam.emojinationtest";  // TEST URI
    public static final String ADD_MOBS_APPLICATION_ID = "ca-app-pub-7652009776235513~1976717846";
    public static final String ADD_MOBS_VIDEO_TEST_ID = "ca-app-pub-3940256099942544/5224354917"; // FOR TEST ONLY
    public static final String ADD_MOBS_VIDEO_ID_SMILEYS = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/3263370046
    public static final String ADD_MOBS_VIDEO_ID_COINS = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL  TO CHANGE WITH : ca-app-pub-7652009776235513/7732908143
    public static final String ADD_MOBS_VIDEO_ID_HINT_ONE = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/7820661768
    public static final String ADD_MOBS_VIDEO_ID_HINT_TWO = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/9680538343
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
    public static final String COINS_DIALOG_FRAGMENT_TAG = "coinsDialog";
    public static final String HINT_ONE_DIALOG_FRAGMENT_TAG = "hintOneDialog";
    public static final String HINT_TWO_DIALOG_FRAGMENT_TAG = "hintTwoDialog";
    public static final String SOLVED_DIALOG_FRAGMENT_TAG = "solvedDialog";
    public static final String PODIUM_DIALOG_ARG_ENIGMA = "enigma";
    public static final String LIFE_DIALOG_ARG_USER = "user";
    public static final String LIFE_DIALOG_ARG_END_TIME = "endTime";
    public static final String LIFE_DIALOG_ARG_TIMER_RUNNING = "timmerRunning";
    public static final String HINT_ONE_DIALOG_ARG_ENIGMA = "enigma";
    public static final String HINT_TWO_DIALOG_ARG_ENIGMA = "enigma";
    public static final String HINT_TWO_DIALOG_ARG_POSITION_CHAR_LIST = "positionCharList";
    public static final String SOLVED_DIALOG_ARG_POINTS = "points";
    public static final String SOLVED_DIALOG_ARG_ENIGMA = "solvedEnigma";
    public static final String SOLVED_DIALOG_ARG_RESOLVED_TIMES = "numberOfResolvedTimes";
    public static final String SOLVED_DIALOG_ARG_CATEGORY = "enigmaCategory";
    public static final String COINS_DIALOG_ARG_USER = "userUid";

}
