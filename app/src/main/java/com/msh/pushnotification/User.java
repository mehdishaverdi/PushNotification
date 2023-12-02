package com.msh.pushnotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

public class User
{
    static User instance = new User();
    Context applicationContext;
    String[] fontPath = {"fonts/IRANYekanRegularFaNum.ttf", "fonts/IRANYekanBoldFaNum.ttf"};
    Typeface[] fonts = new Typeface[2];
    boolean fontsLoaded = false;

    public User()
    {

    }

    public static User getInstance()
    {
        return instance;
    }

    public void setContext(Context context)
    {
        this.applicationContext = context;
    }

    public Context getContext()
    {
        return this.applicationContext;
    }

    public void setValue(String key, String value)
    {
        if (applicationContext != null)
        {
            SharedPreferences settings = applicationContext.getSharedPreferences(BuildConfig.APPLICATION_ID, 0);
            if (settings == null)
                return;

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public String getValue(String key)
    {
        SharedPreferences settings = applicationContext.getSharedPreferences(BuildConfig.APPLICATION_ID, 0);
        String defaultValue = "";
        return settings.getString(key, defaultValue);
    }

    public Typeface getTypeface(Context context, int fontIdentifier)
    {
        if (!fontsLoaded)
        {
            loadFonts(context);
        }
        return fonts[fontIdentifier];
    }

    private void loadFonts(Context context)
    {
        try
        {
            for (int i = 0; i < fonts.length; i++)
            {
                fonts[i] = Typeface.createFromAsset(context.getAssets(), fontPath[i]);
            }
            fontsLoaded = true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
