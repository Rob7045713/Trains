package apra.trainsgame;

public class Color {
	public static final Color RED = new Color(java.awt.Color.RED);
	public static final Color BLUE = new Color(java.awt.Color.BLUE);
	public static final Color GREEN = new Color(java.awt.Color.GREEN);
	public static final Color YELOW = new Color(java.awt.Color.YELLOW);
	public static final Color WHITE = new Color(java.awt.Color.WHITE);
	public static final Color BLACK = new Color(java.awt.Color.BLACK);

	private java.awt.Color color;
	
	public Color(int color)
	{
		this.color = new java.awt.Color(color);
	}
	
	private Color(java.awt.Color color)
	{
		this.color = color;
	}
		
	public Color(int r, int g, int b)
	{
		this.color = new java.awt.Color(r, g, b);
	}
	
	public Color(int a, int r, int g, int b)
	{
		this.color = new java.awt.Color(r, g, b, a);
	}

	public int getRed()
	{
		return color.getRed();
	}
	
	public int getBlue()
	{
		return color.getBlue();
	}
	
	public int getGreen()
	{
		return color.getGreen();
	}
	
	public int getAlpha()
	{
		return color.getAlpha();
	}
	
	public java.awt.Color getColor()
	{
		return color;
	}
}
