package eu.ha3.mc.misc.modloader_deprecated;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/* xw-placeholder */

public class mod_ChatLogToFile extends BaseMod
{
	private File file;
	
	public mod_ChatLogToFile()
	{
		this.file = null;
		
	}
	
	@Override
	public String getVersion()
	{
		return "r6 for 1.5.2";
		
	}
	
	@Override
	public void load()
	{
		try
		{
			this.file = new File(Minecraft.getMinecraft().mcDataDir, "chatlog.txt");
			if (!this.file.exists())
			{
				this.file.createNewFile();
			}
			
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateNow = formatter.format(currentDate.getTime());
			
			PrintWriter writer = new PrintWriter(new FileWriter(this.file, true));
			writer.println();
			writer.println();
			writer.println("========================");
			writer.println("Starting new session on: " + dateNow + " (" + System.currentTimeMillis() + ")");
			writer.println("========================");
			writer.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void clientChat(String contents)
	{
		PrintWriter writer;
		try
		{
			writer = new PrintWriter(new FileWriter(this.file, true));
			writer.println(contents);
			writer.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
		}
		
	}
	
}
