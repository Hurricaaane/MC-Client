package net.minecraft.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class mod_ChatLogToFolder extends BaseMod
{
	private File folder;
	
	private final SimpleDateFormat fileDateFormat;
	private final SimpleDateFormat currentDateFormat;
	
	private File currentFile;
	private String datePrex;
	
	public mod_ChatLogToFolder()
	{
		this.fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.currentDateFormat = new SimpleDateFormat("HH:mm:ss");
	}
	
	@Override
	public String getVersion()
	{
		return "r0 for 1.5.2";
		
	}
	
	@Override
	public void load()
	{
		createLogManager(new File(Minecraft.getMinecraft().mcDataDir, "chatlogs/"));
		
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat explicitFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNow = explicitFormatter.format(currentDate.getTime());
		
		append(
			"", "", "========================",
			"Starting new session on: " + dateNow + " (" + System.currentTimeMillis() + ")", "========================");
		
	}
	
	public void createLogManager(File folder)
	{
		this.folder = folder;
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		
		this.datePrex = "";
	}
	
	@Override
	public void clientChat(String contents)
	{
		append(contents);
	}
	
	private void ensureCurrentFile(Date date) throws IOException
	{
		String currentDatePrex = this.fileDateFormat.format(date);
		if (!this.datePrex.equals(currentDatePrex))
		{
			this.datePrex = currentDatePrex;
			this.currentFile = new File(this.folder, currentDatePrex + ".log");
			if (this.currentFile.exists())
			{
				this.currentFile.createNewFile();
			}
		}
	}
	
	public void append(String... lines)
	{
		PrintWriter fw = null;
		try
		{
			Date date = new Date();
			ensureCurrentFile(date);
			
			fw = new PrintWriter(new FileWriter(this.currentFile, true));
			for (String line : lines)
			{
				fw.println(this.currentDateFormat.format(date) + "\t" + line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fw != null)
			{
				fw.close();
			}
		}
		
	}
	
}
