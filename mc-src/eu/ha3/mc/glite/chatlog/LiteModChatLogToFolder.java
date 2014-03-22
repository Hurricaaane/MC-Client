package eu.ha3.mc.glite.chatlog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import com.mumfrey.liteloader.ChatListener;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.LiteLoader;

/* xw-placeholder */

public class LiteModChatLogToFolder implements LiteMod, ChatListener, InitCompleteListener
{
	private File folder;
	
	private final SimpleDateFormat fileDateFormat;
	private final SimpleDateFormat currentDateFormat;
	
	private File currentFile;
	private String datePrex;
	
	public LiteModChatLogToFolder()
	{
		super();
		
		this.fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.currentDateFormat = new SimpleDateFormat("HH:mm:ss");
	}
	
	@Override
	public String getName()
	{
		return "ChatLogToFolder";
	}
	
	@Override
	public String getVersion()
	{
		return "r1 for 1.7.2";
	}
	
	@Override
	public void onInitCompleted(Minecraft minecraft, LiteLoader loader)
	{
		createLogManager(new File(Minecraft.getMinecraft().mcDataDir, "chatlogs/"));
		
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat explicitFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNow = explicitFormatter.format(currentDate.getTime());
		
		append(
			"", "", "========================",
			"Starting new session on: " + dateNow + " (" + System.currentTimeMillis() + ")", "========================");
		
	}
	
	private void createLogManager(File folder)
	{
		this.folder = folder;
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		
		this.datePrex = "";
	}
	
	@Override
	public void onChat(IChatComponent chat, String message)
	{
		append(message);
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
	
	private void append(String... lines)
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
	
	@Override
	public void init(File configPath)
	{
	}
	
	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath)
	{
	}
	
	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
	{
	}
}
