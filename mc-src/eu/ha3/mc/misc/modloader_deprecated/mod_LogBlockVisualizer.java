package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.misc.logblockvisual.LBVisHaddon;

/* xw-placeholder */

public class mod_LogBlockVisualizer extends HaddonBridgeModLoader
{
	public mod_LogBlockVisualizer()
	{
		super(new LBVisHaddon());
		//super(new DisabledHaddon());
		
	}
	
	@Override
	public String getVersion()
	{
		return "x0";
		
	}
	
}
