package com.github.jinatonic.confetti;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.List;

/**
 * A helper temporary view that helps render the confetti. This view will attach itself to the
 * view root, perform the animation, and then once all of the confetti has completed its animation,
 * it will automatically remove itself from the parent.
 */
public class ConfettiView extends View implements View.OnLayoutChangeListener {
    private List<Confetto> confetti;
    private boolean terminated;

    public static ConfettiView newInstance(Context context) {
        final ConfettiView confettiView = new ConfettiView(context, null);
        confettiView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int defaultElevation = context.getResources().getDimensionPixelOffset(
                    R.dimen.confetti_default_elevation);
            confettiView.setElevation(defaultElevation);
        }

        return confettiView;
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(List<Confetto> confetti) {
        this.confetti = confetti;
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

    /**
     * Reset the internal state of this view to allow for a new confetti animation.
     */
    public void reset() {
        this.terminated = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewGroup parent = (ViewGroup) getParent();
        parent.removeOnLayoutChangeListener(this);
        parent.addOnLayoutChangeListener(this);

        // If we did not bind before attaching to the window, that means this ConfettiView no longer
        // has a ConfettiManager backing it and should just be terminated.
        if (confetti == null) {
            terminate();
        }
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
                vg.invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!terminated) {
            canvas.save();
            for (Confetto confetto : this.confetti) {
                confetto.draw(canvas);
            }
            canvas.restore();
        }
    }
}
