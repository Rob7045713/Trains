package apra.trainsgame;

public interface DrawSurface {
	public void fillRect(Rectangle r);
	public void setBackgroundColor(Color color);
	public void setColor(Color color);
	public void paint(TrainGame game);
}
