import java.awt.Color;

public class ColoredPolygon extends java.awt.Polygon
{
    Color myColor;

    ColoredPolygon (int [] x, int [] y, int n, Color color)
    {
	super(x,y,n);
	myColor = color;
    }

    public Color getColor ()
    {
	return myColor;
    }
}