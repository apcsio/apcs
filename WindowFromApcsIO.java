
package apcs;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Window is a lightweight graphics library that makes it much easier to
 * build interactive games and simulations with Java.
 */
public class Window extends JApplet {
	// Serial version UID
	private static final long serialVersionUID = 1L;

	// Information about the currently active WindowInstance.
	private static boolean initialized = false;
	private static int width, height;
	private static final int defaultWidth = 500, defaultHeight = 500;

	// Mapping between String names and underlying integer values for color codes
	// and mappings from human-readable keys to virtual keys.
	private static Map <String, Integer> keyMap;
	private static Map <String, Integer> colorMap;
	private static ArrayList <String> imagePath;

	/**
	 * Creates a Window with the given width and height.
	 * @param width - the width of the window
	 * @param height - the height of the window
	 * @return an object to represent this window instance
	 */
	public static WindowInstance size(int width, int height) {
		// Run the initialization routine the first time a window is created.
		if (! initialized) {
			initialize();
			Window.width = width;
			Window.height = height;
			initialized = true;
		}
		if (! isApplet) {
			isApplication = true;
		}

		// Thread safety locks on the Window class.
		synchronized (Window.class) {
			// Get the currently running instance, if there is one.
			WindowInstance instance = instanceMap.get();

			// If there is no instance already running, create one and set it as the current instance.
			if (instance == null) {
				// Create a JFrame for the window.
				JFrame frame = new JFrame("");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Window window = new Window();
				window.bufferSize = new Dimension(width, height);
				instanceMap.set(window.master);

				// Create a container for the frame's content.
				Container pane = frame.getContentPane();
				pane.add(window);
				pane.setSize(window.getSize());
				pane.setMinimumSize(window.getSize());
				// frame.getContentPane().setIgnoreRepaint(true);

				// Initialize and start the window.
				window.init();
				frame.pack();
				frame.setResizable(false);
				frame.setVisible(true);
				window.start();
				return window.master;
			}
			return instance;
		}
	}

	/**
	 * Returns the width of the window.
	 * @return width of the window, in pixels.
	 */
	public static int width() {
		return getInstanceFromThread().getWidth();
	}

	/**
	 * Returns the height of the window.
	 * @return height of the window, in pixels.
	 */
	public static int height() {
		return getInstanceFromThread().getHeight();
	}

	/**
	 * Wait for the given number of seconds.
	 */
	public static void wait(double seconds) {
		sleep((int) (seconds * 1000));
	}

	/**
	 * Wait for the given number of seconds.
	 */
	public static void wait(int seconds) {
		sleep(seconds * 1000);
	}

