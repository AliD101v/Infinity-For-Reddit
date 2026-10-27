package ml.docilealligator.infinityforreddit.eink;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Tap zones along the edge(s) of a vertical list that scroll it by one page without animation: the upper half of a
 * zone pages up and the lower half pages down. Only quick taps are consumed; drags and long presses starting in a zone
 * behave as usual. Optionally draws a thin outline with arrows so the zones can be found.
 */
class PageTurnTapZones extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener {
    // Fraction of the visible height kept on screen after a page turn, so the reader keeps some context.
    private static final float PAGE_OVERLAP = 0.1f;

    private final int zoneWidth;
    private final boolean leftZone;
    private final boolean rightZone;
    private final int touchSlop;
    private final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
    private final Paint hintPaint;
    private final Path arrowPath = new Path();
    private final float arrowSize;

    private float downX;
    private float downY;
    private boolean trackingTap;

    static void attach(@NonNull RecyclerView recyclerView, int hintColor) {
        PageTurnTapZones tapZones = new PageTurnTapZones(recyclerView, hintColor);
        recyclerView.addOnItemTouchListener(tapZones);
        if (EInk.showPageTurnTapZones()) {
            recyclerView.addItemDecoration(tapZones);
        }
    }

    private PageTurnTapZones(RecyclerView recyclerView, int hintColor) {
        float density = recyclerView.getResources().getDisplayMetrics().density;
        zoneWidth = Math.round(EInk.pageTurnTapZoneWidthDp() * density);
        String side = EInk.pageTurnTapZoneSide();
        leftZone = EInk.TAP_ZONE_SIDE_LEFT.equals(side) || EInk.TAP_ZONE_SIDE_BOTH.equals(side);
        rightZone = !EInk.TAP_ZONE_SIDE_LEFT.equals(side);
        touchSlop = ViewConfiguration.get(recyclerView.getContext()).getScaledTouchSlop();
        arrowSize = 8 * density;
        hintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hintPaint.setColor(hintColor);
        hintPaint.setStyle(Paint.Style.STROKE);
        hintPaint.setStrokeWidth(Math.max(1, density));
    }

    /** @return -1 for page up, 1 for page down, 0 if (x, y) is outside the tap zones. */
    private int pageDirectionAt(RecyclerView recyclerView, float x, float y) {
        boolean inZone = (leftZone && x <= zoneWidth) || (rightZone && x >= recyclerView.getWidth() - zoneWidth);
        if (!inZone) {
            return 0;
        }
        return y < recyclerView.getHeight() / 2f ? -1 : 1;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getX();
                downY = e.getY();
                trackingTap = pageDirectionAt(recyclerView, downX, downY) != 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (trackingTap && (Math.abs(e.getX() - downX) > touchSlop || Math.abs(e.getY() - downY) > touchSlop)) {
                    trackingTap = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (trackingTap && e.getEventTime() - e.getDownTime() < longPressTimeout) {
                    trackingTap = false;
                    int visibleHeight = recyclerView.getHeight() - recyclerView.getPaddingTop() - recyclerView.getPaddingBottom();
                    int distance = Math.round(visibleHeight * (1 - PAGE_OVERLAP));
                    recyclerView.scrollBy(0, pageDirectionAt(recyclerView, downX, downY) * distance);
                    // Intercepting cancels the touch for the item under the finger, so it doesn't register a click.
                    return true;
                }
                trackingTap = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_CANCEL:
                trackingTap = false;
                break;
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            trackingTap = false;
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (leftZone) {
            drawZoneHint(canvas, parent, zoneWidth, zoneWidth / 2f);
        }
        if (rightZone) {
            drawZoneHint(canvas, parent, parent.getWidth() - zoneWidth, parent.getWidth() - zoneWidth / 2f);
        }
    }

    private void drawZoneHint(Canvas canvas, RecyclerView parent, float edgeX, float centerX) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        canvas.drawLine(edgeX, top, edgeX, bottom, hintPaint);

        float upArrowY = parent.getHeight() / 4f;
        float downArrowY = parent.getHeight() * 3 / 4f;
        arrowPath.reset();
        arrowPath.moveTo(centerX - arrowSize, upArrowY + arrowSize / 2);
        arrowPath.lineTo(centerX, upArrowY - arrowSize / 2);
        arrowPath.lineTo(centerX + arrowSize, upArrowY + arrowSize / 2);
        arrowPath.moveTo(centerX - arrowSize, downArrowY - arrowSize / 2);
        arrowPath.lineTo(centerX, downArrowY + arrowSize / 2);
        arrowPath.lineTo(centerX + arrowSize, downArrowY - arrowSize / 2);
        canvas.drawPath(arrowPath, hintPaint);
    }
}
