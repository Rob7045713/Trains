import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Rectangle;
import java.lang.Integer;
import java.lang.Double;

public class Train
{
	public static final double BASE_SPEED = .0025;
	public static final float MIN_SPEED = .0025f * 60;
	public static final float ACCEL = .00001f * 60;
    public static final double ACCELERATION = .00001;
    public static final double COLLISION_WIDTH = .002;
    public static final double ACCELERATION_WIDTH = .04;

    private ArrayDeque<Vector2D> segments;
    private Vector2D headPosition;
    private Vector2D direction;
    private float headSpeed;
    private float tailSpeed;
    private float headAcceleration;
    private float tailAcceleration;
    private boolean isDead;
    
    // TODO remove this section
    private ArrayDeque<Direction> myHeadings;
    private ArrayDeque<Double> myLengths;

    private double myEngineX;
    private double myEngineY;

    private double myEngineSpeed;
    private double myKabooseSpeed;
    // up to here

    public Train()
    {
    	this(0.0, 0.0, Direction.RIGHT, 0.0);
    }
    
    public Train (double X, double Y, Direction heading, double length)
    {
    	myEngineX = X;
    	myEngineY = Y;
    	myHeadings = new ArrayDeque<Direction> ();
    	myLengths = new ArrayDeque<Double> ();
    	myHeadings.addFirst(heading);
    	myLengths.addFirst (new Double (length));
    	myEngineSpeed = BASE_SPEED;
    	myKabooseSpeed = BASE_SPEED;
    }

