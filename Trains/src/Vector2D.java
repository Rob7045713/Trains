
public class Vector2D {
	public float x;
	public float y;
	
	public Vector2D(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setValue(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void add(float scalar)
	{
		this.x += scalar;
		this.y += scalar;
	}
	
	public void add(float x, float y)
	{
		this.x += x;
		this.y += y;
	}
	
	public void add(Vector2D vector)
	{
		this.x += vector.x;
		this.y += vector.y;
	}
	
	public void mult(float scalar)
	{
		this.x *= scalar;
		this.y *= scalar;
	}
	
	public void mult(float x, float y)
	{
		this.x *= x;
		this.y *= y;
	}
	
	public void mult(Vector2D vector)
	{
		this.x *= vector.x;
		this.y *= vector.y;
	}
	
	public float dot(Vector2D v)
	{
		return x * v.x + y * v.y;
	}
}
