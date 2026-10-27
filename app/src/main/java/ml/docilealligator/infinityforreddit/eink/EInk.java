package ml.docilealligator.infinityforreddit.eink;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

/**
 * E-ink display support. Holds the E-ink settings (mirrored from the default SharedPreferences so views can read them
 * cheaply) and helpers that replace animated UI changes with instant ones when animations are disabled.
 */
public final class EInk {
    private static final String TAG = "EInk";

    public static final String DISABLE_ANIMATIONS = "eink_disable_animations";
    public static final String GALLERY_SWIPE_NAVIGATION = "eink_gallery_swipe_navigation";
    public static final String GALLERY_TAP_NAVIGATION = "eink_gallery_tap_navigation";
    public static final String PAGE_TURN_TAP_ZONES = "eink_page_turn_tap_zones";
    public static final String PAGE_TURN_TAP_ZONE_SIDE = "eink_page_turn_tap_zone_side";
    public static final String PAGE_TURN_TAP_ZONE_WIDTH = "eink_page_turn_tap_zone_width";
    public static final String PAGE_TURN_SHOW_TAP_ZONES = "eink_page_turn_show_tap_zones";
    public static final String APPLY_PRESET = "eink_apply_preset";
    public static final String PRESET_PROMPT_SHOWN = "eink_preset_prompt_shown";

    public static final String TAP_ZONE_SIDE_RIGHT = "right";
    public static final String TAP_ZONE_SIDE_LEFT = "left";
    public static final String TAP_ZONE_SIDE_BOTH = "both";
    private static final String DEFAULT_TAP_ZONE_WIDTH_DP = "72";

    private static volatile boolean animationsDisabled;
    private static volatile boolean gallerySwipeNavigation = true;
    private static volatile boolean galleryTapNavigation;
    private static volatile boolean pageTurnTapZones;
    private static volatile String pageTurnTapZoneSide = TAP_ZONE_SIDE_RIGHT;
    private static volatile int pageTurnTapZoneWidthDp = Integer.parseInt(DEFAULT_TAP_ZONE_WIDTH_DP);
    private static volatile boolean showPageTurnTapZones = true;

    @Nullable
    private static SharedPreferences sharedPreferences;
    // SharedPreferences only keeps weak references to its listeners.
    @Nullable
    private static SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private EInk() {
    }

    public static void init(@NonNull Application application, @NonNull SharedPreferences defaultSharedPreferences) {
        sharedPreferences = defaultSharedPreferences;
        loadSettings(defaultSharedPreferences);
        preferenceChangeListener = (preferences, key) -> {
            if (key == null || key.startsWith("eink_")) {
                loadSettings(preferences);
                applyAnimatorDurationScale(application);
            }
        };
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        application.registerActivityLifecycleCallbacks(new EInkActivityLifecycleCallbacks());
    }

    private static void loadSettings(SharedPreferences preferences) {
        animationsDisabled = preferences.getBoolean(DISABLE_ANIMATIONS, false);
        gallerySwipeNavigation = preferences.getBoolean(GALLERY_SWIPE_NAVIGATION, true);
        galleryTapNavigation = preferences.getBoolean(GALLERY_TAP_NAVIGATION, false);
        pageTurnTapZones = preferences.getBoolean(PAGE_TURN_TAP_ZONES, false);
        pageTurnTapZoneSide = preferences.getString(PAGE_TURN_TAP_ZONE_SIDE, TAP_ZONE_SIDE_RIGHT);
        try {
            pageTurnTapZoneWidthDp = Integer.parseInt(preferences.getString(PAGE_TURN_TAP_ZONE_WIDTH, DEFAULT_TAP_ZONE_WIDTH_DP));
        } catch (NumberFormatException e) {
            pageTurnTapZoneWidthDp = Integer.parseInt(DEFAULT_TAP_ZONE_WIDTH_DP);
        }
        showPageTurnTapZones = preferences.getBoolean(PAGE_TURN_SHOW_TAP_ZONES, true);
    }

    @Nullable
    static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static boolean animationsDisabled() {
        return animationsDisabled;
    }

    /** Whether pagers and tabs should animate when switching pages programmatically. */
    public static boolean smoothPageChanges() {
        return !animationsDisabled;
    }

    static boolean gallerySwipeNavigation() {
        return gallerySwipeNavigation;
    }

    static boolean galleryTapNavigation() {
        return galleryTapNavigation;
    }

