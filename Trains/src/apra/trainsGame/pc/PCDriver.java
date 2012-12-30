package apra.trainsGame.pc;

import apra.trainsGame.TrainGame;

public class PCDriver {
	public static final int WIDTH = 1020;
	public static final int PIXELS_PER_UNIT = WIDTH;
	public static final int HEIGHT = 720;
	
	/**
     * Main method to run the game.
     * 
     * @param args
     */
    public static void main (String [] args)
    {
    		TrainGame game = new TrainGame();
    		InputManager im = new InputManager();
        	DrawSurface ds = new DrawSurface(WIDTH, HEIGHT, DrawSurface.WHITE);
        	
        	ds.addKeyListener(im.getListener()); 
        	im.initKeyBindings(game);
        	game.setInputManager(im);
        	game.setDrawSurface(ds);
    		
    		game.run ();
    }

	
}
