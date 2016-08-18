package com.github.jinatonic.confetti;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Interpolator;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * A helper manager class for configuring a set of confetti and displaying them on the UI.
 */
public class ConfettiManager {
    public static final long INFINITE_DURATION = Long.MAX_VALUE;

    private final Confetto.Configurator configurator = new Confetto.Configurator();
    private final Random random = new Random();
    private final ConfettoGenerator confettoGenerator;
    private final ConfettiSource confettiSource;
    private final ViewGroup parentView;
    private final ConfettiView confettiView;

    private final Queue<Confetto> recycledConfetti = new LinkedList<>();
    private final List<Confetto> confetti = new ArrayList<>(300);
    private ValueAnimator animator;
    private long lastEmittedTimestamp;

    // All of the below configured values are in milliseconds despite the setter methods take them
    // in seconds as the parameters. The parameters for the setters are in seconds to allow for
    // users to better understand/visualize the dimensions.

    // Configured attributes for the entire confetti group
    private int numInitialCount;
    private long emissionDuration;
    private float emissionRate, emissionRateInverse;
    private boolean fadeOut;
    private Interpolator fadeOutInterpolator;
    private Rect bound;

    // Configured attributes for each confetto
    private float velocityX, velocityDeviationX;
    private float velocityY, velocityDeviationY;
    private float accelerationX, accelerationDeviationX;
    private float accelerationY, accelerationDeviationY;
    private Float targetVelocityX, targetVelocityXDeviation;
    private Float targetVelocityY, targetVelocityYDeviation;
    private int initialRotation, initialRotationDeviation;
    private float rotationalVelocity, rotationalVelocityDeviation;
    private float rotationalAcceleration, rotationalAccelerationDeviation;
    private Float targetRotationalVelocity, targetRotationalVelocityDeviation;
    private long ttl;

    public interface ConfettoGenerator {
        /**
         * Generate a random confetto to animate.
         *
         * @param random a {@link Random} that can be used to generate random confetto.
         * @return the randomly generated confetto.
         */
        Confetto generateConfetto(Random random);
    }

    public ConfettiManager(Context context, ConfettoGenerator confettoGenerator,
            ConfettiSource confettiSource, ViewGroup parentView) {
        this(context, confettoGenerator, confettiSource, parentView,
                ConfettiView.newInstance(context));
    }

    public ConfettiManager(Context context, ConfettoGenerator confettoGenerator,
            ConfettiSource confettiSource, ViewGroup parentView, ConfettiView confettiView) {
        this.confettoGenerator = confettoGenerator;
        this.confettiSource = confettiSource;
        this.parentView = parentView;
        this.confettiView = confettiView;
        this.confettiView.bind(confetti);

        // Set the defaults
        this.ttl = -1;
        this.bound = new Rect(0, 0, parentView.getWidth(), parentView.getHeight());
    }

    /**
     * The number of confetti initially emitted before any time has elapsed.
     *
     * @param numInitialCount the number of initial confetti.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setNumInitialCount(int numInitialCount) {
        this.numInitialCount = numInitialCount;
        return this;
    }

    /**
     * Configures how long this manager will emit new confetti after the animation starts.
     *
     * @param emissionDuration how long to emit new confetti in millis. This value can be
     *   {@link #INFINITE_DURATION} for a never-ending emission.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setEmissionDuration(long emissionDuration) {
        this.emissionDuration = emissionDuration;
        return this;
    }

    /**
     * Configures how frequently this manager will emit new confetti after the animation starts
     * if {@link #emissionDuration} is a positive value.
     *
     * @param emissionRate the rate of emission in # of confetti per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setEmissionRate(float emissionRate) {
        this.emissionRate = emissionRate / 1000f;
        this.emissionRateInverse = 1f / this.emissionRate;
        return this;
    }

    /**
     * @see {@link #setVelocityX(float, float)} but with no deviation.
     */
    public ConfettiManager setVelocityX(float velocityX) {
        return setVelocityX(velocityX, 0f);
    }

