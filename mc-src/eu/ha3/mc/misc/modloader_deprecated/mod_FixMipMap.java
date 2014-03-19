package eu.ha3.mc.misc.modloader_deprecated;

import org.lwjgl.opengl.GL11;

import eu.ha3.mc.glitter.Minecraft;

/* xw-placeholder */

public class mod_FixMipMap extends BaseMod
{
	@Override
	public String getVersion() { return ""; }
	
	@Override
	public void load() { ModLoader.setInGameHook(this, true, false); }
	
	// Call on every frame...
	@Override
	public boolean onTickInGame(float sub, Minecraft mc) { GL11.glDisable(GL11.GL_BLEND); return true; }
}
