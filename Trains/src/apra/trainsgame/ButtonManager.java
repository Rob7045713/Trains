package apra.trainsgame;

import java.util.ArrayList;

public class ButtonManager{

    private class Button{
	public Rectangle boundingBox;
	public GameAction gameAction;

	public Button (Rectangle box, GameAction action){
	    boundingBox = box;
	    gameAction = action;
	}
	
    }

    private ArrayList<Button> buttons;
    private GameController controller;

    public ButtonManager (GameController c){
	buttons = new ArrayList<Button> ();
	controller = c;
    }

    public void addButton (Rectangle box, GameAction action){
	buttons.add(new Button (box, action));
    }

    public void pointerDown (int x, int y){
	System.out.println ("Pointer down at " + x + ", " + y + ".");
	for(int i = 0; i < buttons.size(); i++){
	    if (buttons.get(i).boundingBox.contains (new Rectangle (x,y,1,1))){
		System.out.println ("Doing the action at " + i + " and buttons.size () is " + buttons.size());
		controller.doAction(buttons.get(i).gameAction);
	    }
	}
    }
    
}