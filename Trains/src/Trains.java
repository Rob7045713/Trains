import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;

/**
 * Game main class.
 * 
 * @author Austin Purves
 * @author Rob Argue
 */
public class Trains
{
    private static final int WIDTH = 1020;
    public static final int PIXELS_PER_UNIT = WIDTH;
    private static final int HEIGHT = 720;
    private static final int WALL_WIDTH = WIDTH/500;
    private static final long MAX_FRAMERATE = 60;
    private static final int NUM_PLAYERS = 2;
    
    private CanvasHolder canvasHolder;
    private ArrayList<Train> players;
    private InputManager inputManager;
    private ConcreteKeyListener listener;
    private boolean isOver;
    private boolean isQuit;
    private static Rectangle BOUNDARY = new Rectangle(0, 0, WIDTH, HEIGHT);
    
    /**
     * Constructor for Trains. Initializes canvas, player list, and input management.
     */
    public Trains()
    {
    	// init canvas holder
    	canvasHolder = new CanvasHolder(WIDTH, HEIGHT);
    	
    	// init player list
    	players = new ArrayList<Train>();
    	for(int i = 0; i < NUM_PLAYERS; i++)
    	{
    		players.add(new Train());
    	}
    	
    	// init input manager
    	listener = new ConcreteKeyListener ();
    	canvasHolder.addKeyListener(listener);
    	inputManager = new InputManager(listener);
    	initKeyBindings();
    	
    }
 
    /**
     * Get a list of players in the game.
     * 
     * @return A list of players in the game
     */
    public ArrayList<Train> getPlayers()
    {
    	return players;
    }
    
