package apra.trainsgame;

import java.lang.reflect.Method;
import java.util.HashMap;

public class GameController {
    /*	public enum GameAction {
		PLAYER_1_UP, PLAYER_1_RIGHT, PLAYER_1_DOWN, PLAYER_1_LEFT, PLAYER_1_CW, PLAYER_1_CCW,
		PLAYER_2_UP, PLAYER_2_RIGHT, PLAYER_2_DOWN, PLAYER_2_LEFT, PLAYER_2_CW, PLAYER_2_CCW,
		GAME_RESTART, GAME_PAUSE, GAME_UNPAUSE, GAME_END
		}*/
	private HashMap<GameAction, Action> actionMap;
	
	public GameController(TrainsGame game) 
	{
		actionMap = new HashMap<GameAction, Action>();
		initMap(game);
	}
	
	private void initMap(TrainsGame game)
	{
		try {
    		
    		// bind player keys
			mapPlayerActions(game.getPlayers().get(0),
					GameAction.PLAYER_1_UP, GameAction.PLAYER_1_DOWN, GameAction.PLAYER_1_LEFT, GameAction.PLAYER_1_RIGHT, GameAction.PLAYER_1_CCW, GameAction.PLAYER_1_CW);
			mapPlayerActions(game.getPlayers().get(1),
					GameAction.PLAYER_2_UP, GameAction.PLAYER_2_DOWN, GameAction.PLAYER_2_LEFT, GameAction.PLAYER_2_RIGHT, GameAction.PLAYER_2_CCW, GameAction.PLAYER_2_CW);
			
    		// bind general keys
			Method setOver = TrainsGame.class.getMethod("setQuit", Boolean.class);
			Object[] trueObj = {new Boolean(true)};
			actionMap.put(GameAction.GAME_END, new Action(game, setOver, trueObj));
			
			Method reset = TrainsGame.class.getMethod("init");
			actionMap.put(GameAction.GAME_RESTART, new Action(game, reset, new Object [0]));
			
		} catch (NoSuchMethodException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		}
	}
	
    private void mapPlayerActions(Player player, GameAction up, GameAction down, GameAction left, GameAction right, GameAction ccw, GameAction cw)
    {
    	try {
			Method setDirection = Player.class.getMethod("setDirection", Vector2D.class);
			Method turnCcw = Player.class.getMethod("turnCcw");
			Method turnCw = Player.class.getMethod("turnCw");
			Object[] upObj = {VectorDirection.UP};
			Object[] rightObj = {VectorDirection.RIGHT};
			Object[] downObj = {VectorDirection.DOWN};
			Object[] leftObj = {VectorDirection.LEFT};
			
			actionMap.put(up, new Action(player, setDirection, upObj));
			actionMap.put(left, new Action(player, setDirection, leftObj));
			actionMap.put(down, new Action(player, setDirection, downObj));
			actionMap.put(right, new Action(player, setDirection, rightObj));
			actionMap.put(ccw, new Action(player, turnCcw, null));
			actionMap.put(cw, new Action(player, turnCw, null));
			
		} catch (NoSuchMethodException e) {
			System.err.println("Error: Player (" + player + ") Key binding failure");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Error: Player (" + player + ") Key binding failure");
			e.printStackTrace();
		}
    }
	
	public void doAction(GameAction action)
	{
		System.out.println (actionMap.get(action));
		actionMap.get(action).Execute();
	}
	
	public void doClick(int x, int y)
	{
		// TODO link this up with in game buttons
	}
}
