package eu.ha3.mc.misc.navstrate;

import eu.ha3.mc.convenience.Ha3KeyActions;

/* xw-placeholder */

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
