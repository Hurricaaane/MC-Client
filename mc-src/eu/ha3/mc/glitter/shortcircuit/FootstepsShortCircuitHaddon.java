package eu.ha3.mc.glitter.shortcircuit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import eu.ha3.mc.haddon.PrivateAccessException;

/* xw-placeholder */

public class FootstepsShortCircuitHaddon extends HaddonImpl
{
	private boolean activated;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onLoad()
	{
		this.activated = true;
		
		try
		{
			File file = new File(Minecraft.getMinecraft().mcDataDir, "footstepsshortcircuit.txt");
			if (!file.exists())
			{
				boolean yes = file.createNewFile();
				if (yes)
				{
					PrintWriter fw = new PrintWriter(new FileWriter(file));
					fw.print("1");
					fw.close();
					
				}
				
			}
			else
			{
				BufferedReader fr = new BufferedReader(new FileReader(file));
				String line = fr.readLine();
				if (line.equals("0"))
				{
					this.activated = false;
					System.out.println("FootstepsShortCurcuit is off");
					
				}
				else
				{
					this.activated = true;
					
				}
				fr.close();
				
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (this.activated)
		{
			// So ever since 1.3.1 SS'ing footsteps clientside doesn't work anymore
			// Sounds are sent using generic LevelSound packets from the server
			// Since footsteps use the same sounds as the digging sound,
			// we can't use the approach of muting sounds.
			//
			// However the observation is that all footsteps sent from the server
			// happen to be at volume 0.15f and at 65 pitch. So we catch them and
			// cancel them by setting the volume to zero.
			
			try
			{
				Packet.packetIdToClassMap.addKey(62, FootstepsSCP62.class);
				
				// packetClassToIdMap
				HashMap map = (HashMap) util().getPrivateValueLiteral(Packet.class, null, "a", 1);
				map.put(FootstepsSCP62.class, 62);
				
			}
			catch (PrivateAccessException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
	}
	
}
