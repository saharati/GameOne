package objects.mario;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

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
	
	private static final String IMAGE_PATH = "./images/mario/";
	
	private final String _url;
	private final String _className;
	
	private ImageIcon _icon;
	private ImageIcon _flippedIcon;
	
	private MarioType(final String url, final String className)
	{
		_url = url;
		_className = className;
	}
	
	public String getUrl()
	{
		return _url;
	}
	
	public String getClassName()
	{
		return _className;
	}
	
	public boolean appearsOnMapBuilder()
	{
		return _className != null;
	}
	
	public ImageIcon getIcon()
	{
		return _icon;
	}
	
	public ImageIcon getFlippedIcon()
	{
		return _flippedIcon;
	}
	
	public void initializeImageIcon()
	{
		_icon = new ImageIcon(IMAGE_PATH + _url);
		
		switch (this)
		{
			case PLAYER:
			case PLAYER2:
			case MARIO:
			case MARIO2:
			case SUPERMARIO:
			case SUPERMARIO2:
				BufferedImage flippedImage = new BufferedImage(_icon.getIconWidth(), _icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g2d = (Graphics2D) flippedImage.getGraphics();
				
				_icon.paintIcon(null, g2d, 0, 0);
				g2d.dispose();
				
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -flippedImage.getHeight());
				
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				flippedImage = op.filter(flippedImage, null);
				
				tx = AffineTransform.getScaleInstance(-1, -1);
				tx.translate(-flippedImage.getWidth(), -flippedImage.getHeight());
				
				op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				flippedImage = op.filter(flippedImage, null);
				
				_flippedIcon = new ImageIcon(flippedImage);
				break;
		}
	}
}