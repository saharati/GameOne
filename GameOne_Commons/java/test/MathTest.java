package test;

/**
 * This class is not a part of the project.
 * Just for the exercise.
 * @author Sahar
 * TODO Continue this class (finals etc).
 */
public final class MathTest
{
	public static void main(String[] args)
	{
		System.out.println("Hezka: " + hezka(2, 10));
		System.out.println("Shoresh: " + shoresh(27, 3));
		System.out.println("KefelMekuzar1: " + kefelMekuzar1("(x+3)^2"));
		System.out.println("KefelMekuzar1: " + kefelMekuzar1("(x-2)^2"));
		System.out.println("KefelMekuzar1: " + kefelMekuzar1("(3-x)^2"));
		System.out.println("KefelMekuzar1: " + kefelMekuzar1("(x+1)^2"));
		System.out.println("KefelMekuzar1: " + kefelMekuzar1("(2x-4)^2")); // TODO correct this and continue from here.
	}
	
	private static int hezka(int num, int hezka)
	{
		int result = num;
		for (int i = 1;i < hezka;i++)
			result *= num;
		
		return result;
		
		// A.K.A Math.pow(num, hezka);
	}
	
	private static int shoresh(int num, int shoresh)
	{
		// What number will become {num} when it will multiply itself {shoresh} times?
		int current = 1;
		while (current <= num)
		{
			int result = current;
			for (int i = 1;i < shoresh;i++)
				result *= current;
			
			if (result == num)
				return current;
			
			current++;
		}
		
		return -1;
		
		// A.K.A Math.pow(num, 1.0d / shoresh);
	}
	
	private static String kefelMekuzar1(String quest)
	{
		if (quest.startsWith("("))
		{
			quest = quest.substring(1);
			quest = quest.substring(0, quest.length() - 3);
			for (int i = 1;i < quest.length();i++)
			{
				if (quest.charAt(i) == '+' || quest.charAt(i) == '-')
				{
					final String a = quest.substring(0, i);
					final String b = quest.substring(i + 1);
					int aNum;
					int bNum;
					try
					{
						aNum = Integer.parseInt(a);
					}
					catch (NumberFormatException e)
					{
						aNum = 0;
					}
					try
					{
						bNum = Integer.parseInt(b);
					}
					catch (NumberFormatException e)
					{
						bNum = 0;
					}
					// Formula: a^2 +- 2ab + b^2
					// a^2
					String res = "";
					if (aNum != 0)
						res += hezka(aNum, 2);
					else
						res += a + "^2";
					// +-
					res += " " + quest.charAt(i) + " ";
					// 2ab
					int num = 2;
					String add = "";
					if (aNum != 0)
						num *= aNum;
					else
						add += a;
					if (bNum != 0)
						num *= bNum;
					else
						add += b;
					res += num + add;
					res += " + ";
					// b^2
					if (bNum != 0)
						res += hezka(bNum, 2);
					else
						res += b + "^2";
					
					return res;
				}
			}
		}
		else
		{
			
		}
		
		return null;
	}
}