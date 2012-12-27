import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class CanvasHolder extends Canvas
{
    private ArrayList<Rectangle> rects;
    private ArrayList<ColoredPolygon> polys;
    private JFrame frame;
    int myWidth;
    int myHeight;
    int repaints = 0;

    public CanvasHolder (int width, int height)
    {
	super ();
	rects  = new ArrayList<Rectangle> ();
	polys = new ArrayList<ColoredPolygon> ();
	frame = new JFrame("window");
	frame.setSize(width,height);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(this);
	frame.setVisible(true);
	myWidth = width;
	myHeight = height;
	this.createBufferStrategy (2);
    }

    public void addKeyListener (ConcreteKeyListener listener)
    {
	frame.addKeyListener (listener);
    }

    public void add (Rectangle rect, Color color)
    {
	int [] x = { rect.x, rect.x + rect.width, rect.x + rect.width, rect.x };
	int [] y = { rect.y, rect.y, rect.y + rect.height, rect.y + rect.height };
	polys.add (new ColoredPolygon (x, y, 4, color));
	rects.add (rect);
    }

    public void add (ColoredPolygon poly)
    {
	polys.add (poly);
    }

    public void clear ()
    {
	rects.clear ();
	polys.clear ();
    }

    public void showNow ()
    {	
	Graphics g = this.getBufferStrategy ().getDrawGraphics ();
	g.clearRect(0,0,myWidth,myHeight);
	for (int i = 0; i < polys.size (); i++)
	{
	    ColoredPolygon poly = polys.get(i);
	    g.setColor (poly.getColor ());
	    g.fillPolygon(poly);
	}
	g.dispose ();
	this.getBufferStrategy().show ();
	/*for (int i = 0; i < rects.size (); i++)
	{
	    Rectangle rect = rects.get(i);
	    g.setColor (rect.getColor ());
	    g.fillRect(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight());
	    }*/
	//Toolkit.getDefaultToolkit().sync();
	repaints++;
	if (repaints%60 == 0) System.out.println ("60 paints");
	if (rects.size () == 0) System.out.println ("painting blank!");
    }

    public void paint (Graphics g2)
    { 
	Graphics g = this.getBufferStrategy ().getDrawGraphics ();
	g.clearRect(0,0,myWidth,myHeight);
	for (int i = 0; i < polys.size (); i++)
	{
	    ColoredPolygon poly = polys.get(i);
	    g.setColor (poly.getColor ());
	    g.fillPolygon(poly);
	}
	g.dispose ();
	this.getBufferStrategy().show ();
	Toolkit.getDefaultToolkit().sync();
	repaints++;
	if (repaints%60 == 0) System.out.println ("60 paints");
	if (rects.size () == 0) System.out.println ("painting blank!");
    }

    public static void main (String [] args)
    {
	System.out.println ("Yo, world. Whaddup? Did my frame work?");

	CanvasHolder holder = new CanvasHolder (1280,720);

	System.out.println ("I'm done.");

    }

}