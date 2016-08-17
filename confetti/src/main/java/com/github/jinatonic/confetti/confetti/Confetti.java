package com.github.jinatonic.confetti.confetti;

import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Abstract class that represents a single confetti on the screen. This class holds all of the
 * internal states for the confetti to help it animate.
 */
public abstract class Confetti {
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
     * This method should be called after all of the confetti's state variables are configured
     * and before the confetti gets animated.
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
     * Update the confetti internal state based on the provided passed time.
     *
     * @param passedTime time since the beginning of the animation.
     * @return whether this particular confetti has terminated.
     */
    public boolean applyUpdate(long passedTime, int canvasWidth, int canvasHeight) {
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

            terminated = currentX >= canvasWidth || currentY >= canvasHeight;
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
     * Primary method for rendering this confetti on the canvas.
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
     * confetti on the canvas.
     *
     * @param canvas the canvas to draw on.
     * @param matrix an identity matrix to use for draw manipulations.
     * @param paint the paint to perform canvas draw operations on. This paint has already been
     *              configured via {@link #configurePaint(Paint)}.
     * @param x the x position of the confetti relative to the canvas.
     * @param y the y position of the confetti relative to the canvas.
     * @param rotation the rotation (in degrees) to draw the confetti.
     */
    protected abstract void drawInternal(Canvas canvas, Matrix matrix, Paint paint, float x,
            float y, float rotation);

    /**
     * Helper builder class to construct all of the necessary values used in {@link #prepare()}.
     * The primary purpose of this is to better visualize the values you are setting.
     */
    public static class Configurator {
        private Confetti confetti;

        public Configurator setConfetti(Confetti confetti) {
            this.confetti = confetti;

            // Reset everything to default value
            confetti.initialDelay = 0f;
            confetti.initialX = confetti.initialY = 0f;
            confetti.initialVelocityX = confetti.initialVelocityY = 0f;
            confetti.accelerationX = confetti.accelerationY = 0f;
            confetti.maximumVelocityX = confetti.maximumVelocityY = 0f;
            confetti.initialRotation = 0f;
            confetti.initialRotationalVelocity = confetti.rotationalAcceleration = 0f;
            confetti.maximumRotationalVelocity = 0f;

            return this;
        }

        public Configurator setInitialDelay(float val) {
            confetti.initialDelay = val;
            return this;
        }

        public Configurator setInitialX(float val) {
            confetti.initialX = val;
            return this;
        }

        public Configurator setInitialY(float val) {
            confetti.initialY = val;
            return this;
        }

        public Configurator setInitialVelocityX(float val) {
            confetti.initialVelocityX = val;
            return this;
        }

        public Configurator setInitialVelocityY(float val) {
            confetti.initialVelocityY = val;
            return this;
        }

        public Configurator setAccelerationX(float val) {
            confetti.accelerationX = val;
            return this;
        }

        public Configurator setAccelerationY(float val) {
            confetti.accelerationY = val;
            return this;
        }

        public Configurator setMaximumVelocityX(float val) {
            confetti.maximumVelocityX = val;
            return this;
        }

        public Configurator setMaximumVelocityY(float val) {
            confetti.maximumVelocityY = val;
            return this;
        }

        public Configurator setInitialRotation(float val) {
            confetti.initialRotation = val;
            return this;
        }

        public Configurator setInitialRotationalVelocity(float val) {
            confetti.initialRotationalVelocity = val;
            return this;
        }

        public Configurator setRotationalAcceleration(float val) {
            confetti.rotationalAcceleration = val;
            return this;
        }

        public Configurator setMaximumRotationalVelocity(float val) {
            confetti.maximumRotationalVelocity = val;
            return this;
        }

        public Configurator setTTL(long val) {
            confetti.ttl = val;
            return this;
        }

        public Configurator setFadeOut(boolean fadeOut, Interpolator fadeOutInterpolator) {
            confetti.fadeOut = fadeOut;
            confetti.fadeOutInterpolator = fadeOutInterpolator;
            return this;
        }

        public Confetti configure() {
            confetti.prepare();
            return confetti;
        }
    }
}
