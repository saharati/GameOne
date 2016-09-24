package objects.mario;

/**
 * Class representing all mario object types.
 * @author Sahar
 */
public enum MarioType
{
	ALIEN("alien.png", "Alien"),
	ALIEN2("alien2.png", null),
	ALIENNEW("aliennew.png", "Alien2"),
	BACKGROUND("background.png", null),
	BACKGROUND2("background2.png", null),
	BALL("ball.png", null),
	CANNON("cannon.png", "Cannon"),
	COIN("coin.png", "Coin"),
	COIN2("coin2.png", null),
	CUBE("cube.png", "Cube"),
	CUBE2("cube2.png", "BreakableCube"),
	FLAME("flame.png", null),
	FLAME2("flame2.png", null),
	FLAME3("flame3.png", null),
	FLARES("flares.png", "Flares"),
	FLAT("flat.png", "Flat"),
	FLOWER("flower.png", null),
	FLOWER2("flower2.png", null),
	GROUND("ground.png", "Ground"),
	GROUND2("ground2.png", "HeadlessGround"),
	GROUNDABOVE("groundAbove.png", null),
	JUKE("juke.png", "Juke"),
	JUKE2("juke2.png", null),
	JUKE3("juke3.png", null),
	LIGHTNING("lightning.png", "LightningHorizontal"),
	LIGHTNING2("lightning2.png", "LightningVertical"),
	MARIO("mario.png", null),
	MARIO2("mario2.png", null),
	MUSHRUM("mushrum.png", "Mushrum"),
	PLAYER("player.png", "Player"),
	PLAYER2("player2.png", null),
	SHOOT("shoot.png", null),
	SUPERMARIO("supermario.png", null),
	SUPERMARIO2("supermario2.png", null),
	TRAMP("tramp.png", "Tramp"),
	TUBE("tube.png", "TubeEntrance"),
	TUBE2("tube2.png", "TubeExit"),
	WALL("wall.png", "Wall");
	
	private final String _image;
	private final String _className;
	
	private MarioType(final String image, final String className)
	{
		_image = image;
		_className = className;
	}
	
	public String getImage()
	{
		return _image;
	}
	
	public String getClassName()
	{
		return _className;
	}
	
	public boolean appearsOnMapBuilder()
	{
		return _className != null;
	}
}