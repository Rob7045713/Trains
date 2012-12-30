package apra.trainsgame.pc;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import apra.trainsgame.Action;
import apra.trainsgame.InputManager;
import apra.trainsgame.Player;
import apra.trainsgame.TrainGame;
import apra.trainsgame.Vector2D;
import apra.trainsgame.VectorDirection;

/**
 * Input manager for a game. Manages key bindings and executing commands.
 * 
 * @author Rob Argue
 */
public class PCInputManager implements InputManager {
	private enum Layout {QWERTY, DVORAK}
    private static final Layout KEYBOARD_LAYOUT = Layout.QWERTY;
	
	private HashMap<Integer, Action> keyMapping;
	private ConcreteKeyListener listener;

	/**
	 * Constructor for an input manager.
	 */
	public PCInputManager()
	{
		listener = new ConcreteKeyListener();
		keyMapping = new HashMap<Integer, Action>();
	}
	
	public ConcreteKeyListener getListener()
	{
		return listener;
	}
	
	/**
     * Initialize all key bindings for the game.
     */
    public void initKeyBindings(TrainGame game)
    {
    	try {
    		
    		// bind player keys
    		if (KEYBOARD_LAYOUT == Layout.QWERTY)
    		{
    			bindPlayerKeys(game.getPlayers().get(0), KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D);
    		}
    		if (KEYBOARD_LAYOUT == Layout.DVORAK)
    		{
    			bindPlayerKeys(game.getPlayers().get(0), KeyEvent.VK_COMMA, KeyEvent.VK_O, KeyEvent.VK_A, KeyEvent.VK_E);
    		}
    		bindPlayerKeys(game.getPlayers().get(1), KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
			
    		// bind general keys
			Method setOver = TrainGame.class.getMethod("setOver", Boolean.class);
			Object[] trueObj = {new Boolean(true)};
			register(KeyEvent.VK_ESCAPE, new Action(game, setOver, trueObj));
			
			Method reset = TrainGame.class.getMethod("init");
			register(KeyEvent.VK_ENTER, new Action(game, reset, new Object [0]));
			
		} catch (NoSuchMethodException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		}
    	
    }

    /**
     * Initialize key bindings for a particular player. Intended for use with <code>KeyEvent</code> constants.
     * 
     * @param player Player number of the player to bind keys for
     * @param up Key to bind to the up action
     * @param down Key to bind to the down action
     * @param left Key to bind to the left action
     * @param right Key to bind to the right action
     */
    private void bindPlayerKeys(Player player, int up, int down, int left, int right)
    {
    	try {
			Method setDirection = Player.class.getMethod("setDirection", Vector2D.class);
			Object[] upObj = {VectorDirection.UP};
			Object[] rightObj = {VectorDirection.RIGHT};
			Object[] downObj = {VectorDirection.DOWN};
			Object[] leftObj = {VectorDirection.LEFT};
			
			register(up, new Action(player, setDirection, upObj));
			register(left, new Action(player, setDirection, leftObj));
			register(down, new Action(player, setDirection, downObj));
			register(right, new Action(player, setDirection, rightObj));
			
		} catch (NoSuchMethodException e) {
			System.err.println("Error: Player (" + player + ") Key binding failure");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Error: Player (" + player + ") Key binding failure");
			e.printStackTrace();
		}
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
	public void doInput(TrainGame game)
	{
		Iterator<KeyEvent> iterator = listener.getKeyEventsIterator();
		while (iterator.hasNext())
		{
			execute(iterator.next().getKeyCode());
		}
	}
}
