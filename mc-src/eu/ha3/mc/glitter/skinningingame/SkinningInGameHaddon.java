package eu.ha3.mc.glitter.skinningingame;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.Display;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.convenience.Ha3StaticUtilities;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;

/* xw-placeholder */

public class SkinningInGameHaddon extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents
{
	private SkinningSystem system;
	private EdgeTrigger bindTrigger;
	private List<Entry<String, Long>> messageStack;
	
	private boolean previousFocusState;
	private boolean isCaptureEnabled;
	private int ticksRan;
	
	private Minecraft mc;
	
	public SkinningInGameHaddon()
	{
		this.messageStack = new ArrayList<Entry<String, Long>>();
	}
	
	@Override
	public void onLoad()
	{
		this.mc = Minecraft.getMinecraft();
		
		if (isInstalledMLP())
		{
			this.system = new SkinningSystemMLP(this);
		}
		else
		{
			this.system = new SkinningSystemMC(this);
		}
		
		this.bindTrigger = new EdgeTrigger(new EdgeModel() {
			@Override
			public void onTrueEdge()
			{
				toggleCaptureState();
			}
			
			@Override
			public void onFalseEdge()
			{
			}
			
		});
		manager().hookTickEvents(true);
		
	}
	
	private boolean isInstalledMLP()
	{
		if (true)
			return false;
		
		return Ha3StaticUtilities.classExists("Pony", this)
			|| Ha3StaticUtilities.classExists("net.minecraft.src.Pony", this);
	}
	
	@Override
	public void onTick()
	{
		this.bindTrigger.signalState(util().areKeysDown(29, 42, 25));
		this.ticksRan++;
		
		if (this.isCaptureEnabled)
		{
			boolean isRefreshTick = this.ticksRan % 30 == 0;
			
			if (isRefreshTick)
			{
				this.system.update();
			}
			
			if (!Display.isActive() && util().isCurrentScreen(net.minecraft.src.GuiIngameMenu.class))
			{
				Minecraft.getMinecraft().displayGuiScreen(new SkinningInGamePauseGUI());
				
			}
			
			if (this.previousFocusState != Display.isActive())
			{
				this.previousFocusState = Display.isActive();
				
				if (!isRefreshTick && this.previousFocusState == true)
				{
					this.system.update();
				}
				
			}
		}
		
	}
	
	@Override
	public void onFrame(float semi)
	{
		updateMessageStack();
		this.system.render(semi);
		drawMessageStack(semi, 8, 12);
	}
	
	private void toggleCaptureState()
	{
		if (!this.isCaptureEnabled)
		{
			enableCaptureState();
		}
		else
		{
			disableCaptureState();
		}
		
	}
	
	public void enableCaptureState()
	{
		if (this.isCaptureEnabled)
			return;
		this.isCaptureEnabled = true;
		
		this.previousFocusState = true;
		
		this.system.enable();
		manager().hookFrameEvents(true);
		
	}
	
	public void disableCaptureState()
	{
		if (!this.isCaptureEnabled)
			return;
		this.isCaptureEnabled = false;
		
		this.system.disable();
		manager().hookFrameEvents(false);
		
	}
	
	private long getMessageStackTime()
	{
		return System.currentTimeMillis();
		
	}
	
	private void updateMessageStack()
	{
		int i = 0;
		long timer = getMessageStackTime();
		
		while (i < this.messageStack.size())
		{
			if (this.messageStack.get(i).getValue() < timer)
			{
				this.messageStack.remove(i);
			}
			else
			{
				i++;
			}
			
		}
		
	}
	
	private void drawMessageStack(float semi, int xl, int yl)
	{
		FontRenderer renderer = this.mc.fontRenderer;
		
		for (int i = 0; i < this.messageStack.size(); i++)
		{
			renderer.drawStringWithShadow(
				this.messageStack.get(i).getKey(), xl, yl + (renderer.FONT_HEIGHT + 2) * i, 0xFFFFFF);
		}
		
	}
	
	public void addMessageToStack(String message, float seconds)
	{
		this.messageStack.add(new AbstractMap.SimpleEntry<String, Long>(message, getMessageStackTime()
			+ (long) (seconds * 1000)));
		
	}
	
}
