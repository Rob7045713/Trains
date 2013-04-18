package apra.trainsgame.pc;

import apra.trainsgame.Color;
import apra.trainsgame.TrainsGame;

public class PCDriver {
//	private static final int WIDTH = 320;
//	private static final int HEIGHT = 569;
	private static final int WIDTH = 320;
	private static final int HEIGHT = 569;
	
	/**
     * Main method to run the game.
     * 
     * @param args
     */
    public static void main (String [] args)
    {
    		
    		PCInputManager im = new PCInputManager();
        	PCDrawSurface ds = new PCDrawSurface(WIDTH, HEIGHT, Color.WHITE);
        	TrainsGame game = new TrainsGame(WIDTH, HEIGHT, im, ds);
        	
        	ds.addKeyListener(im.getListener());
		ds.addMouseListener(im.getMouseListener());
    		
    		game.run ();
    		
    		System.exit(0);
    }

	
}
