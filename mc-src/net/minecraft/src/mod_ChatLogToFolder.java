package net.minecraft.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.minecraft.client.Minecraft;

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
	public mod_ChatLogToFolder()
	{
	}
	
	@Override
	public String getVersion()
	{
		return "r0 for 1.5.2";
		
	}
	
	@Override
	public void load()
	{
		try
		{
			createLogManager(new File(Minecraft.getMinecraftDir(), "chatlogs/"));
			
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat explicitFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateNow = explicitFormatter.format(currentDate.getTime());
			
			append(
				"", "", "========================",
				"Starting new session on: " + dateNow + " (" + System.currentTimeMillis() + ")",
				"========================");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void clientChat(String contents)
	{
		append(contents);
		
	}
	
	private File folder;
	
	private SimpleDateFormat fileDateFormat;
	private SimpleDateFormat currentDateFormat;
	
	private File currentFile;
	private String datePrex;
	
	public void createLogManager(File folder)
	{
		this.folder = folder;
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		this.fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.currentDateFormat = new SimpleDateFormat("HH:mm:ss");
		
		this.datePrex = "";
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
