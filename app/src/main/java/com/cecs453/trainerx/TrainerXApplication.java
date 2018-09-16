package com.cecs453.trainerx;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.distribute.Distribute;

public class TrainerXApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        AppCenter.start(this, "f03bbcb4-2025-4e0e-a650-f976c592ef43", Distribute.class);
    }
}
