package ru.openet.nix.opennetclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SettingsHolderActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_holder);
        mToolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.preferences);
        /*getFragmentManager().beginTransaction()
                .add(R.id.settings_fragment_holder, new SettingsFragment()).commit();*/
    }
}
