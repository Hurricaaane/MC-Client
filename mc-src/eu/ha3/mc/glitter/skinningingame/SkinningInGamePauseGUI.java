package eu.ha3.mc.glitter.skinningingame;

import org.lwjgl.opengl.Display;

/* xw-placeholder */

public class SkinningInGamePauseGUI extends GuiScreen
{
	@Override
	public void drawDefaultBackground()
	{
	}
	
	@Override
	public void updateScreen()
	{
		if (Display.isActive())
			this.mc.displayGuiScreen(null);
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
}
