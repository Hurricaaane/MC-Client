package eu.ha3.mc.misc.logblockvisual;

/* xw-placeholder */

public class LBVisRenderEntity extends Entity
{
	public LBVisRenderEntity(World par1World)
	{
		super(par1World);
		
		this.ignoreFrustumCheck = true;
		
	}
	
	@Override
	public void onEntityUpdate()
	{
		EntityPlayer ply = Minecraft.getMinecraft().thePlayer;
		
		setPosition(ply.posX, ply.posY, ply.posZ);
		
	}
	
	@Override
	protected void entityInit()
	{
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
	}
}
