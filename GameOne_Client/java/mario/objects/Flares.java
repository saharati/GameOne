package mario.objects;

import java.util.LinkedList;
import java.util.List;

import mario.SuperMario;
import mario.MarioTaskManager;
import objects.mario.MarioType;

public final class Flares extends AbstractObject
{
	private static final long serialVersionUID = 3985108844407639047L;
	
	private static final int FLAMES_NUMBER = 80;
	
	private final List<Flames> _flames = new LinkedList<>();
	private Flames _topFlame;
	private int _posDelay = 1000;
	private int _negDelay = 1000;
	
	public Flares(final int x, final int y)
	{
		super(x, y, MarioType.FLARES);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		_topFlame = new Flames(getX(), getY(), MarioType.FLAME, MarioType.FLAME2);
		for (int i = 0;i < FLAMES_NUMBER;i++)
			_flames.add(new Flames(getX(), getY() - i, MarioType.FLAME3));
		
		SuperMario.getInstance().addObject(_topFlame, true);
		_flames.forEach(f -> SuperMario.getInstance().addObject(f, true));
		
		MarioTaskManager.getInstance().add(this);
	}
	
	@Override
	public void onEnd()
	{
		super.onEnd();
		
		_flames.clear();
		_topFlame = null;
		_posDelay = 1000;
		_negDelay = 1000;
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
				_topFlame.setLocation(_topFlame.getX(), _flames.get(FLAMES_NUMBER - _posDelay).getY() - _topFlame.getHeight());
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