    public Train(Vector2D position, Vector2D direction, float length)
    {
    	this.headPosition = position;
    	this.direction = direction;
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
    
    public void setDead(boolean isDead)
    {
    	this.isDead = isDead;
    }
      
    public boolean isDead()
    {
    	return isDead;
    }
 
    public Vector2D getPosition()
    {
    	return headPosition;
    }
    
    public void update(long elapsed, Trains game)
    {
    	updateAccelerations(game);
    	updatePositions(elapsed);
    	
    	for (Train player : game.getPlayers())
    	{
    		if (checkCollisions(player) > 0)
    		{
    			isDead = true;
    		}
    	}
    }
    
    private void updatePositions(long elapsed)
    {
    	// calculate new speed
    	headSpeed += headAcceleration * elapsed;
    
    	if (headSpeed < MIN_SPEED)
    		headSpeed = MIN_SPEED;
    	
    	// calculate change in position and update head position
    	Vector2D deltaPos = (direction).mult(headSpeed * elapsed);
    	headPosition = headPosition.add(deltaPos);
    	
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
    
    private void updateAccelerations(Trains game)
    {
    	int accelCollisions = 0;
    	
    	int ppu = Trains.PIXELS_PER_UNIT;
		int width = (int) (ACCELERATION_WIDTH * ppu);
		int x = (int) (ppu * (headPosition.x - ACCELERATION_WIDTH / 2));
		int y = (int) (ppu * (headPosition.y - ACCELERATION_WIDTH / 2));
		Rectangle r = new Rectangle(x, y, width, width);
    	
    	for (Train player : game.getPlayers())
    	{
    		if (player != this)
    			accelCollisions += player.checkCollisions(r);
    	}
    	
    	if (accelCollisions > 0)
    	{
    		headAcceleration = (float) ACCEL;
    	}
    	else
    	{
    		headAcceleration = (float) (-1 * ACCEL);
    	}
    	
    	Vector2D tailPosition = getTailPosition();
    	x = (int) (ppu * (tailPosition.x - ACCELERATION_WIDTH / 2));
		y = (int) (ppu * (tailPosition.y - ACCELERATION_WIDTH / 2));
		r = new Rectangle(x, y, width, width);
    	
		accelCollisions = 0;
		
    	for (Train player : game.getPlayers())
    	{
    		if (player != this)
    			accelCollisions += player.checkCollisions(r);
    	}
    	
    	if (accelCollisions > 0)
    	{
    		tailAcceleration = (float) ACCEL;
    	}
    	else
    	{
    		tailAcceleration = (float) (-1 * ACCEL);
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
    	
    	while (begin > 0)
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
    	return checkCollisions(train, false);
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
    		int ppu = Trains.PIXELS_PER_UNIT;
    		
    		Rectangle r = new Rectangle((int) (ppu * topLeft.x), (int) (ppu * topLeft.y),
    				(int) (ppu * width), (int) (ppu * height));
    		
    		rectangles.add(r);
    	}
    	
    	return rectangles;
    }
    
    // TODO probably remove this
    public int checkCollisions(Train train, boolean checkAcceleration)
    {
    	int begin = 0;
    	
    	if (train == this)
    	{
    		begin = 2;
    		
    		if (checkAcceleration)
    		{
    			return 0;
    		}
    	}
    	
    	Rectangle r = train.getRectangles().get(0);
    	
    	if (checkAcceleration)
    	{
    		int ppu = Trains.PIXELS_PER_UNIT;
    		int width = (int) (ACCELERATION_WIDTH * ppu);
    		int x = (int) (ppu * (headPosition.x - ACCELERATION_WIDTH / 2));
    		int y = (int) (ppu * (headPosition.y - ACCELERATION_WIDTH / 2));
    		r = new Rectangle(x, y, width, width);
    	}
    	
    	return checkCollisions(r, begin);
    }
   
    public Direction getHeading ()
    {
    	return myHeadings.getFirst();
    }

    public Iterator<Direction> getHeadingsIterator ()
    {
    	return myHeadings.iterator ();
    }

    public Iterator<Double> getLengthsIterator ()
    {
	return myLengths.iterator ();
    }

    public double getX ()
    {
    	return myEngineX;
    }
    
    public void setX(double x)
    {
    	this.myEngineX = x;
    }

    public double getY ()
    {
    	return myEngineY;
    }
    
    public void setY(double y)
    {
    	this.myEngineY = y;
    }
    
    public void setLength(double length)
    {
    	this.myLengths.clear();
    	this.myLengths.addFirst(new Double(length));
    }

    public void initHeading(Direction heading)
   {
	   myHeadings.clear();
	   myHeadings.addFirst(heading);
	   myEngineSpeed = BASE_SPEED;
	   myKabooseSpeed = BASE_SPEED;
   }
    
    public void setHeading(Direction heading)
    {
    	if (heading.ordinal() % 2 != myHeadings.getFirst().ordinal() % 2)
	    {
    		myHeadings.addFirst(heading);
    		myLengths.addFirst(new Double (0));
	    }
    }

    public boolean crashedInto(Train train)
    {
    	//first get the piece of newly laid train
    	double engineX = this.getX ();
    	double engineY = this.getY ();
    	double length = myKabooseSpeed;
    	Direction heading = myHeadings.getFirst();
    	double x = 0;
    	double y = 0;
    	double width = 0;
    	double height = 0;
    	//define a rectangle for this thing
    	switch (heading)
	    {
	    case UP:
	    	x=engineX-COLLISION_WIDTH/2;
	    	y=engineY-COLLISION_WIDTH/2;
	    	width = COLLISION_WIDTH;
	    	height = length+COLLISION_WIDTH;
	    	break;
	    case RIGHT:
	    	x=engineX-length-COLLISION_WIDTH/2;
	    	y=engineY-COLLISION_WIDTH/2;
	    	width = length+COLLISION_WIDTH;
	    	height = COLLISION_WIDTH;
	    	break;
	    case DOWN:
	    	x=engineX-COLLISION_WIDTH/2;
	    	y=engineY-length-COLLISION_WIDTH/2;
	    	width = COLLISION_WIDTH;
	    	height = length+COLLISION_WIDTH;
	    	break;
	    case LEFT:
	    	x=engineX -COLLISION_WIDTH/2;
	    	y=engineY-COLLISION_WIDTH/2;
	    	width = length+COLLISION_WIDTH;
	    	height = COLLISION_WIDTH;
	    }
    	
    	Iterator<Direction> headings = train.getHeadingsIterator();
    	Iterator<Double> lengths = train.getLengthsIterator();
    	double thatEngineX = train.getX();
    	double thatEngineY = train.getY();
    	Direction thatHeading;
    	double thatLength = 0;
    	double thatX = 0;
    	double thatY = 0;
    	double thatWidth = 0;
    	double thatHeight = 0;
    	boolean crashed = false;
    	if (this==train) for (int i = 0; i<2&&headings.hasNext (); i++)
    	{
    		Direction aHeading = headings.next();
    		double aLength = lengths.next().doubleValue();
    		switch (aHeading)
		    {
		    case UP:
		    	thatEngineY+=aLength;
		    	break;
		    case RIGHT:
		    	thatEngineX-=aLength;
		    	break;
		    case DOWN:
		    	thatEngineY-=aLength;
		    	break;
		    case LEFT:
		    	thatEngineX+=aLength;
		    }
	    }
    	
    	while (headings.hasNext())
	    {
    		thatHeading = headings.next();
    		thatLength = lengths.next().doubleValue();
    		switch (thatHeading)
		    {
		    case UP:
		    	thatX=thatEngineX-COLLISION_WIDTH/2;
		    	thatY=thatEngineY-COLLISION_WIDTH/2;
		    	thatWidth = COLLISION_WIDTH;
		    	thatHeight = thatLength+COLLISION_WIDTH;
		    	thatEngineY+=thatLength;
		    	break;
		    case RIGHT:
		    	thatX=thatEngineX-thatLength-COLLISION_WIDTH/2;
		    	thatY=thatEngineY-COLLISION_WIDTH/2;
		    	thatWidth = thatLength+COLLISION_WIDTH;
		    	thatHeight = COLLISION_WIDTH;
		    	thatEngineX-=thatLength;
		    	break;
		    case DOWN:
		    	thatX=thatEngineX-COLLISION_WIDTH/2;
		    	thatY=thatEngineY-thatLength-COLLISION_WIDTH/2;
		    	thatWidth = COLLISION_WIDTH;
		    	thatHeight = thatLength+COLLISION_WIDTH;
		    	thatEngineY-=thatLength;
		    	break;
		    case LEFT:
		    	thatX=thatEngineX-COLLISION_WIDTH/2;
		    	thatY=thatEngineY-COLLISION_WIDTH/2;
		    	thatWidth = thatLength+COLLISION_WIDTH;
		    	thatHeight = COLLISION_WIDTH;
		    	thatEngineX+=thatLength;
		    }
    		
    		if(x+width>thatX&&x<thatX+thatWidth&&y+height>thatY&&y<thatY+thatHeight)
		    {
    			crashed = true;
		    }
	    }
    	return crashed;
    }

    public void step()
    {
    	double firstLength = myLengths.getFirst ().doubleValue();
    	myLengths.removeFirst ();
    	myLengths.addFirst (new Double (firstLength+myEngineSpeed));
    	double lastLength = myLengths.getLast ().doubleValue();
    	if (lastLength < myKabooseSpeed)
	    {
    		myLengths.removeLast ();
    		myHeadings.removeLast ();
    		lastLength += myLengths.getLast ().doubleValue();
	    }
    	
    	myLengths.removeLast ();
    	myLengths.addLast (new Double (lastLength-myKabooseSpeed));
	
    	switch (myHeadings.getFirst())
    	{
    	case UP:
    		myEngineY-=myEngineSpeed;
    		break;
    	case RIGHT:
    		myEngineX+=myEngineSpeed;
    		break;
    	case DOWN:
    		myEngineY+=myEngineSpeed;
    		break;
    	case LEFT:
    		myEngineX-=myEngineSpeed;
    	};
    }

    public boolean isGrinding (double x, double y, Direction heading, Train train)
    {
    	Iterator<Direction> headings = train.getHeadingsIterator ();
    	Iterator<Double> lengths = train.getLengthsIterator ();
    	Direction thatHeading;
    	double thatLength = 0;
    	double thatEngineX = train.myEngineX;
    	double thatEngineY = train.myEngineY;
    	double thatX = 0 ;
    	double thatY = 0;
    	double thatWidth = 0;
    	double thatHeight = 0;
    	boolean grinding = false;
    	while (headings.hasNext ())
	    {
    		thatHeading = headings.next();
    		thatLength = lengths.next().doubleValue ();
    		
    		switch (thatHeading)
    		{
    		case UP:
    			thatX=thatEngineX-COLLISION_WIDTH/2;
    			thatY=thatEngineY-COLLISION_WIDTH/2;
    			thatWidth = COLLISION_WIDTH;
    			thatHeight = thatLength+COLLISION_WIDTH;
    			thatEngineY+=thatLength;
    			break;
    		case RIGHT:
    			thatX=thatEngineX-thatLength-COLLISION_WIDTH/2;
    			thatY=thatEngineY-COLLISION_WIDTH/2;
    			thatWidth = thatLength+COLLISION_WIDTH;
    			thatHeight = COLLISION_WIDTH;
    			thatEngineX-=thatLength;
    			break;
    		case DOWN:
    			thatX=thatEngineX-COLLISION_WIDTH/2;
    			thatY=thatEngineY-thatLength-COLLISION_WIDTH/2;
    			thatWidth = COLLISION_WIDTH;
    			thatHeight = thatLength+COLLISION_WIDTH;
				thatEngineY-=thatLength;
				break;
    		case LEFT:
    			thatX=thatEngineX-COLLISION_WIDTH/2;
    			thatY=thatEngineY-COLLISION_WIDTH/2;
    			thatWidth = thatLength+COLLISION_WIDTH;
    			thatHeight = COLLISION_WIDTH;
    			thatEngineX+=thatLength;
    		}
		
    		if((heading.ordinal() % 2 == 0
    				&& x+ACCELERATION_WIDTH/2>thatX
    				&& x-ACCELERATION_WIDTH/2<thatX+thatWidth
    				&& y>thatY
    				&& y<thatY+thatHeight) ||
    				(heading.ordinal() % 2 == 1
    				&& x>thatX
    				&& x<thatX+thatWidth
    				&& y+ACCELERATION_WIDTH/2>thatY
    				&& y-ACCELERATION_WIDTH/2<thatY+thatHeight))
    		{
    			grinding = true;
		    }
	    }
    	return grinding;
    }

    public void doAcceleration (Train train)
    {
    	double x = myEngineX;
    	double y = myEngineY;
    	Direction heading = myHeadings.getFirst ();
    	if (isGrinding(x,y,heading,train))
    		myEngineSpeed+=ACCELERATION;
    	else if (myEngineSpeed > BASE_SPEED)
    		myEngineSpeed-=ACCELERATION;
    	else if (myEngineSpeed < BASE_SPEED)
    		myEngineSpeed=BASE_SPEED;	
    	Iterator<Direction> headings = myHeadings.iterator();
    	Iterator<Double> lengths = myLengths.iterator();
    	
    	while (headings.hasNext())
    	{		
    		heading = headings.next();
    		double aLength = lengths.next().doubleValue();
    		
    		switch (heading)
		    {
		    case UP:
		    	y+=aLength;
		    	break;
		    case RIGHT:
		    	x-=aLength;
		    	break;
		    case DOWN:
		    	y-=aLength;
		    	break;
		    case LEFT:
		    	x+=aLength;
		    }
	    }

    	if (isGrinding(x,y,heading,train))
    		myKabooseSpeed+=ACCELERATION;
    	else if (myKabooseSpeed > BASE_SPEED)
    		myKabooseSpeed-=ACCELERATION;
    	else if (myKabooseSpeed < BASE_SPEED)
    		myKabooseSpeed=BASE_SPEED;
   	}
}