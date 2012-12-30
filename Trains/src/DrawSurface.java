import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;


public class DrawSurface extends Canvas {
	public static final int BLACK = Color.BLACK.getRGB() + 255;
	public static final int RED = Color.RED.getRGB() + 255;
	public static final int BLUE = Color.BLUE.getRGB() + 255;
	public static final int GREEN = Color.GREEN.getRGB() + 255;
	public static final int YELOW = Color.YELLOW.getRGB() + 255;
	public static final int WHITE = Color.WHITE.getRGB() + 255;
	
	private Graphics g;
	private Color backgroundColor;
	private int width;
	private int height;
	
	private JFrame frame;
	
	public DrawSurface(int width, int height, int backgroundColor)
	{
		super();
		
		this.width = width;
		this.height = height;
		
		frame = new JFrame("window");
    	frame.setSize(width,height);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(this);
    	frame.setVisible(true);
    	
    	this.backgroundColor = new Color(backgroundColor);
    	
    	this.createBufferStrategy (2);
    	this.g = getBufferStrategy().getDrawGraphics();
	}
	
	public void addKeyListener (ConcreteKeyListener listener)
    {
    	frame.addKeyListener (listener);
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
		g.fillRect(0, 0, width, height);
	}
	
	public void setColor(int color)
	{
		g.setColor(new Color(color));
	}
	
	public void setBackgroundColor(int color)
	{
		backgroundColor = new Color(color);
	}
	
	public void fillRect(Rectangle r)
	{
		g.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
}
