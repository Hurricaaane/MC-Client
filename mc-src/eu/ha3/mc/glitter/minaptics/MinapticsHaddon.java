package eu.ha3.mc.glitter.minaptics;

import java.io.File;
import java.io.IOException;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.convenience.Ha3KeyManager_2;
import eu.ha3.mc.haddon.Identity;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.OperatorKeyer;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.implem.HaddonIdentity;
import eu.ha3.mc.haddon.implem.HaddonImpl;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import eu.ha3.util.property.simple.ConfigProperty;

public class MinapticsHaddon extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents
{
	// Identity
	protected final String NAME = "Minaptics";
	protected final int VERSION = 0;
	protected final String FOR = "1.7.2";
	protected final String ADDRESS = "http://glitter.ha3.eu";
	protected final Identity identity = new HaddonIdentity(this.NAME, this.VERSION, this.FOR, this.ADDRESS);
	
	private Minecraft mc;
	
	private Ha3KeyManager_2 keyManager = new Ha3KeyManager_2();
	
	private float fovLevel;
	private float fovLevelTransition;
	private float fovLevelSetup;
	
	private boolean isZoomed;
	private int eventNumOnZoom;
	
	private long zoomTime;
	
	private long lastTime;
	
	private float basePlayerPitch;
	
	private int eventNum;
	
	private float wasMouseSensitivity;
	
	private MinapticsVariator VAR;
	private ConfigProperty memory;
	private boolean isHolding;
	
	private boolean trySave;
	
	private EdgeTrigger changeFOVEdge;
	
	@Override
	public Identity getIdentity()
	{
		return this.identity;
	}
	
