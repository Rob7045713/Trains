import java.awt.Color;

public class Rectangle2
{
    private int myX, myY, myWidth, myHeight;
    private Color myColor;

    public Rectangle2 (int x, int y, int width, int height, Color color)
    {
	myX = x;
	myY = y;
	myWidth = width;
	myHeight = height;
	myColor = color;
    }

    public int getX () {return myX;}
    public int getY () {return myY;}
    public int getWidth () {return myWidth;}
    public int getHeight () {return myHeight;}
    public Color getColor () {return myColor;}
}