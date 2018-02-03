package ru.openet.nix.opennetclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Nix on 01.02.2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener{
    private Preference mAboutPreference, mLicencePreference, mDevPreference;
    private CheckBoxPreference mBrowserPreference, mThemePreference;
    private String mMessage;

    public static final String KEY_BROWSER_TYPE = "b_key";
    public static final String KEY_THEME_TYPE = "t_key";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLicenseMessage();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_settings, rootKey);
        mBrowserPreference = (CheckBoxPreference) getPreferenceManager().findPreference(getString(R.string.browser_key));
        mThemePreference = (CheckBoxPreference) getPreferenceManager().findPreference(getString(R.string.theme_key));
        mAboutPreference = getPreferenceManager().findPreference(getString(R.string.app_key));
        mLicencePreference = getPreferenceManager().findPreference(getString(R.string.licence_key));
        mDevPreference = getPreferenceManager().findPreference(getString(R.string.go_dev_key));
        mAboutPreference.setOnPreferenceClickListener(this);
        mLicencePreference.setOnPreferenceClickListener(this);
        mDevPreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "a_key":
                Toast.makeText(getContext(), R.string.app_vesion, Toast.LENGTH_LONG).show();
                break;
            case "dev_key":
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
                startActivity(Intent.createChooser(intent, "Send Email"));
                break;
            case "l_key":
                showLicenses();
                break;
        }
        return true;
    }

    private void initLicenseMessage(){
        try{
            AssetManager assetManager = getContext().getAssets();
            InputStream inputStream = assetManager.open("licence.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                if(receiveString.equals("")){
                    stringBuilder.append(System.getProperty("line.separator"));
                }else{
                    stringBuilder.append(receiveString);
                }
            }
            inputStream.close();
            mMessage = stringBuilder.toString();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void showLicenses(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.licence)
                .setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
