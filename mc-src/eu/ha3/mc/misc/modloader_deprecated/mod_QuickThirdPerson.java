package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.glitter.quickthirdperson.QuickThirdPersonHaddon;

/* xw-placeholder */

public class mod_QuickThirdPerson extends HaddonBridgeModLoader
{
	public mod_QuickThirdPerson()
	{
		super(new QuickThirdPersonHaddon());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r7 for 1.6.2";
		
	}
	
}
