package eu.ha3.mc.misc.preface;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.haddon.SupportsTickEvents;

/* xw-placeholder */

public class PrefaceHaddon extends HaddonImpl implements SupportsTickEvents
{
	private static final PrefaceHaddon instance = new PrefaceHaddon();
	
	private boolean defined;
	private EdgeTrigger trigger;
	
	private PrefaceHaddon()
	{
		this.defined = false;
		this.trigger = new EdgeTrigger(new EdgeModel() {
			
			@Override
			public void onTrueEdge()
			{
				open();
			}
			
			@Override
			public void onFalseEdge()
			{
			}
		});
		
	}
	
	public static PrefaceHaddon getInstance()
	{
		return instance;
		
	}
	
	public boolean isDefined()
	{
		return this.defined;
		
	}
	
	public void define()
	{
		if (isDefined())
			return;
		
		this.defined = true;
		System.out.println("HaddonPreface is now defined.");
		
	}
	
	@Override
	public void onLoad()
	{
		// Is never called, do not use.
		
	}
	
	@Override
	public void onTick()
	{
		this.trigger.signalState(util().areKeysDown(29, 42, 35));
		System.out.println("ff");
		
	}
	
	protected void open()
	{
		Minecraft.getMinecraft().displayGuiScreen(null);
		System.out.println("ff");
		
	}
	
}
