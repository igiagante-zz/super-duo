package com.example.igiagante.football;

import android.app.Application;

import api.Provider;
import api.impl.FixtureManager;
import api.impl.ProviderFactory;

/**
 * @author igiagante on 10/12/15.
 */
public class MainApplication extends Application {

    private static MainApplication sInstance;

    private FixtureManager mFixtureManager;
    private Provider mProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();
    }

    protected void initializeInstance() {
        // do all your initialization here
        mFixtureManager = new FixtureManager();
        ProviderFactory mProviderFactory = new ProviderFactory(this, mFixtureManager);
        mProvider = mProviderFactory.getProvider();
    }

    public FixtureManager getFixtureManager() {
        return mFixtureManager;
    }

    public Provider getProvider() {
        return mProvider;
    }
}
