import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Input manager for a game. Manages key bindings and executing commands.
 * 
 * @author Rob Argue
 */
public class InputManager {
	private HashMap<Integer, Action> keyMapping;
	private ConcreteKeyListener listener;

	/**
	 * Constructor for an input manager.
	 */
	public InputManager(ConcreteKeyListener listener)
	{
		this.listener = listener;
		keyMapping = new HashMap<Integer, Action>();
	}
	
	/**
	 * Register a key to action mapping with the input manager.
	 * 
	 * @param key Key number to bind the action to
	 * @param action <code>Action</code> to execute with keypress
	 */
	public void register(Integer key, Action action)
	{
		keyMapping.put(key, action);
	}
	
	/**
	 * Executes the action associated with a key.
	 * 
	 * @param key Key to execute the action for
	 */
	public void execute(Integer key)
	{
		if (keyMapping.containsKey(key))
		{
			keyMapping.get(key).Execute();
		}
	}

	/**
	 * Respond to all input.
	 */
	public void executeInput()
	{
		Iterator<KeyEvent> iterator = listener.getKeyEventsIterator();
		while (iterator.hasNext())
		{
			execute(iterator.next().getKeyCode());
		}
	}
}
