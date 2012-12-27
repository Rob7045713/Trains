import java.awt.Point;

public class VectorDirection {
	public static final Point UP = new Point(0, -1);
	public static final Point RIGHT = new Point(1, 0);
	public static final Point DOWN = new Point(0, 1);
	public static final Point LEFT = new Point(-1, 0);
	
	public static Direction DirectionFromPoint(Point point)
	{
		double x = point.getX();
		double y = point.getY();
		
		Point dir = new Point((int) Math2.sign(x), (int) Math2.sign(y));
		
		if (dir.equals(UP))
		{
			return Direction.UP; 
		}
		else if (dir.equals(RIGHT))
		{
			return Direction.RIGHT;
		}
		else  if (dir.equals(DOWN))
		{
			return Direction.DOWN;
		}
		else
		{
			return Direction.LEFT;
		}
	}
}
