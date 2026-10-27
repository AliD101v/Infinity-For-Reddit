package ml.docilealligator.infinityforreddit.eink;

import android.os.Bundle;

import androidx.preference.Preference;

import org.greenrobot.eventbus.EventBus;

import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.customviews.preference.CustomFontPreferenceFragmentCompat;
import ml.docilealligator.infinityforreddit.events.RecreateActivityEvent;

public class EInkPreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.eink_preferences, rootKey);

        Preference applyPresetPreference = findPreference(EInk.APPLY_PRESET);
        if (applyPresetPreference != null) {
            applyPresetPreference.setOnPreferenceClickListener(preference -> {
                EInkPreset.showApplyDialog(mActivity, R.string.eink_preset_dialog_title);
                return true;
            });
        }

        // Screens apply these when they are created; the gallery settings are read on every gesture instead.
        String[] recreateKeys = {EInk.DISABLE_ANIMATIONS, EInk.PAGE_TURN_TAP_ZONES, EInk.PAGE_TURN_TAP_ZONE_SIDE,
                EInk.PAGE_TURN_TAP_ZONE_WIDTH, EInk.PAGE_TURN_SHOW_TAP_ZONES};
        for (String key : recreateKeys) {
            Preference preference = findPreference(key);
            if (preference != null) {
                preference.setOnPreferenceChangeListener((changedPreference, newValue) -> {
                    EventBus.getDefault().post(new RecreateActivityEvent());
                    return true;
                });
            }
        }
    }
}
