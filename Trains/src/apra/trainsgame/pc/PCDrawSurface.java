package apra.trainsgame.pc;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;

import apra.trainsgame.DrawSurface;
import apra.trainsgame.Rectangle;
import apra.trainsgame.TrainGame;


public class PCDrawSurface extends Canvas implements DrawSurface {
	/**
	 * Just to get rid of a warning, not used at the moment
	 */
	private static final long serialVersionUID = 1L;
	
	private Graphics g;
	private Color backgroundColor;
	private int width;
	private int height;
	
	private JFrame frame;
	
	public PCDrawSurface(int width, int height, apra.trainsgame.Color backgroundColor)
	{
		super();
		
		this.width = width;
		this.height = height;
		
		frame = new JFrame("window");
    	frame.setSize(width,height);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(this);
    	frame.setVisible(true);
    	
    	this.backgroundColor = backgroundColor.getColor();
    	
    	this.createBufferStrategy (2);
    	this.g = getBufferStrategy().getDrawGraphics();
	}
	
	public void addKeyListener (ConcreteKeyListener listener)
    {
    	frame.addKeyListener(listener);
    }

    public void paint(TrainGame game)
    {	
    	Graphics g = this.getBufferStrategy().getDrawGraphics ();
    	this.g = g;
    	
    	clear();
    	game.draw(this);
	
    	g.dispose ();
    	this.getBufferStrategy().show();
    }
	
	public void clear()
	{
		g.setColor(backgroundColor);
		g.clearRect(0, 0, width, height);
	}
	
	public void setColor(apra.trainsgame.Color color)
	{
		g.setColor(color.getColor());
	}
	
	public void setBackgroundColor(apra.trainsgame.Color color)
	{
		backgroundColor = color.getColor();
	}
	
	public void fillRect(Rectangle r)
	{
		g.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
}
