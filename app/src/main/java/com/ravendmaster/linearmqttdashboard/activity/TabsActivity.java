package com.ravendmaster.linearmqttdashboard.activity;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.ravendmaster.linearmqttdashboard.TabData;
import com.ravendmaster.linearmqttdashboard.TabListFragment;
import com.ravendmaster.linearmqttdashboard.service.AppSettings;
import com.ravendmaster.linearmqttdashboard.R;

public class TabsActivity extends AppCompatActivity {

    TabListFragment mTabsListFragment;

    public static TabsActivity instance;


    public void showPopupMenuTabEditButtonOnClick(View view) {

        final TabData tab = (TabData) view.getTag();

        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_tab, popup.getMenu());

        currentTabData = tab;

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tab_edit:
                        showTabEditDialog();
                        return true;
                    case R.id.tab_remove:
                        showTabRemoveDialog();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.tabs_container, fragment, "fragment").commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        setContentView(R.layout.activity_tabs);

        mTabsListFragment = TabListFragment.newInstance();
        showFragment(mTabsListFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.options_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    TabData currentTabData;

    void showTabRemoveDialog(){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Remove tab");  // заголовок
        ad.setMessage("A set of widgets on the panel will be lost. Continue?"); // сообщение
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                final AppSettings appSettings = AppSettings.getInstance();
                appSettings.removeTabByDashboardID(currentTabData.id);
                mTabsListFragment.notifyDataSetChanged();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.show();
    }

    void showTabEditDialog() {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.tab_name_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        if (currentTabData != null) {
            userInput.setText(currentTabData.name);
        }
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newName = userInput.getText().toString();
                                if (currentTabData == null) {
                                    MainActivity.presenter.addNewTab(newName);
                                } else {
                                    currentTabData.name = newName;
                                }
                                mTabsListFragment.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_new_tab:
                currentTabData = null;
                showTabEditDialog();
                break;
            case R.id.close:

                finish();
                break;

        }
        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();

        MainActivity.presenter.saveTabsList(this);


    }
}
