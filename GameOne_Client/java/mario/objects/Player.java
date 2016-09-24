package mario.objects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import mario.MarioBuilder;
import mario.MarioScreen;
import mario.TaskManager;
import mario.prototypes.Direction;
import mario.prototypes.JumpType;
import objects.mario.MarioType;
import util.random.Rnd;

/**
 * The player instance.
 * @author Sahar
 */
public final class Player extends AbstractObject
{
	private static final long serialVersionUID = -5619383353466379122L;
	
	private static final MarioType[][] TYPES_PER_LEVEL = 
	{
		{MarioType.PLAYER, MarioType.PLAYER2},
		{MarioType.MARIO, MarioType.MARIO2},
		{MarioType.SUPERMARIO, MarioType.SUPERMARIO2}
	};
	private static final ImageIcon[][] FLIPPED_IMAGES = new ImageIcon[3][2];
	
	private int _level;
	private int _jumpCount;
	private int _invisCount;
	private int _swallowCount;
	private int _delay;
	private Direction _dir;
	private JumpType _jumpType = JumpType.NONE;
	
	public Player(final int x, final int y)
	{
		super(x, y, MarioType.PLAYER, MarioType.PLAYER2);
		
		for (int i = 0;i < TYPES_PER_LEVEL.length;i++)
		{
			for (int j = 0;j < TYPES_PER_LEVEL[i].length;j++)
			{
				final ImageIcon originalImage = MarioBuilder.getInstance().getAllImages().get(TYPES_PER_LEVEL[i][j].getImage());
				
				BufferedImage flippedImage = new BufferedImage(originalImage.getIconWidth(), originalImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g2d = (Graphics2D) flippedImage.getGraphics();
				
				originalImage.paintIcon(null, g2d, 0, 0);
				g2d.dispose();
				
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -flippedImage.getHeight());
				
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				flippedImage = op.filter(flippedImage, null);
				
				tx = AffineTransform.getScaleInstance(-1, -1);
				tx.translate(-flippedImage.getWidth(), -flippedImage.getHeight());
				
				op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				flippedImage = op.filter(flippedImage, null);
				
				FLIPPED_IMAGES[i][j] = new ImageIcon(flippedImage);
			}
		}
		
		TaskManager.getInstance().add(this);
	}
	
	public void levelUp()
	{
		if (_level == 2)
			return;
		
		_level++;
		
		setImages(TYPES_PER_LEVEL[_level]);
		setBounds(getX(), getY(), getWidth(), getHeight());
	}
	
	public void levelDown()
	{
		if (_invisCount > 0 || _swallowCount > 0 || _level == -1)
			return;
		
		_invisCount = 300;
		_level--;
		
		if (_level == -1)
		{
			deleteMe();
			return;
		}
		
		setImages(TYPES_PER_LEVEL[_level]);
		setBounds(getX(), getY(), getWidth(), getHeight());
	}
	
	public void jump(final JumpType type)
	{
		_jumpCount = 0;
		_jumpType = type;
	}
	
	public void swallow()
	{
		_swallowCount = getHeight();
	}
	
	public boolean isJumping()
	{
		return _jumpType != JumpType.NONE;
	}
	
	public void setDirection(final Direction dir)
	{
		_dir = dir;
	}
	
	public Direction getDirection()
	{
		return _dir;
	}
	
	public boolean canShoot()
	{
		if (_level != 2)
			return false;
		if (_delay > 0)
			return false;
		
		return true;
	}
	
