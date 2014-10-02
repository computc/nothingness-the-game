package computc.worlds.dungeons;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.Graphics;

import computc.Game;
import computc.cameras.Camera;
import computc.entities.Coin;
import computc.entities.Enemy;
import computc.entities.Entity;
import computc.entities.Key;
import computc.entities.OldMan;
import computc.worlds.rooms.Room;
import computc.worlds.rooms.RoomLayout;
import computc.worlds.tiles.Tile;
import computc.worlds.tiles.TileSubSet;
import computc.worlds.tiles.TileSet;

public abstract class Dungeon
{
	protected HashMap<String, Room> rooms = new HashMap<String, Room>();
	public LinkedList<Key> keys = new LinkedList<Key>();
	public LinkedList<Enemy> enemies = new LinkedList<Enemy>();
	protected Room firstRoom;
	public Room lastRoom;
	public OldMan oldman;
	
	ArrayList<TileSet> tilesets = new ArrayList<TileSet>();
	LinkedList<RoomLayout> randomRoomLayouts = new LinkedList<RoomLayout>();
	HashMap<String, RoomLayout> specialRoomLayouts = new HashMap<String, RoomLayout>();
	private boolean debug;
	public boolean chainEnabled = true;
	
	public Dungeon()
	{
		this.tilesets.add(Game.assets.getTileSet("./res/tilesets/stoney.tileset.xml"));
		this.tilesets.add(Game.assets.getTileSet("./res/tilesets/snowy.tileset.xml"));
		this.tilesets.add(Game.assets.getTileSet("./res/tilesets/dirty.tileset.xml"));
		
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/arena.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/blob.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/brackets.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/corners.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/crumbs.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/diagonals.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/dot.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/fivedots.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/fourdots.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/grid.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/loop.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/niches.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/oval.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/spiral.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/square.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/threedots.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/threelines.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/twodots.room.tmx"));
		this.randomRoomLayouts.add(Game.assets.getRoomLayout("./res/rooms/twolines.room.tmx"));
		this.specialRoomLayouts.put("first room", Game.assets.getRoomLayout("./res/rooms/empty.room.tmx"));
		this.specialRoomLayouts.put("last room", Game.assets.getRoomLayout("./res/rooms/clamp.room.tmx"));
	}
	
	public void initiate()
	{
		for(Room room : this.getAllRooms())
		{
			room.initiate();
		}
	}
	
	public void update(int delta)
	{
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e = enemies.get(i);
			e.update(delta);
				if(e.isDead())
				{
					enemies.remove(i);
					i--;
				}
		}
	}

	public void render(Graphics graphics, Camera camera)
	{
		for(Room room : this.getAllRooms())
		{
			room.render(graphics, camera);
		}

		for(Enemy enemy: this.getAllEnemies())
		{
			enemy.render(graphics, camera);
		}
		
		for(Key key : this.keys)
		{
			key.render(graphics, camera);
		}
		
		if(this.oldman != null)
		{
			this.oldman.render(graphics, camera);
		}
	}
	
	public void renderOnMap(Graphics graphics, Camera camera)
	{
		for(Room room : this.getAllRooms())
		{
			room.renderOnMap(graphics, camera);
		}
	}
	
	public LinkedList<Enemy> getAllEnemies()
	{
		return this.enemies;
	}
	
	public void addRoom(Room room)
	{
		int rx = room.getRoomyX();
		int ry = room.getRoomyY();
		
		if(this.hasRoom(rx, ry))
		{
			throw new DungeonException();
		}
		else
		{
			this.rooms.put(rx + ":" + ry, room);
		}
	}
	
	public Room getRoom(int rx, int ry)
	{
		return this.rooms.get(rx + ":" + ry);
	}
	
	public boolean hasRoom(int rx, int ry)
	{
		return this.rooms.containsKey(rx + ":" + ry);
	}
	
	public LinkedList<Room> getAllRooms()
	{
		return new LinkedList<Room>(this.rooms.values());
	}
	
	public Room getFirstRoom()
	{
		return this.firstRoom;
	}
	
	public void setFirstRoom(Room firstRoom)
	{
		this.firstRoom = firstRoom;
	}
	
	public Tile getTile(float x, float y)
	{
		int rx = (int)(Math.floor(x / Room.WIDTH));
		int ry = (int)(Math.floor(y / Room.HEIGHT));
		
		int tx = (int)(Math.floor((x - (rx * Room.WIDTH)) / Tile.SIZE));
		int ty = (int)(Math.floor((y - (ry * Room.HEIGHT)) / Tile.SIZE));
		
		return this.getRoom(rx, ry).getTile(tx, ty);
	}
	
	public TileSet getTileSet(int index)
	{
		return this.tilesets.get(index);
	}
	
	public TileSet getRandomTileSet()
	{
		return this.tilesets.get(Game.random.nextInt(tilesets.size()));
	}
	
	public RoomLayout getRandomRoomLayout()
	{
		RoomLayout randomRoomLayout = this.randomRoomLayouts.pop();
		this.randomRoomLayouts.add(randomRoomLayout);
		return randomRoomLayout;
	}
	
	public RoomLayout getSpecialRoomLayout(String type)
	{
		return this.specialRoomLayouts.get(type);
	}
	
	// debug mode for viewing box2d physics
		public void rigidBodyDebugDraw(Set<Body> bodies, Set<Body> staticBodies) 
		{
			glClear(GL_COLOR_BUFFER_BIT);
			
			for(Body body: bodies)
			{
					glPushMatrix();
					Vec2 bodyPosition = body.getPosition().mul(30);
					glTranslatef(bodyPosition.x, bodyPosition.y, 0);
					glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1);
					glRectd(-0.5f * 30, -0.060f * 30, 0.5f * 30, 0.060f * 30);
		//			System.out.println("the actual box 2d position of the body is: "  + body.getPosition().x + " , " + body.getPosition().y);
					glPopMatrix();
			}
			
			for(Body body: staticBodies)
			{
					glPushMatrix();
					Vec2 staticBodyPosition = body.getPosition().mul(30);
					glTranslatef(staticBodyPosition.x, staticBodyPosition.y, 0);
					glRectd(-1f * 30, -1f * 30, 1f * 30, 1f * 30);
					glPopMatrix();
			}
		}
		
		public boolean toggleDebugDraw()
		{
			return debug = !debug;
		}
		
		public boolean getDebugDraw()
		{
			return debug;
		}
		
		// if you want turn chain off
		public void disableChain()
		{
			chainEnabled = false;
		}
		
		public void enableChain()
		{
			chainEnabled = true;
		}
}