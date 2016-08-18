package com.github.jinatonic.confetti.confetto;

import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Abstract class that represents a single confetto on the screen. This class holds all of the
 * internal states for the confetto to help it animate.
 */
public abstract class Confetto {
    private final Matrix matrix = new Matrix();
    private final Paint workPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Configured coordinate states
    private float initialDelay;
    private float initialX, initialY, initialVelocityX, initialVelocityY,
            accelerationX, accelerationY;
    private float maximumVelocityX, maximumVelocityY;
    private long millisToReachMaximumVelocityX, millisToReachMaximumVelocityY;

    // Configured rotation states
    private float initialRotation, initialRotationalVelocity, rotationalAcceleration;
    private float maximumRotationalVelocity;
    private long millisToReachMaximumRotationalVelocity;

    // Configured animation states
    private long ttl;
    // TODO: incorporate fadeout
    private boolean fadeOut;
    private Interpolator fadeOutInterpolator;

    // Current draw states
    private float currentX, currentY;
    private float currentRotation;
    private float alpha;
    private boolean startedAnimation, terminated;

    /**
     * This method should be called after all of the confetto's state variables are configured
     * and before the confetto gets animated.
     */
    protected void prepare() {
        millisToReachMaximumVelocityX = (long)
                ((maximumVelocityX - initialVelocityX) / accelerationX);
        millisToReachMaximumVelocityY = (long)
                ((maximumVelocityY - initialVelocityY) / accelerationY);
        millisToReachMaximumRotationalVelocity = (long)
                ((maximumRotationalVelocity - initialRotationalVelocity) / rotationalAcceleration);

        configurePaint(workPaint);
    }

    protected void configurePaint(Paint paint) {
        // Hook for subclasses to configure the default paint attributes.
    }

    /**
     * Update the confetto internal state based on the provided passed time.
     *
     * @param passedTime time since the beginning of the animation.
     * @param bound the space in which the confetto can display in.
     * @return whether this particular confetto has terminated.
     */
    public boolean applyUpdate(long passedTime, Rect bound) {
        passedTime -= initialDelay;
        startedAnimation = passedTime >= 0;

        terminated |= startedAnimation && ttl > passedTime;
        if (startedAnimation && !terminated) {
            currentX = computeDistance(passedTime, initialX, initialVelocityX, accelerationX,
                    millisToReachMaximumVelocityX, maximumVelocityX);
            currentY = computeDistance(passedTime, initialY, initialVelocityY, accelerationY,
                    millisToReachMaximumVelocityY, maximumVelocityY);
            currentRotation = computeDistance(passedTime, initialRotation,
                    initialRotationalVelocity, rotationalAcceleration,
                    millisToReachMaximumRotationalVelocity, maximumRotationalVelocity);

            // TODO fix this
            terminated = currentX >= bound.width() || currentY >= bound.height();
        }

        return terminated;
    }

    private float computeDistance(long t, float xi, float vi, float ai, long maxTime,
            float vMax) {
        if (t < maxTime) {
            // distance covered with linear acceleration
            // distance = xi + vi * t + 1/2 * a * t^2
            return xi + vi * t + 0.5f * ai * t * t;
        } else {
            // distance covered with linear acceleration + distance covered with max velocity
            // distance = xi + vi * maxTime + 1/2 * a * maxTime^2 + (t - maxTime) * vMax;
            return xi + vi * maxTime + 0.5f * ai * maxTime * maxTime + (t - maxTime) * vMax;
        }
    }

    /**
     * Primary method for rendering this confetto on the canvas.
     *
     * @param canvas the canvas to draw on.
     */
    public void draw(Canvas canvas) {
        if (startedAnimation && !terminated) {
            matrix.reset();
            drawInternal(canvas, matrix, workPaint, currentX, currentY, currentRotation);
        }
    }

    /**
     * Subclasses need to override this method to optimize for the way to draw the appropriate
     * confetto on the canvas.
     *
     * @param canvas the canvas to draw on.
     * @param matrix an identity matrix to use for draw manipulations.
     * @param paint the paint to perform canvas draw operations on. This paint has already been
     *              configured via {@link #configurePaint(Paint)}.
     * @param x the x position of the confetto relative to the canvas.
     * @param y the y position of the confetto relative to the canvas.
     * @param rotation the rotation (in degrees) to draw the confetto.
     */
    protected abstract void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x,
            float y, float rotation);

    /**
     * Helper builder class to construct all of the necessary values used in {@link #prepare()}.
     * The primary purpose of this is to better visualize the values you are setting.
     */
    public static class Configurator {
        private Confetto confetto;

        public Configurator setConfetto(Confetto confetto) {
            this.confetto = confetto;

            // Reset everything to default value
            confetto.initialDelay = 0f;
            confetto.initialX = confetto.initialY = 0f;
            confetto.initialVelocityX = confetto.initialVelocityY = 0f;
            confetto.accelerationX = confetto.accelerationY = 0f;
            confetto.maximumVelocityX = confetto.maximumVelocityY = 0f;
            confetto.initialRotation = 0f;
            confetto.initialRotationalVelocity = confetto.rotationalAcceleration = 0f;
            confetto.maximumRotationalVelocity = 0f;

            return this;
        }

        public Configurator setInitialDelay(float val) {
            confetto.initialDelay = val;
            return this;
        }

        public Configurator setInitialX(float val) {
            confetto.initialX = val;
            return this;
        }

        public Configurator setInitialY(float val) {
            confetto.initialY = val;
            return this;
        }

        public Configurator setInitialVelocityX(float val) {
            confetto.initialVelocityX = val;
            return this;
        }

        public Configurator setInitialVelocityY(float val) {
            confetto.initialVelocityY = val;
            return this;
        }

        public Configurator setAccelerationX(float val) {
            confetto.accelerationX = val;
            return this;
        }

        public Configurator setAccelerationY(float val) {
            confetto.accelerationY = val;
            return this;
        }

        public Configurator setMaximumVelocityX(float val) {
            confetto.maximumVelocityX = val;
            return this;
        }

        public Configurator setMaximumVelocityY(float val) {
            confetto.maximumVelocityY = val;
            return this;
        }

        public Configurator setInitialRotation(float val) {
            confetto.initialRotation = val;
            return this;
        }

        public Configurator setInitialRotationalVelocity(float val) {
            confetto.initialRotationalVelocity = val;
            return this;
        }

        public Configurator setRotationalAcceleration(float val) {
            confetto.rotationalAcceleration = val;
            return this;
        }

        public Configurator setMaximumRotationalVelocity(float val) {
            confetto.maximumRotationalVelocity = val;
            return this;
        }

        public Configurator setTTL(long val) {
            confetto.ttl = val;
            return this;
        }

        public Configurator setFadeOut(boolean fadeOut, Interpolator fadeOutInterpolator) {
            confetto.fadeOut = fadeOut;
            confetto.fadeOutInterpolator = fadeOutInterpolator;
            return this;
        }

        public Confetto configure() {
            confetto.prepare();
            return confetto;
        }
    }
}
