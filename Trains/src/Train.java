import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Rectangle;
import java.lang.Integer;
import java.lang.Double;
import java.awt.Color;
import java.awt.Graphics;

public class Train
{
	/*
	 * Physics constants
	 */
    public static final float MIN_SPEED 			= .0025f / 16.0f;
    public static final float ACCEL 				= .00001f / (16.0f * 16.0f);
    public static final float COLLISION_WIDTH 		= .002f;
    public static final float ACCEL_WIDTH 			= .08f;
    public static final float DECEL_MODIFIER 		= 1.0f;
    public static final float SELF_ACCEL_MODIFIER 	= -1.0f;
    public static final int MULTI_ACCEL 			= 1;
    
    /*
     * Drawing options
     */
    private static final boolean DRAW_HEAD_BOX = true;
    private static final boolean DRAW_TAIL_BOX = false;

    /*
     * Misc
     */
    public enum End { HEAD, TAIL }
    
    /*
     * Class fields
     */
    private ArrayDeque<Vector2D> segments;
    private Vector2D headPosition;
    private Vector2D direction;
    private float headSpeed;
    private float tailSpeed;
    private float headAcceleration;
    private float tailAcceleration;
    private boolean isDead;
    private Color color;
    
    public Train(Color color)
    {
    	this(new Vector2D(0,0), VectorDirection.RIGHT, 0.0f);
	this.color = color;
    }

    public Train(Vector2D position, Vector2D direction, float length)
    {
    	this.headPosition = position;
    	this.direction = direction.copy();
    	this.headSpeed = MIN_SPEED;
    	this.tailSpeed = MIN_SPEED;
    	this.headAcceleration = 0.0f;
    	this.tailAcceleration = 0.0f;
    	this.isDead = false;
    	
    	Vector2D segment = new Vector2D(direction);
    	segment.mult(length);
    	
    	segments = new ArrayDeque<Vector2D>();
    	segments.add(segment);
    }
    
    public Color getColor()
    {
    	return color;
    }
       