    /**
     * Set the velocityX used by this manager. This value defines the initial X velocity
     * for the generated confetti. The actual confetti's X velocity will be
     * (velocityX +- [0, velocityDeviationX]).
     *
     * @param velocityX the X velocity in pixels per second.
     * @param velocityDeviationX the deviation from X velocity in pixels per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setVelocityX(float velocityX, float velocityDeviationX) {
        this.velocityX = velocityX / 1000f;
        this.velocityDeviationX = velocityDeviationX / 1000f;
        return this;
    }

    /**
     * @see {@link #setVelocityY(float, float)} but with no deviation.
     */
    public ConfettiManager setVelocityY(float velocityY) {
        return setVelocityY(velocityY, 0f);
    }

    /**
     * Set the velocityY used by this manager. This value defines the initial Y velocity
     * for the generated confetti. The actual confetti's Y velocity will be
     * (velocityY +- [0, velocityDeviationY]).
     *
     * @param velocityY the Y velocity in pixels per second.
     * @param velocityDeviationY the deviation from Y velocity in pixels per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setVelocityY(float velocityY, float velocityDeviationY) {
        this.velocityY = velocityY / 1000f;
        this.velocityDeviationY = velocityDeviationY / 1000f;
        return this;
    }

    /**
     * @see {@link #setAccelerationX(float, float)} but with no deviation.
     */
    public ConfettiManager setAccelerationX(float accelerationX) {
        return setAccelerationX(accelerationX, 0f);
    }

    /**
     * Set the accelerationX used by this manager. This value defines the X acceleration
     * for the generated confetti. The actual confetti's X acceleration will be
     * (accelerationX +- [0, accelerationDeviationX]).
     *
     * @param accelerationX the X acceleration in pixels per second^2.
     * @param accelerationDeviationX the deviation from X acceleration in pixels per second^2.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setAccelerationX(float accelerationX, float accelerationDeviationX) {
        this.accelerationX = accelerationX / 1000000f;
        this.accelerationDeviationX = accelerationDeviationX / 1000000f;
        return this;
    }

    /**
     * @see {@link #setAccelerationY(float, float)} but with no deviation.
     */
    public ConfettiManager setAccelerationY(float accelerationY) {
        return setAccelerationY(accelerationY, 0f);
    }

    /**
     * Set the accelerationY used by this manager. This value defines the Y acceleration
     * for the generated confetti. The actual confetti's Y acceleration will be
     * (accelerationY +- [0, accelerationDeviationY]).
     *
     * @param accelerationY the Y acceleration in pixels per second^2.
     * @param accelerationDeviationY the deviation from Y acceleration in pixels per second^2.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setAccelerationY(float accelerationY, float accelerationDeviationY) {
        this.accelerationY = accelerationY / 1000000f;
        this.accelerationDeviationY = accelerationDeviationY / 1000000f;
        return this;
    }

    /**
     * @see {@link #setTargetVelocityX(float, float)} but with no deviation.
     */
    public ConfettiManager setTargetVelocityX(float targetVelocityX) {
        return setTargetVelocityX(targetVelocityX, 0f);
    }

    /**
     * Set the target X velocity that confetti can reach during the animation. The actual confetti's
     * target X velocity will be (targetVelocityX +- [0, targetVelocityXDeviation]).
     *
     * @param targetVelocityX the target X velocity in pixels per second.
     * @param targetVelocityXDeviation  the deviation from target X velocity in pixels per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setTargetVelocityX(float targetVelocityX,
            float targetVelocityXDeviation) {
        this.targetVelocityX = targetVelocityX / 1000f;
        this.targetVelocityXDeviation = targetVelocityXDeviation / 1000f;
        return this;
    }

    /**
     * @see {@link #setTargetVelocityY(float, float)} but with no deviation.
     */
    public ConfettiManager setTargetVelocityY(float targetVelocityY) {
        return setTargetVelocityY(targetVelocityY, 0f);
    }

