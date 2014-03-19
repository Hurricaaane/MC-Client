package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.glitter.shortcircuit.FootstepsShortCircuitHaddon;

/* xw-placeholder */

public class mod_FootstepsShortCircuit extends HaddonBridgeModLoader
{
	public mod_FootstepsShortCircuit()
	{
		super(new FootstepsShortCircuitHaddon());
	}
	
	@Override
	public String getVersion()
	{
		return "r2 for 1.4.4 DISCONTINUED";
		
	}
	
}
