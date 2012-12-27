
public class Math2 {
	public static double sign(double a)
	{
		return a / Math.abs(a);
	}
	
	public static float sign(float a)
	{
		return a / Math.abs(a);
	}
	
	public static int sign(int a)
	{
		return a / Math.abs(a);
	}
	
	public static long sign(long a)
	{
		return a / Math.abs(a);
	}
	
	public static float dot(Vector2D a, Vector2D b)
	{
		return a.x * b.x + a.y * b.y;
	}
}
