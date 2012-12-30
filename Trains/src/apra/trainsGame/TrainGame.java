package apra.trainsgame;
import java.util.ArrayList;

import apra.trainsgame.pc.DrawSurface;
import apra.trainsgame.pc.InputManager;
import apra.trainsgame.pc.PCDriver;

/**
 * Game main class.
 * 
 * @author Austin Purves
 * @author Rob Argue
 */
public class TrainGame
{
    private static final long MAX_FRAMERATE = 60;
    private static final int NUM_PLAYERS = 2;
    private static final int[] playerColors = {DrawSurface.RED, DrawSurface.BLUE, DrawSurface.GREEN, DrawSurface.RED};
    
    private DrawSurface drawSurface;
    private ArrayList<Player> players;
    private InputManager inputManager;
    private boolean isQuit;
    private static Rectangle BOUNDARY = new Rectangle(0, 0, PCDriver.WIDTH, PCDriver.HEIGHT);
    private GameState state; 
    
    /**
     * Constructor for Trains. Initializes canvas, player list, and input management.
     */
    public TrainGame()
    {
    	// init player list
    	players = new ArrayList<Player>();
    	for(int i = 0; i < NUM_PLAYERS; i++)
    	{
    		players.add(new Player(playerColors[i]));
    	}
    	
    	init();	
    }
    
    public void setInputManager(InputManager im)
    {
    	this.inputManager = im;
    }
 
    public void setDrawSurface(DrawSurface ds)
    {
    	this.drawSurface = ds;
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
    			new Vector2D(.8f, (float) (.4 / PCDriver.PIXELS_PER_UNIT * PCDriver.HEIGHT)),
    			new Vector2D(.8f, (float) (.6 / PCDriver.PIXELS_PER_UNIT * PCDriver.HEIGHT)) };
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
			
			ds.setColor(winner - 0xff000000 + 0x02000000);
			ds.fillRect(BOUNDARY);
			
			for (Player player : game.getPlayers())
			{
				player.draw(ds);
			}
		}
    	
    }
}