    public void draw(Graphics g)
    {
    	for (Rectangle r : getRectangles())
    	{
    		g.setColor(color);
    		g.fillRect(r.x, r.y, r.width, r.height);
    	}

    	if (DRAW_HEAD_BOX)
	    {
    		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 64));
    		Rectangle r = getEndBox(End.HEAD);
    		g.fillRect(r.x, r.y, r.width, r.height);
	    }
	
    	if (DRAW_TAIL_BOX)
	    {
    		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 32));
    		Rectangle r = getEndBox(End.TAIL);
    		g.fillRect(r.x, r.y, r.width, r.height);
	    }
    }

    public void init(Vector2D position, Vector2D direction, float length)
    {
    	// TODO find out if this is necessary
    	this.headPosition = position;
    	this.direction = direction.copy();
    	this.headSpeed = MIN_SPEED;
    	this.tailSpeed = MIN_SPEED;
    	this.headAcceleration = 0.0f;
    	this.tailAcceleration = 0.0f;
    	this.isDead = false;
    	
    	Vector2D segment = new Vector2D(direction);
    	segment = segment.mult(-1.0f*length);
    	
    	segments = new ArrayDeque<Vector2D>();
    	segments.add(segment);
    	//System.out.println (segment.x + "," + segment.y);
    }
    
    public void setDead(boolean isDead)
    {
    	this.isDead = isDead;
    }
      
    public boolean isDead()
    {
    	return isDead;
    }
 
    public void setDirection(Vector2D direction)
    {
    	if (! (this.direction.equals(direction) || this.direction.mult(-1).equals(direction)))
    	{
    		this.direction = direction.copy();
    	}
    }

    public Vector2D getPosition()
    {
    	return headPosition;
    }
    
    public void update(long elapsed, TrainGame game)
    {
    	updateAccelerations(game);
    	updatePositions(elapsed);

    	// check for player collisions
    	for (Train player : game.getPlayers())
    	{
    		if (player.checkCollisions(this) > 0)
    			isDead = true;
    	}
    	
    	// check for out of bounds
    	if (! game.inBounds(getRectangles().get(0)))
    		isDead = true;
    }
    
    private void updatePositions(long elapsed)
    {
    	// calculate new speed
    	headSpeed += headAcceleration * (float)elapsed;

    	if (headSpeed < MIN_SPEED)
    		headSpeed = MIN_SPEED;
    	
    	// calculate change in position and update head position
    	Vector2D deltaPos = (direction).mult(headSpeed * (float)elapsed);
    	//System.out.println ("direction is " + direction.x + "," + direction.y);
    	//System.out.println ("headSpeed is " + headSpeed + " while elapsed is " + elapsed);
    	//System.out.println (headPosition.x + "," + headPosition.y + " added to " + deltaPos.x + "," + deltaPos.y);
    	headPosition = headPosition.add(deltaPos);
    	//System.out.println (headPosition.x + "," + headPosition.y);
    	
    	// check if direction has changed => push new segment
    	if (!direction.equals(segments.getFirst().norm().mult(-1.0f)))
    	{
    		segments.addFirst(new Vector2D(0, 0));
    	}
    	
    	// update the first segment
    	Vector2D newVec = segments.removeFirst().add(deltaPos.mult(-1.0f));
    	segments.addFirst(newVec);
    	
    	// calculate new tail speed
    	tailSpeed += tailAcceleration * elapsed;
    	
    	if (tailSpeed < MIN_SPEED)
    		tailSpeed = MIN_SPEED;
    	
    	// calculate distance to be covered by the tail
    	float deltaLength = tailSpeed * elapsed;
    	
    	// shorten and remove tail segments as necessary
    	Vector2D segment;
    	while (deltaLength > 0)
    	{
    		segment = segments.removeLast();
    		
    		// segment is shorter => delete
    		if (segment.magnitude() <= deltaLength)
    		{
    			deltaLength -= segment.magnitude();
    			
    			// was the last segment
    			if (segments.isEmpty())
    			{
    				isDead = true;
    				break;
    			}
    		}
    		else	// segment is longer => shrink
    		{
    			float newLength = segment.magnitude() - deltaLength;
    			segments.addLast(segment.norm().mult(newLength));
    			
    			deltaLength = 0;
    		}
    	}
    }
    
    public Rectangle getEndBox(End end)
    {
    	int ppu = TrainGame.PIXELS_PER_UNIT;
		int width = (int) (ACCEL_WIDTH * ppu);
		int x = 0;
		int y = 0;
    	
    	if (end == End.HEAD)
    	{    		
    		x = (int) (ppu * (headPosition.x - ACCEL_WIDTH / 2));
    		y = (int) (ppu * (headPosition.y - ACCEL_WIDTH / 2));
    	}
    	else
    	{
   		Vector2D tailPosition = getTailPosition();
        	x = (int) (ppu * (tailPosition.x - ACCEL_WIDTH / 2));
    		y = (int) (ppu * (tailPosition.y - ACCEL_WIDTH / 2));
    	}
    	
    	return new Rectangle(x, y, width, width);
    }
    
    private void updateAccelerations(TrainGame game)
    {
    	// TODO combine these into a method somehow
    	
    	int accelCollisions = 0;
    	
		Rectangle r = getEndBox(End.HEAD);
    	
    	for (Train player : game.getPlayers())
    	{
    		if (player != this)
    			accelCollisions += player.checkCollisions(r);
    	}
    	
    	if (accelCollisions > 0)
    	{
    		headAcceleration = ACCEL * Math.min(accelCollisions, MULTI_ACCEL); // TODO make this more versitile
    	}
    	else if (checkCollisions(r) > 0 && SELF_ACCEL_MODIFIER >= 0) // TODO figure out this + multi-accel
    	{
    		headAcceleration = ACCEL * SELF_ACCEL_MODIFIER;
    	}
    	else
    	{
    		headAcceleration = (float) (-1 * ACCEL * DECEL_MODIFIER);
    	}
    	
		r = getEndBox(End.TAIL);
    	
		accelCollisions = 0;
		
    	for (Train player : game.getPlayers())
    	{
    		if (player != this)
    			accelCollisions += player.checkCollisions(r);
    	}
    	
    	if (accelCollisions > 0)
    	{
    		tailAcceleration = (float) ACCEL * Math.min(accelCollisions, MULTI_ACCEL); // TODO make this more versitile
    	}
    	else if (checkCollisions(r) > 0 && SELF_ACCEL_MODIFIER >= 0) // TODO figure out this + multi-accel
    	{
    		tailAcceleration = ACCEL * SELF_ACCEL_MODIFIER;
    	}
    	else
    	{
    		tailAcceleration = (float) (-1 * ACCEL * DECEL_MODIFIER);
    	}
    }
    
    public int checkCollisions(Rectangle r)
    {
    	return checkCollisions(r, 0);
    }
    
    private int checkCollisions(Rectangle r, int begin)
    {
    	int numCollisions = 0;
    	
    	ArrayList<Rectangle> rectangles = getRectangles();
    	
    	while (begin > 0 && !rectangles.isEmpty())
    	{
    		rectangles.remove(0);
    		begin--;
    	}
    	
    	for (Rectangle rect : rectangles)
    	{
    		if (r.intersects(rect))
    			numCollisions++;
    	}
    	
    	return numCollisions;
    }
    
    public int checkCollisions(Train train)
    {
    	int begin = (train == this) ? 2 : 0;
    	
    	Rectangle r = train.getRectangles().get(0);
    	
    	return checkCollisions(r, begin);
    }

    public Vector2D getTailPosition()
    {
    	Vector2D tailPosition = headPosition;
    	
    	for (Vector2D v : segments)
    		tailPosition = tailPosition.add(v);
    	
    	return tailPosition;
    }
    
    public ArrayList<Rectangle> getRectangles()
    {	
    	ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
    	Vector2D pos = new Vector2D(headPosition);
    	
    	for (Vector2D segment : segments)
    	{
    		// find the dimensions of the rectangle
    		float width = Math.abs(segment.x) + (float) COLLISION_WIDTH;
    		float height = Math.abs(segment.y) + (float) COLLISION_WIDTH;
    		
    		// find the top-left corner of the rectangle
    		Vector2D bigOffset = new Vector2D(Math.min(segment.x, 0), Math.min(segment.y, 0));
    		Vector2D smallOffset = (new Vector2D(1,1)).mult((float) COLLISION_WIDTH / 2.0f * -1.0f);
    		Vector2D topLeft = pos.add(bigOffset).add(smallOffset);
    		
    		// TODO reconsider this
    		int ppu = TrainGame.PIXELS_PER_UNIT;
    		
    		Rectangle r = new Rectangle((int) (ppu * topLeft.x), (int) (ppu * topLeft.y),
    				(int) (ppu * width), (int) (ppu * height));
    		
    		rectangles.add(r);
		pos = pos.add(segment);
    	}
    	
    	return rectangles;
    }
}