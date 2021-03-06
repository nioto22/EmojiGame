package com.example.nioto.emojigame.utils;

public class Constants {
    // APPLICATION
    public static final String APPLICATION_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=pro.cloudteam.emojinationtest";  // TEST URI
    public static final String ADD_MOBS_APPLICATION_ID = "ca-app-pub-7652009776235513~1976717846";
    public static final String ADD_MOBS_VIDEO_TEST_ID = "ca-app-pub-3940256099942544/5224354917"; // FOR TEST ONLY
    public static final String ADD_MOBS_INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712"; // FOR TEST ONLY
    public static final String ADD_MOBS_VIDEO_ID_SMILEYS = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/3263370046
    public static final String ADD_MOBS_VIDEO_ID_COINS = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL  TO CHANGE WITH : ca-app-pub-7652009776235513/7732908143
    public static final String ADD_MOBS_VIDEO_ID_HINT_ONE = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/7820661768
    public static final String ADD_MOBS_VIDEO_ID_HINT_TWO = "ca-app-pub-3940256099942544/5224354917"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/9680538343
    public static final String ADD_MOBS_INTERSTITIAL_ENIGMA_CREATED = "ca-app-pub-3940256099942544/1033173712"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/8369429866
    public static final String ADD_MOBS_INTERSTITIAL_ENIGMA_RESOLVED = "ca-app-pub-3940256099942544/1033173712"; // FOR REAL TO CHANGE WITH : ca-app-pub-7652009776235513/5342390395
    //PLAY ACTIVITY
    // TOOLBAR TITLES
    public static final String PLAY_ACTIVITY_TITLE = "EmojiGame";
    public static final String TOOLBAR_TITLE_IN_PLAY = "Enigmes en Jeu";
    public static final String TOOLBAR_TITLE_HISTORIC = "Enigmes Jouées";
    public static final String TOOLBAR_TITLE_OWN = "Enigmes Créées";
    // SORT / FILTER OPTIONS
    public static final String FILTER_CATEGORY_ALL = "CATEGORIE";
    public static final String FILTER_CATEGORY_PERSONAGE = "PERSONNAGE";
    public static final String FILTER_CATEGORY_CINEMA = "CINEMA";
    public static final String FILTER_CATEGORY_MUSIC = "MUSIQUE";
    public static final String FILTER_CATEGORY_EXPRESSION = "EXPRESSIONS";
    public static final String FILTER_CATEGORY_OBJECT = "OBJET";
    public static final String FILTER_CATEGORY_WORD = "NOM COMMUN";
    public static final String FILTER_CATEGORY_OTHER = "AUTRE";
    public static final String SORT_DIFICULTY_ASC = "Difficulté Asc.";
    public static final String SORT_DIFICULTY_DESC = "Difficulté Desc.";
    public static final String SORT_DATE_ASC = "Date Asc.";
    public static final String SORT_DATE_DESC = "Date Desc.";
    public static final String SORT_PLAYER_ASC = "Joueur A-Z";
    public static final String SORT_PlAYER_DESC = "Joueur Z-A";
    public static final String FILTER_ENIGMA_ALL = "Toutes";
    public static final String FILTER_ENIGMA_NEW_ONE = "Nouvelles";
    public static final String FILTER_ENIGMA_ON_GOING = "En cours";
    public static final String FILTER_ENIGMA_RESOLVED = "Résolues";
    public static final String FILTER_ENIGMA_OWN = "Crées par vous";
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
    public static final String SOLVE_ACTIVITY_TITLE_OWN_ENIGMA = "Enigme créée";
    // EXTRA_BUNDLE
    public static final String EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY = "EXTRA_BUNDLE_EDIT_ENIGMA_ACTIVITY";
    public static final int INTENT_UPDATE_ACTIVITY_KEY = 14;

