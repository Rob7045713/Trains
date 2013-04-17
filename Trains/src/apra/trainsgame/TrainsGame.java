package apra.trainsgame;
import java.util.ArrayList;

//import apra.trainsgame.pc.PCDriver;

/**
 * Game main class.
 * 
 * @author Austin Purves
 * @author Rob Argue
 */
public class TrainsGame extends Thread
{
	public static int pixelsPerUnit;

    private static final long MAX_FRAMERATE = 60;
    private static final int NUM_PLAYERS = 2;
    private static final Color[] playerColors = {Color.RED, Color.BLUE, Color.GREEN, Color.RED};
    
    private DrawSurface drawSurface;
    private ArrayList<Player> players;
    private InputManager inputManager;
    private boolean isQuit;
    private static Rectangle boundary;
    private GameState state; 
    private GameController gameController;
    private ButtonManager buttonManager;
    private int width;
    private int height;

    /**
     * Constructor for Trains. Initializes canvas, player list, and input management.
     */
    public TrainsGame(int w, int h, InputManager im, DrawSurface ds)
    {
	width = w;
	height = h;
	
    	this.inputManager = im;
    	this.drawSurface = ds;
    	this.isQuit = false;
    	
    	boundary = new Rectangle(0, (height-width)/2, width, width);
    	pixelsPerUnit = width;
    	// init player list
    	players = new ArrayList<Player>();
    	for(int i = 0; i < NUM_PLAYERS; i++)
    	{
    		players.add(new Player(playerColors[i]));
    	}
    	
    	this.gameController = new GameController(this);
	this.buttonManager = new ButtonManager(this.gameController);

	buttonManager.addButton (new Rectangle (0,(height-width)/2,width,width), GameAction.GAME_RESTART);
	/*buttonManager.addButton (new Rectangle (0,0,width/2,(height-width)/2), GameAction.PLAYER_1_CW);
	buttonManager.addButton (new Rectangle (width/2,0,width/2,(height-width)/2), GameAction.PLAYER_1_CCW);*/
	/*buttonManager.addButton (new Rectangle (0,height-(height-width)/2,width/2,(height-width)/2), GameAction.PLAYER_2_CCW);
	  buttonManager.addButton (new Rectangle (width/2,height-(height-width)/2,width/2,(height-width)/2), GameAction.PLAYER_2_CW);*/

	buttonManager.addButton (new Rectangle (0,0,(height-width)/3,(height-width)/2), GameAction.PLAYER_1_LEFT);
	buttonManager.addButton (new Rectangle (width-(height-width)/3,0,(height-width)/3,(height-width)/2), GameAction.PLAYER_1_RIGHT);
	buttonManager.addButton (new Rectangle ((height-width)/3,0,width-(height-width)*2/3,(height-width)/4), GameAction.PLAYER_1_UP);
	buttonManager.addButton (new Rectangle ((height-width)/3,(height-width)/4,width-(height-width)*2/3,(height-width)/4), GameAction.PLAYER_1_DOWN);

	buttonManager.addButton (new Rectangle (0,height-(height-width)/2,(height-width)/3,(height-width)/2), GameAction.PLAYER_2_LEFT);
	buttonManager.addButton (new Rectangle (width-(height-width)/3,height-(height-width)/2,(height-width)/3,(height-width)/2), GameAction.PLAYER_2_RIGHT);
	buttonManager.addButton (new Rectangle ((height-width)/3,height-(height-width)/2,width-(height-width)*2/3,(height-width)/4), GameAction.PLAYER_2_UP);
	buttonManager.addButton (new Rectangle ((height-width)/3,height-(height-width)/4,width-(height-width)*2/3,(height-width)/4), GameAction.PLAYER_2_DOWN);

    	init();	
    	
    }

    /*Austin's input methods, should probably be refactored*/
    public void pointerDown (int x, int y){
	buttonManager.pointerDown (x, y);
    }

    public void pointerUp (int x, int y){
    }
    
    public void setInputManager(InputManager im)
    {
    	this.inputManager = im;
    }
 
