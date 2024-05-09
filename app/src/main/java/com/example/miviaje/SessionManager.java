package com.example.miviaje;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "session";
    private static final String KEY_SESSION = "session_on";
    private static final String GUEST_SESSION = "guest_on";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void login() {
        editor.putBoolean(KEY_SESSION, true);
        editor.apply();
    }

    public void loginAsGuest() {
        editor.putBoolean(GUEST_SESSION, true);
        editor.apply();
    }

    public void logOut() {
        editor.putBoolean(KEY_SESSION, false);
        editor.apply();
    }

    public boolean isLoged() {
        return pref.getBoolean(KEY_SESSION, false);
    }
}

