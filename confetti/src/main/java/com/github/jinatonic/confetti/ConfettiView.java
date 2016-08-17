package com.github.jinatonic.confetti;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.jinatonic.confetti.confetti.Confetti;

import java.util.List;

/**
 * A helper temporary view that helps render the confetti. This view will attach itself to the
 * view root, perform the animation, and then once all of the confetti has completed its animation,
 * it will automatically remove itself from the parent.
 */
public class ConfettiView extends View implements View.OnLayoutChangeListener {
    private List<Confetti> confettiList;
    private Rect bound;
    private long animateStartTime;
    private boolean terminated;

    public static ConfettiView newInstance(Context context) {
        final ConfettiView confettiView = new ConfettiView(context, null);
        confettiView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return confettiView;
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Starts the animation specified by the list of confettis and the bound.
     *
     * @param confettiList the list of confettis to animate.
     * @param bound the bound in which the confetti must reside in.
     */
    public void animate(List<Confetti> confettiList, Rect bound) {
        this.confettiList = confettiList;
        this.bound = bound;
        this.animateStartTime = SystemClock.elapsedRealtime();
        this.terminated = false;
        invalidate();
    }

    /**
     * Terminate the current running animation (if any) and remove this view from the parent.
     */
    public void terminate() {
        if (!terminated) {
            this.terminated = true;
            getParent().requestLayout();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewGroup parent = (ViewGroup) getParent();
        parent.removeOnLayoutChangeListener(this);
        parent.addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6,
            int i7) {
        if (terminated) {
            final ViewParent parent = getParent();
            if (parent != null && parent instanceof ViewGroup) {
                final ViewGroup vg = (ViewGroup) parent;
                vg.removeViewInLayout(this);
                vg.removeOnLayoutChangeListener(this);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!terminated) {
            final long elapsedTime = SystemClock.elapsedRealtime() - animateStartTime;
            final int width = getWidth();
            final int height = getHeight();
            boolean terminated = true;

            canvas.save();
            canvas.clipRect(bound);
            for (Confetti confetti : confettiList) {
                // TODO: this should take in bounds to better determine if animation is terminated
                terminated &= confetti.applyUpdate(elapsedTime, width, height);
                confetti.draw(canvas);
            }
            canvas.restore();

            this.terminated |= terminated;

            if (!terminated) {
                invalidate();
            } else {
                getParent().requestLayout();
            }
        }
    }
}
