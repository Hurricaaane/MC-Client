package eu.ha3.mc.glitter.quickthirdperson;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import eu.ha3.mc.convenience.Ha3KeyActions;
import eu.ha3.mc.convenience.Ha3KeyManager_2;
import eu.ha3.mc.haddon.Identity;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.OperatorKeyer;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.implem.HaddonIdentity;
import eu.ha3.mc.haddon.implem.HaddonImpl;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;

/* xw-placeholder */

public class QuickThirdPersonHaddon extends HaddonImpl
	implements SupportsFrameEvents, SupportsTickEvents, Ha3KeyActions
{
	// Identity
	protected final String NAME = "QuickThirdPerson";
	protected final int VERSION = 0;
	protected final String FOR = "1.7.2";
	protected final String ADDRESS = "http://glitter.ha3.eu";
	protected final Identity identity = new HaddonIdentity(this.NAME, this.VERSION, this.FOR, this.ADDRESS);
	
	private float directivePitch;
	private float directiveYaw;
	
	private float desiredPitch;
	private float desiredYaw;
	
	private float modifiedDesiredPitch;
	private float modifiedDesiredYaw;
	
	private boolean wasEnabled;
	private KeyBinding bind;
	
	private Ha3KeyManager_2 keyManager = new Ha3KeyManager_2();
	private boolean lockPlayerDirection;
	private boolean viewAsDirection;
	
	private boolean activate;
	private int previousTPmode;
	
	@Override
	public Identity getIdentity()
	{
		return this.identity;
	}
	
	@Override
	public void onLoad()
	{
		this.lockPlayerDirection = true;
		this.viewAsDirection = false;
		
		this.previousTPmode = 0;
		
		util().registerPrivateSetter("debugCamYaw", EntityRenderer.class, -1, "debugCamYaw", "field_78485_D", "G");
		util().registerPrivateSetter(
			"prevDebugCamYaw", EntityRenderer.class, -1, "prevDebugCamYaw", "field_78486_E", "H");
		util().registerPrivateSetter("debugCamPitch", EntityRenderer.class, -1, "debugCamPitch", "field_78487_F", "I");
		util().registerPrivateSetter(
			"prevDebugCamPitch", EntityRenderer.class, -1, "prevDebugCamPitch", "field_78488_G", "J");
		
		//this.bind = new KeyBinding("key.quickthirdperson", 47);
		//manager().addKeyBinding(this.bind, "QTP Forward");
		
		this.bind = new KeyBinding("QTP Forward", 47, "key.categories.misc");
		((OperatorKeyer) op()).addKeyBinding(this.bind);
		this.keyManager.addKeyBinding(this.bind, this);
		
		((OperatorCaster) op()).setFrameEnabled(true);
		((OperatorCaster) op()).setTickEnabled(true);
		
	}
	
	@Override
	public void onTick()
	{
		this.keyManager.onTick();
	}
	
	@Override
	public void onFrame(float semi)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		int tpMode = mc.gameSettings.thirdPersonView;
		boolean shouldEnable = this.activate && tpMode > 0 && tpMode == this.previousTPmode;
		this.previousTPmode = mc.gameSettings.thirdPersonView;
		
		Minecraft.getMinecraft().gameSettings.debugCamEnable = shouldEnable;
		
		if (!shouldEnable)
		{
			this.wasEnabled = false;
			this.activate = false;
			return;
			
		}
		
		if (tpMode == 1)
		{
			thirdPersonAlgorithmA();
		}
		else if (tpMode == 2)
		{
			thirdPersonAlgorithmB();
		}
		
	}
	
	private void thirdPersonAlgorithmA()
	{
		
		if (!this.wasEnabled)
		{
			this.wasEnabled = true;
			
			copyDirection();
			
			resetDesiredAngles(this.directivePitch, this.directiveYaw);
			
		}
		
		if (this.lockPlayerDirection)
			if (util().isCurrentScreen(null))
			{
				gatherDesiredAngles();
			}
		
		if (this.viewAsDirection)
		{
			copyViewToDirection();
			this.viewAsDirection = false;
			
		}
		
		if (this.lockPlayerDirection)
		{
			applyDirection();
		}
		else
		{
			copyDirection();
		}
		
		float viewOffsetsYaw = 180f;
		
		try
		{
			util().setPrivate(Minecraft.getMinecraft().entityRenderer, "debugCamYaw", this.desiredYaw + viewOffsetsYaw);
			util().setPrivate(
				Minecraft.getMinecraft().entityRenderer, "prevDebugCamYaw", this.desiredYaw + viewOffsetsYaw);
			util().setPrivate(Minecraft.getMinecraft().entityRenderer, "debugCamPitch", this.desiredPitch);
			util().setPrivate(Minecraft.getMinecraft().entityRenderer, "prevDebugCamPitch", this.desiredPitch);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void thirdPersonAlgorithmB()
	{
		
		if (!this.wasEnabled)
		{
			this.wasEnabled = true;
			
			copyDirection();
			
			resetDesiredAngles(this.directivePitch, this.directiveYaw);
			
		}
		
		if (util().isCurrentScreen(null))
		{
			gatherDesiredAngles();
		}
		
		if (this.viewAsDirection)
		{
			copyViewToDirection();
			applyDirection();
			this.viewAsDirection = false;
			
		}
		
		if (this.lockPlayerDirection)
		{
			similarizeDirection();
			applyDirection();
		}
		else
		{
			applyDirection();
		}
		
		float viewOffsetsYaw = 180f;
		
		try
		{
			// debugCamYaw;
			// prevDebugCamYaw;
			// debugCamPitch;
			// prevDebugCamPitch;
			
			util().setPrivate(Minecraft.getMinecraft().entityRenderer, "debugCamYaw", this.desiredYaw + viewOffsetsYaw);
			util().setPrivate(
				Minecraft.getMinecraft().entityRenderer, "prevDebugCamYaw", this.desiredYaw + viewOffsetsYaw);
			util().setPrivate(Minecraft.getMinecraft().entityRenderer, "debugCamPitch", this.desiredPitch);
			util().setPrivate(Minecraft.getMinecraft().entityRenderer, "prevDebugCamPitch", this.desiredPitch);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void copyViewToDirection()
	{
		this.directivePitch = this.desiredPitch;
		this.directiveYaw = this.desiredYaw;
		normalizeDirectivePitch();
		
	}
	
	private void applyDirection()
	{
		if (!this.wasEnabled)
			return;
		
		EntityLivingBase ply = Minecraft.getMinecraft().thePlayer;
		
		ply.rotationPitch = this.directivePitch;
		ply.rotationYaw = this.directiveYaw;
		
	}
	
	private void copyDirection()
	{
		if (!this.wasEnabled)
			return;
		
		EntityLivingBase ply = Minecraft.getMinecraft().thePlayer;
		
		this.directivePitch = ply.rotationPitch;
		this.directiveYaw = ply.rotationYaw;
		normalizeDirectivePitch();
		
	}
	
	private void similarizeDirection()
	{
		if (!this.wasEnabled)
			return;
		
		this.directivePitch -= this.modifiedDesiredPitch;
		this.directiveYaw += this.modifiedDesiredYaw;
		normalizeDirectivePitch();
		
	}
	
	private void normalizeDirectivePitch()
	{
		if (this.directivePitch < -90F)
		{
			this.directivePitch = -90F;
		}
		
		if (this.directivePitch > 90F)
		{
			this.directivePitch = 90F;
		}
		
	}
	
	private void gatherDesiredAngles()
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float f1 = f * f * f * 8F;
		float f2 = mc.mouseHelper.deltaX * f1;
		float f3 = mc.mouseHelper.deltaY * f1;
		int l = 1;
		
		if (mc.gameSettings.invertMouse)
		{
			l = -1;
		}
		
		setDesiredAngles(f2, f3 * l);
		
	}
	
	private void resetDesiredAngles(float desiredAnglesPitch, float desiredAnglesYaw)
	{
		this.desiredPitch = desiredAnglesPitch;
		this.desiredYaw = desiredAnglesYaw;
	}
	
	private void setDesiredAngles(float par1, float par2)
	{
		this.modifiedDesiredPitch = par2 * 0.15f;
		this.modifiedDesiredYaw = par1 * 0.15f;
		this.desiredPitch -= this.modifiedDesiredPitch;
		this.desiredYaw += this.modifiedDesiredYaw;
		
		if (this.desiredPitch < -90F)
		{
			this.desiredPitch = -90F;
		}
		
		if (this.desiredPitch > 90F)
		{
			this.desiredPitch = 90F;
		}
	}
	
	@Override
	public void doBefore()
	{
		this.activate = true;
	}
	
	@Override
	public void doDuring(int curTime)
	{
		if (curTime >= 5)
		{
			this.lockPlayerDirection = false;
		}
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < 5)
		{
			this.viewAsDirection = true;
		}
		else
		{
			this.lockPlayerDirection = true;
		}
	}
	
}
