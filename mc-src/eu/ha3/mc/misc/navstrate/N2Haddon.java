package eu.ha3.mc.misc.navstrate;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import eu.ha3.mc.convenience.Ha3KeyManager;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;

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

public class N2Haddon extends HaddonImpl implements SupportsFrameEvents, SupportsKeyEvents
{
	private Ha3KeyManager keyManager;
	private KeyBinding navKeyBinding;
	
	public NavstrateData readData;
	public NavstrateData writeData;
	
	public NavstrateGatherer gatherer;
	int stepLimit;
	
	boolean isOn;
	private boolean needsRebuffering;
	private int bufferedImage;
	private int[] intArray;
	private int intColor;
	private int bufferSize;
	
	long worldTime;
	
	AxisAlignedBB bbox;
	
	@Override
	public void onLoad()
	{
		//if (true)
		//	return;
		
		this.isOn = false;
		this.needsRebuffering = true;
		this.bufferSize = 256;
		
		this.stepLimit = 92;
		
		this.worldTime = 0;
		
		//this.readData = new NavstrateData(128, 48, 128);
		this.writeData = new NavstrateData(128, 48, 128);
		this.gatherer = null;
		
		manager().addKeyBinding(new KeyBinding("key.navstrate", 65), "Navstrate");
		
		this.keyManager = new Ha3KeyManager();
		this.keyManager.addKeyBinding(this.navKeyBinding, new NavstrateKey(this));
		manager().hookFrameEvents(true);
		
		this.bbox = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		
	}
	
	public synchronized void performSnapshot()
	{
		// DEBUG
		//stepLimit = 92;
		
		if (this.gatherer != null)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		int x = (int) Math.floor(mc.thePlayer.posX);
		int y = (int) Math.floor(mc.thePlayer.posY);
		int z = (int) Math.floor(mc.thePlayer.posZ);
		
		this.writeData.shiftData(x - this.writeData.xPos, y - this.writeData.yPos, z - this.writeData.zPos);
		this.gatherer = new NavstrateGatherer();
		this.gatherer.setDaemon(true);
		this.gatherer.setCaller(this);
		this.gatherer.setSleepTime(8);
		this.gatherer.prepareAnalysis(this.writeData, x, y, z, this.stepLimit);
		this.gatherer.start();
	}
	
	public synchronized void finishSnapshot()
	{
		this.gatherer = null;
	}
	
