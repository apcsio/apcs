# APCS graphics

A lightweight graphics library originally designed for high school students taking AP Computer Science. It makes Java game development significantly easier, and is actively maintained by [Techlab Education](https://techlab.education/java). There is a [getting started guide](https://techlab.education/java/start) to help you get Java, Eclipse (a popular IDE), and APCS graphics set up on your computer. 

## Creating a window

The `Window.size` method creates a window of the given height.

```java
Window.size(width, height)
```

The `Window.size` method is optional - performing any other drawing command will automatically create a window of the default size (500 x 500).

## Drawing

The APCS library has methods for drawing common shapes like circles, rectangles, ovals, squares, lines, polygons, and text. These methods are organized under the `Window.out` class.

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

`Window.out.square` draws a square centered at the position (`x`, `y`) with the given `side` length.

### Draw a rectangle

`Window.out.rectangle` draws a rectangle centered at the position (`x`, `y`) with the given `width` and `height`.

```java
Window.out.rectangle(x, y, width, height)
```

### Draw an oval

Draws an oval centered at the position (`x`, `y`) with the given width and height.

```java
Window.out.oval(x, y, width, height);
```

### Background

```java
Window.out.background("black")
```