    public void setDrawSurface(DrawSurface ds)
    {
    	this.drawSurface = ds;
    }
 
    public GameController getGameController()
    {
    	return gameController;
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
     * Setter for isQuit.
     * 
     * @param over New value to use for isQuit
     */
    public void setQuit(Boolean quit)
    {
    	this.isQuit = quit;
    }
    
    /**
     * Initialize the game to the beginning state.
     */
    public void init()
    {
    	Vector2D[] positions = {
    			new Vector2D(.8f, (float) (.4 / pixelsPerUnit * height)),
    			new Vector2D(.8f, (float) (.6 / pixelsPerUnit * height)) };
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
    public void update(long elapsed) 
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
    public void run()
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
    }

    /**
     * Check if a <code>Rectangle</code> is contained by the bounds of the playing field
     * 
     * @param rect <code>Rectangle</code> to check bounds for
     * @return <code>true</code> if the rectangle is in bounds
     */
    public boolean inBounds (Rectangle rect)
    {
    	return boundary.contains(rect);
    }
    
    interface GameState
    {
    	public void update(TrainsGame game, long elapsed);
    	public void draw(TrainsGame game, DrawSurface ds);
    }
    
    class RunRoundState implements GameState
    {

		@Override
		public void update(TrainsGame game, long elapsed) {
			
			// respond to input
			inputManager.doInput(game);
			
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
		public void draw(TrainsGame game, DrawSurface ds) {
			for (Player player : game.getPlayers())
			{
				player.draw(ds);
			}

			/*ds.setColor (new Color (100,255,0,0));
			ds.fillRect (new Rectangle (0,0,width/2,(height-width)/2));
			ds.setColor (new Color (50,255,0,0));
			ds.fillRect (new Rectangle (width/2,0,width/2,(height-width)/2));*/

			ds.setColor (new Color (75,255,0,0));
			ds.fillRect (new Rectangle (0,0,(height-width)/3,(height-width)/2));
			ds.fillRect (new Rectangle (width-(height-width)/3,0,(height-width)/3,(height-width)/2));
			ds.setColor (new Color (50,255,0,0));
			ds.fillRect (new Rectangle ((height-width)/3,0,width-(height-width)*2/3,(height-width)/4));
			ds.setColor (new Color (100,255,0,0));
			ds.fillRect (new Rectangle ((height-width)/3,(height-width)/4,width-(height-width)*2/3,(height-width)/4));

			ds.setColor (new Color (75,0,0,255));
			ds.fillRect (new Rectangle (0,height-(height-width)/2,(height-width)/3,(height-width)/2));
			ds.fillRect (new Rectangle (width-(height-width)/3,height-(height-width)/2,(height-width)/3,(height-width)/2));
			ds.setColor (new Color (100,0,0,255));
			ds.fillRect (new Rectangle ((height-width)/3,height-(height-width)/2,width-(height-width)*2/3,(height-width)/4));
			ds.setColor (new Color (50,0,0,255));
			ds.fillRect (new Rectangle ((height-width)/3,height-(height-width)/4,width-(height-width)*2/3,(height-width)/4));

			/*ds.setColor (new Color (100,0,0,255));
			ds.fillRect (new Rectangle (0,height-(height-width)/2,width/2,(height-width)/2));
			ds.setColor (new Color (50,0,0,255));
			ds.fillRect (new Rectangle (width/2,height-(height-width)/2,width/2,(height-width)/2));*/
		}
    	
    }

    class GameOverState implements GameState
    {

		@Override
		public void update(TrainsGame game, long elapsed) {
			// respond to input
			inputManager.doInput(game);
		}

		@Override
		public void draw(TrainsGame game, DrawSurface ds) {
			Color winner = Color.BLACK;
			
			for (Player player : players)
			{
				if (!player.isDead())
					winner = player.getColor();
			}
			
			ds.setColor(new Color(32, winner.getRed(), winner.getGreen(), winner.getBlue()));
			ds.fillRect(boundary);
			
			for (Player player : game.getPlayers())
			{
				player.draw(ds);
			}
		}
    	
    }
}