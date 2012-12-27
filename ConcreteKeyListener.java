import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ConcreteKeyListener implements KeyListener
{
    ArrayList<KeyEvent> keyEvents;

    public ConcreteKeyListener ()
    {
    	keyEvents = new ArrayList<KeyEvent> ();
    }

    public Iterator<KeyEvent> getKeyEventsIterator ()
    {
    	Iterator<KeyEvent> iterator = keyEvents.iterator ();
		return iterator;
    }

    public void clear ()
    {
    	keyEvents.clear ();	
    }

    public void keyPressed (KeyEvent aKeyEvent)
    {
    	keyEvents.add (aKeyEvent);
    }

    public void keyTyped (KeyEvent e)
    {
    }

    public void keyReleased (KeyEvent e)
    {
    }
}