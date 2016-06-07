package pl.codeinprogress.notes.firebase;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.appcompat.BuildConfig;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;

import pl.codeinprogress.notes.R;

/**
 * Created by tomaszmartin on 02.06.2016.
 */

public class FirebaseApplication extends Application {

    private FirebaseAnalytics analytics;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseRemoteConfig configuration;
    private FirebaseStorage storage;

    public FirebaseRemoteConfig getConfiguration() {
        return configuration;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseAnalytics getAnalytics() {
        return analytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        analytics = FirebaseAnalytics.getInstance(this);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        storage = FirebaseStorage.getInstance();
        configuration = FirebaseRemoteConfig.getInstance();
        configuration.setDefaults(R.xml.firebase);
    }

}