	@Override
	public void onFrame(float f)
	{
		if (this.worldTime != Minecraft.getMinecraft().theWorld.getWorldTime())
		{
			this.keyManager.handleRuntime();
			this.worldTime = Minecraft.getMinecraft().theWorld.getWorldTime();
			
		}
		
		if (!this.isOn)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		int screenXshift = 32;
		int screenYshift = 32;
		int width = 100;
		int tall = 80;
		
		if (this.needsRebuffering)
		{
			this.needsRebuffering = false;
			
			this.bufferedImage =
				mc.renderEngine.allocateAndSetupTexture(new BufferedImage(this.bufferSize, this.bufferSize, 2));
			this.intArray = new int[this.bufferSize * this.bufferSize];
			
		}
		
		for (int i = 0; i < this.bufferSize * this.bufferSize; i++)
		{
			this.intArray[i] = 0;
		}
		
		NavstrateData parData = this.writeData;
		int px = (int) Math.floor(mc.thePlayer.posX);
		int py = (int) Math.floor(mc.thePlayer.posY);
		int pz = (int) Math.floor(mc.thePlayer.posZ);
		
		{
			int itrans_prep = parData.xSize / 2 + px - parData.xPos - width / 2;
			int jtrans_prep = parData.ySize / 2 - parData.yPos;
			int ktrans_prep = parData.zSize / 2 + pz - parData.zPos - tall / 2;
			
			for (int i = 0; i < width; i++)
			{
				int itrans = itrans_prep + i;
				
				for (int k = 0; k < tall; k++)
				{
					int ktrans = ktrans_prep + k;
					
					if (parData.isValidPos(itrans, 0, ktrans))
					{
						int contains = 0;
						int min = this.stepLimit;
						int wall = 0;
						int wallCur = this.stepLimit;
						for (int j = 0; j < parData.ySize; j++)
						{
							int step = parData.getData(itrans, j, ktrans);
							
							if (step > 0)
							{
								if (step < min)
								{
									min = step;
								}
								
								contains++;
								
							}
							else if (step < 0)
							{
								wall++;
								
								if (j == jtrans_prep + py)
								{
									wallCur = -step;
								}
								
							}
							
						}
						
						if (min != this.stepLimit)
						{
							float alphaWork = (float) (this.stepLimit - min) / this.stepLimit;
							if (alphaWork > 1)
							{
								alphaWork = 1;
							}
							else if (alphaWork < 0)
							{
								alphaWork = 0;
							}
							else
							{
								alphaWork = 1 - (float) Math.pow(1 - alphaWork, 2);
							}
							
							int alpha = (int) (alphaWork * 8);
							
							setColor(0, 255, 0, alpha);
							setPixel(i, k, width);
							
						}
						if (contains > 0)
						{
							float alphaWork = (float) contains / 32;
							if (alphaWork > 1)
							{
								alphaWork = 1;
							}
							else
							{
								alphaWork = 1 - (1 - alphaWork) * (1 - alphaWork);
							}
							
							int alpha = (int) (alphaWork * 32);
							
							setColor(0, 255, 0, alpha);
							addPixel(i, k, width);
							
						}
						if (wall > 0)
						{
							float alphaWork = (float) wall / 8;
							if (alphaWork > 1)
							{
								alphaWork = 1;
							}
							else
							{
								alphaWork = alphaWork * alphaWork;
							}
							
							int alpha = (int) (alphaWork * 164);
							
							setColor(0, 255, 128, alpha);
							addPixel(i, k, width);
							
						}
						if (wallCur != this.stepLimit)
						{
							float alphaWork = (float) (this.stepLimit - wallCur) / this.stepLimit * 0.7f + wall / 6;
							if (alphaWork > 1)
							{
								alphaWork = 1;
							}
							else
							{
								alphaWork = 1 - (1 - alphaWork) * (1 - alphaWork);
							}
							
							int alpha = (int) (alphaWork * 128);
							
							setColor(0, 255, 255, alpha);
							setPixel(i, k, width);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		mc.renderEngine.createTextureFromBytes(this.intArray, this.bufferSize, this.bufferSize, this.bufferedImage);
		
		float displayMagnifier = 1f;
		int widthDisplay = (int) (width * displayMagnifier);
		int tallDisplay = (int) (tall * displayMagnifier);
		
		if (true)
		{
			GL11.glEnable(3042 /*GL_BLEND*/);
			GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
			GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
			GL11.glBlendFunc(770, 771);
			//GL11.glShadeModel(7424 /*GL_FLAT*/);
			
			drawQuad(screenXshift, screenYshift, screenXshift + widthDisplay, screenYshift + tallDisplay);
			
		}
		
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		GL11.glBindTexture(3553, this.bufferedImage);
		drawQuadUV(screenXshift, screenYshift, screenXshift + widthDisplay, screenYshift + tallDisplay, width, tall);
		GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
		
		GL11.glShadeModel(7425 /*GL_SMOOTH*/);
		@SuppressWarnings("unchecked")
		List<Entity> entityList =
			mc.theWorld.getEntitiesWithinAABB(
				net.minecraft.src.EntityPlayer.class, this.bbox.setBounds(px - width / 2, py - parData.ySize, // TODO for Y coords, should theorically be ySize / 2
					pz - tall / 2, px + width / 2, py + parData.ySize, pz + tall / 2));
		
		int centerX = screenXshift + width / 2;
		int centerY = screenYshift + tall / 2;
		int radii = tall / 5;
		for (Iterator<Entity> iter = entityList.iterator(); iter.hasNext();)
		{
			//float plyYaw = mc.thePlayer.rotationYaw;
			EntityPlayer player = (EntityPlayer) iter.next();
			int xWorldShift = (int) (px - player.posX);
			int zWorldShift = (int) (pz - player.posZ);
			
			float yaw = (float) ((-90 + player.rotationYaw) / 360F * Math.PI * 2);
			
			if (xWorldShift < width / 2 && zWorldShift < tall / 2)
			{
				Tessellator tessellator = Tessellator.instance;
				{
					int alpha = 192 - (int) (128 * (Math.abs(py - player.posY) / 8));
					if (alpha < 0)
					{
						alpha = 0;
					}
					
					float xA = (float) Math.cos(yaw + Math.PI / 4) * radii;
					float yA = (float) Math.sin(yaw + Math.PI / 4) * radii;
					float xB = (float) Math.cos(yaw - Math.PI / 4) * radii;
					float yB = (float) Math.sin(yaw - Math.PI / 4) * radii;
					
					tessellator.startDrawing(4);
					tessellator.setColorRGBA(0, 255, 255, alpha);
					tessellator.addVertex(centerX + xWorldShift, centerY + zWorldShift, 0.0D);
					tessellator.setColorRGBA(0, 255, 0, 0);
					tessellator.addVertex(centerX + xA + xWorldShift, centerY + yA + zWorldShift, 0.0D);
					tessellator.addVertex(centerX + xB + xWorldShift, centerY + yB + zWorldShift, 0.0D);
					tessellator.draw();
				}
				
				if (player != mc.thePlayer)
				{
					{
						int radix = tall / 15;
						
						float xA = (float) Math.cos(yaw + Math.PI / 4) * radix;
						float yA = (float) Math.sin(yaw + Math.PI / 4) * radix;
						float xB = (float) Math.cos(yaw - Math.PI / 4) * radix;
						float yB = (float) Math.sin(yaw - Math.PI / 4) * radix;
						
						tessellator.startDrawing(4);
						tessellator.setColorRGBA(192, 192, 255, 255);
						tessellator.addVertex(centerX + xWorldShift, centerY + zWorldShift, 0.0D);
						tessellator.setColorRGBA(128, 128, 255, 128);
						tessellator.addVertex(centerX + xA + xWorldShift, centerY + yA + zWorldShift, 0.0D);
						tessellator.addVertex(centerX + xB + xWorldShift, centerY + yB + zWorldShift, 0.0D);
						tessellator.draw();
						
					}
					
					{
						float radix = tall / 25;
						
						int alpha = (int) (128 * (Math.abs(py - player.posY) / 8));
						if (alpha > 128)
						{
							alpha = 128;
						}
						
						float inv = 1f;
						if (py > player.posY)
						{
							inv = -1f;
						}
						
						float ber = 0.9f;
						
						tessellator.startDrawing(4);
						tessellator.setColorRGBA(255, 255, 0, alpha * 0);
						tessellator.addVertex(centerX + xWorldShift, centerY - inv * radix * 0.5 + zWorldShift, 0.0D);
						tessellator.setColorRGBA(255, 255, 0, alpha);
						if (inv > 0)
						{
							tessellator.addVertex(centerX - radix + xWorldShift, centerY
								+ inv * radix * ber + zWorldShift, 0.0D);
						}
						tessellator.addVertex(
							centerX + radix + xWorldShift, centerY + inv * radix * ber + zWorldShift, 0.0D);
						if (inv < 0)
						{
							tessellator.addVertex(centerX - radix + xWorldShift, centerY
								+ inv * radix * ber + zWorldShift, 0.0D);
						}
						tessellator.draw();
					}
					
				}
				
			}
			
		}
		GL11.glShadeModel(7424 /*GL_FLAT*/);
		GL11.glDisable(3042 /*GL_BLEND*/);
		GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
		GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
		
		return;
		
	}
	
	@Override
	public void onKey(KeyBinding event)
	{
		this.keyManager.handleKeyDown(event);
		
	}
	
	protected void drawQuadUV(int i, int j, int k, int l, int width, int height)
	{
		if (i < k)
		{
			int j1 = i;
			i = k;
			k = j1;
		}
		if (j < l)
		{
			int k1 = j;
			j = l;
			l = k1;
		}
		
		double wbf = (double) width / this.bufferSize;
		double hbf = (double) height / this.bufferSize;
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(i, l, 0.0D, 0.0D, hbf);
		tessellator.addVertexWithUV(k, l, 0.0D, wbf, hbf);
		tessellator.addVertexWithUV(k, j, 0.0D, wbf, 0.0D);
		tessellator.addVertexWithUV(i, j, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
		
	}
	
	protected void drawQuad(int i, int j, int k, int l)
	{
		if (i < k)
		{
			int j1 = i;
			i = k;
			k = j1;
		}
		if (j < l)
		{
			int k1 = j;
			j = l;
			l = k1;
		}
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA(0, 0, 0, 32);
		tessellator.addVertex(i, l, 0.0D);
		tessellator.addVertex(k, l, 0.0D);
		tessellator.addVertex(k, j, 0.0D);
		tessellator.addVertex(i, j, 0.0D);
		tessellator.draw();
		
	}
	
	public void setPixel(int i, int j, int width)
	{
		this.intArray[i + j * this.bufferSize] = this.intColor;
		
	}
	
	public void addPixel(int i, int j, int width)
	{
		int prev = this.intArray[i + j * this.bufferSize];
		float a = (this.intColor >> 24 & 0xFF) / 255f;
		
		int ua = (prev >> 24 & 0xFF) + (this.intColor >> 24 & 0xFF);
		int ur = (prev >> 16 & 0xFF) + (int) (a * (this.intColor >> 16 & 0xFF));
		int ug = (prev >> 8 & 0xFF) + (int) (a * (this.intColor >> 8 & 0xFF));
		int ub = (prev & 0xFF) + (int) (a * (this.intColor & 0xFF));
		if (ua > 255)
		{
			ua = 255;
		}
		if (ur > 255)
		{
			ur = 255;
		}
		if (ug > 255)
		{
			ug = 255;
		}
		if (ub > 255)
		{
			ub = 255;
		}
		
		this.intArray[i + j * this.bufferSize] = ua << 24 | ur << 16 | ug << 8 | ub;
		
	}
	
	public void setColor(int r, int g, int b, int a)
	{
		this.intColor = 0x00000000 | a << 24 | r << 16 | g << 8 | b;
	}
	
	public void toggle()
	{
		this.isOn = !this.isOn;
		
	}
	
	public void rescan()
	{
		resetData();
		this.isOn = true;
		performSnapshot();
		
	}
	
	public boolean isOn()
	{
		return this.isOn;
	}
	
	public synchronized void resetData()
	{
		if (this.gatherer != null)
		{
			this.gatherer.interrupt();
		}
		this.gatherer = null;
		
		this.writeData.emptyMemory();
		
	}
	
}
