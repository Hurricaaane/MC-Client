package net.minecraft.src;

import java.io.File;
import java.io.IOException;

import eu.ha3.easy.EdgeModel;
import eu.ha3.easy.EdgeTrigger;
import eu.ha3.mc.convenience.Ha3KeyManager;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.util.property.simple.ConfigProperty;

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

public class MinapticsHaddon extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents
{
	private Minecraft mc;
	
	private Ha3KeyManager keyManager;
	
	private MinapticsMouseFilter mouseFilterXAxis;
	private MinapticsMouseFilter mouseFilterYAxis;
	
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
	private boolean wasAlreadySmoothing;
	
	private float smootherLevel;
	private float smootherIntensity;
	
	private MinapticsVariator VAR;
	private ConfigProperty memory;
	private boolean isHolding;
	
	private boolean trySave;
	
	private EdgeTrigger changeFOVEdge;
	
	@Override
	public void onLoad()
	{
		this.mc = Minecraft.getMinecraft();
		this.keyManager = new Ha3KeyManager();
		
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
		this.smootherIntensity = 0.5f;
		
		/*this.wasMouseSensitivity = 0;
		this.wasAlreadySmoothing = false;
		
		this.zoomTime = 0;
		this.eventNum = 0;
		this.eventNumOnZoom = 0;
		this.lastTime = 0;
		this.basePlayerPitch = 0;*/
		
		this.fovLevelTransition = this.fovLevel;
		this.fovLevelSetup = this.fovLevel;
		
		KeyBinding zoomKeyBinding = new KeyBinding("key.zoom", this.VAR.ZOOM_KEY);
		manager().addKeyBinding(zoomKeyBinding, "Zoom (Minaptics)");
		this.keyManager.addKeyBinding(zoomKeyBinding, new MinapticsZoomBinding(this));
		
		if (this.VAR.SMOOTHER_ENABLE)
		{
			this.mouseFilterXAxis = new MinapticsMouseFilter();
			this.mouseFilterYAxis = new MinapticsMouseFilter();
			
			updateSmootherStatus();
			try
			{
				// mouseFilterXAxis
				// mouseFilterYAxis
				util().setPrivateValueLiteral(
					net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "u", 9, this.mouseFilterXAxis);
				util().setPrivateValueLiteral(
					net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "v", 10, this.mouseFilterYAxis);
			}
			catch (PrivateAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
	}
	
	private void setCameraZoom(float value)
	{
		try
		{
			// debugCamFOV
			// prevDebugCamFOV
			util().setPrivateValueLiteral(
				net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "L", 26, value);
			util().setPrivateValueLiteral(
				net.minecraft.src.EntityRenderer.class, this.mc.entityRenderer, "M", 27, value);
			
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
			if (this.smootherLevel == 0f)
			{
				doForceSmoothCamera();
			}
			
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
	
	@Override
	public void onKey(KeyBinding event)
	{
		this.keyManager.handleKeyDown(event);
		
	}
	
	private void zoomToggle()
	{
		this.isZoomed = !this.isZoomed;
		
		if (this.isZoomed)
		{
			this.wasMouseSensitivity = this.mc.gameSettings.mouseSensitivity;
			
			if (this.VAR.SMOOTHER_ENABLE)
				if (this.smootherLevel != 0f || !this.VAR.SMOOTHER_WHILE_ZOOMED)
				{
					if (this.smootherLevel == 0f)
					{
						this.wasAlreadySmoothing = this.mc.gameSettings.smoothCamera;
					}
					
					this.mc.gameSettings.smoothCamera = true;
					
				}
			
		}
		else
		{
			this.mc.gameSettings.mouseSensitivity = this.wasMouseSensitivity;
			
			if (this.VAR.SMOOTHER_ENABLE)
				if (this.smootherLevel == 0f)
				{
					this.mc.gameSettings.smoothCamera = this.wasAlreadySmoothing;
					
					doLetSmoothCamera();
					
				}
			
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
		this.keyManager.handleRuntime();
	}
	
	public void zoomDoBefore()
	{
		if (util().isCurrentScreen(net.minecraft.src.GuiChat.class))
			return;
		
		if (util().isCurrentScreen(net.minecraft.src.GuiInventory.class)
			|| util().isCurrentScreen(net.minecraft.src.GuiContainerCreative.class))
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
	
	private void updateSmootherStatus()
	{
		if (this.smootherLevel == 0f)
		{
			doLetSmoothCamera();
			this.mc.gameSettings.smoothCamera = false;
			
		}
		else
		{
			this.mc.gameSettings.smoothCamera = true;
			doForceSmoothCamera();
			
		}
		
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
	
	private void doForceSmoothCamera()
	{
		if (!this.VAR.SMOOTHER_ENABLE)
			return;
		
		float mixSensitivity = this.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
		mixSensitivity = mixSensitivity * mixSensitivity * mixSensitivity * 8f;
		float smoothBase =
			this.smootherLevel == 0f ? this.VAR.SMOOTHER_INTENSITY_IDLE : (1f - this.smootherLevel * 0.999f)
				* this.smootherIntensity;
		
		if (this.isZoomed)
		{
			smoothBase = smoothBase * 1 / this.fovLevelSetup;
		}
		
		float cSmooth = mixSensitivity * smoothBase;
		if (cSmooth > 1f)
		{
			cSmooth = 1f;
		}
		
		this.mouseFilterXAxis.force(cSmooth);
		this.mouseFilterYAxis.force(cSmooth);
	}
	
	private void doLetSmoothCamera()
	{
		this.mouseFilterXAxis.let();
		this.mouseFilterYAxis.let();
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