
public class Vector2D {
	public static final float DELTA = (float) 1e-10;
	
	public float x;
	public float y;
	
	public Vector2D(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2D(Vector2D vector)
	{
		this.x = vector.x;
		this.y = vector.y;
	}
	
	public Vector2D copy()
	{
		return new Vector2D(this);
	}
	
	public void setValue(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float magnitude()
	{
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public Vector2D add(float scalar)
	{
		return new Vector2D(x + scalar, y + scalar);
	}
	
	public Vector2D add(float x, float y)
	{
		return new Vector2D(this.x + x, this.y + y);
	}
	
	public Vector2D add(Vector2D vector)
	{
		return new Vector2D(this.x + vector.x, this.y + vector.y);
	}
	
	public Vector2D mult(float scalar)
	{
		return new Vector2D(this.x * scalar, this.y * scalar);
	}
	
	public Vector2D mult(float x, float y)
	{
		return new Vector2D(this.x * x, this.y * y);
	}
	
	public Vector2D mult(Vector2D vector)
	{
		return new Vector2D(this.x * vector.x, this.y * vector.y);
	}
	
	public float dot(Vector2D v)
	{
		return x * v.x + y * v.y;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj.getClass() == this.getClass())
		{
			Vector2D v = (Vector2D) obj;
			if (Math.abs(x - v.x) < DELTA && Math.abs(y - v.y) < DELTA)
				return true;
		}
		
		return false;
	}
	
	public Vector2D norm()
	{
		float m = magnitude();
		return new Vector2D(x / m, y / m);
	}
}
