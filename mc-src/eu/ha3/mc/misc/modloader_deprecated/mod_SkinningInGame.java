package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.glitter.skinningingame.SkinningInGameHaddon;

/* xw-placeholder */

public class mod_SkinningInGame extends HaddonBridgeModLoader
{
	public mod_SkinningInGame()
	{
		super(new SkinningInGameHaddon());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r5 for 1.5.2";
		
	}
	
}
