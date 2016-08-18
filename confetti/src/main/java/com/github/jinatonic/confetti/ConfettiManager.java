package com.github.jinatonic.confetti;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
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

    private static Float defaultAccelerationX, defaultAccelerationY;

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

    // Configured attributes for each confetti
    private float velocityX, velocityDeviationX;
    private float velocityY, velocityDeviationY;
    private float accelerationX, accelerationDeviationX;
    private float accelerationY, accelerationDeviationY;
    private float maximumVelocityX, maximumVelocityY;
    private float rotationalVelocity, rotationalVelocityDeviation;
    private float rotationalAcceleration, rotationalAccelerationDeviation;
    private float maximumRotationalVelocity;
    private long ttl;
    private boolean fadeOut;
    private Interpolator fadeOutInterpolator;
    private Rect bound;

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

        if (defaultAccelerationX == null) {
            final Resources res = context.getResources();
            defaultAccelerationX = res.getDimension(
                    R.dimen.default_acceleration_x_dp_per_second) / 1000f;
            defaultAccelerationY = res.getDimension(
                    R.dimen.default_acceleration_y_dp_per_second) / 1000f;
        }

        // Set the defaults
        this.accelerationX = defaultAccelerationX;
        this.accelerationY = defaultAccelerationY;
        this.maximumVelocityX = this.maximumVelocityY = this.maximumRotationalVelocity =
                Float.MAX_VALUE;
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
     *                         {@link #INFINITE_DURATION} for a never-ending emission.
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
     * Set the velocityX used by this manager. This value defines the initial X velocity
     * for the confetti generated by this manager. The actual confetti's X velocity will be
     * (velocityX +- [0, velocityDeviationX]).
     *
     * @param velocityX the velocityX that this manager will use to generate confetti in pixels
     *                  per second.
     * @param velocityDeviationX the deviation from velocityX in pixels per second that will be
     *                           used when randomly generating velocityX for each confetti.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setVelocityX(float velocityX, float velocityDeviationX) {
        this.velocityX = velocityX / 1000f;
        this.velocityDeviationX = velocityDeviationX / 1000f;
        return this;
    }

    /**
     * Set the velocityY used by this manager. This value defines the initial Y velocity
     * for the confetti generated by this manager. The actual confetti's Y velocity will be
     * (velocityY +- [0, velocityDeviationY]).
     *
     * @param velocityY the velocityY that this manager will use to generate confetti in pixels
     *                  per second.
     * @param velocityDeviationY the deviation from velocityY in pixels per second that will be
     *                           used when randomly generating velocityY for each confetti.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setVelocityY(float velocityY, float velocityDeviationY) {
        this.velocityY = velocityY / 1000f;
        this.velocityDeviationY = velocityDeviationY / 1000f;
        return this;
    }

    /**
     * Set the accelerationX used by this manager. This value defines the X acceleration
     * for the confetti generated by this manager. The actual confetti's X acceleration will be
     * (accelerationX +- [0, accelerationDeviationX]).
     *
     * @param accelerationX the accelerationX that this manager will use to generate confetti
     *                      in pixels per second^2.
     * @param accelerationDeviationX the deviation from accelerationX in pixels per second^2
     *                               that will be used when randomly generating accelerationX
     *                               for each confetti.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setAccelerationX(float accelerationX, float accelerationDeviationX) {
        this.accelerationX = accelerationX / 1000f;
        this.accelerationDeviationX = accelerationDeviationX / 1000f;
        return this;
    }

    /**
     * Set the accelerationY used by this manager. This value defines the Y acceleration
     * for the confetti generated by this manager. The actual confetti's Y acceleration will be
     * (accelerationY +- [0, accelerationDeviationY]).
     *
     * @param accelerationY the accelerationY that this manager will use to generate confetti
     *                      in pixels per second^2.
     * @param accelerationDeviationY the deviation from accelerationY in pixels per second^2
     *                               that will be used when randomly generating accelerationY for
     *                               each confetti.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setAccelerationY(float accelerationY, float accelerationDeviationY) {
        this.accelerationY = accelerationY / 1000f;
        this.accelerationDeviationY = accelerationDeviationY / 1000f;
        return this;
    }

    /**
     * Set the maximum X velocity that confetti can reach during the animation.
     *
     * @param maximumVelocityX the maximum X velocity in pixels per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setMaximumVelocityX(float maximumVelocityX) {
        this.maximumVelocityX = maximumVelocityX / 1000f;
        return this;
    }

    /**
     * Set the maximum Y velocity that confetti can reach during the animation.
     *
     * @param maximumVelocityY the maximum Y velocity in pixels per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setMaximumVelocityY(float maximumVelocityY) {
        this.maximumVelocityY = maximumVelocityY / 1000f;
        return this;
    }

    /**
     * Set the rotational velocity used by this manager. This value defines the the initial
     * velocity of the rotation for the confetti generated by this manager. The actual confetti's
     * rotational velocity will be (rotationalVelocity +- [0, rotationalVelocityDeviation]).
     *
     * @param rotationalVelocity the rotationalVelocity that this manager will use in degrees
     *                           per second.
     * @param rotationalVelocityDeviation the deviation from rotationalVelocity in degrees per
     *                                    second that will be used when randomly generating
     *                                    rotationalVelocity for each confetti.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setRotationalVelocity(float rotationalVelocity,
            float rotationalVelocityDeviation) {
        this.rotationalVelocity = rotationalVelocity / 1000f;
        this.rotationalVelocityDeviation = rotationalVelocityDeviation / 1000f;
        return this;
    }

    /**
     * Set the rotational acceleration used by this manager. This value defines the the
     * acceleration of the rotation for the confetti generated by this manager. The actual
     * confetti's rotational acceleration will be
     * (rotationalAcceleration +- [0, rotationalAccelerationDeviation]).
     *
     * @param rotationalAcceleration the rotationalAcceleration that this manager will use in
     *                               degrees per second^2.
     * @param rotationalAccelerationDeviation the deviation from rotationalAcceleration in degrees
     *                                        per second^2 that will be used when randomly
     *                                        generating rotationalAcceleration for each confetti.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setRotationalAcceleration(float rotationalAcceleration,
            float rotationalAccelerationDeviation) {
        this.rotationalAcceleration = rotationalAcceleration / 1000f;
        this.rotationalAccelerationDeviation = rotationalAccelerationDeviation / 1000f;
        return this;
    }

    /**
     * Set the maximum rotational velocity that confetti can reach during the animation.
     *
     * @param maximumRotationalVelocity the maximum rotational velocity in degrees per second.
     * @return the confetti manager so that the set calls can be chained.
     */
    public ConfettiManager setMaximumRotationalVelocity(float maximumRotationalVelocity) {
        this.maximumRotationalVelocity = maximumRotationalVelocity / 1000f;
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
     *                            0 means time 0 or position 0, and 1 means time TTL or position
     *                            right at the respective bound.
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
        if (animator != null) {
            animator.cancel();
        }

        attachConfettiViewToParent();

        // TODO: what happens if we don't clear out previous confetti? o.O
        confetti.clear();
        for (int i = 0; i < numInitialCount; i++) {
            addNewConfetti(0);
        }

        // Never-ending animator, we will cancel once the termination condition is reached.
        animator = ValueAnimator.ofInt(0)
                .setDuration(Long.MAX_VALUE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final long elapsedTime = valueAnimator.getCurrentPlayTime();
                processNewEmission(elapsedTime);

                final Iterator<Confetto> iterator = confetti.iterator();
                while (iterator.hasNext()) {
                    final Confetto confetti = iterator.next();
                    final boolean terminated = confetti.applyUpdate(elapsedTime, bound);
                    if (terminated) {
                        iterator.remove();
                        recycledConfetti.add(confetti);
                    }
                }

                if (confetti.size() == 0 && elapsedTime >= emissionDuration) {
                    terminate();
                } else {
                    confettiView.invalidate();
                }

            }
        });

        animator.start();
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
    }

    private void addNewConfetti(long initialDelay) {
        Confetto confetti = recycledConfetti.poll();
        if (confetti == null) {
            confetti = confettoGenerator.generateConfetto(random);
        }
        configureConfetto(configurator, confetti, confettiSource, random, initialDelay);
        this.confetti.add(confetti);
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
                    addNewConfetti(elapsedTime);
                }
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
                .setMaximumVelocityX(maximumVelocityX)
                .setMaximumVelocityY(maximumVelocityY)
                .setInitialRotation(random.nextInt(360))
                .setInitialRotationalVelocity(getVarianceAmount(rotationalVelocity,
                        rotationalVelocityDeviation, random))
                .setRotationalAcceleration(getVarianceAmount(rotationalAcceleration,
                        rotationalAccelerationDeviation, random))
                .setMaximumRotationalVelocity(maximumRotationalVelocity)
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
