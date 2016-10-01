package objects;

/**
 * A parameter sent with UpdateGameScore packet.
 * @author Sahar
 */
public enum GameResult
{
	WIN,
	LOSE,
	TIE,
	LEAVE,
	NONE,
	EXIT
}
/*
 * Note about LEAVE and EXIT
 * LEAVE - Occurs when a player clicks the exit button on the current game he is playing.
 * EXIT - Occurs when the player crashes or completely exits the client.
 * When a player exits, the server doesn't gets notified with score like in LEAVE, so the server must send the group EXIT packet.
 * And then the clients respond back with the same packet with score attached.
 */