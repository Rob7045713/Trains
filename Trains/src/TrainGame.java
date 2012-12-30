import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Game main class.
 * 
 * @author Austin Purves
 * @author Rob Argue
 */
public class TrainGame
{
    private static final int WIDTH = 1020;
    public static final int PIXELS_PER_UNIT = WIDTH;
    private static final int HEIGHT = 720;
    private static final int WALL_WIDTH = WIDTH/500;
    private static final long MAX_FRAMERATE = 60;
    private static final int NUM_PLAYERS = 2;
    private static final int[] playerColors = {DrawSurface.RED, DrawSurface.BLUE, DrawSurface.GREEN, DrawSurface.RED};
    private enum Layout {QWERTY, DVORAK}
    private static final Layout KEYBOARD_LAYOUT = Layout.QWERTY;
    
    private DrawSurface drawSurface;
    private ArrayList<Player> players;
    private InputManager inputManager;
    private ConcreteKeyListener listener;
    private boolean isQuit;
    private static Rectangle BOUNDARY = new Rectangle(0, 0, WIDTH, HEIGHT);
    private GameState state; 
    
    /**
     * Constructor for Trains. Initializes canvas, player list, and input management.
     */
    public TrainGame()
    {
    	// init canvas holder
    	drawSurface = new DrawSurface(WIDTH, HEIGHT, DrawSurface.WHITE);
    	
    	// init player list
    	players = new ArrayList<Player>();
    	for(int i = 0; i < NUM_PLAYERS; i++)
    	{
    		players.add(new Player(playerColors[i]));
    	}
    	
    	// init input manager
    	listener = new ConcreteKeyListener();
    	drawSurface.addKeyListener(listener);  // TODO generalize this
    	inputManager = new InputManager(listener);
    	initKeyBindings();
    	
    	// init state
    	state = new RunRoundState(); 	
    }
 
    /**
     * Get a list of players in the game.
     * 
     * @return A list of players in the game
     */
    public ArrayList<Player> getPlayers()
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
    		if (KEYBOARD_LAYOUT == Layout.QWERTY)
    		{
    			bindPlayerKeys(0, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D);
    		}
    		if (KEYBOARD_LAYOUT == Layout.DVORAK)
    		{
    			bindPlayerKeys(0, KeyEvent.VK_COMMA, KeyEvent.VK_O, KeyEvent.VK_A, KeyEvent.VK_E);
    		}
    		bindPlayerKeys(1, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
			
    		// bind general keys
			Method setOver = TrainGame.class.getMethod("setOver", Boolean.class);
			Object[] trueObj = {new Boolean(true)};
			inputManager.register(KeyEvent.VK_ESCAPE, new Action(this, setOver, trueObj));
			
			Method reset = TrainGame.class.getMethod("init");
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
			Method setHeading = Player.class.getMethod("setDirection", Vector2D.class);
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
    
    /**
     * Initialize the game to the beginning state.
     */
    public void init()
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
    	
    	state = new RunRoundState();
    }
    
    /**
     * Update the game.
     * 
     * @param elapsed Time elapsed (in milliseconds) since the last update
     */
    private void update(long elapsed) 
    {
    	state.update(this, elapsed);
    }
    
    public void draw(DrawSurface ds)
    {
    	state.draw(this, ds);
    }
    
    /**
     * Run a round of the game.
     */
    private void run()
    {
    	long oldTime;
    	long elapsed = 0;
      	long frameTime = 1000 / MAX_FRAMERATE;
	
    	while (!isQuit)
    	{
    		// update / draw
    		oldTime = System.currentTimeMillis();
    		update(elapsed);
    		drawSurface.paint(this);
    		elapsed = System.currentTimeMillis()-oldTime;

    		// framerate limiter
    		if (elapsed < frameTime)
    		{
    			try
    			{
    				Thread.sleep (frameTime - elapsed);
    			} 
    			catch (InterruptedException ie)
    			{
    				System.out.println ("Interrupted!!!");
    			}
    		
    			elapsed = System.currentTimeMillis() - oldTime;
    		}
    	
    		//System.out.println (1000/elapsed);
    		
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
     * Main method to run the game.
     * 
     * @param args
     */
    public static void main (String [] args)
    {
    		TrainGame game = new TrainGame();
    		game.init();
    		game.run ();
    }
    
    interface GameState
    {
    	public void update(TrainGame game, long elapsed);
    	public void draw(TrainGame game, DrawSurface ds);
    }
    
    class RunRoundState implements GameState
    {

		@Override
		public void update(TrainGame game, long elapsed) {
			
			// respond to input
			inputManager.executeInput();
			
			// update players
			for (Player player : players)
	    	{
	    		player.update(elapsed, game);
	    	}
	    	
			// check if down to the last player
	    	int playersAlive = 0;
	    	for (Player player : players)
	    	{
	    		if (!player.isDead())
	    			playersAlive++;
	    	}
	    	
	    	if (playersAlive <= 1)
	    		state = new GameOverState();
		}

		@Override
		public void draw(TrainGame game, DrawSurface ds) {
			for (Player player : game.getPlayers())
			{
				player.draw(ds);
			}
		}
    	
    }

    class GameOverState implements GameState
    {

		@Override
		public void update(TrainGame game, long elapsed) {
			// respond to input
			inputManager.executeInput();
		}

		@Override
		public void draw(TrainGame game, DrawSurface ds) {
			int winner = DrawSurface.BLACK;
			
			for (Player player : players)
			{
				if (!player.isDead())
					winner = player.getColor();
			}
			
			ds.setColor(winner - 255 + 64);
			ds.fillRect(BOUNDARY);
			
			for (Player player : game.getPlayers())
			{
				player.draw(ds);
			}
		}
    	
    }
}