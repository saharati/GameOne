package network.response;

import client.Client;
import mario.SuperMario;
import network.PacketReader;
import pacman.PacmanBuilder;
import s2048.S2048;
import snake.SnakeScreen;
import tetris.TetrisScreen;
import windows.GameSelect;
import windows.Login;

/**
 * Packet indicates that logging out succeed.
 * @author Sahar
 */
public final class LogoutResponse extends PacketReader<Client>
{
	@Override
	public void read()
	{
		
	}
	
	@Override
	public void run(final Client client)
	{
		if (client.getCurrentGame() != null)
		{
			GameSelect.getInstance().setVisible(false);
			GameSelect.getInstance().enableAllButtons();
			
			switch (client.getCurrentGame())
			{
				case MARIO:
					if (SuperMario.getInstance().isPlaying())
						SuperMario.getInstance().onEnd();
					else
						SuperMario.getInstance().reload();
					break;
				case PACMAN:
					if (PacmanBuilder.getInstance().getCurrentMap() == null)
						PacmanBuilder.getInstance().reset();
					else
						PacmanBuilder.getInstance().getCurrentMap().dispose();
					break;
				case G2048:
					S2048.getInstance().reset();
					break;
				case SNAKE:
					SnakeScreen.getInstance().reset();
					break;
				case TETRIS:
					TetrisScreen.getInstance().reset();
					break;
			}
		}
		
		client.getCurrentWindow().setVisible(false);
		client.setCurrentDetails(Login.getInstance(), null, false);
		client.getCurrentWindow().setVisible(true);
		
		GameSelect.getInstance().getChatWindow().setText("");
		GameSelect.getInstance().getSender().setText("");
	}
}