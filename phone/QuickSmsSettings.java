package cn.ingenic.glasssync.phone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import com.sctek.smartglasses.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cn.ingenic.glasssync.Config;
import cn.ingenic.glasssync.DefaultSyncManager;
import cn.ingenic.glasssync.data.DefaultProjo;
import cn.ingenic.glasssync.data.Projo;
import cn.ingenic.glasssync.data.ProjoType;

/**
 *  merage from Phone/src/...
 * Helper class to manage the "Respond via SMS" feature for incoming calls.
 * @see InCallScreen.internalRespondViaSms()
 * @author dfdun
 */

/**
 * Settings activity under "Call settings" to let you manage the canned
 * responses; see respond_via_sms_settings.xml
 */
public class QuickSmsSettings extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
    public static final String SHARED_PREFERENCES_NAME = "respond_via_sms_prefs";
    private static final String KEY_CANNED_RESPONSE_PREF_1 = "canned_response_pref_1";
    private static final String KEY_CANNED_RESPONSE_PREF_2 = "canned_response_pref_2";
    private static final String KEY_CANNED_RESPONSE_PREF_3 = "canned_response_pref_3";
    private static final String KEY_CANNED_RESPONSE_PREF_4 = "canned_response_pref_4";
    public static final int SYNC_SMS_CODE = 30;// opeator code

    final String KEY_SYNC_VIA_SMS = "sync_via_sms";
    final String[] keys = { KEY_CANNED_RESPONSE_PREF_1,
            KEY_CANNED_RESPONSE_PREF_2, KEY_CANNED_RESPONSE_PREF_3,
            KEY_CANNED_RESPONSE_PREF_4 };
    ArrayList<Preference> mPreferences = new ArrayList<Preference>();

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager()
                .setSharedPreferencesName(SHARED_PREFERENCES_NAME);

        addPreferencesFromResource(R.xml.respond_via_sms_settings);

        EditTextPreference pref;
        pref = (EditTextPreference) findPreference(KEY_CANNED_RESPONSE_PREF_1);
        pref.setTitle(pref.getText());
        pref.setOnPreferenceChangeListener(this);
        mPreferences.add(pref);

        pref = (EditTextPreference) findPreference(KEY_CANNED_RESPONSE_PREF_2);
        pref.setTitle(pref.getText());
        pref.setOnPreferenceChangeListener(this);
        mPreferences.add(pref);

        pref = (EditTextPreference) findPreference(KEY_CANNED_RESPONSE_PREF_3);
        pref.setTitle(pref.getText());
        pref.setOnPreferenceChangeListener(this);
        mPreferences.add(pref);

        pref = (EditTextPreference) findPreference(KEY_CANNED_RESPONSE_PREF_4);
        pref.setTitle(pref.getText());
        pref.setOnPreferenceChangeListener(this);
        mPreferences.add(pref);

        findPreference(KEY_SYNC_VIA_SMS).setOnPreferenceClickListener(this);
    }

    // Preference.OnPreferenceChangeListener implementation
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        EditTextPreference pref = (EditTextPreference) preference;
        pref.setTitle((String) newValue);
        Projo projo = new DefaultProjo(EnumSet.allOf(PhoneColumn.class),
                ProjoType.DATA);
        projo.put(PhoneColumn.state, SYNC_SMS_CODE);
        projo.put(PhoneColumn.name, preference.getKey());
        projo.put(PhoneColumn.phoneNumber, ((String) newValue));
        Config config = new Config(PhoneModule.PHONE);
        ArrayList<Projo> datas = new ArrayList<Projo>(1);
        datas.add(projo);
        DefaultSyncManager.getDefault().request(config, datas);
        return true; // means it's OK to update the state of the Preference with
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(KEY_SYNC_VIA_SMS)) {
            Log.i("dfdun", "preference is clicked");
            Config config = new Config(PhoneModule.PHONE);
            ArrayList<Projo> datas = new ArrayList<Projo>(1);
            for (Preference pref : mPreferences) {
                Projo projo = new DefaultProjo(
                        EnumSet.allOf(PhoneColumn.class), ProjoType.DATA);
                projo.put(PhoneColumn.state, SYNC_SMS_CODE);
                projo.put(PhoneColumn.name, pref.getKey());
                projo.put(PhoneColumn.phoneNumber, pref.getTitle());
                datas.add(projo);
            }
            DefaultSyncManager.getDefault().request(config, datas);
        }
        return true;
    }
}
