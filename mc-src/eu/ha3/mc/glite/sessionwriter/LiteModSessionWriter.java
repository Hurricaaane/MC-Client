package eu.ha3.mc.glite.sessionwriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.LiteLoader;

/* xw-placeholder */

/**
 * This writes the login session values into a file, as a very simplistic way to
 * enable mod testing on servers in testing phases (i.e. eclipse debug mode).<br>
 * <br>
 * Only works on Windows, because the author works on Windows...
 * 
 * @author Hurry
 */
public class LiteModSessionWriter implements LiteMod, InitCompleteListener
{
	public LiteModSessionWriter()
	{
	}
	
	@Override
	public String getName()
	{
		return "SessionWriter";
	}
	
	@Override
	public String getVersion()
	{
		return "x2 Custom (*)";
	}
	
	public static File getAppDir(String from)
	{
		String homedir = System.getProperty("user.home", ".");
		File file;
		/*switch (EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()])
		{
		case 1:
		case 2:
			file = new File(homedir, "." + from + "/");
			break;
		
		case 3:*/
		String datafolder = System.getenv("APPDATA");
		if (datafolder != null)
		{
			file = new File(datafolder, "." + from + "/");
		}
		else
		{
			file = new File(homedir, "." + from + "/");
		}
		/*	break;
		
		case 4:
			file = new File(homedir, "Library/Application Support/" + from);
			break;
		
		default:
			file = new File(homedir, from + "/");
			break;
		}*/
		
		// TODO: Check if the absence of the minecraft folder cause problems
		/*if (!file.exists() && !file.mkdirs())
		{
			throw new RuntimeException((new StringBuilder()).append(
					"The working directory could not be created: ")
					.append(file).toString());
		}
		else
		{*/
		return file;
		//}
		
	}
	
	@Override
	public void onInitCompleted(Minecraft minecraft, LiteLoader loader)
	{
		Minecraft mc = Minecraft.getMinecraft();
		Session sess = mc.getSession();
		String username = sess.getUsername();
		String sessionId = sess.getSessionID();
		
		try
		{
			File f = new File(getAppDir("minecraft"), "mcsession.txt");
			if (!f.exists())
			{
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			
			fw.write(username + " " + sessionId);
			fw.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
	{
	}
	
	@Override
	public void init(File configPath)
	{
	}
	
	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath)
	{
	}
	
}
