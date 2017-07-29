package objects.pacman;

public enum PacmanObject
{
	EMPTY("empty.png"),
	STAR("star.png"),
	WALL("wall.jpg"),
	FOOD("food.png"),
	MOB1("mob1.png"),
	MOB2("mob2.png"),
	MOB3("mob3.png"),
	MOB4("mob4.png"),
	MOB_SLOW("slow.png"),
	PLAYER_UP("up.png"),
	PLAYER_DOWN("down.png"),
	PLAYER_LEFT("left.png"),
	PLAYER_RIGHT("right.png"),
	PLAYER_NORMAL("normal.png");
	
	private final String _image;
	
	private PacmanObject(final String image)
	{
		_image = image;
	}
	
	public String getImage()
	{
		return _image;
	}
	
	public boolean isPlayer()
	{
		return this == PLAYER_NORMAL || this == PLAYER_UP || this == PLAYER_DOWN || this == PLAYER_LEFT || this == PLAYER_RIGHT;
	}
	
	public boolean isMonster()
	{
		return this == MOB1 || this == MOB2 || this == MOB3 || this == MOB4 || this == MOB_SLOW;
	}
	
	public boolean isSlow()
	{
		return this == MOB_SLOW;
	}
	
	public boolean isEmpty()
	{
		return this == EMPTY;
	}
	
	public boolean isWall()
	{
		return this == WALL;
	}
	
	public boolean isStar()
	{
		return this == STAR;
	}
	
	public boolean isFood()
	{
		return this == FOOD;
	}
	
	public static PacmanObject getObjectForDirection(final char direction)
	{
		switch (direction)
		{
			case 'a':
				return PLAYER_LEFT;
			case 's':
				return PLAYER_DOWN;
			case 'w':
				return PLAYER_UP;
			case 'd':
				return PLAYER_RIGHT;
		}
		
		return PLAYER_NORMAL;
	}
}