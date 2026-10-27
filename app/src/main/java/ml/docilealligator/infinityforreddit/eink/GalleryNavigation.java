package ml.docilealligator.infinityforreddit.eink;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.futured.hauler.HaulerView;
import ml.docilealligator.infinityforreddit.customviews.LinearLayoutManagerBugFixed;
import ml.docilealligator.infinityforreddit.customviews.ViewPagerBugFixed;
import ml.docilealligator.infinityforreddit.utils.SharedPreferencesUtils;

/**
 * Gallery navigation settings for e-ink displays: swiping either drags images as usual, changes them instantly (when
 * animations are disabled) or is turned off, and tapping near the left/right edge can go to the previous/next image.
 * Used by the inline galleries in posts and by the full-screen gallery pager (ViewPagerBugFixed).
 */
public final class GalleryNavigation {
    // Taps within this fraction of the gallery's width from the left/right edge change the image.
    private static final float EDGE_TAP_ZONE = 0.25f;

    private GalleryNavigation() {
    }

    /** Whether galleries follow the finger while swiping (the default behavior). */
    public static boolean isDragEnabled() {
        return EInk.gallerySwipeNavigation() && !EInk.animationsDisabled();
    }

    /** Whether a horizontal swipe should change the image instantly instead of dragging it. */
    public static boolean isInstantSwipeEnabled() {
        return EInk.gallerySwipeNavigation() && EInk.animationsDisabled();
    }

    public static boolean isTapNavigationEnabled() {
        return EInk.galleryTapNavigation();
    }

    /** @return -1 to go to the previous image, 1 for the next one, 0 if x is not in an edge tap zone. */
    public static int edgeTapDirection(float x, int width) {
        if (!EInk.galleryTapNavigation() || width <= 0) {
            return 0;
        }
        if (x < width * EDGE_TAP_ZONE) {
            return -1;
        }
        if (x > width * (1 - EDGE_TAP_ZONE)) {
            return 1;
        }
        return 0;
    }

    /**
     * Sets up a full-screen gallery: swipe and tap navigation, and when animations are disabled, closing it with a
     * vertical swipe without the drag-to-dismiss animation (if "Swipe vertically to go back from media" is on).
     */
    public static void setUpFullScreenGallery(@NonNull Activity activity, @NonNull ViewPagerBugFixed viewPager,
                                              @NonNull HaulerView haulerView) {
        viewPager.setGalleryNavigationEnabled(true);
        if (!EInk.animationsDisabled()) {
            return;
        }

        // The pager handles the whole gesture itself; the drag-to-dismiss HaulerView (and the NestedScrollView that
        // feeds it) would only take vertical swipes away from it.
        haulerView.setDragEnabled(false);
        viewPager.setKeepGesturesFromParents(true);
        SharedPreferences sharedPreferences = EInk.getSharedPreferences();
        if (sharedPreferences != null
                && sharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_VERTICALLY_TO_GO_BACK_FROM_MEDIA, true)) {
            viewPager.setOnSwipeToCloseListener(activity::finish);
        }
    }

    /**
     * For the click handling of inline galleries: call on a tap before treating it as a click.
     *
     * @return true if the tap was in an edge tap zone and has been handled (the click should be ignored).
     */
    public static boolean onInlineGalleryTap(@NonNull RecyclerView gallery, @NonNull MotionEvent e) {
        int direction = edgeTapDirection(e.getX(), gallery.getWidth());
        if (direction == 0 || getPagerLayoutManager(gallery) == null) {
            return false;
        }
        changeInlineGalleryImage(gallery, direction);
        return true;
    }

    /** For the touch handling of inline galleries: call when a drag ends, with the total distance moved. */
    public static void onInlineGallerySwipe(@NonNull RecyclerView gallery, float dx, float dy) {
        if (!isInstantSwipeEnabled()) {
            return;
        }
        int touchSlop = ViewConfiguration.get(gallery.getContext()).getScaledTouchSlop();
        if (Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy)) {
            changeInlineGalleryImage(gallery, dx < 0 ? 1 : -1);
        }
    }

    /** @return the layout manager if the gallery is shown as a horizontal pager (not as a grid), otherwise null. */
    @Nullable
    private static LinearLayoutManager getPagerLayoutManager(RecyclerView gallery) {
        RecyclerView.LayoutManager layoutManager = gallery.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager && !(layoutManager instanceof GridLayoutManager)
                && ((LinearLayoutManager) layoutManager).getOrientation() == RecyclerView.HORIZONTAL) {
            return (LinearLayoutManager) layoutManager;
        }
        return null;
    }

    private static void changeInlineGalleryImage(RecyclerView gallery, int direction) {
        LinearLayoutManager layoutManager = getPagerLayoutManager(gallery);
        RecyclerView.Adapter<?> adapter = gallery.getAdapter();
        if (layoutManager == null || adapter == null) {
            return;
        }

        int target = layoutManager.findFirstVisibleItemPosition() + direction;
        if (target < 0 || target >= adapter.getItemCount()) {
            return;
        }
        if (isDragEnabled()) {
            gallery.smoothScrollToPosition(target);
        } else {
            // Smooth scrolling needs canScrollHorizontally(), which InlineGalleryLayoutManager turns off.
            gallery.scrollToPosition(target);
        }
    }

    /** Horizontal layout manager for inline galleries that ignores finger drags unless isDragEnabled(). */
    public static class InlineGalleryLayoutManager extends LinearLayoutManagerBugFixed {
        private final HorizontalSwipeClaimer horizontalSwipeClaimer = new HorizontalSwipeClaimer();

        public InlineGalleryLayoutManager(Context context) {
            super(context, RecyclerView.HORIZONTAL, false);
        }

        @Override
        public boolean canScrollHorizontally() {
            return isDragEnabled() && super.canScrollHorizontally();
        }

        @Override
        public void onAttachedToWindow(RecyclerView view) {
            super.onAttachedToWindow(view);
            view.addOnItemTouchListener(horizontalSwipeClaimer);
        }

        @Override
        public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
            super.onDetachedFromWindow(view, recycler);
            view.removeOnItemTouchListener(horizontalSwipeClaimer);
        }
    }

    /**
     * A gallery that doesn't scroll never claims horizontal swipes, so the vertical list around it would take over
     * the gesture as soon as the finger drifts vertically. While swipes are instant, claim them like a scrolling
     * gallery would, so they reach the gallery's touch handling.
     */
    private static class HorizontalSwipeClaimer implements RecyclerView.OnItemTouchListener {
        private float downX;
        private float downY;

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
            if (!isInstantSwipeEnabled()) {
                return false;
            }
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                downX = e.getX();
                downY = e.getY();
            } else if (e.getActionMasked() == MotionEvent.ACTION_MOVE) {
                float dx = Math.abs(e.getX() - downX);
                int touchSlop = ViewConfiguration.get(recyclerView.getContext()).getScaledTouchSlop();
                ViewParent parent = recyclerView.getParent();
                if (dx > touchSlop && dx > Math.abs(e.getY() - downY) && parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
