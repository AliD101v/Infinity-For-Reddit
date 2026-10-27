package ml.docilealligator.infinityforreddit.eink;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import ml.docilealligator.infinityforreddit.Infinity;
import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.asynctasks.InsertCustomTheme;
import ml.docilealligator.infinityforreddit.customtheme.CustomTheme;
import ml.docilealligator.infinityforreddit.database.RedditDataRoomDatabase;
import ml.docilealligator.infinityforreddit.events.RecreateActivityEvent;
import ml.docilealligator.infinityforreddit.font.ContentFontFamily;
import ml.docilealligator.infinityforreddit.font.ContentFontStyle;
import ml.docilealligator.infinityforreddit.font.FontFamily;
import ml.docilealligator.infinityforreddit.font.TitleFontFamily;
import ml.docilealligator.infinityforreddit.font.TitleFontStyle;
import ml.docilealligator.infinityforreddit.utils.SharedPreferencesUtils;

/**
 * One-tap setup for e-ink displays: turns on the E-ink settings, applies the E-ink theme and fonts, and switches off
 * existing features that animate or move content around.
 */
public class EInkPreset {
    private static final String THEME_LIGHT = "0";

    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences mLightThemeSharedPreferences;
    private final SharedPreferences mDarkThemeSharedPreferences;
    private final SharedPreferences mAmoledThemeSharedPreferences;
    private final RedditDataRoomDatabase mRedditDataRoomDatabase;
    private final Executor mExecutor;

    @Inject
    public EInkPreset(@Named("default") SharedPreferences sharedPreferences,
                      @Named("light_theme") SharedPreferences lightThemeSharedPreferences,
                      @Named("dark_theme") SharedPreferences darkThemeSharedPreferences,
                      @Named("amoled_theme") SharedPreferences amoledThemeSharedPreferences,
                      RedditDataRoomDatabase redditDataRoomDatabase, Executor executor) {
        mSharedPreferences = sharedPreferences;
        mLightThemeSharedPreferences = lightThemeSharedPreferences;
        mDarkThemeSharedPreferences = darkThemeSharedPreferences;
        mAmoledThemeSharedPreferences = amoledThemeSharedPreferences;
        mRedditDataRoomDatabase = redditDataRoomDatabase;
        mExecutor = executor;
    }

    public static void showApplyDialog(@NonNull Activity activity, @StringRes int titleResId) {
        new MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialogTheme)
                .setTitle(titleResId)
                .setMessage(R.string.eink_preset_dialog_message)
                .setPositiveButton(R.string.eink_preset_apply, (dialogInterface, i) ->
                        ((Infinity) activity.getApplication()).getAppComponent().eInkPreset().apply(activity))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void apply(@NonNull Context context) {
        mSharedPreferences.edit()
                .putBoolean(EInk.DISABLE_ANIMATIONS, true)
                .putBoolean(EInk.GALLERY_SWIPE_NAVIGATION, true)
                .putBoolean(EInk.GALLERY_TAP_NAVIGATION, true)
                .putBoolean(EInk.PAGE_TURN_TAP_ZONES, true)
                .putString(SharedPreferencesUtils.THEME_KEY, THEME_LIGHT)
                .putString(SharedPreferencesUtils.FONT_FAMILY_KEY, FontFamily.AtkinsonHyperlegible.name())
                .putString(SharedPreferencesUtils.TITLE_FONT_FAMILY_KEY, TitleFontFamily.AtkinsonHyperlegibleBold.name())
                .putString(SharedPreferencesUtils.CONTENT_FONT_FAMILY_KEY, ContentFontFamily.AtkinsonHyperlegible.name())
                .putString(SharedPreferencesUtils.TITLE_FONT_SIZE_KEY, TitleFontStyle.Large.name())
                .putString(SharedPreferencesUtils.CONTENT_FONT_SIZE_KEY, ContentFontStyle.Large.name())
                .putString(SharedPreferencesUtils.VIDEO_AUTOPLAY, SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_NEVER)
                .putBoolean(SharedPreferencesUtils.LOCK_TOOLBAR, true)
                .putBoolean(SharedPreferencesUtils.LOCK_BOTTOM_APP_BAR, true)
                .putBoolean(SharedPreferencesUtils.LOCK_JUMP_TO_NEXT_TOP_LEVEL_COMMENT_BUTTON, true)
                .putBoolean(SharedPreferencesUtils.ENABLE_SWIPE_ACTION, false)
                .putBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, false)
                .putBoolean(SharedPreferencesUtils.DISABLE_SWIPING_BETWEEN_TABS, true)
                .apply();

        CustomTheme eInkTheme = EInkTheme.create(context);
        eInkTheme.isLightTheme = true;
        Context applicationContext = context.getApplicationContext();
        InsertCustomTheme.insertCustomTheme(mExecutor, new Handler(Looper.getMainLooper()), mRedditDataRoomDatabase,
                mLightThemeSharedPreferences, mDarkThemeSharedPreferences, mAmoledThemeSharedPreferences, eInkTheme,
                false, new InsertCustomTheme.InsertCustomThemeListener() {
                    @Override
                    public void success() {
                        Toast.makeText(applicationContext, R.string.eink_preset_applied, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new RecreateActivityEvent());
                    }

                    @Override
                    public void duplicate() {
                        // Not reachable: duplicates are only reported when checkDuplicate is true.
                    }
                });
    }
}