	@Override
	public void onLoad()
	{
		util().registerPrivateSetter("debugCamFOV", EntityRenderer.class, -1, "debugCamFOV", "field_78493_M", "P");
		util().registerPrivateSetter(
			"prevDebugCamFOV", EntityRenderer.class, -1, "prevDebugCamFOV", "field_78494_N", "Q");
		
		this.mc = Minecraft.getMinecraft();
		
		this.memory = new ConfigProperty();
		this.memory.setProperty("fov_level", 0.3f);
		this.memory.commit();
		
		this.changeFOVEdge = new EdgeTrigger(new EdgeModel() {
			@Override
			public void onTrueEdge()
			{
			}
			
			@Override
			public void onFalseEdge()
			{
				fixFov();
			}
		});
		
		// Load memory from source
		try
		{
			this.memory.setSource(new File(Minecraft.getMinecraft().mcDataDir, "minaptics_memory.cfg")
				.getCanonicalPath());
			this.memory.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused memory not to work: " + e.getMessage());
		}
		
		this.VAR = new MinapticsVariator();
		File configFile = new File(Minecraft.getMinecraft().mcDataDir, "minaptics.cfg");
		if (configFile.exists())
		{
			log("Config file found. Loading...");
			try
			{
				ConfigProperty config = new ConfigProperty();
				config.setSource(configFile.getCanonicalPath());
				config.load();
				
				MinapticsVariator var = new MinapticsVariator();
				var.loadConfig(config);
				
				this.VAR = var;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			log("Loaded.");
		}
		
		//
		
		this.fovLevel = this.memory.getFloat("fov_level");
		
		this.fovLevelTransition = this.fovLevel;
		this.fovLevelSetup = this.fovLevel;
		
		KeyBinding zoomKeyBinding = new KeyBinding("Minaptics Zoom", this.VAR.ZOOM_KEY, "key.categories.misc");
		
		((OperatorKeyer) op()).addKeyBinding(zoomKeyBinding);
		this.keyManager.addKeyBinding(zoomKeyBinding, new MinapticsZoomBinding(this));
		
		((OperatorCaster) op()).setFrameEnabled(true);
		((OperatorCaster) op()).setTickEnabled(true);
		
	}
	
	private void setCameraZoom(float value)
	{
		try
		{
			util().setPrivate(this.mc.entityRenderer, "debugCamFOV", value);
			util().setPrivate(this.mc.entityRenderer, "prevDebugCamFOV", value);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
		return;
	}
	
	@Override
	public void onFrame(float semi)
	{
		if (this.isZoomed)
		{
			this.mc.gameSettings.mouseSensitivity = doChangeSensitivity(this.wasMouseSensitivity);
		}
		
		boolean shouldChangeFOV = shouldChangeFOV();
		if (shouldChangeFOV)
		{
			fixFov();
		}
		else
		{
			if (!this.isZoomed && this.trySave)
			{
				saveMemory();
				this.trySave = false;
			}
		}
		
		this.changeFOVEdge.signalState(shouldChangeFOV);
		
	}
	
	private void fixFov()
	{
		float fov = 70f;
		fov += this.mc.gameSettings.fovSetting * 40f;
		
		if (this.mc.thePlayer.isInsideOfMaterial(Material.water))
		{
			fov = fov * 60f / 70f;
		}
		
		setCameraZoom((1f - doChangeFOV(1f)) * -1 * fov);
	}
	
	private void zoomToggle()
	{
		this.isZoomed = !this.isZoomed;
		
		if (this.isZoomed)
		{
			this.wasMouseSensitivity = this.mc.gameSettings.mouseSensitivity;
		}
		else
		{
			this.mc.gameSettings.mouseSensitivity = this.wasMouseSensitivity;
		}
		
		if (System.currentTimeMillis() - this.zoomTime > this.VAR.ZOOM_DURATION)
		{
			this.zoomTime = System.currentTimeMillis();
		}
		else
		{
			this.zoomTime = System.currentTimeMillis() * 2 - this.zoomTime - this.VAR.ZOOM_DURATION;
		}
		
	}
	
	@Override
	public void onTick()
	{
		this.keyManager.onTick();
	}
	
	public void zoomDoBefore()
	{
		if (util().getCurrentScreen() instanceof GuiChat)
			return;
		
		if (util().getCurrentScreen() instanceof GuiContainer)
			return;
		
		if (!this.isZoomed)
		{
			zoomToggle();
			this.eventNumOnZoom = this.eventNum;
			
		}
		else if (!this.VAR.SLIDER_ENABLE && this.isZoomed)
		{
			zoomToggle();
		}
	}
	
	public void zoomDoDuring(int timeKey)
	{
		if (this.VAR.SLIDER_ENABLE && !this.VAR.NOTOGGLE_ENABLE && timeKey >= this.VAR.TWEAK_TRIGGER && !this.isHolding)
		{
			this.isHolding = true;
			
			this.basePlayerPitch = this.mc.thePlayer.rotationPitch;
			this.lastTime = System.currentTimeMillis();
		}
		else if (timeKey >= this.VAR.TWEAK_TRIGGER && this.isHolding)
		{
			if (this.mc.gameSettings.thirdPersonView == 0)
			{
				float diffPitch = this.basePlayerPitch - this.mc.thePlayer.rotationPitch;
				
				this.fovLevelSetup = this.fovLevel - diffPitch * 0.5f;
				
				if (this.fovLevelSetup < this.VAR.FOV_MIN)
				{
					this.fovLevelSetup = this.VAR.FOV_MIN;
				}
				else if (this.fovLevelSetup > this.VAR.FOV_MAX)
				{
					this.fovLevelSetup = this.VAR.FOV_MAX;
				}
			}
		}
	}
	
	public void zoomDoAfter(int timeKey)
	{
		if (!this.VAR.SLIDER_ENABLE && this.VAR.NOTOGGLE_ENABLE)
		{
			zoomToggle();
		}
		else if (timeKey >= this.VAR.TWEAK_TRIGGER)
		{
			this.fovLevel = this.fovLevelSetup;
			this.memory.setProperty("fov_level", this.fovLevelSetup);
			
		}
		else
		{
			if (this.isZoomed && this.eventNumOnZoom != this.eventNum)
			{
				zoomToggle();
				this.trySave = true;
			}
			
		}
		this.isHolding = false;
		
		this.eventNum++;
	}
	
	private boolean shouldChangeFOV()
	{
		return this.isHolding || this.isZoomed || System.currentTimeMillis() - this.zoomTime < this.VAR.ZOOM_DURATION;
		
	}
	
	private float doChangeFOV(float inFov)
	{
		float baseLevel;
		float delta = (System.currentTimeMillis() - this.lastTime) / 1000f;
		
		delta = delta * 4f;
		
		if (delta > 1f)
		{
			delta = 1f;
		}
		
		this.lastTime = System.currentTimeMillis();
		
		this.fovLevelTransition = this.fovLevelTransition + (this.fovLevelSetup - this.fovLevelTransition) * delta;
		
		baseLevel = this.fovLevelTransition;
		
		if (System.currentTimeMillis() - this.zoomTime >= this.VAR.ZOOM_DURATION)
		{
			if (this.isZoomed)
				return inFov * baseLevel;
			else
				return inFov * 1f;
		}
		
		float flushtrum = (System.currentTimeMillis() - this.zoomTime) / (float) this.VAR.ZOOM_DURATION;
		
		if (!this.isZoomed)
		{
			flushtrum = 1f - flushtrum;
		}
		
		flushtrum = flushtrum * flushtrum;
		
		return inFov * (1f - (1f - this.fovLevel) * flushtrum);
	}
	
	private float doChangeSensitivity(float f1)
	{
		return f1 * (float) Math.max(0.5, this.fovLevelSetup);
		
	}
	
	private void saveMemory()
	{
		// If there were changes...
		if (this.memory.commit())
		{
			log("Saving configuration...");
			
			// Write changes on disk.
			this.memory.save();
		}
		
	}
	
	public static void log(String contents)
	{
		System.out.println("(Minaptics) " + contents);
		
	}
	
}