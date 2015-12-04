# APCS graphics

A lightweight graphics library originally designed for high school students taking AP Computer Science. It makes Java game development significantly easier, and is actively maintained by [Techlab Education](https://techlab.education/java). There is a [getting started guide](https://techlab.education/java/start) to help you get Java, Eclipse (a popular IDE), and APCS graphics set up on your computer. 

## Creating a window

The `Window.size` method creates a window of the given height.

```java
Window.size(width, height)
```

The `Window.size` method is optional - performing any other drawing command will automatically create a window of the default size (500 x 500).

## Drawing

The APCS library has methods for drawing common shapes like circles, rectangles, ovals, and squares.

### Draw a circle

A circle c

```java
Window.out.circle(x, y, radius);
```

### Draw a rectangle

```java
Window.out.rectangle(x, y, width, height)
```
