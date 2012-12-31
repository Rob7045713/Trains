package apra.trainsgame;

import java.lang.reflect.Method;
import java.util.HashMap;

public class GameController {
	public enum GameAction {
		PLAYER_1_UP, PLAYER_1_RIGHT, PLAYER_1_DOWN, PLAYER_1_LEFT, PLAYER_1_CW, PLAYER_1_CCW,
		PLAYER_2_UP, PLAYER_2_RIGHT, PLAYER_2_DOWN, PLAYER_2_LEFT, PLAYER_2_CW, PLAYER_2_CCW,
		GAME_RESTART, GAME_PAUSE, GAME_UNPAUSE, GAME_END
		}
	private HashMap<GameAction, Action> actionMap;
	
	public GameController(TrainGame game) 
	{
		actionMap = new HashMap<GameAction, Action>();
		initMap(game);
	}
	
	private void initMap(TrainGame game)
	{
		try {
    		
    		// bind player keys
			mapPlayerActions(game.getPlayers().get(0),
					GameAction.PLAYER_1_UP, GameAction.PLAYER_1_DOWN, GameAction.PLAYER_1_LEFT, GameAction.PLAYER_1_RIGHT);
			mapPlayerActions(game.getPlayers().get(1),
					GameAction.PLAYER_2_UP, GameAction.PLAYER_2_DOWN, GameAction.PLAYER_2_LEFT, GameAction.PLAYER_2_RIGHT);
			
    		// bind general keys
			Method setOver = TrainGame.class.getMethod("setOver", Boolean.class);
			Object[] trueObj = {new Boolean(true)};
			actionMap.put(GameAction.GAME_END, new Action(game, setOver, trueObj));
			
			Method reset = TrainGame.class.getMethod("init");
			actionMap.put(GameAction.GAME_RESTART, new Action(game, reset, new Object [0]));
			
		} catch (NoSuchMethodException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Error: Key binding failure");
			e.printStackTrace();
		}
	}
	
	private void mapPlayerActions(Player player, GameAction up, GameAction down, GameAction left, GameAction right)
    {
    	try {
			Method setDirection = Player.class.getMethod("setDirection", Vector2D.class);
			Object[] upObj = {VectorDirection.UP};
			Object[] rightObj = {VectorDirection.RIGHT};
			Object[] downObj = {VectorDirection.DOWN};
			Object[] leftObj = {VectorDirection.LEFT};
			
			actionMap.put(up, new Action(player, setDirection, upObj));
			actionMap.put(left, new Action(player, setDirection, leftObj));
			actionMap.put(down, new Action(player, setDirection, downObj));
			actionMap.put(right, new Action(player, setDirection, rightObj));
			
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
		actionMap.get(action).Execute();
	}
	
	public void doClick(int x, int y)
	{
		// TODO link this up with in game buttons
	}
}
