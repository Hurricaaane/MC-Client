package eu.ha3.mc.misc.modloader_deprecated;

import eu.ha3.mc.glitter.minaptics.MinapticsHaddon;

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
