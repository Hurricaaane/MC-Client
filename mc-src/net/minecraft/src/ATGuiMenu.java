package net.minecraft.src;

import java.io.File;

import net.minecraft.client.Minecraft;

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

public class ATGuiMenu extends GuiScreen
{
	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;
	private ATGuiSlotPack packSlotContainer;
	
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle;
	
	private ATHaddon mod;
	
	/** The ID of the button that has been pressed. */
	private int buttonId;
	
	private int selectedSlot;
	private String tip;
	
	public ATGuiMenu(GuiScreen par1GuiScreen, ATHaddon haddon)
	{
		this.selectedSlot = -1;
		
		this.screenTitle = "Audiotori";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.mod = haddon;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		this.packSlotContainer = new ATGuiSlotPack(this);
		
		StringTranslate stringtranslate = StringTranslate.getInstance();
		
		final int _GAP = 2;
		final int _UNIT = 20;
		final int _WIDTH = 155 * 2;
		
		final int _MIX = _GAP + _UNIT;
		
		final int _LEFT = this.width / 2 - _WIDTH / 2;
		final int _RIGHT = this.width / 2 + _WIDTH / 2;
		
		/*Map<String, MAtExpansion> expansions = this.mod.getExpansionManager().getExpansions();
		int id = 0;
		

		List<String> sortedNames = new ArrayList<String>(expansions.keySet());
		Collections.sort(sortedNames);
		
		for (int expansionIndex = this.pageFromZero * this.IDS_PER_PAGE; expansionIndex < this.pageFromZero
			* this.IDS_PER_PAGE + this.IDS_PER_PAGE
			&& expansionIndex < sortedNames.size(); expansionIndex++)
		{
			final String uniqueIdentifier = sortedNames.get(expansionIndex);
			final MAtExpansion expansion = expansions.get(uniqueIdentifier);
			this.expansionList.add(expansion);
			
			HGuiSliderControl sliderControl =
				new HGuiSliderControl(
					id, _LEFT + _MIX, _MIX * (id + 1), _WIDTH - _MIX * 2, _UNIT, "", expansion.getVolume() * 0.5f);
			sliderControl.setListener(new HSliderListener() {
				@Override
				public void sliderValueChanged(HGuiSliderControl slider, float value)
				{
					expansion.setVolume(value * 2);
					if (value != 0f && !expansion.isRunning())
					{
						expansion.turnOn();
					}
					slider.updateDisplayString();
					
				}
				
				@Override
				public void sliderPressed(HGuiSliderControl hGuiSliderControl)
				{
				}
				
				@Override
				public void sliderReleased(HGuiSliderControl hGuiSliderControl)
				{
					if (MAtGuiMenu.this.mod.getConfig().getBoolean("sound.autopreview"))
					{
						expansion.playSample();
					}
				}
			});
			
			sliderControl.setDisplayStringProvider(new HDisplayStringProvider() {
				@Override
				public String provideDisplayString()
				{
					String display = expansion.getFriendlyName() + ": ";
					if (expansion.getVolume() == 0f)
					{
						if (expansion.isRunning())
						{
							display = display + "Will be disabled";
						}
						else
						{
							display = display + "Disabled";
						}
					}
					else
					{
						display = display + (int) Math.floor(expansion.getVolume() * 100) + "%";
					}
					
					return display;
				}
			});
			sliderControl.updateDisplayString();
			
			this.controlList.add(sliderControl);
			
			this.controlList.add(new GuiButton(400 + id - 1, _RIGHT - _UNIT, _MIX * (id + 1), _UNIT, _UNIT, "?"));
			
			id++;
			
		}
		
		this.controlList.add(new GuiButton(220, _RIGHT - _UNIT, _MIX * (this.IDS_PER_PAGE + 2), _UNIT, _UNIT, this.mod
			.getConfig().getBoolean("sound.autopreview") ? "^o^" : "^_^"));
		
		final int _PREVNEWTWIDTH = _WIDTH / 3;
		
		if (this.pageFromZero != 0)
		{
			this.controlList.add(new GuiButton(
				201, _LEFT + _MIX, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, stringtranslate
					.translateKey("Previous")));
		}
		if (this.pageFromZero * this.IDS_PER_PAGE + this.IDS_PER_PAGE < sortedNames.size())
		{
			this.controlList.add(new GuiButton(
				202, _RIGHT - _MIX - _PREVNEWTWIDTH, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT,
				stringtranslate.translateKey("Next")));
		}*/
		
		final int _ASPLIT = 2;
		final int _AWID = _WIDTH / _ASPLIT - _GAP * (_ASPLIT - 1) / 2;
		
		final int _SEPARATOR = 4;
		final int _HEIGHT = this.height;
		
		this.controlList.add(new GuiButton(210, _LEFT, _HEIGHT - _SEPARATOR - _MIX * 2, _AWID, _UNIT, this.mod
			.getConfig().getBoolean("start.enabled") ? "Start Enabled: ON" : "Start Enabled: OFF"));
		
		/*this.controlList.add(new GuiButton(
			211, _LEFT + _AWID + _GAP, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3), _AWID, _UNIT, this.mod
				.getConfig().getBoolean("reversed.controls") ? "Menu: Hold Down Key" : "Menu: Press Key"));
		*/
		
		final int _TURNOFFWIDTH = _WIDTH / 5;
		
		this.controlList.add(new GuiButton(200, _LEFT + _MIX, _HEIGHT - _SEPARATOR - _MIX * 1, _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
		
		this.controlList.add(new GuiButton(
			213, _RIGHT - _TURNOFFWIDTH - _MIX, _HEIGHT - _SEPARATOR - _MIX * 2, _TURNOFFWIDTH, _UNIT, "Load"));
		
		this.controlList.add(new GuiButton(
			212, _RIGHT - _TURNOFFWIDTH - _MIX, _HEIGHT - _SEPARATOR - _MIX * 1, _TURNOFFWIDTH, _UNIT, "Unload"));
		
		//this.screenTitle = stringtranslate.translateKey("controls.title");
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 200)
		{
			// This triggers onGuiClosed
			this.mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 210)
		{
			boolean newEnabledState = !this.mod.getConfig().getBoolean("start.enabled");
			this.mod.getConfig().setProperty("start.enabled", newEnabledState);
			par1GuiButton.displayString = newEnabledState ? "Start Enabled: ON" : "Start Enabled: OFF";
			this.mod.saveConfig();
		}
		else if (par1GuiButton.id == 212)
		{
			this.mod.getPackManager().deactivate();
		}
		else if (par1GuiButton.id == 213)
		{
			File[] files =
				{
					new File(Minecraft.getMinecraftDir(), "audiotori/substitute/"),
					new File(Minecraft.getMinecraftDir(), "audiotori/pony/") };
			this.mod.getPackManager().feedAndActivateAndSay(files);
		}
		else
		{
			this.packSlotContainer.actionPerformed(par1GuiButton);
		}
	}
	
	@Override
	public void onGuiClosed()
	{
	}
	
	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		if (this.buttonId >= 0)
		{
		}
		else
		{
			super.mouseClicked(par1, par2, par3);
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.tip = null;
		this.packSlotContainer.drawScreen(par1, par2, par3);
		drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 0xffffff);
		drawCenteredString(this.fontRenderer, "Top layer (overrides)", this.width / 2, 20, 0xA0A0A0);
		drawCenteredString(this.fontRenderer, "Bottom layer (compliants)", this.width / 2, this.height - 60, 0xA0A0A0);
		
		super.drawScreen(par1, par2, par3);
		
		if (this.tip != null)
		{
			displayFloatingNote(this.tip, par1, par2);
		}
	}
	
	private void displayFloatingNote(String tipContents, int par2, int par3)
	{
		if (tipContents != null)
		{
			int var5 = par3 - 12;
			int var6 = this.fontRenderer.getStringWidth(tipContents);
			int var4 = par2 - var6 / 2;
			
			int computedX = var4;
			int computedWidth = var6 + 3;
			int computedXmost = computedX + computedWidth;
			if (computedX < 0)
			{
				computedX = 3;
			}
			else if (computedXmost > this.width)
			{
				computedX = computedX - computedXmost + this.width;
			}
			drawGradientRect(computedX - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
			this.fontRenderer.drawStringWithShadow(tipContents, computedX, var5, -1);
		}
	}
	
	public int getSelectedSlot()
	{
		return this.selectedSlot;
	}
	
	public void setSelected(int elementId)
	{
		this.selectedSlot = elementId;
		
	}
	
	public int getSize()
	{
		return this.mod.getPackManager().getPackCount();
	}
	
	public ATPack getPack(int id)
	{
		return this.mod.getPackManager().getPack(id);
	}
	
	public String inputTip(String tip)
	{
		return this.tip = tip;
	}
	
}
