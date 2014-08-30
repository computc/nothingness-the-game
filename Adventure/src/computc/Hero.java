package computc;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Hero extends Entity
{
	public static boolean nextArea;
	private boolean dead = false;
	private int health;
	private int maxHealth;
	
	public Hero(World world, int tx, int ty) throws SlickException
	{
		super(world, tx, ty);
		
		this.image = new Image("res/hero.png");
		
		moveSpeed = 0.15f;
		health = 5;
	}
	
	public void update(Input input, int delta)
	{
		float step = this.moveSpeed * delta;
		
		if(input.isKeyDown(Input.KEY_UP))
		{
			this.direction = Direction.NORTH;
			this.y -= step;
			
			if(this.y < 0)
			{
				this.world.dungeon.move(Direction.NORTH);
				this.y = this.world.dungeon.getCurrentRoom().getHeightInPixels();
			}
		}
		else if(input.isKeyDown(Input.KEY_DOWN))
		{
			this.direction = Direction.SOUTH;
			this.y += step;
			
			if(this.y > this.world.dungeon.getCurrentRoom().getHeightInPixels())
			{
				this.world.dungeon.move(Direction.SOUTH);
				this.y = 0;
			}
		}
		
		if(input.isKeyDown(Input.KEY_LEFT))
		{
			this.direction = Direction.WEST;
			this.x -= step;

			if(this.x < 0)
			{
				this.world.dungeon.move(Direction.WEST);
				this.x = this.world.dungeon.getCurrentRoom().getWidthInPixels();
			}
		}
		else if(input.isKeyDown(Input.KEY_RIGHT))
		{
			this.direction = Direction.EAST;
			this.x += step;
			
			if(this.x > this.world.dungeon.getCurrentRoom().getWidthInPixels())
			{
				this.world.dungeon.move(Direction.EAST);
				this.x = 0;
			}
		}
	}
	
	public void render(Graphics graphics, Camera camera)
	{
		if(blinking) 
		{
			long elapsed = (System.nanoTime() - blinkTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) 
			{
				return;
			}
		}
			
		super.render(graphics, camera);
	}
	
	private void hit(int damage) 
	{
		if(blinking)
			return;
		health -= damage;
		
		if(health < 0)
			health = 0;
		
		if(health == 0) 
			dead = true;
		
		blinking = true;
		blinkTimer = (int) System.nanoTime();
	}
}