package com.boom.android.featuretoggle;

public class StaticToggles {
    public static final String ADMOB_BANNER = "google_admob_banner";
    public static final java.util.Map<String, Boolean> toggles = new java.util.HashMap<String, Boolean>();

    static {
        toggles.put(ADMOB_BANNER, false);
    }

}
