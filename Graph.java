import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * A simple graphing utility.
 * @author Keshav Saharia
 * 		   keshav@techlabeducation.com
 */
public class Graph {
	// The underlying JFrame
	private JFrame frame;
	
	// The list of points to be drawn.
	private ArrayList <Point> points;
	
	// Size
	private int width = 800;
	private int height = 600;
	
	private boolean autoscale = false;
	private Point scale = new Point(1, 1);
	
	private static final double defaultPadding = 60;
	private Point padding = new Point(defaultPadding, defaultPadding);
	
	// Options
	private boolean drawAxis = true;
	private boolean drawAxisLines = true;
	private boolean drawAxisLabels = true;
	private int labelSize = 10;
	private int labelFrequency = 5;
	
	private boolean drawPoints = true;
	private boolean drawPointLabels = true;
	private int pointSize = 4;
	
	private boolean drawLine = true;
	
	
	public Graph() {
		points = new ArrayList <Point> ();
		
		frame = new JFrame() {
			public void paint(Graphics g) {
				// Set the size and background of the graph.
				frame.setSize(width, height);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, width, height);
				
				// Calculate scaling automatically.
				if (autoscale && points.size() > 0) {
					
					// Calculate x range, scaling, and padding
					int xrange = points.get(points.size() - 1).getX() - points.get(0).getX();
					if (xrange > width)
						scale.setX(width / xrange);
					else scale.setX(1);
					padding.setX(Math.max(-1 * points.get(0).getX(), defaultPadding));
					
					int ymin = points.get(0).getY(), 
						ymax = points.get(0).getY();
					
					for (int i = 1 ; i < points.size() ; i++) {
						if (points.get(i).getY() < ymin) {
							ymin = points.get(i).getY();
						}
						if (points.get(i).getY() > ymax) {
							ymax = points.get(i).getY();
						}
					}
					if (ymax - ymin > height)
						scale.setY(height / (ymax - ymin));
					else scale.setY(1);
					padding.setY(Math.max(-1 * ymin, defaultPadding));
					
					// Recalculate display position.
					for (Point p : points) {
						p.display();
					}
				}
				
				// Draw the x and y axis.
				if (drawAxis) {
					
					// Draw axis labels
					if (drawAxisLabels) {
						g.setColor(Color.lightGray);
						g.setFont(new Font("Arial", 10, labelSize));
						boolean lyprecise = (labelSize * labelFrequency) < scale.y * 2;
						for (int ly = height - ((int) (padding.y * scale.y)) ; ly >= 0 ; ly -= labelSize * labelFrequency) {
							double labelValue = (height - ly) / scale.y - padding.y;
							
							g.drawString((lyprecise) ? String.format("%3.4f", labelValue) : 
													   String.format("%5d", (int) labelValue), 
									((int) (padding.x * scale.x)) - labelSize * 3 - ((lyprecise) ? labelSize : 0), 
									ly + ((labelValue != 0) ? labelSize / 2 : labelSize));
						}
						for (int lx = ((int) (padding.x * scale.x)) + labelSize * labelFrequency ; lx <= width ; lx += labelSize * labelFrequency) {
							g.drawString("" + ((int) ((lx / scale.x) - padding.x)), 
									lx - labelSize / 2, height - ((int) (padding.y * scale.y - labelSize)));
						}
					}
					
					if (drawAxisLines) {
						g.setColor(Color.lightGray);
						for (int ly = height - ((int) (padding.y * scale.y)) ; ly >= 0 ; ly -= labelSize * labelFrequency) {
							g.drawLine((int) (padding.x * scale.x), ly, width, ly);
						}
					}
					
					g.setColor(Color.black);
					g.drawLine((int) (padding.x * scale.x), 0, 
							   (int) (padding.x * scale.x), height);
					System.out.println(padding.y);
					g.drawLine(0, (int) (height - padding.y * scale.y), 
							   width, (int) (height - padding.y * scale.y));
				}
				
				
				// Draw each point.
				g.setColor(Color.BLUE);
				Point dp = null;
				for (int i = 0 ; i < points.size() ; i++) {
					Point p = points.get(i);
					if (drawPoints) 
						g.fillOval(p.dx - pointSize / 2, p.dy - pointSize / 2, pointSize, pointSize);
					
					if (drawLine && dp != null) {
						g.drawLine(dp.dx, dp.dy, p.dx, p.dy);
					}
					dp = p;
				}
			}
		};
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	/**
	 * Plots the given x, y coordinate on this graph.
	 * @param x - the x coordinate to plot
	 * @param y - the y coordinate to plot
	 */
	public Graph plot(int x, int y) { return plot((double) x, (double) y); }
	public Graph plot(int x, long y) { return plot((double) x, (double) y); }
	public Graph plot(int x, double y) { return plot((double) x, (double) y); }
	public Graph plot(long x, int y) { return plot((double) x, (double) y); }
	public Graph plot(long x, long y) { return plot((double) x, (double) y); }
	public Graph plot(long x, double y) { return plot((double) x, (double) y); }
	public Graph plot(double x, int y) { return plot((double) x, (double) y); }
	public Graph plot(double x, long y) { return plot((double) x, (double) y); }
	
