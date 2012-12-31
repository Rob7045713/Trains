package apra.trainsgame.pc;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import apra.trainsgame.Action;
import apra.trainsgame.GameController.GameAction;
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
	
	private HashMap<Integer, GameAction> keyMapping;
	private ConcreteKeyListener listener;

	/**
	 * Constructor for an input manager.
	 */
	public PCInputManager()
	{
		listener = new ConcreteKeyListener();
		keyMapping = new HashMap<Integer, GameAction>();
		initKeyBindings();
	}
	
	public ConcreteKeyListener getListener()
	{
		return listener;
	}
	
	/**
     * Initialize all key bindings for the game.
     */
    private void initKeyBindings()
    {
    	// bind player keys
    	if (KEYBOARD_LAYOUT == Layout.QWERTY)
    	{
    		keyMapping.put(KeyEvent.VK_W, GameAction.PLAYER_1_UP);
    		keyMapping.put(KeyEvent.VK_S, GameAction.PLAYER_1_DOWN);
    		keyMapping.put(KeyEvent.VK_A, GameAction.PLAYER_1_LEFT);
    		keyMapping.put(KeyEvent.VK_D, GameAction.PLAYER_1_RIGHT);
    	}
    	if (KEYBOARD_LAYOUT == Layout.DVORAK)
    	{
    		keyMapping.put(KeyEvent.VK_COMMA, GameAction.PLAYER_1_UP);
    		keyMapping.put(KeyEvent.VK_O, GameAction.PLAYER_1_DOWN);
    		keyMapping.put(KeyEvent.VK_A, GameAction.PLAYER_1_LEFT);
    		keyMapping.put(KeyEvent.VK_E, GameAction.PLAYER_1_RIGHT);
    	}
    	
    	keyMapping.put(KeyEvent.VK_UP, GameAction.PLAYER_2_UP);
    	keyMapping.put(KeyEvent.VK_DOWN, GameAction.PLAYER_2_DOWN);
    	keyMapping.put(KeyEvent.VK_LEFT, GameAction.PLAYER_2_LEFT);
    	keyMapping.put(KeyEvent.VK_RIGHT, GameAction.PLAYER_2_RIGHT);
    	
    	// bind general keys
    	keyMapping.put(KeyEvent.VK_ESCAPE, GameAction.GAME_END);
    	keyMapping.put(KeyEvent.VK_ENTER, GameAction.GAME_RESTART);    	
    }

	/**
	 * Respond to all input.
	 */
	public void doInput(TrainGame game)
	{
		Iterator<KeyEvent> iterator = listener.getKeyEventsIterator();
		while (iterator.hasNext())
		{
			int key = iterator.next().getKeyCode();
			if (keyMapping.containsKey(key))
			{
				game.getGameController().doAction(keyMapping.get(key));
			}
		}
	}
}
