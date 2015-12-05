# APCS graphics

A lightweight open-source graphics library created by a [Java instructor](http://keshav.is/building/apcs) for high school students taking AP Computer Science. It makes Java game development accessible to students who are programming for the first time, and is actively maintained by [Techlab Education](https://techlab.education). There is a [getting started guide](https://techlab.education/java/start) to help you get Java, Eclipse (a popular IDE), and APCS graphics set up on your computer. You can also [read the curriculum](https://techlab.education/java) that we use for teaching with APCS graphics.

We would love to hear how you are using APCS graphics. Email [operations@techlabeducation.com](mailto:operations@techlabeducation.com) to start a conversation!

## Philosophy

All graphics functionality is abstracted to static methods, so there are no mysterious classes to extend or rigid class hierarchies to conform to. You can quickly visualize a [simulation of a bouncing ball](https://techlab.education/java/physics), recreate the viral game [Flappy Bird](https://techlab.education/java/flappy-bird), or build a multiplayer [agar.io clone](https://techlab.education/blog/agar).

![](https://raw.githubusercontent.com/apcsio/apcsio.github.io/master/image/physics.gif)

```java
// A bouncing ball simulation
int x = 50, y = 50, dy = 0;

while (true) {
    Window.out.background("white");

    // Draw the ball.
    Window.out.color("red");
    Window.out.circle(x, y, 20);

    // Move the ball.
    x = x + 5;
    y = y + dy;
    dy = dy + 1;

    // Bounce the ball if it hits the ground.
    if (y > 480) {
        y = 480;
        dy = dy * -9 / 10;
    }

    // Complete a frame of the animation.
    Window.frame();
}

```

## Creating a window

The `Window.size` method sets the dimensions of the window.

```java
Window.size(width, height)
```

Calling `Window.size` is optional - performing any other drawing command will automatically create a window of the default size (500 x 500).

## Drawing

The APCS library has methods for drawing common shapes like circles, rectangles, ovals, squares, lines, polygons, and text. These methods are organized under the `Window.out` class.

> Calling any `Window.out` method will open the output window.

### Set the color

The color of the shape being drawn is determined by the window's current drawing color, which is initially white. To change the drawing color, use the `Window.out.color` method. You can either use a `String` color (e.g. `Window.out.color("red")`) or an RGB value to specify the color.

```java
Window.out.color(colorName)
Window.out.color(red, green, blue)
```

The 500 most popular colors are included in the window library - to add additional colors, use `Window.addColor`.

```java
Window.addColor(colorName, red, green, blue)
```

![](https://raw.githubusercontent.com/apcsio/apcsio.github.io/master/image/colors.gif)

> In the [control flow lab](https://techlab.education/java/control-flow), students use mouse clicks and bounding boxes to select the color in an interactive drawing application.

### Draw a circle

`Window.out.circle` draws a circle centered at the position (`x`, `y`) with the given `radius`.

```java
Window.out.circle(x, y, radius)
```

![](https://raw.githubusercontent.com/apcsio/apcsio.github.io/master/image/multi-ball.gif)

```java
// A class to describe a single ball.
public class Ball {
    int x, y, dx, dy;

    public Ball() { ... }
    public void draw() { ... }
    public void move() { ... }
}

// Create a list of Ball objects.
Ball[] list = new Ball[100];
for (int i = 0 ; i < 100 ; i++) {
    list[i] = new Ball();
}

while (true) {
    Window.out.background("white");

    // Draw and move each ball.
    for (Ball b : list) {
        b.draw();
        b.move();
    }

    Window.frame();
}
```

### Draw a square

`Window.out.square` draws a square centered at the position (`x`, `y`) with the given side `length`.

```java
Window.out.square(x, y, length)
```

![](https://raw.githubusercontent.com/apcsio/apcsio.github.io/master/image/gameoflife.gif)

> Conway's game of life is a simulation devised by the mathematician John Horton Conway, where a universe of cells lives and dies according to a simple set of rules:

> **The Underpopulation Rule**: A living cell with fewer than two live neighbors dies.

> **The Survival Rule**: A living cell with two or three live neighbors survives to the next generation.

> **The Overpopulation Rule**: A living cell with more than three neighbors dies.

> **The Reproduction Rule**: A dead cell with exactly 3 neighbors comes to life in the next generation.

> - [Conway's Game of Life lab](https://techlab.education/lab/game-of-life)

### Draw a rectangle

`Window.out.rectangle` draws a rectangle centered at the position (`x`, `y`) with the given `width` and `height`.

```java
Window.out.rectangle(x, y, width, height)
```

![](https://raw.githubusercontent.com/apcsio/apcsio.github.io/master/image/flappybird.gif)

> One of the first challenges in the [Flappy Bird lab](https://techlab.education/java/flappy-bird) is calculating the positions and sizes of the pipe rectangles.

### Draw an oval

`Window.out.oval` draws an oval centered at the position (`x`, `y`) with the given `width` and `height`.

```java
Window.out.oval(x, y, width, height)
```

### Draw a line

`Window.out.line` draws a line between the starting position (`x1`, `y1`) and the ending position (`x2`, `y2`).

```java
Window.out.line(x1, y1, x2, y2)
```

### Draw an image

`Window.out.image` draws an image with the given (`x`, `y`) coordinate at the top left corner.

### Background

Sets the background of the window to the color either specified by its string name or its RGB value.

> Setting the background to a given color will change the current drawing color to that color.

```java
Window.out.background(colorName)
Window.out.background(red, green, blue)
```

![](https://raw.githubusercontent.com/apcsio/apcsio.github.io/master/image/single-ball.gif)

```java
int x = 250, y = 250,
    dx = 4, dy = 3;

while (true) {
    // Draw the background first.
    Window.out.background("white");

    // Change the color to the ball's color, then draw it.
    Window.out.color("red");
    Window.out.circle(x, y, 10);

    // Move the ball.
    x = x + dx;
    y = y + dy;

    // Check for collisions.
    if (x > 490) { ... }
    if (x < 10) { ... }
    if (y > 490) { ... }
    if (y < 10) { ... }

    // Draw one frame.
    Window.frame();
}

```

## Animating

Animations are created by showing still images called **frames** in rapid succession. The easiest way to create animations in the window is to endlessly loop a block of instructions that draws a single frame. Since the instructions are usually performed relatively fast by the computer, we need to insert a small pause between each repetition to give our brains enough time to process the frame.

The easiest way to insert that pause is with the `Window.frame` method, which can optionally take in the number of milliseconds to wait for. The default delay between frames is `33` milliseconds, to achieve a **frame rate** of around 30 frames per second (**fps**).

```java
Window.frame()
Window.frame(milliseconds)
```

### Waiting

You can insert a manual delay into the program with `Window.sleep` which takes an integer number of `milliseconds` to wait for.

```java
Window.sleep( milliseconds )
```

### Framing

The `Window.frame` method starts **double buffering** the window. After the first call to `Window.frame`, the visual state of the window is frozen, and all subsequent drawing commands are performed on a background window that is not shown. Once the `Window.frame` method is called again, the background window's contents are copied and frozen on the visible window, and all subsequent drawings are again done on the background. This process eliminates flicker by ensuring a frame is fully rendered before displaying it on the screen, and adjusts the delay time between frames by the time taken to render the frame.

```java
// Setup...
while (true) {
  // Draw one frame...
  Window.frame();
}
```

### Frame rate

You can set the **frame rate** (the number of frames displayed each second) with the `Window.setFrameRate` method. Setting the frame rate will automatically calculate the number of milliseconds to wait when `Window.frame` is called with no arguments.

```java
Window.setFrameRate(rate)
```

## Mouse input

The methods for getting input from the mouse are organized under the `Window.mouse` class.

### Mouse position

`Window.mouse.getX` and `Window.mouse.getY` return the x and y position of the mouse, respectively.

```java
Window.mouse.getX()
Window.mouse.getY()
```

### Mouse click

`Window.mouse.clicked` returns `true` if the mouse is clicked, and `false` otherwise.

```java
Window.mouse.clicked()
```

## Keyboard input

The methods for getting keyboard input are organized under the `Window.key` class.

### Key pressed

```java
Window.key.pressed()
Window.key.pressed( char )
Window.key.pressed( string )
```

### Key released

```java
Window.key.released()
Window.key.released( char )
Window.key.released( string )
```

### Key typed

```java
Window.key.typed( char )
Window.key.typed( string )
```
