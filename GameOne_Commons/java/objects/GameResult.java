package objects;

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
 * In case of EXIT, the server doesn't gets notified with score like in LEAVE, so the server must send the group EXIT packet.
 * The clients will then respond back with the same packet with the score attached.
 */