    /**
     * Set the target Y velocity that confetti can reach during the animation. The actual confetti's
     * target Y velocity will be (targetVelocityY +- [0, targetVelocityYDeviation]).
     *
     * @param targetVelocityY the target Y velocity in pixels per second.
     * @param targetVelocityYDeviation  the deviation from target Y velocity in pixels per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setTargetVelocityY(float targetVelocityY,
            float targetVelocityYDeviation) {
        this.targetVelocityY = targetVelocityY / 1000f;
        this.targetVelocityYDeviation = targetVelocityYDeviation / 1000f;
        return this;
    }

    /**
     * @see {@link #setInitialRotation(int, int)} but with no deviation.
     */
    public ConfettiManager setInitialRotation(int initialRotation) {
        return setInitialRotation(initialRotation, 0);
    }

    /**
     * Set the initialRotation used by this manager. This value defines the initial rotation in
     * degrees for the generated confetti. The actual confetti's initial rotation will be
     * (initialRotation +- [0, initialRotationDeviation]).
     *
     * @param initialRotation the initial rotation in degrees.
     * @param initialRotationDeviation the deviation from initial rotation in degrees.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setInitialRotation(int initialRotation, int initialRotationDeviation) {
        this.initialRotation = initialRotation;
        this.initialRotationDeviation = initialRotationDeviation;
        return this;
    }

    /**
     * @see {@link #setRotationalVelocity(float, float)} but with no deviation.
     */
    public ConfettiManager setRotationalVelocity(float rotationalVelocity) {
        return setRotationalVelocity(rotationalVelocity, 0f);
    }

    /**
     * Set the rotationalVelocity used by this manager. This value defines the the initial
     * rotational velocity for the generated confetti. The actual confetti's initial
     * rotational velocity will be (rotationalVelocity +- [0, rotationalVelocityDeviation]).
     *
     * @param rotationalVelocity the initial rotational velocity in degrees per second.
     * @param rotationalVelocityDeviation the deviation from initial rotational velocity in
     *   degrees per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setRotationalVelocity(float rotationalVelocity,
            float rotationalVelocityDeviation) {
        this.rotationalVelocity = rotationalVelocity / 1000f;
        this.rotationalVelocityDeviation = rotationalVelocityDeviation / 1000f;
        return this;
    }

    /**
     * @see {@link #setRotationalAcceleration(float, float)} but with no deviation.
     */
    public ConfettiManager setRotationalAcceleration(float rotationalAcceleration) {
        return setRotationalAcceleration(rotationalAcceleration, 0f);
    }

    /**
     * Set the rotationalAcceleration used by this manager. This value defines the the
     * acceleration of the rotation for the generated confetti. The actual confetti's rotational
     * acceleration will be (rotationalAcceleration +- [0, rotationalAccelerationDeviation]).
     *
     * @param rotationalAcceleration the rotational acceleration in degrees per second^2.
     * @param rotationalAccelerationDeviation the deviation from rotational acceleration in degrees
     *   per second^2.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setRotationalAcceleration(float rotationalAcceleration,
            float rotationalAccelerationDeviation) {
        this.rotationalAcceleration = rotationalAcceleration / 1000000f;
        this.rotationalAccelerationDeviation = rotationalAccelerationDeviation / 1000000f;
        return this;
    }

    /**
     * @see {@link #setTargetRotationalVelocity(float, float)} but with no deviation.
     */
    public ConfettiManager setTargetRotationalVelocity(float targetRotationalVelocity) {
        return setTargetRotationalVelocity(targetRotationalVelocity, 0f);
    }

