![](https://github.com/jinatonic/confetti/blob/master/assets/confetti_with_touch.gif)
![](https://github.com/jinatonic/confetti/blob/master/assets/falling_confetti_top.gif)
![](https://github.com/jinatonic/confetti/blob/master/assets/explosion_confetti.gif)
![](https://github.com/jinatonic/confetti/blob/master/assets/falling_confetti_point.gif)

What is Confetti?
=================

*Confetti (plural) vs confetto (singular)*

[Confetti](https://en.wikipedia.org/wiki/Confetti) is a high-performance, easily-configurable
particle system library that can animate any set of objects through space. You can specify your
starting conditions and physical conditions (e.g. X and Y acceleration, boundaries, etc.),
and let the confetti library take care of the rest.


Getting started
---------------

Add the confetti dependency to your `build.gradle`.

```groovy
implementation 'com.github.jinatonic.confetti:confetti:1.1.2'
```


Simple usage
------------

The only thing you need to get confetti on your screen is a parent view to host the `ConfettiView`
and thus the confetti animation. From this point on, this parent view is referred to as `container`.

Please note that the library uses measurements from `container` to figure out how to best animate
the confetti. If the `container` is not measured when creating the confetti, then nothing will
show up on the screen. A common pitfall is creating confetti inside activity lifecycle as the views
are most likely not measured at those points.

You can generate pre-configured confetti from `CommonConfetti`. You only need to provide it with
the parent `container`, a `ConfettiSource`, and an array of possible colors for the confetti.
The default confetti shapes are circle, triangle, and square.

```java
CommonConfetti.rainingConfetti(container, new int[] { Color.BLACK })
        .infinite();
```


More custom usage
-----------------

First, we need to define what our individual [confetto](http://www.dictionary.com/browse/confetto)
is through the `ConfettoGenerator`. Each call of `generateConfetto` must generate a brand new
`Confetto` object (the `ConfettiManager` will recycle the generated confetto as needed so you
might see fewer and fewer calls to `generateConfetto` as the animation goes on). We pass in a
`Random` into `generateConfetto` in case you want to randomly generate a confetto from a list
of possible confetti.

A simple `ConfettoGenerator` might look like this:

```java
final List<Bitmap> allPossibleConfetti = constructBitmapsForConfetti();
// Alternatively, we provide some helper methods inside `Utils` to generate square, circle,
// and triangle bitmaps.
// Utils.generateConfettiBitmaps(new int[] { Color.BLACK }, 20 /* size */);

final int numConfetti = allPossibleConfetti.size();
final ConfettoGenerator confettoGenerator = new ConfettoGenerator() {
    @Override
    public Confetto generateConfetto(Random random) {
        final Bitmap bitmap = allPossibleConfetti.get(random.nextInt(numConfetti));
        return new BitmapConfetto(bitmap);
    }
}
```

Once we have our `ConfettoGenerator`, we'll need to define a `ConfettiSource` from which confetti
will appear out of. This source can be any arbitrary point or line.

```java
final int containerMiddleX = container.getWidth() / 2;
final int containerMiddleY = container.getHeight() / 2;
final ConfettiSource confettiSource = new ConfettiSource(containerMiddleX, containerMiddleY);
```

Now you are ready! construct your `ConfettiManager`, configure the animation to your liking, and
then call `animate()`!

```java
new ConfettiManager(context, confettoGenerator, confettiSource, container)
        .setEmissionDuration(1000)
        .setEmissionRate(100)
        .setVelocityX(20, 10)
        .setVelocityY(100)
        .setRotationalVelocity(180, 180)
        .animate();
```

The `animate()` call will create and configure the various `Confetto` objects on demand,
create a new `ConfettiView`, initialize the proper states for all of the components, attach the
view to the `container` and start the animation. The `ConfettiView` will auto-detach itself once
all of the confetti have terminated and are off the screen.

For more sample usage of the library, please check out the
[confetti-sample](https://github.com/jinatonic/confetti/tree/master/confetti-sample) app that's
included in this project.


Configuration
-------------

The `ConfettiManager` is easily configurable. For simplicity's sake, all of the velocity and
acceleration attributes are in pixels per second or pixels per second^2, whereas all of the raw
time attributes (such as `ttl` and `emissionDuration`) are in milliseconds.

You will notice that most of the setters for the physical attributes (e.g. velocity, acceleration,
rotation) can take in either one argument for the actual value or two arguments. The second
argument allows you to specify a random deviation if you want to randomize the behavior among
all of the generated confetto.

For example:

```java
confettiManager.setVelocityX(200f, 50f);
```

The generated confetto will have an initial X velocity of anywhere between `200 - 50` or `150` and
`200 + 50` or `250`, eventually distributed.

`enableFadeOut(Interpolator fadeOutInterpolator)` is another interesting method. You can specify
that fade out occurs as a confetto nears its boundary (either reaching the physical boundary
specified in `bound` (this is either the entirety of `container` or set in `setBound`) or reaching
`ttl`). The interpolator essentially takes in a value between 0 and 1 (0 means that the confetto
is at its source, 1 means the confetto is at its bound) and outputs an alpha value between 0 and 1
(0 is transparent and 1 is opaque). This way, we allow you to have the full power of specifying
how the fade out occurs.

Or, if you are lazy, you can just use `Utils.getDefaultAlphaInterpolator()`.


Advanced usage
==============

Enable touch and drag
---------------------

If you call `confettiManager.setTouchEnabled(true)`, you can allow the user to touch and drag
the confetti that are on the screen. When the user let go of the confetti, the confetti will
start at that location with the user initiated velocity and the pre-configured acceleration
and resume animation from there.


Custom Confetto
---------------

It's very easy to define a custom `Confetto` (see `BitmapConfetto`). You simply need to extend
from the `Confetto` class and implement `drawInternal`. The function will provide you with a
`Canvas` to draw on as well as a work `Matrix` and `Paint` so you don't have to allocate objects
in the draw path. You then need to essentially draw your confetto however you want onto the canvas
using the specified `x`, `y`, and `rotation`.

The cool part is that you can interpret `rotation` however you want. Instead of an angle in degrees,
you can choose to interpret rotation where each degree corresponds to a new shape or new image.
This way, you can achieve some cool animation effects as the confetti flow through the screen.


Changing confetti configuration mid-animation
---------------------------------------------

If you have a handle on the `ConfettiManager`, you can actually very easily change the configuration
mid-animation for more unique experiences. For example:

```java
confettiManager.setEmissionRate(100)
        .animate();

new Handler().postDelayed(new Runnable() {
    @Override public void run() {
        confettiManager.setEmissionRate(20);
    }
}, 3000);
```

The above snippet will configure the initial emission rate to be 100 confetti per second and start
the animation. After 3 seconds, it will reduce the emission rate to 20 confetti per second. This 
applies to all attributes (e.g. changing velocity or acceleration based on some outside condition).


Future development
==================

* Add more samples and pre-configured confetti into `CommonConfetti`.


License
=======

    Copyright 2016 Robinhood Markets, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
