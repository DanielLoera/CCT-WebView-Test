package com.loera.ccttest;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private EditText edit;
    private String TAG = "CCT Tab Test";

    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private CustomTabsClient mCustomTabsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = (EditText) findViewById(R.id.edit);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mCustomTabsClient == null)
                    return;
                String currentText = s.toString();
                if(currentText.endsWith(".com") || currentText.endsWith(".org") || currentText.endsWith(".io")){
                    preloadChrome(currentText);
                }
            }
        });
        setupChromeTab();
    }

    private void setupChromeTab(){
        CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                mCustomTabsClient = client;
                Log.i(TAG, "Connected to Chrome Service");
                boolean warmup = mCustomTabsClient.warmup(0);
                Log.i(TAG,"Chrome Warmup: "+warmup);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        boolean ok = CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, connection);
        Log.i(TAG, "ChromeClient: "+ok);
    }

    private void preloadChrome(String url){
        CustomTabsSession session = mCustomTabsClient.newSession(new ChromeCallback());
        boolean mayLaunch = session.mayLaunchUrl(Uri.parse(url), null, null);
        Log.i(TAG, "May Launch Url Set: " + mayLaunch + " for: " + url);
    }

    private class ChromeCallback extends CustomTabsCallback{
        private final String TAG = "Chrome CallBack";
        public void onNavigationEvent(int navigationEvent, Bundle extras){
            Log.i(TAG, "New navigation event: " + navigationEvent);
        }
    }

    public void chromeButton(View v){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
        builder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        builder.setExitAnimations(this, android.R.anim.fade_out, android.R.anim.fade_out);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(edit.getText().toString()));
    }

    public void webButton(View v){
        Intent i = new Intent(this, WebActivity.class);
        i.putExtra("url", edit.getText().toString());
        startActivity(i);
    }
}