    /**
     * Set the target rotational velocity that confetti can reach during the animation. The actual
     * confetti's target rotational velocity will be
     * (targetRotationalVelocity +- [0, targetRotationalVelocityDeviation]).
     *
     * @param targetRotationalVelocity the target rotational velocity in degrees per second.
     * @param targetRotationalVelocityDeviation the deviation from target rotational velocity
     *   in degrees per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setTargetRotationalVelocity(float targetRotationalVelocity,
            float targetRotationalVelocityDeviation) {
        this.targetRotationalVelocity = targetRotationalVelocity / 1000f;
        this.targetRotationalVelocityDeviation = targetRotationalVelocityDeviation / 1000f;
        return this;
    }

    /**
     * Specifies a custom bound that the confetti will clip to. By default, the confetti will be
     * able to animate throughout the entire screen. The dimensions specified in {@param bound} is
     * global dimensions, e.g. x=0 is the top of the screen, rather than relative dimensions.
     *
     * @param bound the bound that clips the confetti as they animate.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setBound(Rect bound) {
        this.bound = bound;
        return this;
    }

    /**
     * Specifies a custom time to live for the confetti generated by this manager. When a confetti
     * reaches its time to live timer, it will disappear and terminate its animation.
     *
     * <p>The time to live value does not include the initial delay of the confetti.
     *
     * @param ttl the custom time to live in milliseconds.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setTTL(long ttl) {
        this.ttl = ttl;
        return this;
    }

    /**
     * Enables fade out for all of the confetti generated by this manager. Fade out means that
     * the confetti will animate alpha according to the {@param fadeOutInterpolator} according
     * to its TTL or, if TTL is not set, its bounds.
     *
     * @param fadeOutInterpolator an interpolator that interpolates [0, 1] into an alpha value.
     *   0 means time 0 or position 0, and 1 means time TTL or position right at the respective
     *   bound.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager enableFadeOut(Interpolator fadeOutInterpolator) {
        this.fadeOut = true;
        this.fadeOutInterpolator = fadeOutInterpolator;
        return this;
    }

    /**
     * Disables fade out for all of the confetti generated by this manager.
     *
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager disableFadeOut() {
        this.fadeOut = false;
        this.fadeOutInterpolator = null;
        return this;
    }

    /**
     * Start the confetti animation configured by this manager.
     */
    public void animate() {
        cleanupExistingAnimation();
        attachConfettiViewToParent();
        addNewConfetti(numInitialCount, 0);
        startNewAnimation();
    }

    /**
     * Terminate the currently running animation if there is any.
     */
    public void terminate() {
        if (animator != null) {
            animator.cancel();
        }
        confettiView.terminate();
    }

    private void cleanupExistingAnimation() {
        if (animator != null) {
            animator.cancel();
        }

        lastEmittedTimestamp = 0;
        final Iterator<Confetto> iterator = confetti.iterator();
        while (iterator.hasNext()) {
            recycledConfetti.add(iterator.next());
            iterator.remove();
        }
    }

    private void attachConfettiViewToParent() {
        final ViewParent currentParent = confettiView.getParent();
        if (currentParent != null) {
            if (currentParent != parentView) {
                ((ViewGroup) currentParent).removeView(confettiView);
                parentView.addView(confettiView);
            }
        } else {
            parentView.addView(confettiView);
        }

        confettiView.reset();
    }

    private void addNewConfetti(int numConfetti, long initialDelay) {
        for (int i = 0; i < numConfetti; i++) {
            Confetto confetto = recycledConfetti.poll();
            if (confetto == null) {
                confetto = confettoGenerator.generateConfetto(random);
            }
            configureConfetto(configurator, confetto, confettiSource, random, initialDelay);
            this.confetti.add(confetto);
        }
    }

