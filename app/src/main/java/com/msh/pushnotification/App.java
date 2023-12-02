package com.msh.pushnotification;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

public class App extends MultiDexApplication
{
    private static Context appContext;

    @Override
    public void onCreate()
    {
        super.onCreate();

        appContext = getApplicationContext();
        try
        {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
        {
            e.printStackTrace();
        }
    }

    public static Context getAppContext()
    {
        return appContext;
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