    // CREATE ACTIVITY
    public static final String CREATE_ACTIVITY_TITLE = "Créer votre énigme";
    public static final String CREATE_ACTIVITY_TITLE_UPDATE_ENIGMA = "Modifier énigme";
    public static final String EXTRA_BUNDLE_ENIGMA_CREATED_ENIGMA = "EXTRA_BUNDLE_ENIGMA_CREATED_ENIGMA";
    public static final String EXTRA_BUNDLE_ENIGMA_CREATED_CATEGORY = "EXTRA_BUNDLE_ENIGMA_CREATED_CATEGORY";
    // PROFILE ACTIVITY
    public static final String PROFILE_ACTIVITY_TITLE = "Votre Profil";

    // SHARED PREFERENCES
    public static final String SHARED_PREF_NAME = "SHARED_PREF_NAME";
    public static final String SHARED_PREF_LIFE_LEFT_TIME = "SHARED_PREF_LIFE_LEFT_TIME";
    public static final String SHARED_PREF_LIFE_END_TIME = "SHARED_PREF_LIFE_END_TIME";
    public static final String SHARED_PREF_LIFE_IS_TIMER_RUNNING = "SHARED_PREF_LIFE_IS_TIMER_RUNNING";

    //TIMER
    public static final long TIMES_UP_UNTIL_NEW_LIFE = 10800000;

    // DIALOG FRAGMENT
    public static final String PODIUM_DIALOG_FRAGMENT_TAG = "podiumDialog";
    public static final String SMILEYS_DIALOG_FRAGMENT_TAG = "smileysDialog";
    public static final String COINS_DIALOG_FRAGMENT_TAG = "coinsDialog";
    public static final String HINT_ONE_DIALOG_FRAGMENT_TAG = "hintOneDialog";
    public static final String HINT_TWO_DIALOG_FRAGMENT_TAG = "hintTwoDialog";
    public static final String SOLVED_DIALOG_FRAGMENT_TAG = "solvedDialog";
    public static final String ENIGMA_CREATED_DIALOG_FRAGMENT_TAG = "enigmaCreatedDialog";
    public static final String UPDATED_USERNAME_DIALOG_FRAGMENT_TAG = "updatedUsernameDialog";
    public static final String HINT_ONE_MANDATORY_DIALOG_FRAGMENT_TAG = "hintOneMandatoryDialog";
    public static final String PODIUM_DIALOG_ARG_ENIGMA = "enigma";
    public static final String LIFE_DIALOG_ARG_USER = "user";
    public static final String LIFE_DIALOG_ARG_END_TIME = "endTime";
    public static final String LIFE_DIALOG_ARG_TIMER_RUNNING = "timmerRunning";
    public static final String HINT_ONE_DIALOG_ARG_ENIGMA = "enigma";
    public static final String HINT_TWO_DIALOG_ARG_ENIGMA = "enigma";
    public static final String HINT_TWO_DIALOG_ARG_SOLUTION_TYPE_LIST = "solutionInTypeArray";
    public static final String SOLVED_DIALOG_ARG_POINTS = "points";
    public static final String SOLVED_DIALOG_ARG_ENIGMA = "solvedEnigma";
    public static final String SOLVED_DIALOG_ARG_RESOLVED_TIMES = "numberOfResolvedTimes";
    public static final String SOLVED_DIALOG_ARG_CATEGORY = "enigmaCategory";
    public static final String COINS_DIALOG_ARG_USER = "userUid";
    public static final String ENIGMA_CREATED_DIALOG_ARG_ENIGMA = "enigmaCreatedEnigma";
    public static final String ENIGMA_CREATED_DIALOG_ARG_CATEGORY = "enigmaCreatedCategory";
    public static final String UPDATED_USERNAME_DIALOG_ARG_USER_UID = "updatedUsernameUserUid";
    public static final String UPDATED_USERNAME_DIALOG_ARG_USER_USERNAME = "updatedUsernameUserUsername";

}
