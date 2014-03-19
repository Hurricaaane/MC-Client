package eu.ha3.mc.glitter.skinningingame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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

public class SkinningSystemMC implements SkinningSystem
{
	private SkinningInGameHaddon haddon;
	
	private final String skinPath = "/skin_edit.png";
	
	private String textureBefore = null;
	private BufferedImage imageBefore = null;
	
	private Minecraft mc;
	
	public SkinningSystemMC(SkinningInGameHaddon haddon)
	{
		this.haddon = haddon;
		this.mc = Minecraft.getMinecraft();
	}
	
	@Override
	public void enable()
	{
		//this.textureBefore = this.mc.thePlayer.texture;
		this.imageBefore = null;
		update();
		
	}
	
	@Override
	public void disable()
	{
		//String skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + this.mc.thePlayer.username + ".png";
		//this.mc.thePlayer.skinUrl = skinUrl;
		//this.mc.thePlayer.texture = this.textureBefore;
		
	}
	
	@Override
	public void update()
	{
		loadSkinFromFile(this.mc.thePlayer, new File(this.mc.mcDataDir, this.skinPath));
		
	}
	
	@Override
	public void render(float semi)
	{
		this.mc.fontRenderer.drawStringWithShadow("Skin edit mode", 2, 2, 0xffff00);
	}
	
	//
	
	private void loadSkinFromFile(EntityClientPlayerMP player, File file)
	{
		try
		{
			loadSkinFromStream(player, new FileInputStream(file));
		}
		catch (FileNotFoundException e)
		{
			failSkin(e);
		}
		
	}
	
	private void loadSkinFromStream(EntityClientPlayerMP player, InputStream instream)
	{
		try
		{
			BufferedImage bufferedimage = ImageIO.read(instream);
			
			//this.mc.thePlayer.texture = this.skinPath;
			
			//RenderEngine re = this.mc.renderEngine;
			//re.setupTexture(bufferedimage, re.getTexture(this.mc.thePlayer.texture));
			//this.mc.thePlayer.skinUrl = null;
			
			TextureUtil.allocateAndSetupTexture(player.getSkinTexture().getTextureName(), bufferedimage);
			
		}
		catch (Exception e)
		{
			failSkin(e);
			
		}
		finally
		{
			try
			{
				if (instream != null)
				{
					instream.close();
				}
			}
			catch (IOException e)
			{
			}
			
		}
		
	}
	
	private void failSkin(Exception e)
	{
		if (e != null)
		{
			this.haddon.addMessageToStack("Error: " + e.getMessage(), 2);
		}
		else
		{
			this.haddon.addMessageToStack("Error: (?)", 2);
		}
		
	}
	
}