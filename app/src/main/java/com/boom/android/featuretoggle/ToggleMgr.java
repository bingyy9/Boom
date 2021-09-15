package com.boom.android.featuretoggle;

public class ToggleMgr {
    private static ToggleMgr instance;

    public boolean isToogleEnabled(String feature) {
        if (StaticToggles.toggles.isEmpty()) {
            return false;
        }

        if (StaticToggles.toggles.containsKey(feature)) {
            return StaticToggles.toggles.get(feature);
        } else {
            return false;
        }
    }

    public boolean isGoogleAdMobEnabled(){
        return isToogleEnabled(StaticToggles.ADMOB_BANNER);
    }


    public static ToggleMgr getInstance() {
        if (null == instance)
            instance = new ToggleMgr();

        return instance;
    }
}