    private void startNewAnimation() {
        // Never-ending animator, we will cancel once the termination condition is reached.
        animator = ValueAnimator.ofInt(0)
                .setDuration(Long.MAX_VALUE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final long elapsedTime = valueAnimator.getCurrentPlayTime();
                processNewEmission(elapsedTime);
                updateConfetti(elapsedTime);

                if (confetti.size() == 0 && elapsedTime >= emissionDuration) {
                    terminate();
                } else {
                    confettiView.invalidate();
                }
            }
        });

        animator.start();
    }

    private void processNewEmission(long elapsedTime) {
        if (elapsedTime < emissionDuration) {
            if (lastEmittedTimestamp == 0) {
                lastEmittedTimestamp = elapsedTime;
            } else {
                final long timeSinceLastEmission = elapsedTime - lastEmittedTimestamp;

                // Randomly determine how many confetti to emit
                final int numNewConfetti = (int)
                        (random.nextFloat() * emissionRate * timeSinceLastEmission);
                if (numNewConfetti > 0) {
                    lastEmittedTimestamp += emissionRateInverse * numNewConfetti;
                    addNewConfetti(numNewConfetti, elapsedTime);
                }
            }
        }
    }

    private void updateConfetti(long elapsedTime) {
        final Iterator<Confetto> iterator = confetti.iterator();
        while (iterator.hasNext()) {
            final Confetto confetto = iterator.next();
            final boolean terminated = confetto.applyUpdate(elapsedTime, bound);
            if (terminated) {
                iterator.remove();
                recycledConfetti.add(confetto);
            }
        }
    }

    private void configureConfetto(Confetto.Configurator configurator, Confetto confetto,
            ConfettiSource confettiSource, Random random, long initialDelay) {
        configurator.setConfetto(confetto)
                .setInitialDelay(initialDelay)
                .setInitialX(confettiSource.getInitialX(random.nextFloat()))
                .setInitialY(confettiSource.getInitialY(random.nextFloat()))
                .setInitialVelocityX(getVarianceAmount(velocityX, velocityDeviationX, random))
                .setInitialVelocityY(getVarianceAmount(velocityY, velocityDeviationY, random))
                .setAccelerationX(getVarianceAmount(accelerationX, accelerationDeviationX, random))
                .setAccelerationY(getVarianceAmount(accelerationY, accelerationDeviationY, random))
                .setTargetVelocityX(targetVelocityX == null ? null :
                        getVarianceAmount(targetVelocityX, targetVelocityXDeviation, random))
                .setTargetVelocityY(targetVelocityY == null ? null :
                        getVarianceAmount(targetVelocityY, targetVelocityYDeviation, random))
                .setInitialRotation(
                        getVarianceAmount(initialRotation, initialRotationDeviation, random))
                .setInitialRotationalVelocity(getVarianceAmount(rotationalVelocity,
                        rotationalVelocityDeviation, random))
                .setRotationalAcceleration(getVarianceAmount(rotationalAcceleration,
                        rotationalAccelerationDeviation, random))
                .setTargetRotationalVelocity(targetRotationalVelocity == null ? null :
                        getVarianceAmount(targetRotationalVelocity,
                                targetRotationalVelocityDeviation, random))
                .setTTL(ttl)
                .setFadeOut(fadeOut, fadeOutInterpolator)
                .configure();
    }

    private float getVarianceAmount(float base, float deviation, Random random) {
        // Normalize random to be [-1, 1] rather than [0, 1]
        return base + (deviation * (random.nextFloat() * 2 - 1));
    }

    /**
     * The source from which confetti will appear. This can be either a line or a point.
     *
     * <p>Please note that the specified source represents the top left corner of the drawn
     * confetti. If you want the confetti to appear from off-screen, you'll have to offset it
     * with the confetti's size.
     */
    public static class ConfettiSource {
        public final int x0, y0, x1, y1;

        /**
         * Specifies a point source from which all confetti will emit from.
         *
         * @param x x-coordinate of the point relative to the {@link ConfettiView}'s parent.
         * @param y y-coordinate of the point relative to the {@link ConfettiView}'s parent.
         */
        public ConfettiSource(int x, int y) {
            this(x, y, x, y);
        }

        /**
         * Specifies a line source from which all confetti will emit from.
         *
         * @param x0 x-coordinate of the first point relative to the {@link ConfettiView}'s parent.
         * @param y0 y-coordinate of the first point relative to the {@link ConfettiView}'s parent.
         * @param x1 x-coordinate of the second point relative to the {@link ConfettiView}'s parent.
         * @param y1 y-coordinate of the second point relative to the {@link ConfettiView}'s parent.
         */
        public ConfettiSource(int x0, int y0, int x1, int y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
        }

        protected float getInitialX(float random) {
            return x0 + (x1 - x0) * random;
        }

        protected float getInitialY(float random) {
            return y0 + (y1 - y0) * random;
        }
    }
}
