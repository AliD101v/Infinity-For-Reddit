package ml.docilealligator.infinityforreddit.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import ml.docilealligator.infinityforreddit.eink.GalleryNavigation;

public class ViewPagerBugFixed extends ViewPager {
    // E-ink gallery navigation (see GalleryNavigation), enabled by the full-screen gallery activities. Gestures are
    // tracked in dispatchTouchEvent because zoomable images block onInterceptTouchEvent until they are swiped at
    // their edge, so taps (and quick swipes on a busy e-ink device) would never reach it.
    private boolean galleryNavigation;
    @Nullable
    private Runnable onSwipeToCloseListener;
    private boolean keepGesturesFromParents;
    private final int touchSlop;
    private float downX;
    private float downY;
    private boolean multiTouch;
    private boolean gestureHandled;
    // Set while a child, e.g. a zoomed-in image that is being panned, keeps the gesture to itself.
    private boolean childDisallowsIntercept;

    public ViewPagerBugFixed(@NonNull Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public ViewPagerBugFixed(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setGalleryNavigationEnabled(boolean enabled) {
        galleryNavigation = enabled;
    }

    /** Called on a vertical swipe (only in gallery navigation mode), e.g. to close the gallery without animation. */
    public void setOnSwipeToCloseListener(@Nullable Runnable onSwipeToCloseListener) {
        this.onSwipeToCloseListener = onSwipeToCloseListener;
    }

    /**
     * Stops parents from taking over gestures in gallery navigation mode, e.g. a NestedScrollView feeding a
     * drag-to-dismiss HaulerView, which would otherwise cancel vertical (and diagonal) swipes meant for this view.
     */
    public void setKeepGesturesFromParents(boolean keepGesturesFromParents) {
        this.keepGesturesFromParents = keepGesturesFromParents;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        childDisallowsIntercept = disallowIntercept;
        // When a child releases the gesture, ViewGroup would also let the parents intercept it again.
        super.requestDisallowInterceptTouchEvent(disallowIntercept || (galleryNavigation && keepGesturesFromParents));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (galleryNavigation && handleGalleryGesture(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (galleryNavigation && !GalleryNavigation.isDragEnabled()) {
            // Keep receiving the gesture, but never drag the pages.
            return true;
        }

        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ignore) {}

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (galleryNavigation && !GalleryNavigation.isDragEnabled()) {
            return false;
        }

        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ignore) {}

        return false;
    }

    /** @return true if the event has been consumed by the gallery navigation. */
    private boolean handleGalleryGesture(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                multiTouch = false;
                gestureHandled = false;
                // ViewGroup resets the disallow flag on ACTION_DOWN without calling requestDisallowInterceptTouchEvent.
                childDisallowsIntercept = false;
                if (keepGesturesFromParents && getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            case MotionEvent.ACTION_POINTER_DOWN:
                multiTouch = true;
                return gestureHandled;
            case MotionEvent.ACTION_MOVE:
                if (!gestureHandled && !multiTouch && !childDisallowsIntercept
                        && GalleryNavigation.isInstantSwipeEnabled()) {
                    int direction = getHorizontalSwipeDirection(ev);
                    if (direction != 0) {
                        cancelGestureForChildren(ev);
                        changePage(direction);
                    }
                }
                return gestureHandled;
            case MotionEvent.ACTION_UP:
                if (!gestureHandled && !multiTouch) {
                    handleGestureEnd(ev);
                }
                return gestureHandled;
            default:
                return gestureHandled;
        }
    }

    private void handleGestureEnd(MotionEvent ev) {
        float dx = ev.getX() - downX;
        float dy = ev.getY() - downY;
        if (!childDisallowsIntercept) {
            // Quick swipes may end before a move event reaches this view after the image releases the gesture.
            int direction = GalleryNavigation.isInstantSwipeEnabled() ? getHorizontalSwipeDirection(ev) : 0;
            if (direction != 0) {
                cancelGestureForChildren(ev);
                changePage(direction);
                return;
            }
            if (onSwipeToCloseListener != null && Math.abs(dy) > Math.max(4 * touchSlop, getHeight() / 10)
                    && Math.abs(dy) > 2 * Math.abs(dx)) {
                cancelGestureForChildren(ev);
                onSwipeToCloseListener.run();
                return;
            }
        }

        boolean isTap = Math.abs(dx) <= touchSlop && Math.abs(dy) <= touchSlop
                && ev.getEventTime() - ev.getDownTime() < ViewConfiguration.getLongPressTimeout();
        if (isTap && isOnMedia(ev.getX(), ev.getY())) {
            int direction = GalleryNavigation.edgeTapDirection(ev.getX(), getWidth());
            if (direction != 0) {
                // Also stops the image from treating the tap as a click (which toggles the toolbar).
                cancelGestureForChildren(ev);
                changePage(direction);
            }
        }
    }

    /** @return 1 for a swipe to the left (next page), -1 for a swipe to the right, 0 if not a horizontal swipe. */
    private int getHorizontalSwipeDirection(MotionEvent ev) {
        float dx = ev.getX() - downX;
        float dy = ev.getY() - downY;
        if (Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy)) {
            return dx < 0 ? 1 : -1;
        }
        return 0;
    }

    /** Whether (x, y) is on the image itself rather than on a button or the caption. */
    private boolean isOnMedia(float x, float y) {
        View view = findClickableViewAt(this, x, y);
        return view == null || (view.getWidth() >= getWidth() / 2 && view.getHeight() >= getHeight() / 2);
    }

    @Nullable
    private static View findClickableViewAt(View view, float x, float y) {
        if (view.getVisibility() != VISIBLE) {
            return null;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                View child = group.getChildAt(i);
                float childX = x + group.getScrollX() - child.getLeft() - child.getTranslationX();
                float childY = y + group.getScrollY() - child.getTop() - child.getTranslationY();
                if (childX >= 0 && childY >= 0 && childX < child.getWidth() && childY < child.getHeight()) {
                    View clickableView = findClickableViewAt(child, childX, childY);
                    if (clickableView != null) {
                        return clickableView;
                    }
                }
            }
        }
        return view.isClickable() || view.isLongClickable() ? view : null;
    }

    /** Sends ACTION_CANCEL to the children and consumes the rest of the gesture. */
    private void cancelGestureForChildren(MotionEvent ev) {
        gestureHandled = true;
        MotionEvent cancelEvent = MotionEvent.obtain(ev);
        cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
        super.dispatchTouchEvent(cancelEvent);
        cancelEvent.recycle();
    }

    private void changePage(int direction) {
        PagerAdapter adapter = getAdapter();
        int target = getCurrentItem() + direction;
        if (adapter != null && target >= 0 && target < adapter.getCount()) {
            setCurrentItem(target, GalleryNavigation.isDragEnabled());
        }
    }
}
