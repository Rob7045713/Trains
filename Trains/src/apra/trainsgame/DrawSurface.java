package apra.trainsgame;

public interface DrawSurface {
	public void fillRect(Rectangle r);
	public void setBackgroundColor(int color);
	public void setColor(int color);
	public void paint(TrainGame game);
}
