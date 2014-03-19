package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.glitter.bindingscompact.BindingsCompactHaddon;

public class mod_BindingsCompact extends HaddonBridgeModLoader
{
	public mod_BindingsCompact()
	{
		super(new BindingsCompactHaddon());
	}
	
	@Override
	public String getVersion()
	{
		return "r8 for 1.6.2";
		
	}
	
}
