package apra.trainsgame.pc;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

// TODO combine this with InputManager
public class ConcreteMouseListener implements MouseListener
{
    ArrayList<MouseEvent> mouseEvents;

    public ConcreteMouseListener ()
    {
    	mouseEvents = new ArrayList<MouseEvent> ();
    }

    public Iterator<MouseEvent> getMouseEventsIterator ()
    {
    	synchronized (this)
    	{
    		ArrayList<MouseEvent> keys = new ArrayList<MouseEvent>();
    		keys.addAll(mouseEvents); 	
    		mouseEvents.clear();
    		return keys.iterator();
    	}
    }

    public void clear ()
    {
    	synchronized (this) 
    	{
    		mouseEvents.clear ();	
    	}
    }

    public void mousePressed (MouseEvent aMouseEvent)
    {
	System.out.println ("ConcreteMouseListener says mouse has been pressed!");
    	synchronized (this) 
    	{
    		mouseEvents.add (aMouseEvent);
    	}
    }

    public void mouseClicked (MouseEvent event){
    }

    public void mouseEntered (MouseEvent event){
    }

    public void mouseExited (MouseEvent event){
    }

    public void mouseReleased (MouseEvent event){
    }
}