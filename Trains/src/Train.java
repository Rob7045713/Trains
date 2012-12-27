import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Rectangle;
import java.lang.Integer;
import java.lang.Double;

public class Train
{
	public static final double BASE_SPEED = .0025;
	public static final float MIN_SPEED = .0025f;
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
    
    private ArrayDeque<Direction> myHeadings;
    private ArrayDeque<Double> myLengths;

    private double myEngineX;
    private double myEngineY;

    private double myEngineSpeed;
    private double myKabooseSpeed;

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
    	
    	Vector2D segment = new Vector2D(direction);
    	segment.mult(length);
    	
    	segments = new ArrayDeque<Vector2D>();
    	segments.add(segment);
    }
    
    
    public void update(long elapsed)
    {
    	updateAccelerations();
    	updatePositions(elapsed);
    }
    
    private void updatePositions(long elapsed)
    {
    	// calculate new speed
    	headSpeed += headAcceleration * elapsed;
    	
    	// calculate change in position and update head position
    	Vector2D deltaPos = (new Vector2D(direction)).mult(headSpeed * elapsed);
    	headPosition = headPosition.add(deltaPos);
    	
    	// check if direction has changed => push new segment
    	if (!direction.equals(segments.getFirst().norm().mult(-1.0f)))
    	{
    		segments.addFirst(new Vector2D(0, 0));
    	}
    	
    	// update the first segment
    	Vector2D newVec = segments.removeFirst().add(deltaPos);
    	segments.addFirst(newVec);
    	
    	// calculate new tail speed
    	tailSpeed += tailAcceleration * elapsed;
    	
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
    				// TODO player lost
    			}
    		}
    		else	// segment is longer => shrink
    		{
    			float newLength = segment.magnitude() - deltaLength;
    			segments.addLast(segment.norm().mult(newLength));
    		}
    	}
    }
    
    private void updateAccelerations()
    {
    	
    }
    
    public int checkCollisions(Rectangle r)
    {
    	return checkCollisions(r, 0);
    }
    
    private int checkCollisions(Rectangle r, int begin)
    {
    	int numCollisions = 0;
    	
    	// TODO this
    	
    	return numCollisions;
    }
    
    public ArrayList<Rectangle> getRectangles()
    {
    	ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
    	Vector2D pos = new Vector2D(headPosition);
    	
    	for (Vector2D segment : segments)
    	{
    		// TODO this
    		Rectangle r = new Rectangle();
    	}
    	
    	return rectangles;
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