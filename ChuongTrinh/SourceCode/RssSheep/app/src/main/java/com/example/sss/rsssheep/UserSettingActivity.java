package com.example.sss.rsssheep;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by SSS on 29/04/2017.
 */

public class UserSettingActivity extends PreferenceActivity {
    private static final int RESULT_CODE_THEME_UPDATED = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SwitchTheme.onActivityCreateSetTheme(this);
        //addPreferencesFromResource(R.xml.user_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new UserSettingsFragments()).commit();
    }

    public static class UserSettingsFragments extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.user_settings);

            final ListPreference listPreference = (ListPreference)findPreference("prefTheme");
            String theme = listPreference.getValue();
            if(theme.equals("0")){
                SwitchTheme.changeTheme(getActivity(), SwitchTheme.THEME_DEFAULT);
            }
            else if(theme.equals("1")){
                SwitchTheme.changeTheme(getActivity(), SwitchTheme.THEME_DARK);
            }

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String chosenTheme = o.toString();

                    if(chosenTheme.equals("0")){
                        listPreference.setValue("0");
                        SwitchTheme.changeTheme(getActivity(), SwitchTheme.THEME_DARK);
                    }
                    else if(chosenTheme.equals("1")){
                        listPreference.setValue("1");
                        SwitchTheme.changeTheme(getActivity(), SwitchTheme.THEME_DEFAULT);
                    }
                    getActivity().recreate();
                    return false;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        if (getParent() == null) {
            setResult(RESULT_CODE_THEME_UPDATED, returnIntent);
        } else {
            getParent().setResult(RESULT_CODE_THEME_UPDATED, returnIntent);
        }
        super.onBackPressed();
    }

    private class RefershActivityOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        private final int resultCode;
        public RefershActivityOnPreferenceChangeListener(int resultCode) {
            this.resultCode = resultCode;
        }

        @Override
        public boolean onPreferenceChange(Preference p, Object newValue) {
            setResult(resultCode);
            return true;
        }
    }
}
