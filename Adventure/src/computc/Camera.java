package computc;

public class Camera
{
	private float x, y;
	private Entity target;
	private float speed = 1f;
	
	public Camera(Entity target)
	{
		this.target = target;
	}
	
	public void update(int delta)
	{
		if(this.getX() < this.getTargetX())
		{
			this.increaseX(this.speed * delta);
		}
		else if(this.getX() > this.getTargetX())
		{
			this.decreaseX(this.speed * delta);
		}
		
		if(this.getY() < this.getTargetY())
		{
			this.increaseY(this.speed * delta);
		}
		else if(this.getY() > this.getTargetY())
		{
			this.decreaseY(this.speed * delta);
		}
	}
	
	public int getX()
	{
		return (int)(this.x);
	}
	
	public int getY()
	{
		return (int)(this.y);
	}
	
	public int getTargetX()
	{
		return this.target.room.getX();
	}
	
	public int getTargetY()
	{
		return this.target.room.getY();
	}
	
	public void increaseX(float amount)
	{
		this.x += amount;
	}
	
	public void decreaseX(float amount)
	{
		this.x -= amount;
	}
	
	public void increaseY(float amount)
	{
		this.y += amount;
	}
	
	public void decreaseY(float amount)
	{
		this.y -= amount;
	}
}