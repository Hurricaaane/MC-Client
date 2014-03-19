package eu.ha3.mc.misc.navstrate;

import eu.ha3.mc.convenience.Ha3KeyActions;

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

public class NavstrateKey implements Ha3KeyActions
{
	N2Haddon nav;
	
	public NavstrateKey(N2Haddon nav)
	{
		this.nav = nav;
		
	}
	
	@Override
	public void doBefore()
	{
		
	}
	
	@Override
	public void doDuring(int curTime)
	{
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < 5)
		{
			if (!this.nav.isOn())
			{
				this.nav.rescan();
				
			}
			else
			{
				this.nav.performSnapshot();
				
			}
			
		}
		else
		{
			this.nav.toggle();
		}
		
	}
	
}
