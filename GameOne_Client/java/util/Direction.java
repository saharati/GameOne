package util;

public enum Direction
{
	ABOVE,
	BELOW,
	RIGHT,
	LEFT;
	
	public static final Direction[] VERTICAL_DIRECTIONS = {ABOVE, BELOW};
	public static final Direction[] HORIZONTAL_DIRECTIONS = {LEFT, RIGHT};
}