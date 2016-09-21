package com.ravendmaster.linearmqttdashboard.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.ravendmaster.linearmqttdashboard.BoardFragment;
import com.ravendmaster.linearmqttdashboard.ListFragment;
import com.ravendmaster.linearmqttdashboard.Log;
import com.ravendmaster.linearmqttdashboard.Utilites;
import com.ravendmaster.linearmqttdashboard.customview.ButtonsSet;
import com.ravendmaster.linearmqttdashboard.customview.MyButton;
import com.ravendmaster.linearmqttdashboard.customview.MyColors;
import com.ravendmaster.linearmqttdashboard.customview.MyTabsController;
import com.ravendmaster.linearmqttdashboard.service.AppSettings;

import com.ravendmaster.linearmqttdashboard.service.MQTTService;
import com.ravendmaster.linearmqttdashboard.service.Presenter;
import com.ravendmaster.linearmqttdashboard.customview.RGBLEDView;
import com.ravendmaster.linearmqttdashboard.service.WidgetData;
import com.ravendmaster.linearmqttdashboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, Presenter.IView, Switch.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, MyButton.OnMyButtonEventListener, ButtonsSet.OnButtonsSetEventListener {

    public MainActivity() {
        super();
        instance = this;
        presenter = new Presenter(this);
    }

    public static Presenter presenter;

    public static MainActivity instance;

    public static DisplayMetrics mDisplayMetrics;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    //DragListView mDragListView;
    //ListAdapter mListAdapter;

    MyTabsController tabsController;

    public final int ConnectionSettings_CHANGE = 0;
    public final int WidgetEditMode_CREATE = 1;
    public final int WidgetEditMode_EDIT = 2;
    public final int WidgetEditMode_COPY = 3;
    public final int Tabs = 4;

    MenuItem menuItem_add_new_widget;
    MenuItem menuItem_clear_dashboard;

    RGBLEDView mqttBrokerStatusRGBLEDView;
    RGBLEDView connectionStatusRGBLEDView;

    Menu optionsMenu;


    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    public boolean onLongClick(View view) {
        Log.d(getClass().getName(), "long click");
        return presenter.onLongClick(view);
    }

    @Override
    public void OnButtonsSetPressed(ButtonsSet buttonSet, int index) {
        presenter.OnButtonsSetPressed(buttonSet, index);
    }

    public enum DASHBOARD_VIEW_MODE {
        SIMPLE,
        COMPACT
    }

    DASHBOARD_VIEW_MODE mDashboardViewMode;

    public DASHBOARD_VIEW_MODE getDashboardViewMode() {
        if (mDashboardViewMode == null) {

            SharedPreferences sprefs = getPreferences(MODE_PRIVATE);
            String dashboard_view_mode_text = sprefs.getString("dashboard_view_mode", "");
            switch (dashboard_view_mode_text) {
                case "simple":
                    mDashboardViewMode = DASHBOARD_VIEW_MODE.SIMPLE;
                    break;
                case "compact":
                    mDashboardViewMode = DASHBOARD_VIEW_MODE.COMPACT;
                    break;
            }

        }
        return mDashboardViewMode;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        menuItem_add_new_widget = menu.findItem(R.id.Add_new_widget);
        menuItem_clear_dashboard = menu.findItem(R.id.Clean_dashboard);


        updatePlayPauseMenuItem();

        AppSettings appSettings = AppSettings.getInstance();
        if (appSettings.server == null) return true;
        if (appSettings.server.equals("ravend.asuscomm.com")) {
            optionsMenu.findItem(R.id.request_prices).setVisible(true);
            optionsMenu.findItem(R.id.clear_buys).setVisible(true);
            optionsMenu.findItem(R.id.disable_ads).setVisible(true);

            optionsMenu.findItem(R.id.action_board).setVisible(true);
            optionsMenu.findItem(R.id.action_lists).setVisible(true);
        }

        optionsMenu.findItem(R.id.donate).setVisible(true);

        getDashboardViewMode();
        doRefreshMenu();

        return super.onCreateOptionsMenu(menu);
    }


    void updatePlayPauseMenuItem() {

        MenuItem menuItemPlayPause = optionsMenu.findItem(R.id.Edit_play_mode);

        MenuItem menuItemAutoCreateWidgets = optionsMenu.findItem(R.id.auto_create_widgets);

        if (presenter.isEditMode()) {
            menuItemPlayPause.setIcon(R.drawable.ic_play);
            menuItemAutoCreateWidgets.setVisible( presenter.getUnusedTopics().length>0 );

        } else {
            menuItemPlayPause.setIcon(R.drawable.ic_pause);
            menuItemAutoCreateWidgets.setVisible(false);
        }

        menuItem_add_new_widget.setVisible(presenter.isEditMode());
        menuItem_clear_dashboard.setVisible(presenter.isEditMode());

    }

    void doRefreshMenu() {
        if (mDashboardViewMode != null) {
            switch (mDashboardViewMode) {
                case SIMPLE:
                    optionsMenu.findItem(R.id.simple_mode).setChecked(true);
                    optionsMenu.findItem(R.id.compact_mode).setChecked(false);
                    break;
                case COMPACT:
                    optionsMenu.findItem(R.id.simple_mode).setChecked(false);
                    optionsMenu.findItem(R.id.compact_mode).setChecked(true);
                    break;
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        presenter.onMainMenuItemSelected();

        switch (item.getItemId()) {
            case R.id.action_lists:
                showFragment(ListFragment.newInstance());
                return true;
            case R.id.action_board:
                mBoardFragment = BoardFragment.newInstance();
                showFragment(mBoardFragment);
                return true;
        }


        switch (item.getItemId()) {

            case R.id.Edit_play_mode:
                presenter.setEditMode(!presenter.isEditMode());
                updatePlayPauseMenuItem();
                refreshDashboard(true);
                if (!presenter.isEditMode()) {
                    //presenter.saveActiveDashboard(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Play mode", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Edit mode", Toast.LENGTH_SHORT).show();
                }
                if (mListFragment != null) {
                    mListFragment.notifyItemChangedAll();
                }
                if (mBoardFragment != null) {
                    mBoardFragment.notifyItemChangedAll();
                }

                break;
            case R.id.Add_new_widget:
                if (presenter.getTabs().getItems().size() == 0) {
                    presenter.addNewTab("default");
                    presenter.saveTabsList(this);
                    Toast.makeText(getApplicationContext(), "Added tab", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(this, WidgetEditorActivity.class);
                intent.putExtra("createNew", true);
                startActivityForResult(intent, WidgetEditMode_CREATE);
                break;

            case R.id.auto_create_widgets:
                if (presenter.getTabs().getItems().size() == 0) {
                    presenter.addNewTab("default");
                    presenter.saveTabsList(this);
                    Toast.makeText(getApplicationContext(), "Added tab", Toast.LENGTH_SHORT).show();
                }
                showDiscoveredDataDialog();
                break;

            case R.id.Save_dashboard:
                presenter.saveActiveDashboard(getApplicationContext(), presenter.getActiveDashboardId());
                break;
            case R.id.Init_default_dashboard:
                presenter.initDemoDashboard();
                refreshDashboard(true);
                presenter.saveActiveDashboard(this, presenter.getActiveDashboardId());
                break;
            case R.id.connection_settings:
                intent = new Intent(this, ConnectionSettingsActivity.class);
                startActivityForResult(intent, ConnectionSettings_CHANGE);
                break;
            case R.id.tabs:
                intent = new Intent(this, TabsActivity.class);
                startActivityForResult(intent, Tabs);
                break;
            case R.id.Clean_dashboard:

                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle("Clean dashboard");  // заголовок
                ad.setMessage("Widgets list is cleared!"); // сообщение
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        presenter.clearDashboard();
                        presenter.saveActiveDashboard(getApplicationContext(), presenter.getActiveDashboardId());
                        mListFragment.notifyDataSetChanged();

                    }
                });

                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                    }
                });

                ad.show();


                break;

            case R.id.simple_mode:
                mDashboardViewMode = DASHBOARD_VIEW_MODE.SIMPLE;
                doRefreshMenu();
                break;

            case R.id.compact_mode:
                mDashboardViewMode = DASHBOARD_VIEW_MODE.COMPACT;
                doRefreshMenu();
                break;


            case R.id.start_service:
                //restart service
                //Intent service_intent = new Intent(this, MQTTService.class);
                //startService(service_intent);
                break;
            case R.id.stop_service:
                //service_intent = new Intent(this, MQTTService.class);
                //stopService(service_intent);
                break;

            case R.id.subscribe:
                presenter.subscribe();
                break;

            case R.id.import_settings:
                intent = new Intent(this, OpenFileActivity.class);
                startActivityForResult(intent, 0);
                break;

            case R.id.request_prices:

                ArrayList<String> skuList = new ArrayList<String>();
                //skuList.add("premiumUpgrade");
                //skuList.add("gas");
                skuList.add("test_1");
                skuList.add("test_2");
                skuList.add("adfree");
                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                try {
                    Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);

                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        Log.d("buy", " getSkuDetails() OK!");

                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                        for (String thisResponse : responseList) {

                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            //if (sku.equals("premiumUpgrade")) mPremiumUpgradePrice = price;
                            //else if (sku.equals("gas")) mGasPrice = price;
                            Log.d("buy", "Sku " + sku + " price = " + price);
                        }

                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.donate:
                if (mService != null) {
                    try {
                        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                                "donate", "inapp", "$$K7NkCD#4My3i#0KxXI~lCXnsIff1oLLuPdBVL~");

                        int response = buyIntentBundle.getInt("RESPONSE_CODE");
                        if (response == 0) {

                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            startIntentSenderForResult(pendingIntent.getIntentSender(),
                                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                    Integer.valueOf(0));
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.clear_buys:
                presenter.doAdfree(this, false);
                break;

            case R.id.disable_ads:
                presenter.doAdfree(this, true);
                makeAdfreeNow();

                break;

            case R.id.share_settings:
                if (shouldAskPermission()) {
                    String perms = "android.permission.WRITE_EXTERNAL_STORAGE";

                    if (checkSelfPermission(perms) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{perms}, 200);
                    } else {
                        if (shouldShowRequestPermissionRationale(perms)) {
                            int permsRequestCode = 200;
                            requestPermissions(new String[]{perms}, permsRequestCode);
                        } else {
                            //уже спрашивали, всё норм
                            shareSettings();
                        }
                    }
                } else {
                    shareSettings();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void refreshTabState() {
        tabsController.refreshState(this);
    }

    void showDiscoveredDataDialog() {

        final Object[] unusedTopics = presenter.getUnusedTopics();// appSettings.getTabNames();

        final CharSequence[] items = new CharSequence[unusedTopics.length];
        int index = 0;
        for (Object tabName : unusedTopics) {
            items[index++] = (String) tabName;
        }
        final ArrayList selectedItems = new ArrayList();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Create widgets for topic:")
                .setCancelable(false)
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                for(Object item:selectedItems){
                                    WidgetData widgetData=new WidgetData();
                                    widgetData.setName(0, (String)unusedTopics[(int)item]);
                                    widgetData.setTopic(0, (String)unusedTopics[(int)item]);

                                    presenter.addWidget(widgetData);
                                }
                                presenter.saveActiveDashboard(getApplicationContext(), MainActivity.presenter.getActiveDashboardId());
                                refreshDashboard(false);
                                onTabSelected();//для обновления содержимого вкладки
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        tabSelectAlertDialog = alertDialogBuilder.create();
        tabSelectAlertDialog.show();
    }

    void shareSettings() {
        File file = createExternalStoragePublicDownloads();
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        this.startActivity(Intent.createChooser(emailIntent, "Share settings..."));
    }

    File createExternalStoragePublicDownloads() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "config.linear");

        try {
            path.mkdirs();
            OutputStream os_ = new FileOutputStream(file);
            ZipOutputStream os = new ZipOutputStream(new BufferedOutputStream(os_));

            String allSettings = AppSettings.getInstance().getSettingsAsString();
            os.putNextEntry(new ZipEntry("settings"));
            buff = Utilites.stringToBytesUTFCustom(allSettings);
            os.flush();
            os.write(buff);
            os.close();


        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Tabs) {
            refreshTabState();
        }

        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    void refreshDashboard(boolean hard_mode) {
        if (presenter.isEditMode() && !hard_mode) return;
    }

    @Override
    public void onRefreshDashboard() {
        refreshDashboard(true);
    }

    @Override
    public void notifyPayloadOfWidgetChanged(int screenTabIndex, int widgetIndex) {
        if (mListFragment != null && presenter.getScreenActiveTabIndex() == screenTabIndex) {
            mListFragment.notifyItemChanged(widgetIndex);
        }

        if (mBoardFragment != null) {
            mBoardFragment.notifyItemChanged(screenTabIndex, widgetIndex);
        }
    }

    @Override
    public void setBrokerStatus(Presenter.CONNECTION_STATUS status) {
        mqttBrokerStatusRGBLEDView.setColorLight(getRGBLEDColorOfConnectionStatus(status));
    }

    @Override
    public void setNetworkStatus(Presenter.CONNECTION_STATUS status) {
        connectionStatusRGBLEDView.setColorLight(getRGBLEDColorOfConnectionStatus(status));
    }

    @Override
    public void onOpenValueSendMessageDialog(WidgetData widgetData) {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.new_value_send_dialog, null);
        TextView nameView = (TextView) promptsView.findViewById(R.id.textView_name);
        nameView.setText(widgetData.getName(0));


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set new_value_send_dialogue_send_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        userInput.setText(presenter.getMQTTCurrentValue(widgetData.getTopic(0)).replace("*", ""));

        if (widgetData.type == WidgetData.WidgetTypes.VALUE && widgetData.mode == 1) {
            userInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL | EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
        }


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                presenter.sendMessageNewValue(userInput.getText().toString());
                                refreshDashboard(true);
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
    public void onTabSelected() {

        refreshDashboard(true);

        if (mListFragment != null) {
            mListFragment.notifyDataSetChanged();
        }
        if (mBoardFragment != null) {
            mBoardFragment.notifyDataSetChanged();
        }
    }

    @Override
    public void showAd() {
        showInterstitial();
    }

    @Override
    public void showPopUpMessage(String title, String text) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(title);
        ad.setMessage(text);
        ad.show();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        presenter.onProgressChanged(seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        presenter.onStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        presenter.onStopTrackingTouch(seekBar);
    }

    //вызов меню параметров виджета (на три точки)
    public void showPopupMenuWidgetEditButtonOnClick(View view) {

        final WidgetData widget = (WidgetData) view.getTag();
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.widget, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.widget_edit:
                        Intent intent = new Intent(MainActivity.this, WidgetEditorActivity.class);
                        intent.putExtra("widget_index", presenter.getWidgetIndex(widget));
                        startActivityForResult(intent, WidgetEditMode_EDIT);
                        return true;

                    case R.id.widget_copy:
                        intent = new Intent(MainActivity.this, WidgetEditorActivity.class);
                        intent.putExtra("createCopy", true);
                        intent.putExtra("widget_index", presenter.getWidgetIndex(widget));
                        startActivityForResult(intent, WidgetEditMode_COPY);
                        return true;

                    case R.id.widget_remove:
                        presenter.removeWidget(widget);
                        presenter.saveActiveDashboard(getApplicationContext(), presenter.getActiveDashboardId());
                        if (mListFragment != null) {
                            mListFragment.notifyDataSetChanged();
                        }
                        if (mBoardFragment != null) {
                            mBoardFragment.notifyDataSetChanged();
                        }
                        return true;

                    case R.id.widget_move:
                        showWidgetMoveToTabDialog(widget);
                        break;
                }
                return false;
            }
        });


        popup.show();
    }


    //вызов комбо бокс списка
    public void showPopupMenuComboBoxSelectorButtonOnClick(View view) {

        presenter.onComboBoxSelector(view);//регистрируем нажатый виджет для дальнейшего publish

        final WidgetData widget = (WidgetData) view.getTag();

        final String[] values = widget.publishValue.split(",");

        final CharSequence[] items = new CharSequence[values.length];
        int index = 0;
        for (String value : values) {
            String[]valueLabel=value.split("\\|");
            String label;
            if(valueLabel.length>0) {
                label = valueLabel.length == 2 ? valueLabel[1] : valueLabel[0];
            }else label="";

            items[index++] = label;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(widget.getName(0))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        String[]valueLabel=values[item].split("\\|");
                        if(valueLabel.length==0)return;
                        String newValue = valueLabel[0];
                        presenter.sendComboBoxNewValue(newValue);
                    }
                })

                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        tabSelectAlertDialog = alertDialogBuilder.create();
        tabSelectAlertDialog.show();

    }

    AlertDialog tabSelectAlertDialog;

    void showWidgetMoveToTabDialog(final WidgetData widget) {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.select_tab, null);

        ListView listView = (ListView) promptsView.findViewById(R.id.listView);

        AppSettings appSettings = AppSettings.getInstance();
        final String[] tabNames = appSettings.getTabNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_item, tabNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tabSelectAlertDialog.cancel();
                AppSettings appSettings = AppSettings.getInstance();
                presenter.moveWidgetTo(getApplicationContext(), widget, appSettings.getDashboardIDByTabIndex(position));
                if (mListFragment != null) {
                    mListFragment.notifyDataSetChanged();
                }
                if (mBoardFragment != null) {
                    mBoardFragment.notifyDataSetChanged();
                }
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setTitle("Select tab")
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        tabSelectAlertDialog = alertDialogBuilder.create();
        tabSelectAlertDialog.show();
    }


    @Override
    public AppCompatActivity getAppCompatActivity() {
        return this;
    }

    int getRGBLEDColorOfConnectionStatus(Presenter.CONNECTION_STATUS status) {
        switch (status) {
            case DISCONNECTED:
                return MyColors.getRed();
            case IN_PROGRESS:
                return MyColors.getYellow();
            case CONNECTED:
                return MyColors.getGreen();
            default:
                return 0;
        }
    }


    @Override
    public void onPause() {
        Log.d(getClass().getName(), "onPause()");

        if (mAdView != null) {
            mAdView.pause();
        }
        presenter.onPause();
        super.onPause();
    }


    @Override
    public void onResume() {
        Log.d(getClass().getName(), "onResume()");
        super.onResume();

        presenter.onResume(this);
        refreshTabState();


        if (mAdView != null) {
            mAdView.resume();
        }


    }

    void makeAdfreeNow() {
        ViewGroup.LayoutParams params = mAdView.getLayoutParams();
        params.height = 0;
        mAdView.setLayoutParams(params);
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "fragment").commit();
    }

    ListFragment mListFragment;
    BoardFragment mBoardFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(getClass().getName(), "onCreate()");
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            inputFileAlreadyPrecessed = false;
        }
        //showFragment(BoardFragment.newInstance());
        mListFragment = ListFragment.newInstance();
        showFragment(mListFragment);

        tabsController = (MyTabsController) findViewById(R.id.my_tabs_controller);

        presenter.onCreate(this);

        if (presenter.getDashboards() == null) {

            Intent service_intent = new Intent(this, MQTTService.class);
            service_intent.setAction("autostart");
            startService(service_intent);

            finish();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            return;
        }


        mDisplayMetrics = getResources().getDisplayMetrics();

        mqttBrokerStatusRGBLEDView = (RGBLEDView) findViewById(R.id.mqtt_broker_status_RGBLed);
        connectionStatusRGBLEDView = (RGBLEDView) findViewById(R.id.connection_status_RGBLed);

        //banner
        mAdView = (AdView) findViewById(R.id.ad_view);
        if (presenter.isAdfree()) {
            makeAdfreeNow();
        } else {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("F394250231C267DE7F334073FF339307")
                    .build();
            mAdView.loadAd(adRequest);
        }


        //InterstitialAd
        MobileAds.initialize(this, "ca-app-pub-5722465293744437~2768742309");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5722465293744437/1091261108");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        //billing
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        //import settings
        checkInputFileAndProcess();
        if (shouldAskPermission()) {
            String perms = "android.permission.READ_EXTERNAL_STORAGE";

            if (checkSelfPermission(perms) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{perms}, 201);
            } else {
                if (shouldShowRequestPermissionRationale(perms)) {
                    int permsRequestCode = 201;
                    requestPermissions(new String[]{perms}, permsRequestCode);
                }
            }
        }
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F394250231C267DE7F334073FF339307")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
        }
    }

    static byte buff[];
    static String result;
    static boolean inputFileAlreadyPrecessed;

    void checkInputFileAndProcess() {
        if (inputFileAlreadyPrecessed) return;
        //inputFileAlreadyPrecessed=true;
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals("android.intent.action.VIEW")) {
            Uri data = intent.getData();
            try {
                InputStream is_ = getContentResolver().openInputStream(data);
                ZipInputStream is = new ZipInputStream(new BufferedInputStream(is_));

                ZipEntry entry;
                while ((entry = is.getNextEntry()) != null) {

                    ByteArrayOutputStream os = new ByteArrayOutputStream();


                    byte[] buff = new byte[1024];
                    int count;
                    while ((count = is.read(buff, 0, 1024)) != -1) {
                        os.write(buff, 0, count);
                    }
                    os.flush();
                    os.close();

                    result = Utilites.bytesToStringUTFCustom(os.toByteArray(), os.toByteArray().length);


                    AlertDialog.Builder ad = new AlertDialog.Builder(this);
                    ad.setTitle("Import settings");  // заголовок
                    ad.setMessage("All settings (except password) will be replaced. Continue?"); // сообщение
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            inputFileAlreadyPrecessed = true;

                            AppSettings settings = AppSettings.getInstance();
                            settings.setSettingsFromString(result);

                            settings.saveTabsSettingsToPrefs(MainActivity.instance);
                            settings.saveConnectionSettingsToPrefs(MainActivity.instance);

                            presenter.createDashboardsBySettings();
                            MainActivity.presenter.saveAllDashboards(getApplicationContext());

                            MainActivity.presenter.connectionSettingsChanged();

                            presenter.onTabPressed(0);
                            refreshTabState();
                        }
                    });
                    ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            inputFileAlreadyPrecessed = true;
                        }
                    });
                    ad.show();


                    //}
                }
                is.close();
                is_.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeAccepted) {
                    Log.d(getClass().getName(), "perm OK");
                    shareSettings();
                } else {
                    Log.d(getClass().getName(), "perm forbidden");
                }
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getName(), "onDestroy()");

        if (mService != null) {
            unbindService(mServiceConn);
        }

        presenter.onDestroy(this);
        saveDashboardViewMode();

        try {
            unbindService(mServiceConn);
        } catch (Exception e) {

        }
    }

    void saveDashboardViewMode() {
        SharedPreferences sprefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sprefs.edit();
        ed.putString("dashboard_view_mode", mDashboardViewMode == DASHBOARD_VIEW_MODE.SIMPLE ? "simple" : "compact");
        if (!ed.commit()) {
            Log.d(getClass().getName(), "dashboard_view_mode commit failure!!!");
        }
    }

    @Override
    public void OnMyButtonDown(MyButton button) {
        presenter.onMyButtonDown(button);
        //Log.d(getClass().getName(), "OnMyButtonDown()");
    }

    @Override
    public void OnMyButtonUp(MyButton button) {
        presenter.onMyButtonUp(button);
        //Log.d(getClass().getName(), "OnMyButtonUp()");
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!buttonView.isPressed()) return;
        presenter.onClickWidgetSwitch(buttonView);
        //Log.d(getClass().getName(), "onCheckedChanged()");
    }
}
