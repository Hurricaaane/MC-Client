package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.glitter.skinningingame.SkinningInGameHaddon;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

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