    /**
     * Initialize all key bindings for the game.
     */
    private void initKeyBindings()
    {
    	try {
    		
    		// bind player keys
    		bindPlayerKeys(0, KeyEvent.VK_COMMA, KeyEvent.VK_O, KeyEvent.VK_A, KeyEvent.VK_E);
    		bindPlayerKeys(1, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
			
    		// bind general keys
			Method setOver = Trains.class.getMethod("setOver", Boolean.class);
			Object[] trueObj = {new Boolean(true)};
			inputManager.register(KeyEvent.VK_ESCAPE, new Action(this, setOver, trueObj));
			
			Method reset = Trains.class.getMethod("reset");
			inputManager.register(KeyEvent.VK_ENTER, new Action(this, reset, new Object [0]));
			
		} catch (NoSuchMethodException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		}
    	
    }

    /**
     * Initialize key bindings for a particular player. Intended for use with <code>KeyEvent</code> constants.
     * 
     * @param player Player number of the player to bind keys for
     * @param up Key to bind to the up action
     * @param down Key to bind to the down action
     * @param left Key to bind to the left action
     * @param right Key to bind to the right action
     */
    private void bindPlayerKeys(int player, int up, int down, int left, int right)
    {
    	try {
			Method setHeading = Train.class.getMethod("setDirection", Vector2D.class);
			Object[] upObj = {VectorDirection.UP};
			Object[] rightObj = {VectorDirection.RIGHT};
			Object[] downObj = {VectorDirection.DOWN};
			Object[] leftObj = {VectorDirection.LEFT};
			
			inputManager.register(up, new Action(players.get(player), setHeading, upObj));
			inputManager.register(left, new Action(players.get(player), setHeading, leftObj));
			inputManager.register(down, new Action(players.get(player), setHeading, downObj));
			inputManager.register(right, new Action(players.get(player), setHeading, rightObj));
			
		} catch (NoSuchMethodException e) {
			System.err.println("Error: Player (" + player + ") Key binding failure");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Error: Player (" + player + ") Key binding failure");
			e.printStackTrace();
		}
    }
    
    /**
     * Setter for isOver.
     * 
     * @param over New value to use for isOver
     */
    public void setOver(Boolean over)
    {
    	this.isQuit = over;
    }

    public void reset ()
    {
    	init ();
    }
    
    /**
     * Initialize the game to the beginning state.
     */
    private void init()
    {
    	Vector2D[] positions = {
    			new Vector2D(.8f, (float) (.4 / PIXELS_PER_UNIT * HEIGHT)),
    			new Vector2D(.8f, (float) (.6 / PIXELS_PER_UNIT * HEIGHT)) };
    	Vector2D[] directions = {
    			VectorDirection.RIGHT,
    			VectorDirection.RIGHT };
    	float[] lengths = {
    			.7f, 
    			.7f };
    	
    	for (int i = 0; i < NUM_PLAYERS; i++)
    	{
    		players.get(i).init(positions[i], directions[i], lengths[i]);
     	}
    	
    	isOver = false;
    }
    
    /**
     * Update the game.
     * 
     * @param elapsed Time elapsed (in milliseconds) since the last update
     */
    private void update(long elapsed) 
    {
    	inputManager.executeInput();
    	if (!isOver)
    		updatePhysics(elapsed);
    }
    
    /**
     * Update the game physics.
     * 
     * @param elspased Time elapsed (in milliseconds) since the last update
     */
    private void updatePhysics(long elapsed)
    {
    	// TODO clean this
    	// TODO update these to be based on time
    	for (Train player : players)
    	{
    		player.update(elapsed, this);
    	}
    	
    	// TODO pretty this up
		if (players.get(0).isDead())
	    {
			isOver = true;
			System.out.println("Player 2 wins");
	    }
		if (players.get(1).isDead())
		{
			isOver = true;
			System.out.println("Player 1 Wins");
		}
		
    }
    
    /**
     * Draw the game to the screen.
     */
    private void draw()
    {
    	// TODO update this to push drawing to players
    	canvasHolder.clear ();
		ShowTrain(players.get(0),Color.BLUE);
		ShowTrain(players.get(1),Color.RED);
		canvasHolder.repaint();
    }
    
    /**
     * Run a round of the game.
     */
    private void runRound()
    {
    	long currentTime = System.currentTimeMillis();
    	long oldTime;
	
    	//System.out.println ("Press esc to exit...");
	
    	while (!isQuit)
    	{
    		// update / draw
	    long elapsed = 0;
		oldTime = System.currentTimeMillis();
    		update(elapsed);
    		draw();
		elapsed = System.currentTimeMillis()-oldTime;

    		// framerate limiter
    		long frameTime = 1000 / MAX_FRAMERATE;
    		if (elapsed < frameTime)
    		{
		    try{Thread.sleep (Math.max (frameTime,elapsed));}catch (InterruptedException ie) {System.out.println ("Interrupted!!!");}
    		
    			currentTime = System.currentTimeMillis();
    			elapsed = currentTime - oldTime;
    		}
    		
    	}
	
    	//System.out.println ("done.");
    }

    /**
     * Check if a <code>Rectangle</code> is contained by the bounds of the playing field
     * 
     * @param rect <code>Rectangle</code> to check bounds for
     * @return <code>true</code> if the rectangle is in bounds
     */
    public boolean inBounds (Rectangle rect)
    {
    	return BOUNDARY.contains(rect);
    }

    /**
     * Draw a <code>Train</code> to the screen.
     * 
     * @param train <code>Train</code> to draw
     * @param color <code>Color</code> to draw the <code>Train</code>
     */
    public void ShowTrain (Train train, Color color)
    {
    	// TODO clean this
    	ArrayList<Rectangle> rects = train.getRectangles();
    	for (Rectangle r : rects)
    	{
    		canvasHolder.add(r, color);
    	}
    	
    	color = new Color(color.getRed(), color.getBlue(), color.getGreen(), 64);
    	canvasHolder.add(train.getEndBox(Train.End.HEAD), color);
    	canvasHolder.add(train.getEndBox(Train.End.TAIL), color);
    }

    /**
     * Main method to run the game.
     * 
     * @param args
     */
    public static void main (String [] args)
    {
    		Trains game = new Trains();
    		game.init();
    		game.runRound ();
    }
}