	public void shoot(final Direction dir)
	{
		_delay = 500;
		
		MarioScreen.getInstance().add(new Shoot((dir == Direction.RIGHT ? (int) getBounds().getMaxX() + 1 : getX() - 1), (int) getBounds().getCenterY(), getY(), dir));
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_delay > 0)
			_delay--;
		if (_swallowCount != 0)
		{
			if (_swallowCount > 0)
			{
				setLocation(getX(), getY() + 1);
				_swallowCount--;
				
				if (_swallowCount == 0)
				{
					final TubeExit rnd = MarioScreen.getInstance().getExitTubes().get(Rnd.get(MarioScreen.getInstance().getExitTubes().size()));
					
					setLocation(rnd.getX() + (rnd.getWidth() - getWidth()) / 2, rnd.getY() + rnd.getHeight() - getHeight());
					
					_swallowCount = -getHeight();
				}
			}
			else
			{
				setLocation(getX(), getY() + 1);
				_swallowCount++;
			}
			
			if (getX() > MarioBuilder.SCREEN_MOVING_POINT)
				MarioScreen.getInstance().getBg().setLocation(-(getX() - MarioBuilder.SCREEN_MOVING_POINT), MarioScreen.getInstance().getBg().getY());
			else
				MarioScreen.getInstance().getBg().setLocation(0, MarioScreen.getInstance().getBg().getY());
		}
		else
		{
			final Map<Direction, List<AbstractObject>> objects = getNearbyObjects(getBounds());
			if (_dir != null)
			{
				if (_dir == Direction.LEFT)
				{
					if (getX() > 0)
					{
						if (objects.get(Direction.LEFT).isEmpty())
							setLocation(getX() - 1, getY());
						else
						{
							boolean canGoThroughAll = true;
							for (AbstractObject o : objects.get(Direction.LEFT))
								if (!o.canGoThrough())
									canGoThroughAll = false;
							
							if (canGoThroughAll)
								setLocation(getX() - 1, getY());
						}
					}
					
					if (getIcon() == FLIPPED_IMAGES[_level][0])
						setIcon(FLIPPED_IMAGES[_level][1]);
					else
						setIcon(FLIPPED_IMAGES[_level][0]);
				}
				else
				{
					if (objects.get(Direction.RIGHT).isEmpty())
						setLocation(getX() + 1, getY());
					else
					{
						boolean canGoThroughAll = true;
						for (AbstractObject o : objects.get(Direction.RIGHT))
							if (!o.canGoThrough())
								canGoThroughAll = false;
						
						if (canGoThroughAll)
							setLocation(getX() + 1, getY());
					}
					
					if (getIcon() == getImages()[0])
						setIcon(getImages()[1]);
					else
						setIcon(getImages()[0]);
				}
				
				if (getX() > MarioBuilder.SCREEN_MOVING_POINT)
					MarioScreen.getInstance().getBg().setLocation(-(getX() - MarioBuilder.SCREEN_MOVING_POINT), MarioScreen.getInstance().getBg().getY());
			}
			
			if (_invisCount > 0)
			{
				if (_invisCount % 2 == 0)
					setVisible(!isVisible());
				
				_invisCount--;
				if (_invisCount == 0 && !isVisible())
					setVisible(true);
			}
			
			if (_jumpType != JumpType.NONE)
			{
				_jumpCount++;
				if (_jumpType == JumpType.JUMP && _jumpCount < 100 || _jumpType == JumpType.FLY && _jumpCount < 250)
				{
					if (objects.get(Direction.ABOVE).isEmpty())
						setLocation(getX(), getY() - 1);
					else
					{
						boolean canGoThroughAll = true;
						for (AbstractObject o : objects.get(Direction.ABOVE))
							if (!o.canGoThrough())
								canGoThroughAll = false;
						
						if (canGoThroughAll)
							setLocation(getX(), getY() - 1);
						else
							_jumpCount = 250;
					}
				}
				else
				{
					_jumpType = JumpType.FALL;
					
					if (objects.get(Direction.BELOW).isEmpty())
						setLocation(getX(), getY() + 1);
					else
					{
						_jumpCount = 0;
						_jumpType = JumpType.NONE;
					}
				}
			}
			else if (objects.get(Direction.BELOW).isEmpty())
				setLocation(getX(), getY() + 1);
			else
			{
				boolean canGoThroughAll = true;
				for (AbstractObject o : objects.get(Direction.BELOW))
					if (!o.canGoThrough())
						canGoThroughAll = false;
				
				if (canGoThroughAll)
					setLocation(getX(), getY() + 1);
			}
			
			if (getY() > MarioBuilder.SCREEN_SIZE.height)
				deleteMe();
			else
				for (final Entry<Direction, List<AbstractObject>> os : objects.entrySet())
					for (final AbstractObject o : os.getValue())
						o.onMeetObject(os.getKey());
		}
	}
	
	@Override
	protected void deleteMe()
	{
		super.deleteMe();
		
		TaskManager.getInstance().stop();
		JOptionPane.showMessageDialog(null, "אתה אפס...", "לה לה לה", JOptionPane.INFORMATION_MESSAGE);
		MarioScreen.getInstance().endGame();
	}
}