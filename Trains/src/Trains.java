import java.awt.Color;
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
    private static final int PIXELS_PER_UNIT = WIDTH;
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
			Method setHeading = Train.class.getMethod("setHeading", Direction.class);
			Object[] upObj = {Direction.UP};
			Object[] rightObj = {Direction.RIGHT};
			Object[] downObj = {Direction.DOWN};
			Object[] leftObj = {Direction.LEFT};
			
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
    	double[] playerInitialXs = {.8, .8};
    	double[] playerInitialYs = {.4/PIXELS_PER_UNIT*HEIGHT, .6/PIXELS_PER_UNIT*HEIGHT};
    	Direction[] playerInitialHeadings = {Direction.RIGHT, Direction.RIGHT};
    	double[] playerInitialLengths = {.7, .7};
    	
    	for (int i = 0; i < NUM_PLAYERS; i++)
    	{
    		players.get(i).setX(playerInitialXs[i]);
        	players.get(i).setY(playerInitialYs[i]);
        	players.get(i).initHeading(playerInitialHeadings[i]);
        	players.get(i).setLength(playerInitialLengths[i]);
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
    private void updatePhysics(long elspased)
    {
    	// TODO clean this
    	// TODO update these to be based on time
    	for (Train player : players)
    	{
    		player.step();
    	}
    	
		if (players.get(0).crashedInto(players.get(1)))
	    {
			isOver = true;
			System.out.println ("Player 2 wins");
	    }
		if (players.get(1).crashedInto(players.get(0)))
	    {
			isOver = true;
			System.out.println ("Player 1 wins");
	    }
		if (players.get(0).crashedInto(players.get(0)))
	    {
			isOver = true;
			System.out.println ("Player 2 wins");
	    }
		if (players.get(1).crashedInto(players.get(1)))
	    {
			isOver = true;
			System.out.println ("Player 1 wins");
	    }
		if (OutOfBounds (players.get(0)))
	    {
			isOver = true;
			System.out.println ("Player 2 wins");
	    }
		if (OutOfBounds (players.get(1)))
	    {
			isOver = true;
			System.out.println ("Player 1 wins");
	    }
		
		// TODO update these to be based on time
		players.get(0).doAcceleration (players.get(1));
		players.get(1).doAcceleration (players.get(0));
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
     * Check if a <code>Train</code> has gone out of bounds of the playing field.
     * 
     * @param train <code>Train</code> to check out-of-bounds-ness for
     * @return <code>true</code> if the train has gone out of bounds
     */
    static boolean OutOfBounds (Train train)
    {
    	// TODO refactor to use rectangle intersect
    	return (train.getX() < 0 
    			|| train.getX()*PIXELS_PER_UNIT > WIDTH
    			|| train.getY() < 0
    			|| train.getY()*PIXELS_PER_UNIT > HEIGHT);
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
    	Iterator<Direction> headings = train.getHeadingsIterator();
    	Iterator<Double> lengths = train.getLengthsIterator();
    	double x = train.getX ();
    	double y = train.getY ();
    	Direction engineHeading = train.getHeading();
    	//Color color2 = new Color (color.getRed(),color.getGreen(),color.getBlue(),100);
    	switch (engineHeading)
    	{
    		case UP:
    			int [] xPoints0 = {(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints0 = {(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints0,
    					yPoints0,
    					3,
    					color));
    			int [] xPoints0a = {(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints0a = {(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints0a,
    					yPoints0a,
    					3,
    					color));
    			break;
    		case RIGHT:
    			int [] xPoints1 = {(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints1 = {(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints1,
    					yPoints1,
    					3,
    					color));
    			int [] xPoints1a = {(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints1a = {(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints1a,
    					yPoints1a,
    					3,
    					color));
    			break;
    		case DOWN:
    			int [] xPoints2 = {(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints2 = {(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints2,
    					yPoints2,
    					3,
    					color));
    			int [] xPoints2a = {(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints2a = {(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints2a,
    					yPoints2a,
    					3,
    					color));
    			break;
    		case LEFT:
    			int [] xPoints3 = {(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints3 = {(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y-Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints3,
    					yPoints3,
    					3,
    					color));
    			int [] xPoints3a = {(int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((x+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			int [] yPoints3a = {(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    					(int)((y+Train.ACCELERATION_WIDTH/2)*PIXELS_PER_UNIT)};
    			canvasHolder.add (new ColoredPolygon (xPoints3a,
    					yPoints3a,
    					3,
    					color));
	    	}
	
    		while(headings.hasNext ())
    		{
    			Direction heading = headings.next();
    			double length = lengths.next ().doubleValue ();
    			Rectangle2 rectangle = new Rectangle2(0,0,0,0, color);
    			switch (heading)
    			{
    			case UP:
    				rectangle = new Rectangle2
    				((int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						(int)((length+Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						color);
    				y+=length;
    				break;
    			case RIGHT:
    				rectangle = new Rectangle2
    				((int)((x-length-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((length+Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						(int)((Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						color);
    				x-=length;
    				break;
    			case DOWN:
    				rectangle = new Rectangle2
    				((int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((y-length-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						(int)((length+Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						color);
    				y-=length;
    				break;
    			case LEFT:
    				rectangle = new Rectangle2
    				((int)((x-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((y-Train.COLLISION_WIDTH/2)*PIXELS_PER_UNIT),
    						(int)((length+Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						(int)((Train.COLLISION_WIDTH)*PIXELS_PER_UNIT),
    						color);
    				x+=length;
    				break;
    			}
		
    			canvasHolder.add(rectangle);
    		}
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