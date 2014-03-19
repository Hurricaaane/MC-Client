package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.misc.placeholdergui.PlaceholderGUIHaddon;

/* xw-placeholder */

public class mod_PlaceholderGUI extends HaddonBridgeModLoader
{
	public mod_PlaceholderGUI()
	{
		super(new PlaceholderGUIHaddon());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r1 for 1.4.2 xxxxxxx NOT PUBLIC";
		
	}
	
}
