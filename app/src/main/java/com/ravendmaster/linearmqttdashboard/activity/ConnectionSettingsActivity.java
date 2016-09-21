package com.ravendmaster.linearmqttdashboard.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ravendmaster.linearmqttdashboard.service.AppSettings;
import com.ravendmaster.linearmqttdashboard.R;

public class ConnectionSettingsActivity extends AppCompatActivity {

    EditText server;
    EditText port;
    EditText username;
    EditText password;
    EditText server_topic;
    EditText push_notifications_subscribe_topic;
    //CheckBox notifications_service;
    CheckBox connection_in_background;
    CheckBox server_mode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean validateUrl(String adress) {
        if(adress.endsWith(".xyz"))return true;
        return Patterns.DOMAIN_NAME.matcher(adress).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Save:


                if (!validateUrl(server.getText().toString())) {
                    //Toast.makeText(getApplicationContext(), "Server address is incorrect!", Toast.LENGTH_SHORT).show();
                    //return false;
                }


                AppSettings settings = AppSettings.getInstance();
                settings.server = server.getText().toString();
                settings.port = port.getText().toString();
                settings.username = username.getText().toString();
                settings.password = password.getText().toString();
                settings.server_topic = server_topic.getText().toString();
                settings.push_notifications_subscribe_topic = push_notifications_subscribe_topic.getText().toString();
                settings.connection_in_background = connection_in_background.isChecked();
                settings.server_mode = server_mode.isChecked();

                settings.saveConnectionSettingsToPrefs(this);

                //MainActivity.presenter.restartService(this);
                if(MainActivity.presenter!=null) {
                    MainActivity.presenter.connectionSettingsChanged();
                }

                finish();
                //MainActivity.connectToMQTTServer(getApplicationContext());
                MainActivity.presenter.resetCurrentSessionTopicList();

                MainActivity.presenter.subscribeToAllTopicsInDashboards(settings);
                break;


                /*
                Intent intent = new Intent();

                intent.putExtra("server", server.getText().toString());
                intent.putExtra("port", port.getText().toString());
                intent.putExtra("username", username.getText().toString());
                intent.putExtra("password", password.getText().toString());
                intent.putExtra("subscribe_topic", subscribe_topic.getText().toString());
                intent.putExtra("push_notifications_subscribe_topic", push_notifications_subscribe_topic.getText().toString());

                setResult(RESULT_OK, intent);
                */



        }
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        server = (EditText) findViewById(R.id.editText_server);
        port = (EditText) findViewById(R.id.editText_port);
        username = (EditText) findViewById(R.id.editText_username);
        password = (EditText) findViewById(R.id.editText_password);
        server_topic = (EditText) findViewById(R.id.editText_server_topic);
        push_notifications_subscribe_topic = (EditText) findViewById(R.id.editText_push_notifications_subscribe_topic);
        //notifications_service = (CheckBox) findViewById(R.id.checkBox_start_notifications_service);
        connection_in_background = (CheckBox) findViewById(R.id.checkBox_connection_in_background);
        server_mode = (CheckBox) findViewById(R.id.checkBox_server_mode);


        AppSettings settings = AppSettings.getInstance();
        server.setText(settings.server);
        port.setText(settings.port);
        username.setText(settings.username);
        password.setText(settings.password);
        server_topic.setText(settings.server_topic);
        push_notifications_subscribe_topic.setText(settings.push_notifications_subscribe_topic);
        connection_in_background.setChecked(settings.connection_in_background);
        server_mode.setChecked(settings.server_mode);

    }

    public void OnClickHelp(View view){
        MainActivity.presenter.OnClickHelp(this, view);
    }

}
