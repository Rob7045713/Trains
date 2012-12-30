package apra.trainsgame.pc;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

// TODO combine this with InputManager
public class ConcreteKeyListener implements KeyListener
{
    ArrayList<KeyEvent> keyEvents;

    public ConcreteKeyListener ()
    {
    	keyEvents = new ArrayList<KeyEvent> ();
    }

    public Iterator<KeyEvent> getKeyEventsIterator ()
    {
    	synchronized (this)
    	{
    		ArrayList<KeyEvent> keys = new ArrayList<KeyEvent>();
    		keys.addAll(keyEvents); 	
    		keyEvents.clear();
    		return keys.iterator();
    	}
    }

    public void clear ()
    {
    	synchronized (this) 
    	{
    		keyEvents.clear ();	
    	}
    }

    public void keyPressed (KeyEvent aKeyEvent)
    {
    	synchronized (this) 
    	{
    		keyEvents.add (aKeyEvent);
    	}
    }

    public void keyTyped (KeyEvent e)
    {
    }

    public void keyReleased (KeyEvent e)
    {
    }
}