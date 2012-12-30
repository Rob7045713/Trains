package apra.trainsgame;



/**
 * Wrapper class for rectangle to enable quickly switching between PC java and android java
 * 
 * @author Rob Argue
 */
public class Rectangle {
	private java.awt.Rectangle rectangle;
	
	public Rectangle(int x, int y, int width, int height)
	{
		rectangle = new java.awt.Rectangle(x, y, width, height);
	}
	
	public java.awt.Rectangle getRectangle()
	{
		return rectangle;
	}
	
	public boolean intersects(Rectangle r)
	{
		return rectangle.intersects(r.getRectangle());
	}
	
	public boolean contains(Rectangle r)
	{
		return rectangle.contains(r.getRectangle());
	}
	
	public int getX()
	{
		return rectangle.x;
	}
	
	public void setX(int x)
	{
		rectangle.x = x;
	}

	public int getY()
	{
		return rectangle.y;
	}
	
	public void setY(int y)
	{
		rectangle.y = y;
	}
	
	public int getWidth()
	{
		return rectangle.width;
	}
	
	public void setWidth(int width)
	{
		rectangle.width = width;
	}
	
	public int getHeight()
	{
		return rectangle.height;
	}
	
	public void setHeight(int height)
	{
		rectangle.height = height;
	}
}
