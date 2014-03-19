package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.glitter.minaptics.MinapticsHaddon;

/* xw-placeholder */

public class mod_Minaptics extends HaddonBridgeModLoader
{
	public mod_Minaptics()
	{
		super(new MinapticsHaddon());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r21 for 1.6.2";
	}
	
}
