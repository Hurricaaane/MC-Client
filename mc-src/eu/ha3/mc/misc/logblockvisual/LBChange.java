package eu.ha3.mc.misc.logblockvisual;

/* xw-placeholder */

public class LBChange
{
	public int getInformativeRank()
	{
		return this.informativeRank;
	}
	
	public void setInformativeRank(int informativeRank)
	{
		this.informativeRank = informativeRank;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
	
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	public String getRawDate()
	{
		return this.date;
	}
	
	public void setRawDate(String date)
	{
		this.date = date;
	}
	
	public String getAction()
	{
		return this.action;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getZ()
	{
		return this.z;
	}
	
	public void setZ(int z)
	{
		this.z = z;
	}
	
	private int informativeRank;
	private String date;
	private String author;
	private String action;
	private int x;
	private int y;
	private int z;
	
	public LBChange()
	{
		this.informativeRank = -1;
		this.author = "";
		this.action = "";
		this.date = "";
		
	}
	
	@Override
	public String toString()
	{
		return "("
			+ this.informativeRank + ") " + this.author + " " + this.action + " at " + this.x + ":" + this.y + ":"
			+ this.z;
		
	}
	
}