	/**
	 * Plots the given x, y coordinate on this graph.
	 * @param x - the x coordinate to plot
	 * @param y - the y coordinate to plot
	 */
	public Graph plot(double x, double y) {
		int index = 0;
		Point p = new Point(x, y);
		while (index < points.size() && points.get(index).getX() < x) {
			index++;
		}
		points.add(index, p);
		p.display();
		frame.repaint();
		return this;
	}
	
	/**
	 * Sets the size of this graph to the given width and height.
	 * @param width - the width of the graph
	 * @param height - the height of the graph
	 * @return
	 */
	public Graph size(int width, int height) {
		this.width = width;
		this.height = height;
		for (Point p : points) {
			p.display();
		}
		frame.repaint();
		return this;
	}
	
	/**
	 * Sets the scale of the graph to the given value.
	 * @param scale - the graph scaling factor
	 */
	public Graph scale(double scale) {
		return this.scale(scale, scale);
	}
	
	/**
	 * Sets the horizontal and vertical scale of this graph to the given values.
	 * @param xscale - horizontal scaling factor
	 * @param yscale - vertical scaling factor
	 */
	public Graph scale(double xscale, double yscale) {
		this.scale = new Point(xscale, yscale);
		if (xscale * padding.x < defaultPadding) {
			padding.setX(defaultPadding / xscale);
		}
		if (yscale * padding.y < defaultPadding) {
			//padding.setY(defaultPadding * yscale);
			padding.setY(defaultPadding / yscale);
		}
		padding.setY(defaultPadding / yscale);
		for (Point p : points) {
			p.display();
		}
		frame.repaint();
		return this;
	}
	
	/**
	 * Sets the label size for points and the axis labels.
	 * @param size - the font size
	 */
	public Graph labelSize(int size) {
		this.labelSize = size;
		frame.repaint();
		return this;
	}
	
	/**
	 * Sets the horizontal and vertical paddings of this graph.
	 */
	public Graph padding(int horizontal, int vertical) {
		padding = new Point(horizontal, vertical);
		for (Point p : points) {
			p.display();
		}
		frame.repaint();
		return this;
	}
	
	/**
	 * Sets autoscaling on/off.
	 */
	public Graph setAutoscale(boolean auto) {
		autoscale = auto;
		frame.repaint();
		return this;
	}
	
	/**
	 * Draws the graph scale.
	 */
	public Graph setAxis(boolean axis) {
		drawAxis = axis;
		frame.repaint();
		return this;
	}
	
	/**
	 * Draws the graph scale.
	 */
	public Graph setAxisLines(boolean lines) {
		drawAxisLines = lines;
		frame.repaint();
		return this;
	}
	
	/**
	 * Draws the graph scale.
	 */
	public Graph setAxisLabels(boolean labels) {
		drawAxisLabels = labels;
		frame.repaint();
		return this;
	}
	
	/**
	 * Draws the point labels.
	 */
	public Graph setPointLabels(boolean label) {
		drawPointLabels = label;
		frame.repaint();
		return this;
	}
	
	/**
	 * Draw dots to indicate each point.
	 */
	public Graph setPoint(boolean draw) {
		drawPoints = draw;
		frame.repaint();
		return this;
	}
	
	/**
	 * Private class for representing points and other vectors.
	 */
	class Point {
		// Underlying numeric representation is precise.
		double x;
		double y;
		
		// The display x, y position of this point.
		private int dx;
		private int dy;
		
		/**
		 * Constructs a point with the given x, y coordinates.
		 * @param x - x coordinate
		 * @param y - y coordinate
		 */
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Constructs a point with the given x, y coordinates.
		 * @param x - x coordinate
		 * @param y - y coordinate
		 */
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Returns the integer value of the x coordinate.
		 * @return integer value of x
		 */
		public int getX() {
			return (int) x;
		}
		
		/**
		 * Returns the integer value of the y coordinate.
		 * @return integer value of y
		 */
		public int getY() {
			return (int) y;
		}
		
		/**
		 * Sets the x coordinate of this point.
		 * @param x - the x coordinate.
		 */
		public void setX(double x) {
			this.x = x;
		}
		
		/**
		 * Sets the y coordinate of this point.
		 * @param y - the y coordinate.
		 */
		public void setY(double y) {
			this.y = y;
		}
		
		/**
		 * Computes the display position for the given graph.
		 * @param g - the graph to position this point on.
		 */
		public void display() {
			dx = (int) ((x + padding.x) * scale.x);
			dy = (int) (height - (padding.y + y) * scale.y);
		}
	}
}
