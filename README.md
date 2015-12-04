# APCS graphics

A lightweight open-source graphics library originally designed for high school students taking AP Computer Science. It makes Java game development accessible to students who are programming for the first time, and is actively maintained by [Techlab Education](https://techlab.education/java). There is a [getting started guide](https://techlab.education/java/start) to help you get Java, Eclipse (a popular IDE), and APCS graphics set up on your computer. You can also [read the curriculum](https://techlab.education/java) that we use in teaching with APCS graphics.

## Philosophy

The APCS library is designed to make it easy for anyone to use code to draw and animate. No object-oriented programming knowledge is required to get started - all graphics functionality is abstracted to static methods. There are no mysterious classes to extend or rigid class hierarchies to conform to. You can quickly visualize a [simulation of a bouncing ball](https://techlab.education/java/physics) or recreate the viral game [Flappy Bird](https://techlab.education/java/flappy-bird). You can bridge the gap between procedural programming and object-oriented programming by [simulating bouncing balls](https://techlab.education/java/bouncing-balls) or [catching falling objects](https://techlab.education/java/catching).

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
Window.out.color( colorName )
Window.out.color( red, green, blue )
```

There are 500 colors built-in to the window library. To add new colors, use `Window.addColor`.

```java
Window.addColor( colorName, red, green, blue )
```

### Draw a circle

`Window.out.circle` draws a circle centered at the position (`x`, `y`) with the given `radius`.

```java
Window.out.circle(x, y, radius);
```

### Draw a square

`Window.out.square` draws a square centered at the position (`x`, `y`) with the given side `length`.

```java
Window.out.square(x, y, length)
```

### Draw a rectangle

`Window.out.rectangle` draws a rectangle centered at the position (`x`, `y`) with the given `width` and `height`.

```java
Window.out.rectangle(x, y, width, height)
```

### Draw an oval

`Window.out.oval` draws an oval centered at the position (`x`, `y`) with the given `width` and `height`.

```java
Window.out.oval(x, y, width, height);
```

### Background

```java
Window.out.background("black")
```
