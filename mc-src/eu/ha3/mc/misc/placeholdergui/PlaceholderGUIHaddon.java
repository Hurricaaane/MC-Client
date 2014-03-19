package eu.ha3.mc.misc.placeholdergui;

import eu.ha3.mc.haddon.SupportsKeyEvents;

/* xw-placeholder */

public class PlaceholderGUIHaddon extends HaddonImpl implements SupportsKeyEvents
{
	private KeyBinding bind;
	
	@Override
	public void onLoad()
	{
		this.bind = new KeyBinding("", 13);
		
		manager().addKeyBinding(this.bind, "Placeholder GUI");
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		if (event == this.bind && event.pressed)
		{
			if (util().isCurrentScreen(PlaceholderGUI.class))
			{
				util().closeCurrentScreen();
				
			}
			else if (util().isCurrentScreen(null))
			{
				Minecraft.getMinecraft().displayGuiScreen(new PlaceholderGUI());
				
			}
			
		}
		
	}
	
}
