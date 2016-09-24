package mario.objects;

import java.util.LinkedList;
import java.util.List;

import mario.MarioScreen;
import mario.TaskManager;
import objects.mario.MarioType;

/**
 * A flares bar splitting flames at a fixed rate.
 * @author Sahar
 */
public final class Flares extends AbstractObject
{
	private static final long serialVersionUID = 3985108844407639047L;
	
	private static final int FLAMES_NUMBER = 80;
	
	private List<Flames> _flames = new LinkedList<>();
	private Flames _topFlame;
	private int _posDelay = 1000;
	private int _negDelay = 1000;
	
	public Flares(final int x, final int y)
	{
		super(x, y, MarioType.FLARES);
		
		_topFlame = new Flames(x, y, MarioType.FLAME, MarioType.FLAME2);
		MarioScreen.getInstance().add(_topFlame);
		
		for (int i = 0;i < FLAMES_NUMBER;i++)
		{
			final Flames f = new Flames(x, y - i, MarioType.FLAME3);
			
			_flames.add(f);
			MarioScreen.getInstance().add(f);
		}
		
		TaskManager.getInstance().add(this);
	}
	
	@Override
	public void notifyTimeOut()
	{
		if (_posDelay > 0)
		{
			_posDelay--;
			if (_posDelay > FLAMES_NUMBER)
				return;
			
			if (_posDelay == FLAMES_NUMBER)
				_topFlame.setVisible(true);
			if (_posDelay > 0)
			{
				_flames.get(FLAMES_NUMBER - _posDelay).setVisible(true);
				_topFlame.setLocation(_topFlame.getX(), _flames.get(FLAMES_NUMBER - _posDelay).getY() - getHeight() + 1);
			}
		}
		else if (_negDelay > 0)
		{
			_negDelay--;
			if (_negDelay > FLAMES_NUMBER)
				return;
			
			if (_negDelay == 0)
				_topFlame.setVisible(false);
			else
				_flames.get(FLAMES_NUMBER - _negDelay).setVisible(false);
		}
		else
			_posDelay = _negDelay = 1000;
	}
}