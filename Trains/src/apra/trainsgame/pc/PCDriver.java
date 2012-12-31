package apra.trainsgame.pc;

import apra.trainsgame.Color;
import apra.trainsgame.TrainsGame;

public class PCDriver {
	private static final int WIDTH = 1020;
	private static final int HEIGHT = 720;
	
	/**
     * Main method to run the game.
     * 
     * @param args
     */
    public static void main (String [] args)
    {
    		TrainsGame game = new TrainsGame(WIDTH, HEIGHT);
    		PCInputManager im = new PCInputManager();
        	PCDrawSurface ds = new PCDrawSurface(WIDTH, HEIGHT, Color.WHITE);
        	
        	ds.addKeyListener(im.getListener());
        	game.setInputManager(im);
        	game.setDrawSurface(ds);
    		
    		game.run ();
    }

	
}
