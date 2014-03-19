package eu.ha3.mc.glitter.minaptics;

import eu.ha3.mc.convenience.Ha3KeyActions;

/* xw-placeholder */

public class MinapticsZoomBinding implements Ha3KeyActions
{
	protected MinapticsHaddon mod;
	
	public MinapticsZoomBinding(MinapticsHaddon mod)
	{
		super();
		this.mod = mod;
		
	}
	
	@Override
	public void doBefore()
	{
		this.mod.zoomDoBefore();
		
	}
	
	@Override
	public void doDuring(int curTime)
	{
		this.mod.zoomDoDuring(curTime);
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		this.mod.zoomDoAfter(curTime);
		
	}
	
}