	/**
	 * Wait for the given number of milliseconds.
	 */
	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception ignored) {}
	}

	/**
	 * Rolls a dice of the given number of sides, and returns the
	 * value that was randomly rolled (between 1 and sides inclusive)
	 * @param sides - number of sides on the dice
	 * @return the value that was rolled, in the inclusive range [1, sides]
	 */
	public static int rollDice(int sides) {
		if (sides < 1) sides = 1;
		return (int) (Math.random() * sides + 1);
	}
	
	/**
	 * Returns a random number between min and max inclusive.
	 * @param min - minimum random number
	 * @param max - maximum random number
	 * @return
	 */
	public static int random(int min, int max) {
		if (min > max)
			return (int) (max + Math.random() * (min - max + 1));
		else 
			return (int) (min + Math.random() * (max - min + 1));
	}

	public static boolean flipCoin() {
		return Math.random() < 0.5;
	}

	/**
	 * Key-related methods.
	 */
	public static class key {
		/**
		 * Returns true if the given key is pressed.
		 */
		public static boolean pressed(String key) {
			if (key == null) return false;
			else if (keyMap.containsKey(key))
				return Window.getInstanceFromThread().isVirtualKeyPressed(keyMap.get(key));
			else if (key.length() > 0)
				return Window.key.pressed(key.charAt(0));
			return false;
		}

		/**
		 * Returns true if the given key is not pressed.
		 */
		public static boolean released(String key) {
			return ! pressed(key);
		}

		/**
		 * Returns true if the key for the given character is pressed.
		 * @param key - the key to check for presses
		 * @return - true if the key is pressed, false otherwise
		 */
		public static boolean pressed(char key) {
			return getInstanceFromThread().isKeyPressed(key);
		}

		/**
		 * Returns true if the given key is not pressed.
		 */
		public static boolean released(char key) {
			return ! pressed(key);
		}
	}

	/**
	 * Mouse-related methods.
	 */
	public static class mouse {
		/**
		 * Returns true if the mouse is clicked.
		 * @return whether or not the mouse is clicked.
		 */
		public static boolean clicked() {
			return getInstanceFromThread().isMouseClicked();
		}

		/**
		 * Returns true if the mouse is not clicked.
		 * @return whether or not the mouse is released.
		 */
		public static boolean released() {
			return ! clicked();
		}

		/**
		 * Returns the x coordinate of the mouse.
		 */
		public static int getX() {
			return Window.getInstanceFromThread().getMouseX();
		}

		/**
		 * Returns the y coordinate of the mouse.
		 */
		public static int getY() {
			return Window.getInstanceFromThread().getMouseY();
		}

		/**
		 * Waits for a click to be registered.
		 */
		public static void waitForClick() {
			while (! getInstanceFromThread().isMouseClicked()) {
				Window.sleep(10);
			}
		}

		/**
		 * Waits for any ongoing click to be released.
		 */
		public static void waitForRelease() {
			while (getInstanceFromThread().isMouseClicked()) {
				Window.sleep(10);
			}
		}
	}

	/**
	 * Creates a simple client-server mesh for distributing key-value pairs. 
	 * The mesh can also be deployed as a stand-alone program via the Mesh class, 
	 * which implements the same basic methods. For the sake of simplicity, 
	 * keys are strings, and values are either integers, doubles, or strings.
	 *  
	 * @see Mesh
	 */
	public static class mesh {
		// The default port that the Window library should use.
		private static final int DEFAULT_PORT = 4965;

		// References to threads for master-slave network.
		private static Server server;
		private static Client client;
		private static ConcurrentHashMap <String, String> stringCache;
		private static ConcurrentHashMap <String, Double> doubleCache;
		private static ConcurrentHashMap <String, Integer> intCache;
		private static boolean running = false;

		/**
		 * Initializes the data structures of the mesh.
		 */
		private static void initialize() {
			stringCache = new ConcurrentHashMap <String, String> ();
			doubleCache = new ConcurrentHashMap <String, Double> (); 
			intCache = new ConcurrentHashMap <String, Integer> ();
			running = true;
		}

		/**
		 * Starts a mesh at the default port.
		 */
		public static void start() {
			start(DEFAULT_PORT);
		}

		/**
		 * Starts a mesh at the given port.
		 * @param port - the port number to listen on
		 */
		public static void start(int port) {
			if (! running) {
				initialize();
				server = new Server(port);
				server.start();
			}
		}

		/**
		 * Joins the mesh at the given IP address, with the default port.
		 * @param ip - the IP address of the server hosting the mesh
		 */
		public static void join(String ip) {
			join(ip, DEFAULT_PORT);
		}

		/**
		 * Joins the mesh at the given IP address and port.
		 * @param ip - the IP address of the server hosting the mesh
		 * @param port - the port the server is listening on
		 */
		public static void join(String ip, int port) {
			if (! running) {
				initialize();
				client = new Client(ip, port);
				client.start();
			}
		}

		/**
		 * Writes a key-value pair to the mesh, so all clients can read them.
		 * 
		 * @param key - a unique identifier for this value
		 * @param value - an integer value
		 */
		public static void write(String key, int value) {
			if (running) {

				// If this value has not changed from its locally cached value.
				if (intCache.containsKey(key) && 
						intCache.get(key) == value) return;

				// Send a new value through this instance's respective thread.
				if (server != null)
					server.put(null, key, value);
				else if (client != null)
					client.put(key, value);
			}
		}
		
		/**
		 * Writes a key-value pair to the mesh, so all clients can read them.
		 * 
		 * @param key - a unique identifier for this value
		 * @param value - a double value
		 */
		public static void write(String key, double value) {
			if (running) {

				// If this value has not changed from its locally cached value.
				if (doubleCache.containsKey(key) && 
						doubleCache.get(key) == value) return;

				// Send a new value through this instance's respective thread.
				if (server != null)
					server.put(null, key, value);
				else if (client != null)
					client.put(key, value);
			}
		}
		
		/**
		 * Writes a key-value pair to the mesh, so all clients can read them.
		 * 
		 * @param key - a unique identifier for this value
		 * @param value - a double value
		 */
		public static void write(String key, String value) {
			if (running) {

				// If this value has not changed from its locally cached value.
				if (stringCache.containsKey(key) && 
						stringCache.get(key).equals(value)) return;

				// Send a new value through this instance's respective thread.
				if (server != null)
					server.put(null, key, value);
				else if (client != null)
					client.put(key, value);
			}
		}

		/**
		 * Reads an integer value from the distributed key-value store.
		 * @param key - the unique identifier for the requested value
		 */
		public static int read(String key) {
			if (intCache.containsKey(key))
				return intCache.get(key);
			else return 0;
		}

		/**
		 * Reads a precise value from the distributed key-value store.
		 * @param key - the unique identifier for the requested value
		 */
		public static double readDouble(String key) {
			if (doubleCache.containsKey(key))
				return doubleCache.get(key);
			else return 0;
		}

		/**
		 * Reads a string value from the distributed key-value store.
		 * @param key - the unique identifier for the requested value
		 */
		public static String readString(String key) {
			if (stringCache.containsKey(key))
				return stringCache.get(key);
			else return null;
		}

		/**
		 * Thread for a server listening on the given port.
		 */
		private static class Server extends Thread {
			private int port;
			private ServerSocket master;
			private ArrayList <ServerClient> clients;

			/**
			 * Initialize this thread to listen on the given port.
			 * @param port
			 */
			public Server(int port) {
				this.port = port;
			}

			/**
			 * Starts listening on the given port.
			 */
			public void run() {
				try {
					master = new ServerSocket(port);
					clients = new ArrayList <ServerClient> ();
					System.out.println("Starting mesh at " + InetAddress.getLocalHost().getHostAddress() + ", port " + port);

					// Keep listening for new clients
					while (true) {
						Socket newClient = master.accept();
						Window.mesh.message("connection from " + newClient.getInetAddress().getHostAddress());

						ServerClient client = new ServerClient(newClient, this);
						synchronized(clients) {
							clients.add(client);
							client.start();
						}
					}
				}
				catch (IOException e) {
					error("Could not create server at port " + port);
				}
			}

			/**
			 * 
			 * @param client - the client that is sending the new value, or null if it is originating from the server.
			 * @param key - unique ID of the data
			 * @param value - the integer value
			 */
			public void put(ServerClient client, String key, int value) {
				intCache.put(key, value);
				synchronized(clients) {
					for (ServerClient c : clients) {
						if (c != client) {
							c.put(key, value);
						}
					}
				}
			}

			/**
			 * 
			 * @param client - the client that is sending the new value, or null if it is originating from the server.
			 * @param key - unique ID of the data
			 * @param value - the integer value
			 */
			public void put(ServerClient client, String key, double value) {
				doubleCache.put(key, value);
				synchronized(clients) {
					for (ServerClient c : clients) {
						if (c != client) {
							c.put(key, value);
						}
					}
				}
			}

			/**
			 * 
			 * @param client - the client that is sending the new value, or null if it is originating from the server.
			 * @param key - unique ID of the data
			 * @param value - the integer value
			 */
			public void put(ServerClient client, String key, String value) {
				stringCache.put(key, value);
				synchronized(clients) {
					for (ServerClient c : clients) {
						if (c != client) {
							c.put(key, value);
						}
					}
				}
			}
		}

		private static class ServerClient extends Thread {
			private Server master;
			private BufferedReader input;
			private PrintWriter output;
			private String ip;
			private boolean connected = false;
			private long bandwidth = 0;

			public ServerClient(Socket socket, Server master) {
				try {
					this.master = master;
					ip = socket.getInetAddress().getHostAddress();
					input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					output = new PrintWriter(socket.getOutputStream(), true);
					connected = true;
				}
				catch (IOException e) {}
			}

			public void run() {
				if (! connected) return;

				// Copy all current data to client
				StringBuilder initialUpdate = new StringBuilder();
				for (String key : intCache.keySet())
					initialUpdate.append('#').append(key).append('=').append(intCache.get(key)).append('\n');
				for (String key : doubleCache.keySet())
					initialUpdate.append('%').append(key).append('=').append(doubleCache.get(key)).append('\n');
				for (String key : stringCache.keySet())
					initialUpdate.append('$').append(key).append('=').append(stringCache.get(key)).append('\n');

				output.println(initialUpdate);
				bandwidth += initialUpdate.length();
				Window.mesh.message("updated " + ip);

				// Keep reading updates from the client until disconnect, and update all other clients with updates
				try {
					while (true) {
						String line = input.readLine();
						if (line == null) 
							break;

						bandwidth += line.length();
						char type = line.charAt(0);
						int equals = line.indexOf('=');
						String key = line.substring(1, equals);
						if (type == '#')
							master.put(this, key, Integer.parseInt(line.substring(equals + 1)));
						else if (type == '%')
							master.put(this, key, Double.parseDouble(line.substring(equals + 1)));
						else if (type == '$')
							master.put(this, key, line.substring(equals + 1));
					}

					input.close();
					output.close();

				} catch (IOException e) {
				}
			}

			public long getBandwidth() {
				return bandwidth;
			}

			public void put(String key, int value) {
				if (connected) {
					output.println('#' + key + '=' + value);
				}
			}

			public void put(String key, double value) {
				if (connected) {
					output.println('%' + key + '=' + value);
				}
			}

			public void put(String key, String value) {
				if (connected) {
					output.println('$' + key + '=' + value);
				}
			}
		}

		private static class Client extends Thread {
			Socket socket;
			BufferedReader input;
			PrintWriter output;
			String ip;
			boolean connected = false;

			public Client(String ip, int port) {
				try {
					this.socket = new Socket(ip, port);
					this.ip = ip;

					input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					output = new PrintWriter(socket.getOutputStream(), true);
					connected = true;
				} catch (IOException e) {}
			}

			public void run() {
				if (! connected) {
					error("Could not connect to " + ip + ".");
					return;
				}
				try {
					while (true) {
						String line = input.readLine();
						if (line == null) {
							error("Connection to " + ip + " closed.");
							break;
						}
						if (line.length() > 0) {
							char type = line.charAt(0);
							int equals = line.indexOf('=');
							String key = line.substring(1, equals);
							if (type == '#')
								intCache.put(key, Integer.parseInt(line.substring(equals + 1)));
							else if (type == '%')
								doubleCache.put(key, Double.parseDouble(line.substring(equals + 1)));
							else if (type == '$')
								stringCache.put(key, line.substring(equals + 1));
						}
					}
					connected = false;
					input.close();
					output.close();
					socket.close();
				} catch (IOException e) {}
			}

			public void put(String key, int value) {
				if (connected && key != null) {
					intCache.put(key, value);
					output.println('#' + key + '=' + value);
				}
			}

			public void put(String key, double value) {
				if (connected && key != null) {
					doubleCache.put(key, value);
					output.println('%' + key + '=' + value);
				}
			}

			public void put(String key, String value) {
				if (connected && key != null) {
					stringCache.put(key, value);
					output.println('$' + key + '=' + value);
				}
			}
		}

		/**
		 * Prints a mesh message.
		 */
		private static DateFormat messageDateFormat = new SimpleDateFormat("HH:mm:ss");

		public static void message(String m) {
			System.out.println("[ " + messageDateFormat.format(new Date()) + " ] " + m);
		}

		public static void error(String e) {
			System.err.println("[ " + messageDateFormat.format(new Date()) + " ] " + e);
		}
	}

	public static class out {
		/**
		 * Fills the circle at the given (x, y) coordinate with the given radius.
		 * @param x - the x coordinate of the circle
		 * @param y - the y coordinate of the circle
		 * @param radius - the radius of the circle
		 */
		public static void circle(int x, int y, int radius) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().fillOval(x - radius, y - radius, radius * 2, radius * 2);
		}

		/**
		 * Fills the circle at the given (x, y) coordinate with the given radius.
		 * @param x - the x coordinate of the circle
		 * @param y - the y coordinate of the circle
		 * @param radius - the radius of the circle
		 */
		public static void circle(int x, int y, double radius) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().fillOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
		}

		/**
		 * Creates a line from the (x, y) coordinate to the (endx, endy) coordinate.
		 * @param x - starting x coordinate
		 * @param y - starting y coordinate
		 * @param endx - ending x coordinate
		 * @param endy - ending y coordinate
		 */
		public static void line(int x, int y, int endx, int endy) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().drawLine(x, y, endx, endy);
		}

		/**
		 * Fills a rectangle centered at the given x, y coordinate with the given width and height.
		 * @param x - x coordinate of the rectangle
		 * @param y - y coordinate of the rectangle
		 * @param width - width of the rectangle
		 * @param height - height of the rectangle
		 */
		public static void rectangle(int x, int y, int width, int height) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().fillRect(x - width / 2, y - height / 2, width, height);
		}

		/**
		 * Fills a rectangle centered at the given x, y coordinate, rotated around its center by the given angle.
		 * @param x - x coordinate of the rectangle
		 * @param y - y coordinate of the rectangle
		 * @param width - width of the rectangle
		 * @param height - height of the rectangle
		 * @param angle - an angle in degrees
		 */
		public static void rectangle(int x, int y, int width, int height, double angle) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			angle = Math.toRadians(angle);

			Window.out.polygon(x + rotatedX(- width / 2, - height / 2, angle), y + rotatedY(-width / 2, -height / 2, angle),
					x + rotatedX(-width / 2, height / 2, angle), y + rotatedY(-width / 2, height / 2, angle),
					x + rotatedX(width / 2, height / 2, angle), y + rotatedY(width / 2, height / 2, angle),
					x + rotatedX(width / 2, -height / 2, angle), y + rotatedY(width / 2, -height / 2, angle));
		}

		// Used in rotation calculations.
		private static int rotatedX(int x, int y, double angle) {
			return (int) (x * Math.cos(angle) - y * Math.sin(angle));
		}

		// Used in rotation calculations.
		private static int rotatedY(int x, int y, double angle) {
			return (int) (x * Math.sin(angle) + y * Math.cos(angle));
		}

		/**
		 * Fills a square centered at the given x, y coordinate with the given side length.
		 * @param x - the x coordinate of the square
		 * @param y - the y coordinate of the square
		 * @param side - side length of the square
		 */
		public static void square(int x, int y, int side) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().fillRect(x - side / 2, y - side / 2, side, side);
		}

		/**
		 * Fills a square centered at the given x, y coordinate with the given side length, rotated by the given angle.
		 * @param x - the x coordinate of the square
		 * @param y - the y coordinate of the square
		 * @param side - side length of the square
		 * @param angle - the angle to rotate the square by
		 */
		public static void square(int x, int y, int side, double angle) {
			rectangle(x, y, side, side, angle);
		}

		/**
		 * Fills the oval centered at the given (x, y) coordinate with the given width and height.
		 * @param x - x coordinate of the oval's center
		 * @param y - y coordinate of the oval's center
		 * @param width - width of the oval
		 * @param height - height of the oval
		 */
		public static void oval(int x, int y, int width, int height) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().fillOval(x - width / 2, y - height / 2, width, height);
		}

		/**
		 * Draws an arc at the given x and y coordinate with the given width, height, start angle, and arc angle.
		 * @param x - starting x coordinate
		 * @param y - starting y coordinate
		 * @param width - width of the arc
		 * @param height - height of the arc
		 * @param startAngle - starting angle
		 * @param arcAngle - angle that the arc curves by
		 */
		public static void arc(int x, int y, int width, int height, int startAngle, int arcAngle) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().drawArc(x - width / 2, y - height / 2, width, height, startAngle, arcAngle);
		}

		/**
		 * Fills a polygon with the given list of x, y coordinates as vertices.
		 * @param x - list of x coordinates
		 * @param y - list of y coordinates
		 */
		public static void polygon(int[] x, int[] y) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().fillPolygon(x, y);
		}

		/**
		 * Fills a polygon with the given list of x, y coordinates.
		 */
		public static void polygon(int ... coordinates) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			if (coordinates != null && coordinates.length > 0) {
				int[] x = new int[coordinates.length / 2];
				int[] y = new int[coordinates.length / 2];
				for (int i = 0 ; i < coordinates.length / 2 ; i++) {
					x[i] = coordinates[i * 2];
					if (i * 2 + 1 < coordinates.length) {
						y[i] = coordinates[i * 2 + 1];
					}
				}
				Window.getInstanceFromThread().fillPolygon(x, y);
			}
		}

		/**
		 * Draws the given text at the given (x, y) coordinate.
		 * @param text - the text to draw
		 * @param x - the x coordinate of the text's bottom left corner.
		 * @param y - the y coordinate of the text's bottom right corner.
		 */
		public static void print(String text, int x, int y) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().drawText(text, x, y);
		}

		/**
		 * Draws the given number at the (x, y) coordinate.
		 * @param value - the number to draw
		 * @param x - the x coordinate of the text's bottom left corner.
		 * @param y - the y coordinate of the text's bottom right corner.
		 */
		public static void print(int value, int x, int y) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().drawText(Integer.toString(value), x, y);
		}

		/**
		 * Draws the given number at the (x, y) coordinate.
		 * @param value - the number to draw
		 * @param x - the x coordinate of the text's bottom left corner.
		 * @param y - the y coordinate of the text's bottom right corner.
		 */
		public static void print(double value, int x, int y) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().drawText(Double.toString(value), x, y);
		}

		/**
		 * Sets the background to the given RGB value.
		 * @param red - red component
		 * @param green - green component
		 * @param blue - blue component
		 */
		public static void background(int red, int green, int blue) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().setColor(red, green, blue);
			Window.getInstanceFromThread().fillRect(0, 0, Window.width, Window.height);
		}

		/**
		 * Sets the background to the given color name.
		 * @param color - name of the background color.
		 */
		public static void background(String color) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			if (color != null) {
				Window.out.color(color);
				Window.getInstanceFromThread().fillRect(0, 0, Window.width, Window.height);
			}
		}

		/**
		 * Sets the background to the current color.
		 */
		public static void background() {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().fillRect(0, 0, Window.width, Window.height);
		}

		/**
		 * Clears the background to the initial black color.
		 */
		public static void clear() {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().setColor(0, 0, 0);
			Window.getInstanceFromThread().fillRect(0, 0, Window.width, Window.height);
		}

		/**
		 * Sets the color to the given HSB value (hue, saturation, and brightness)
		 * @param hue - hue component
		 * @param saturation - saturation component
		 * @param brightness - brightness component
		 */
		public static void color(float hue, float saturation, float brightness) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().setHSB(hue, saturation, brightness);
		}

		/**
		 * Sets the color to the given String color - if the color isn't built in, this will choose black.
		 * @param color - the name of the color.
		 */
		public static void color(String color) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);

			if (color != null) {
				color = color.toLowerCase();

				// Convert color string to a hex value.
				if (color.indexOf('#') == 0 && color.length() == 7) {
					try {
						Window.getInstanceFromThread().setColor(
								Integer.valueOf( color.substring( 1, 3 ), 16 ),
								Integer.valueOf( color.substring( 3, 5 ), 16 ),
								Integer.valueOf( color.substring( 5, 7 ), 16 )
								);
					}
					catch(NumberFormatException e) {
						Window.getInstanceFromThread().setColor(0, 0, 0);
					}
				}
				else if (colorMap.containsKey(color)) {
					int c = colorMap.get(color);
					Window.getInstanceFromThread().setColor((c >> 16) & 0xff, (c >> 8) & 0xff, c & 0xff);
				}
				else Window.getInstanceFromThread().setColor(0, 0, 0);
			}
		}

		/**
		 * Sets the color to the given RGB value (red, green, and blue)
		 * @param red - red component
		 * @param green - green component
		 * @param blue - blue component
		 */
		public static void color(int red, int green, int blue) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.	getInstanceFromThread().setColor(red, green, blue);
		}

		/**
		 * Picks a random color.
		 */
		public static void randomColor() {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().setColor(
					(int) (Math.random() * 256),
					(int) (Math.random() * 256),
					(int) (Math.random() * 256)
					);
		}

		/**
		 * Sets the font to the given font.
		 */
		public static void font(String name, int size) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().setFont(name + "-" + size);
		}

		/**
		 * Sets the font to the given font.
		 */
		public static void fontSize(int size) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			Window.getInstanceFromThread().setFont(font + "-" + size);
		}

		/**
		 * Draws the image stored in the given file path at the given (x, y) coordinate.
		 * @param filename - the name of the file
		 * @param x - the x coordinate of the image's top left corner
		 * @param y - the y coordinate of the image's top left corner
		 */
		public static void image(String filename, int x, int y) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			getInstanceFromThread().drawImage(filename, x, y);
		}

		/**
		 * Draws the image represented by the given path at the given (x, y) coordinate, with the given rotation.
		 * @param filename - the name of the file
		 * @param x - the x coordinate of the image's top left corner
		 * @param y - the y coordinate of the image's top left corner
		 * @param angle - the amount to rotate the image by, in degrees
		 */
		public static void image(String filename, int x, int y, double angle) {
			if (! initialized) Window.size(defaultWidth, defaultHeight);
			getInstanceFromThread().drawImage(filename, x, y, angle);
		}
	}


	private static class message {
		public static void meshMasterStart(String ip, int port) {
			System.out.println("Mesh started at " + ip + ", port " + port);
		}

		public static void meshMasterNewSlave(String hostAddress) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * Standard error messages.
	 */
	private static class error {

		public static void meshMaster(int port) {
			System.err.println("Could not start a mesh at port " + port + ". " +
					"Try closing all open Window instances, then try again.");
		}

		public static void meshAlreadyRunning() {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Ask a question and return the answer.
	 * @param question - the question to ask.
	 * @return the text the user entered.
	 */
	public static String ask(String question) {
		return JOptionPane.showInputDialog(question);
	}

	/**
	 * Set the frame rate of the window to the given value.
	 * @param rate - the number of frames to try showing every second.
	 */
	public static void setFrameRate(int rate) {
		frameRate = 1000 / rate;
		getInstanceFromThread().flipBuffer();
	}

	/**
	 * Push the drawn contents to a frame that remains fixed on the viewing screen until another
	 * frame is produced and drawn.
	 * @param milliseconds - the number of milliseconds to display the frame for without continuing to redraw.
	 */
	public static void frame(int milliseconds) {
		if (! initialized) Window.size(defaultWidth, defaultHeight);
		getInstanceFromThread().flipBuffer();
		Window.sleep(milliseconds);
	}

	/**
	 * Push the drawn contents to a frame.
	 */
	public static void frame() {
		if (! initialized) Window.size(defaultWidth, defaultHeight);
		getInstanceFromThread().flipBuffer();
		Window.sleep(frameRate);
	}

	/**
	 * WindowInstance is the underlying representation of a window, and is
	 * created by the Window class through the 'create' method.
	 */
	class WindowInstance {

		// Getters
		public int getWidth() {				return bufferSize.width;	}
		public int getHeight() {			return bufferSize.height;	}
		public int getMouseClickX() {		return mouseClickX;			}
		public int getMouseClickY() {		return mouseClickY;			}
		public long getMouseClickTime() {	return mouseClickTime;		}
		public int getMouseX() {			return mouseX;				}
		public int getMouseY() {			return mouseY;				}
		public boolean isMouseClicked() {	return isMouseClicked;		}

		// Returns true if the given character key is pressed.
		public boolean isKeyPressed(char key) {
			return key >= 0 && key < keyPressed.length ? keyPressed[key] : false;
		}

		// Returns true if the given virtual key is pressed.
		public boolean isVirtualKeyPressed(int keyCode) {
			return keyCode >= 0 && keyCode < virtualKeyPressed.length ? virtualKeyPressed[keyCode] : false;
		}

		// Returns the graphics2D instance, fixes potential race condition.
		public Graphics2D graphics() {
			while (g == null) sleep(1000);
			return g;
		}

		public void drawImage(String filename, int x, int y) {
			drawImage(filename, x, y, 0);
		}

		// Draws the given image at the given x, y coordinate.
		public void drawImage(String filename, int x, int y, double angle) {
			Graphics2D g = graphics();
			Image img = getImage(filename);
			if (img != null) {
				if (angle != 0) {
					AffineTransform old = new AffineTransform();
					AffineTransform trans = new AffineTransform();

					trans.rotate(Math.toRadians(angle), img.getWidth(null) / 2, img.getHeight(null) / 2);
					trans.translate(x - img.getWidth(null) / 2, y - img.getHeight(null) / 2);
					g.setTransform(trans);
					g.drawImage(img, x, y, img.getWidth(null), img.getHeight(null), null);
					trans.setToIdentity();

					g.setTransform(old);
				}
				else g.drawImage(img, x, y, null);
			}
			else {
				g.drawString(filename + "?", x, y);
			}
			if (paintImmediately) paintWindow();
		}

		// Returns the image associated with the given path description.
		private Image getImage(String path) {
			if (imageMap.containsKey(path))
				return imageMap.get(path);

			if (path.indexOf("http://") == 0 || path.indexOf("https://") == 0) {
				return getURLImage(path);
			}
			else try {
				File imageFile = new File(path);
				int imageFilePath = 0;
				while (! imageFile.exists() && imageFilePath < imagePath.size() ) {
					imageFile = new File(imagePath.get(imageFilePath) + "/" + path);
					imageFilePath++;
				}

				if (imageFile != null) {
					Image image = ImageIO.read(imageFile);
					imageMap.put(path, image);
					return image;
				}
				else return null;
			}
			catch (Exception ex) {
				return null;
			}
		}

		/**
		 * Returns the image at the given URL, caching it to the given File path if necessary.
		 * @param path - the path to save the file
		 * @param intCache - a cache file (can be null to indicate no caching)
		 * @return the downloaded image as an Image object
		 */
		private Image getURLImage(String path) {
			File cache = null;
			Image image = null;

			// MD5 hash the file path to get a unique descriptor for this image.
			try {
				MessageDigest m = MessageDigest.getInstance("MD5");
				m.reset();
				m.update(path.getBytes());

				cache = new File("image/" + new BigInteger(1, m.digest()).toString(16) + ".png");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Try to get the image from the cache.
			try {
				if (cache.exists()) {
					image = ImageIO.read(cache);
					imageMap.put(path, image);
					return image;
				}
			}
			catch (IOException e) {}

			// If no image was found, get the image from the web.
			if (image == null) {
				try {
					// Download the image
					System.out.println("Downloading " + path);
					long start = System.currentTimeMillis();
					image = ImageIO.read(new URL(path));
					System.out.println("Completed in " + (System.currentTimeMillis() - start) + "ms: " + image.getWidth(null) + " x " + image.getHeight(null));
					imageMap.put(path, image);

					// Save the image to the cache
					if (cache != null) {
						cache.mkdirs();
						BufferedImage bufferedImage = new BufferedImage(
								image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
						Graphics2D g = bufferedImage.createGraphics();
						g.drawImage(image, 0, 0, null);
						g.dispose();
						ImageIO.write(bufferedImage, "png", cache);
					}

					return image;
				}
				catch (MalformedURLException e) {}
				catch (IOException e) {}
			}
			return null;
		}

		public void drawLine(int x1, int y1, int x2, int y2) {
			graphics().drawLine(x1, y1, x2, y2);
			if (paintImmediately) paintWindow();
		}

		public void drawText(String text, int x, int y) {
			graphics().drawString(text, x, y);
			if (paintImmediately) paintWindow();
		}

		public void drawPolygon(int[] x, int[] y) {
			graphics().drawPolygon(x, y, x.length);
			if (paintImmediately) paintWindow();
		}

		public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
			graphics().drawArc(x, y, width, height, startAngle, -arcAngle);
			if (paintImmediately) paintWindow();
		}

		public void drawOval(int minX, int minY, int width, int height) {
			graphics().drawOval(minX, minY, width, height);
			if (paintImmediately) paintWindow();
		}

		public void fillOval(int minX, int minY, int width, int height) {
			graphics().fillOval(minX, minY, width, height);
			if (paintImmediately) paintWindow();
		}

		public void fillRect(int x1, int y1, int width, int height) {
			graphics().fillRect(x1, y1, width, height);
			if (paintImmediately) paintWindow();
		}

		public void fillPolygon(int[] x, int[] y) {
			graphics().fillPolygon(x, y, x.length);
			if (paintImmediately) paintWindow();
		}

		public void setColor(int red, int green, int blue) {
			if (red < 0) red = 0;
			if (green < 0) green = 0;
			if (blue < 0) blue = 0;
			if (red > 255) red = 255;
			if (green > 255) green = 255;
			if (blue > 255) blue = 255;

			currentColor = new Color(red, green, blue);
			graphics().setColor(currentColor);
		}

		public void setRGB(int red, int green, int blue) {
			setColor(red, green, blue);
		}

		public void setHSB(float hue, float saturation, float brightness) {
			currentColor = new Color(hue, saturation, brightness);
			graphics().setColor(currentColor);
		}

		public Font setFont(String font) {
			currentFont = Font.decode(font);
			graphics().setFont(currentFont);
			return currentFont;
		}

		public void flipBuffer() {
			// Both flipBuffer and portions of paint() are synchronized
			// on the class object to ensure
			// that both cannot execute at the same time.
			paintImmediately = false; // user has called flipBuffer at least
			// once
			// getSingleton();
			synchronized (Window.this) {
				Image temp = backImageBuffer;
				backImageBuffer = frontImageBuffer;
				frontImageBuffer = temp;

				if (g != null)
					g.dispose();
				paintWindow(); // paint to Video

				g = (Graphics2D) backImageBuffer.getGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, backImageBuffer.getWidth(null),
						backImageBuffer.getHeight(null));
				g.setColor(currentColor);
				g.setFont(currentFont);
			}
		}

		void createBuffers(int width, int height, String options) {
			if (g != null)
				g.dispose();
			if (frontImageBuffer != null)
				frontImageBuffer.flush();
			if (backImageBuffer != null)
				backImageBuffer.flush();
			options = options != null ? options.toLowerCase() : "";
			bufferSize = new Dimension(width, height);
			stretchToFit = options.contains("stretch");

			// if buffers are requested _after_ the window has been realized
			// then faster volatile images are possible
			// BUT volatile images silently fail when tested Vista IE8 and
			// JRE1.6
			boolean useVolatileImages = false;
			if (useVolatileImages) {
				try {
					// Paint silently fails when tested in IE8 Vista JRE1.6.0.14
					backImageBuffer = createVolatileImage(width, height);
					frontImageBuffer = createVolatileImage(width, height);
				} catch (Exception ignored) {

				}
			}
			if (!GraphicsEnvironment.isHeadless()) {
				try {
					GraphicsConfiguration config = GraphicsEnvironment
							.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice().getDefaultConfiguration();
					backImageBuffer = config.createCompatibleImage(width,
							height);
					frontImageBuffer = config.createCompatibleImage(width,
							height);
				} catch (Exception ignored) {
				}
			}

			// as a fall-back we can still use slower BufferedImage with
			// arbitrary RGB model
			if (frontImageBuffer == null) {
				// System.err.println("Creating BufferedImage buffers");
				backImageBuffer = new BufferedImage(bufferSize.width,
						bufferSize.height, BufferedImage.TYPE_INT_RGB);
				frontImageBuffer = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
			}
			master.flipBuffer();// set up graphics, including font and color
			// state
			paintImmediately = true; // actually, user has not yet called
			// flipBuffer
		}

	};

	private static ThreadLocal<WindowInstance> instanceMap = new ThreadLocal<WindowInstance>();

	private static synchronized WindowInstance getInstanceFromThread() {
		WindowInstance instance = instanceMap.get();
		return instance != null ? instance : size(500, 500);
	}

	private static boolean isApplication;
	private static boolean isApplet;

	private WindowInstance master = new WindowInstance();

	private Graphics2D g;
	private Image backImageBuffer, frontImageBuffer;
	private Map<String, Image> imageMap = Collections
			.synchronizedMap(new HashMap<String, Image>());

	private boolean stretchToFit;

	private boolean[] keyPressed = new boolean[256];
	private boolean[] keyTyped = new boolean[256];
	private boolean[] virtualKeyPressed = new boolean[1024];
	private boolean[] virtualKeyTyped = new boolean[1024];

	private int mouseX, mouseY, mouseClickX, mouseClickY;
	private int mouseButtonsAndModifierKeys;
	private long mouseClickTime;
	private boolean isMouseClicked;

	private Dimension bufferSize = new Dimension(500, 500);

	private Color currentColor = Color.WHITE;
	private Font currentFont = Font.decode("Times-18");
	private static String font = "Times";
	private boolean isRunning = true;
	private Thread mainThread;
	private int paintAtX, paintAtY, windowWidth, windowHeight;
	protected boolean paintImmediately;
	private static int frameRate = 33;

	@Override
	public Dimension getMinimumSize() {
		return bufferSize;
	}

	@Override
	public final Dimension getPreferredSize() {
		return getMinimumSize();
	}

	@Override
	public final void init() {
		if (!isApplication)
			isApplet = true;

		instanceMap.set(master);

		setSize(bufferSize);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				addMouseListener(mouseListener);
				addMouseMotionListener(mouseMotionListener);
				addKeyListener(keyListener);
				setFocusTraversalKeysEnabled(false);
				setFocusable(true);
				setVisible(true);
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

			}
		});
	}

	@Override
	@SuppressWarnings("deprecation")
	public final void stop() {
		isRunning = false;
		if (mainThread == null)
			return;
		mainThread.interrupt();
		sleep(500);
		if (mainThread.isAlive())
			mainThread.stop();
		mainThread = null;
	}

	@Override
	public final void start() {
		master.createBuffers(bufferSize.width, bufferSize.height, "");
		isRunning = true;
		if (isApplet) {
			mainThread = new Thread("main") {
				@Override
				public void run() {
					try {
						instanceMap.set(Window.this.master);
						String paramKey = "window-main-class";
						String targetClassName = getParameter(paramKey);

						if (targetClassName == null) {
							System.err.println("Error: no main method.");
							return;
						}
						Class <?> targetClass = Class.forName(targetClassName);
						String[] argValue = new String[0];
						Class <?> [] argTypes = { argValue.getClass() };
						Method main = targetClass.getMethod("main", argTypes);
						main.invoke(null, new Object[] { argValue });
					}
					catch (ThreadDeath ignored) { }
					catch (Exception e) {
						System.err.println("Exception: " + e.getMessage());
						e.printStackTrace();
					}
					finally {
						instanceMap.remove();
					}
				}
			};
			mainThread.start();
		}
	}

	@Override
	public final void destroy() {
		super.destroy();
	}

	@Override
	public void update(Graphics windowGraphics) {
		paint(windowGraphics);
	}

	@Override
	public void paint(Graphics windowGraphics) {
		if (windowGraphics == null)
			return;
		windowWidth = getWidth();
		windowHeight = getHeight();

		if (frontImageBuffer == null) {
			// no image to display
			windowGraphics.clearRect(0, 0, windowWidth, windowHeight);
			return;
		}
		synchronized (Window.class) {
			Image image = paintImmediately ? backImageBuffer : frontImageBuffer;
			if (stretchToFit) {
				paintAtX = paintAtY = 0;
				windowGraphics.drawImage(image, 0, 0, windowWidth,
						windowHeight, this);
			} else {
				int x = windowWidth - bufferSize.width;
				int y = windowHeight - bufferSize.height;
				paintAtX = x / 2;
				paintAtY = y / 2;
				windowGraphics.setColor(Color.BLACK);
				if (y > 0) {
					windowGraphics.fillRect(0, 0, windowWidth + 1, paintAtY);
					windowGraphics.fillRect(0, windowHeight - paintAtY - 1,
							windowWidth + 1, paintAtY + 1);
				}
				if (x > 0) {
					windowGraphics.fillRect(0, 0, paintAtX + 1,
							windowHeight + 1);
					windowGraphics.fillRect(windowWidth - paintAtX - 1, 0,
							paintAtX + 1, windowHeight + 1);
				}
				windowGraphics.drawImage(image, paintAtX, paintAtY, this);
			}
		}
	}

	private void paintWindow() {
		Graphics windowGraphics = getGraphics();
		if (windowGraphics != null)
			paint(getGraphics());
		else
			repaint();
	}

	private KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			char c = e.getKeyChar();
			mouseButtonsAndModifierKeys = e.getModifiersEx();
			if (c >= 0 && c < keyPressed.length)
				keyPressed[c] = keyTyped[c] = true;
			int vk = e.getKeyCode();
			if (vk >= 0 && vk < virtualKeyPressed.length)
				virtualKeyPressed[vk] = virtualKeyTyped[vk] = true;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			char c = e.getKeyChar(); // may be CHAR_UNDEFINED
			mouseButtonsAndModifierKeys = e.getModifiersEx();
			if (c >= 0 && c < keyPressed.length)
				keyPressed[c] = false;
			int vk = e.getKeyCode();
			if (vk >= 0 && vk < virtualKeyPressed.length)
				virtualKeyPressed[vk] = false;
		}

		@Override
		public void keyTyped(KeyEvent e) {}
	};

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			if (windowWidth == 0 || windowHeight == 0)
				return; // no display window yet
			mouseClickX = (stretchToFit ? (int) (0.5 + me.getX()
			* bufferSize.width / (double) windowWidth) : me.getX()
					- paintAtX);
			mouseClickY = (stretchToFit ? (int) (0.5 + me.getY()
			* bufferSize.height / (double) windowHeight) : me.getY()
					- paintAtY);
			mouseClickTime = me.getWhen();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			isMouseClicked = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isMouseClicked = false;
		}
	};

	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent me) {
			if (windowWidth == 0 || windowHeight == 0)
				return;
			mouseX = (stretchToFit ? (int) (0.5 + me.getX() * bufferSize.width
					/ (double) windowWidth) : me.getX() - paintAtX);
			mouseY = (stretchToFit ? (int) (0.5 + me.getY() * bufferSize.height
					/ (double) windowHeight) : me.getY() - paintAtY);
			mouseButtonsAndModifierKeys = me.getModifiersEx();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseListener.mouseClicked(e);
			mouseMoved(e);
		}
	};

	public static void addImages(String path) {
		if (! path.endsWith("/"))
			path += "/";
		if (path.startsWith("~") && System.getProperty("user.home") != null) {
			if (path.charAt(1) != '/')
				path = "/" + path.substring(1);
			else
				path = path.substring(1);
			path = System.getProperty("user.home") + path;
		}

		if (imagePath == null)
			imagePath = new ArrayList <String>();
		imagePath.add(path);
	}

	/**
	 * Creates a mapping between the color string name and the given RGB value.
	 * @param name - name of the color
	 * @param red - red component of color
	 * @param green - green component of color
	 * @param blue - blue component of color
	 */
	public static void addColor(String name, int red, int green, int blue) {
		if (red < 0) red = 0;
		if (green < 0) green = 0;
		if (blue < 0) blue = 0;
		if (red > 255) red = 255;
		if (green > 255) green = 255;
		if (blue > 255) blue = 255;
		colorMap.put(name, ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff));
	}

	/**
	 * Creates a mapping between the given color string name and the given hex code.
	 * @param name - name of the color
	 * @param hex - hex code of the color
	 */
	public static void addColor(String name, String hex) {
		if (hex.indexOf('#') == 0 && hex.length() == 7) {
			colorMap.put(name, (
					Integer.valueOf( hex.substring( 1, 3 ), 16 ) << 16 |
					Integer.valueOf( hex.substring( 3, 5 ), 16 ) << 8 |
					Integer.valueOf( hex.substring( 5, 7 ), 16 )
					));
		}
	}
	/**
	 * Adds a palette from a .apcsp palette file(same method as addPalette())
	 * @param filename - the name of the file ending in a .apcsp extension that contains the colors to be included in the palette
	 * one color per line, each line should look like this: NAME,RED_VALUE,GREEN_VALUE,BLUE_VALUE where RED_VALUE,GREEN_VALUE, and BLUE_VALUE are 
	 * integers between 0 and 255, inclusive for both
	 */
	public static void addPaletteFromFile(String filename) {
        try {
        	
        	Scanner input = new Scanner(new File(filename));
            

            while (input.hasNextLine()) {
                
                String[] splitted = input.nextLine().split(",");

                Window.addColor(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
                
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
	}
	/**
	 * Adds a palette from a String
	 * @param colors - a string that contains color(s) separated by a semicolon(;). Each color should look like this: NAME,RED_VALUE,GREEN_VALUE,
	 * BLUE_VALUE where RED_VALUE,GREEN_VALUE, and BLUE_VALUE are integers between 0 and 255, inclusive for both
	 */
	public static void addPaletteFromString(String colors) {
		String[] colorList = colors.split(";");
		
		for (int i = 0; i < colorList.length; i ++) {
			
			String[] splitted = colorList[i].split(",");
			
	        Window.addColor(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
		}
		
	}
	/**
	 * Adds a palette from a .apcsp palette file(same method as addPaletteFromFile())
	 * @param filename - the name of the file ending in a .apcsp extension that contains the colors to be included in the palette
	 * one color per line, each line should look like this: NAME,RED_VALUE,GREEN_VALUE,BLUE_VALUE where RED_VALUE,GREEN_VALUE, and BLUE_VALUE are 
	 * integers between 0 and 255, inclusive for both
	 */
	public static void addPalette(String filename) {
		addPaletteFromFile(filename);
	}
	/**
	 * Clears all colors added, including palettes and default colors.
	 */
	public static void clearColors() {
		colorMap = new HashMap <String, Integer> ();
	}
	private static void initialize() {
		keyMap = new HashMap <String, Integer> ();
		keyMap.put("space", KeyEvent.VK_SPACE);
		keyMap.put("left", KeyEvent.VK_LEFT);
		keyMap.put("right", KeyEvent.VK_RIGHT);
		keyMap.put("up", KeyEvent.VK_UP);
		keyMap.put("down", KeyEvent.VK_DOWN);
		keyMap.put("escape", KeyEvent.VK_ESCAPE);
		keyMap.put("tab", KeyEvent.VK_TAB);
		keyMap.put("shift", KeyEvent.VK_SHIFT);
		keyMap.put("control", KeyEvent.VK_CONTROL);
		keyMap.put("alt", KeyEvent.VK_ALT);
		keyMap.put("delete", KeyEvent.VK_DELETE);
		keyMap.put("home", KeyEvent.VK_HOME);
		colorMap = new HashMap <String, Integer> ();
		//add colors
		String colors = "alice blue,240,248,255;antique white,250,235,215;aqua,0,255,255;aquamarine,127,255,212;azure,240,255,255;beige,245,"
			+ "245,220;bisque,255,228,196;black,0,0,0;blanched almond,255,235,205;blue,0,0,255;blue violet,138,43,226;brown,139,69,19;burlywood,2"
			+ "22,184,135;cadet blue,95,158,160;chartreuse,127,255,0;chocolate,210,105,30;coral,255,127,80;cornflower blue,100,149,237;cornsilk,255"
			+ ",248,220;cyan,0,255,255;dark blue,0,0,139;dark cyan,0,139,139;dark goldenrod,184,134,11;dark gray,169,169,169;dark green,0,100,0;dark "
			+ "khaki,189,183,107;dark magenta,139,0,139;dark olive green,85,107,47;dark orange,255,140,0;dark orchid,153,50,204;dark red,139,0,0;dark "
			+ "salmon,233,150,122;dark sea green,143,188,143;dark slate blue,72,61,139;dark slate gray,47,79,79;dark turquoise,0,206,209;dark violet,1"
			+ "48,0,211;deep pink,255,20,147;deep sky blue,0,191,255;dim gray,105,105,105;dodger blue,30,144,255;firebrick,178,34,34;floral white,255,"
			+ "250,240;forest green,34,139,34;fuschia,255,0,255;gainsboro,220,220,220;ghost white,255,250,250;gold,255,215,0;goldenrod,218,165,32;gray,"
			+ "128,128,128;grey,128,128,128;green,0,128,0;green yellow,173,255,47;honeydew,240,255,240;hot pink,255,105,180;indian red,205,92,92;indigo"
			+ ",111,0,255;ivory,255,255,240;khaki,240,230,140;lavender,230,230,250;lavender blush,255,240,245;lawn green,124,252,0;lemon chiffon,255,25"
			+ "0,205;light blue,173,216,230;light coral,240,128,128;light cyan,224,255,255;light goldenrod,238,221,130;light goldenrod yellow,250,250,21"
			+ "0;light gray,211,211,211;light green,144,238,144;light pink,255,182,193;light salmon,255,160,122;light sea green,32,178,170;light sky blue"
			+ ",135,206,250;light slate blue,132,112,255;light slate gray,119,136,153;light steel blue,176,196,222;light yellow,255,255,224;lime,0,255,"
			+ "0;lime green,50,205,50;linen,250,240,230;magenta,255,0,255;maroon,128,0,0;medium aquamarine,102,205,170;medium blue,0,0,205;medium orchi"
			+ "d,186,85,211;medium purple,147,112,219;medium sea green,60,179,113;medium slate blue,123,104,238;medium spring green,0,250,154;medium t"
			+ "urquoise,72,209,204;medium violet red,199,21,133;midnight blue,25,25,112;mint cream,245,255,250;misty rose,255,228,225;moccasin,255,228,1"
			+ "81;navajo white,255,222,173;navy,0,0,128;old lace,253,245,230;olive,128,128,0;olive drab,107,142,35;orange,255,165,0;orange red,255,69,0;o"
			+ "rchid,218,112,214;pale goldenrod,238,232,170;pale green,152,251,152;pale turquoise,175,238,238;pale violet red,219,112,147;papaya whip,25"
			+ "5,239,213;peach puff,255,218,185;peru,205,133,63;pink,255,192,203;plum,221,160,221;powder blue,176,224,230;purple,128,0,128;red,255,0,0;r"
			+ "osy brown,188,143,143;royal blue,65,105,225;saddle brown,139,69,19;salmon,250,128,114;sandy brown,244,164,96;sea green,46,139,87;seashel"
			+ "l,255,245,238;sienna,160,82,45;silver,192,192,192;sky blue,135,206,235;slate blue,106,90,205;slate gray,112,128,144;snow,255,250,250;spr"
			+ "ing green,0,255,127;steel blue,70,130,180;tan,210,180,140;teal,0,128,128;thistle,216,191,216;tomato,255,99,71;turquoise,64,224,208;viol"
			+ "et,238,130,238;violet red,208,32,144;wheat,245,222,179;white,255,255,255;white smoke,245,245,245;yellow,255,255,0;yellow green,154,205,50";
		Window.addPaletteFromString(colors);
	}
	
	
}