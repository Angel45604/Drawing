package com.marcos.angel.drawing;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.enrico.colorpicker.colorDialog;

import static com.marcos.angel.drawing.SettingsActivity.SettingsFragment.backgroundColor;
import static com.marcos.angel.drawing.SettingsActivity.SettingsFragment.lineColor;

/**
 * Created by angel on 16/09/2017.
 */

public class SettingsActivity extends AppCompatActivity implements  colorDialog.ColorSelectedListener{

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,new SettingsFragment())
                .commit();
    }

    @Override
    public void onColorSelection(android.support.v4.app.DialogFragment dialogFragment, int selectedColor) {
        int tag;

        tag = Integer.valueOf(dialogFragment.getTag());
        switch(tag){
            case 1:
                colorDialog.setPickerColor(SettingsActivity.this,1,selectedColor);

                colorDialog.setColorPreferenceSummary(backgroundColor,selectedColor,SettingsActivity.this,getResources());
                break;

            case 2:
                colorDialog.setPickerColor(SettingsActivity.this,2,selectedColor);

                colorDialog.setColorPreferenceSummary(lineColor,selectedColor,SettingsActivity.this, getResources());
        }
    }

    public static class SettingsFragment extends PreferenceFragment{

        static Preference backgroundColor, lineColor;

        Context mContext;

        @Override
        public void onAttach(Context context){
            super.onAttach(context);
            mContext=context;
            Log.d("Attached",mContext.toString());
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            Log.d("Attached act",appCompatActivity.toString());

            addPreferencesFromResource(R.xml.preferences);

            backgroundColor = findPreference("backgroundColor");
            lineColor = findPreference("lineColor");

            int color = colorDialog.getPickerColor(getActivity(),1);

            colorDialog.setColorPreferenceSummary(backgroundColor,color,getActivity(),getResources());

            backgroundColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    colorDialog.showColorPicker(appCompatActivity,1);
                    return false;
                }
            });

            int color2= colorDialog.getPickerColor(getActivity(),2);

            colorDialog.setColorPreferenceSummary(lineColor,color2,getActivity(),getResources());

            lineColor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    colorDialog.showColorPicker(appCompatActivity,2);
                    return false;
                }
            });



        }

    }

}
