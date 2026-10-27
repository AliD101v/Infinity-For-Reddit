package ml.docilealligator.infinityforreddit.eink;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.activities.MainActivity;

/** Applies the E-ink settings to every activity, including the media viewers that don't extend BaseActivity. */
class EInkActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (EInk.animationsDisabled()) {
            activity.getWindow().setWindowAnimations(R.style.EInkNoWindowAnimations);
            EInk.disableActivityTransitions(activity);
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        // The system resets the animator duration scale when the process creates its first window and whenever the
        // global setting changes, so re-apply it once the window is attached.
        activity.getWindow().getDecorView().post(() -> EInk.applyAnimatorDurationScale(activity.getApplicationContext()));

        if (activity instanceof MainActivity) {
            SharedPreferences sharedPreferences = EInk.getSharedPreferences();
            if (sharedPreferences != null && EInk.isEInkDevice()
                    && !sharedPreferences.getBoolean(EInk.PRESET_PROMPT_SHOWN, false)) {
                sharedPreferences.edit().putBoolean(EInk.PRESET_PROMPT_SHOWN, true).apply();
                EInkPreset.showApplyDialog(activity, R.string.eink_preset_detected_dialog_title);
            }
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (EInk.animationsDisabled() && activity.isFinishing()
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}