    static String pageTurnTapZoneSide() {
        return pageTurnTapZoneSide;
    }

    static int pageTurnTapZoneWidthDp() {
        return pageTurnTapZoneWidthDp;
    }

    static boolean showPageTurnTapZones() {
        return showPageTurnTapZones;
    }

    /** BOOX (Onyx) devices; used to offer the e-ink preset on first launch. */
    public static boolean isEInkDevice() {
        return "onyx".equalsIgnoreCase(Build.MANUFACTURER) || "onyx".equalsIgnoreCase(Build.BRAND)
                || "boox".equalsIgnoreCase(Build.BRAND);
    }

    /**
     * Sets the duration scale of all ValueAnimator-based animations in this process (property animations,
     * ViewPropertyAnimator, most Material Components animations, indeterminate progress indicators) to 0 when
     * animations are disabled, or back to the system setting. ValueAnimator.setDurationScale is a hidden API, so this
     * is best effort; the explicit handling elsewhere in this class does not depend on it.
     */
    static void applyAnimatorDurationScale(@NonNull Context context) {
        float scale = animationsDisabled ? 0f : Settings.Global.getFloat(context.getContentResolver(),
                Settings.Global.ANIMATOR_DURATION_SCALE, 1f);
        try {
            ValueAnimator.class.getMethod("setDurationScale", float.class).invoke(null, scale);
        } catch (ReflectiveOperationException | RuntimeException e) {
            Log.w(TAG, "Unable to change the animator duration scale", e);
        }
    }

    /** Animation resource for a fragment transaction, or 0 (no animation) when animations are disabled. */
    public static int fragmentAnimation(int animationResId) {
        return animationsDisabled ? 0 : animationResId;
    }

    /** Drop-in replacement for TransitionManager.beginDelayedTransition(root, new AutoTransition()). */
    public static void beginDelayedTransition(@NonNull ViewGroup sceneRoot) {
        if (!animationsDisabled) {
            TransitionManager.beginDelayedTransition(sceneRoot, new AutoTransition());
        }
    }

    /**
     * Starts the smooth scroll, or jumps straight to its target position (aligned to the top) when animations are
     * disabled.
     */
    public static void startSmoothScroll(@NonNull RecyclerView.LayoutManager layoutManager,
                                         @NonNull RecyclerView.SmoothScroller smoothScroller) {
        if (!animationsDisabled) {
            layoutManager.startSmoothScroll(smoothScroller);
            return;
        }

        int targetPosition = smoothScroller.getTargetPosition();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(targetPosition, 0);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) layoutManager).scrollToPositionWithOffset(targetPosition, 0);
        } else {
            layoutManager.scrollToPosition(targetPosition);
        }
    }

    public static void openDrawer(@NonNull DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START, !animationsDisabled);
    }

    public static void closeDrawer(@NonNull DrawerLayout drawerLayout) {
        drawerLayout.closeDrawer(GravityCompat.START, !animationsDisabled);
    }

    /**
     * Makes the toolbar's navigation (hamburger) button open and close the drawer instantly, without the
     * hamburger-to-arrow animation. Call after constructing the ActionBarDrawerToggle, which installs its own click
     * listener on the toolbar.
     */
    public static void setUpDrawerToggle(@NonNull DrawerLayout drawerLayout, @NonNull ActionBarDrawerToggle toggle,
                                         @NonNull Toolbar toolbar) {
        if (!animationsDisabled) {
            return;
        }

        toggle.setDrawerSlideAnimationEnabled(false);
        toolbar.setNavigationOnClickListener(view -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                closeDrawer(drawerLayout);
            } else {
                openDrawer(drawerLayout);
            }
        });
    }

    /**
     * Applies the E-ink settings to a vertically scrolling list of posts or comments: removes item animations (e.g.
     * comment collapse/expand) and adds the page-turn tap zones.
     */
    public static void setUpScrollingList(@Nullable RecyclerView recyclerView, int tapZoneHintColor) {
        if (recyclerView == null) {
            return;
        }

        if (animationsDisabled) {
            recyclerView.setItemAnimator(null);
        }
        if (pageTurnTapZones) {
            PageTurnTapZones.attach(recyclerView, tapZoneHintColor);
        }
    }

    static void disableActivityTransitions(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0);
            activity.overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0);
        } else {
            activity.overridePendingTransition(0, 0);
        }
    }
}
