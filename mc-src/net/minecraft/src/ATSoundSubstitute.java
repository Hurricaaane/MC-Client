package net.minecraft.src;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

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

public class ATSoundSubstitute extends SoundPoolEntry implements ATSoundWrapper
{
	private SoundPoolEntry original;
	private String soundName_SUB;
	private URL soundUrl_SUB;
	
	public ATSoundSubstitute(SoundPoolEntry original, String newSoundName, File substituant)
	{
		super(original.func_110458_a(), original.func_110457_b());
		
		this.original = original;
		
		try
		{
			if (substituant.exists())
			{
				// Why is there a random bullcrap here?
				// For some reason, sometimes sounds get somehow cached somewhere I'm
				// not sure of. This random bullcrap prevents cached sounds of previous
				// loading sequences to replace this one
				this.soundName_SUB = new Random().nextInt(99999) + newSoundName;
				this.soundUrl_SUB = substituant.toURI().toURL();
			}
			else
			{
				System.out.println("(ATS) Tried to substitute "
					+ original.func_110458_a() + " but the file " + substituant.toString() + " does not exist!");
			}
		}
		catch (MalformedURLException e)
		{
		}
		
	}
	
	public ATSoundSubstitute(SoundPoolEntry original, String newSoundName, ATConversible conv)
	{
		super(original.func_110458_a(), original.func_110457_b());
		
		this.original = original;
		
		if (conv.isFile())
		{
			try
			{
				File substituant = conv.getFile();
				if (substituant.exists())
				{
					// Why is there a random bullcrap here?
					// For some reason, sometimes sounds get somehow cached somewhere I'm
					// not sure of. This random bullcrap prevents cached sounds of previous
					// loading sequences to replace this one
					this.soundName_SUB = new Random().nextInt(99999) + newSoundName;
					this.soundUrl_SUB = substituant.toURI().toURL();
				}
				else
				{
					System.out.println("(ATS) Tried to substitute "
						+ original.func_110458_a() + " but the file " + substituant.toString() + " does not exist!");
				}
			}
			catch (MalformedURLException e)
			{
			}
		}
		else
		{
			URL substituant = conv.getURL();
			// Why is there a random bullcrap here?
			// For some reason, sometimes sounds get somehow cached somewhere I'm
			// not sure of. This random bullcrap prevents cached sounds of previous
			// loading sequences to replace this one
			this.soundName_SUB = new Random().nextInt(99999) + newSoundName;
			this.soundUrl_SUB = substituant;
		}
	}
	
	public SoundPoolEntry getOriginal()
	{
		if (this.original instanceof ATSoundSubstitute)
		{
			System.out.println("(ATSS) Nesting occured with "
				+ func_110457_b().toString() + " / " + this.original.func_110457_b().toString() + " !");
			return ((ATSoundSubstitute) this.original).getOriginal();
			
		}
		return this.original;
	}
	
	@Override
	public SoundPoolEntry getSource()
	{
		return getOriginal();
	}
	
	@Override
	public String func_110458_a()
	{
		return this.soundName_SUB;
	}
	
	@Override
	public URL func_110457_b()
	{
		return this.soundUrl_SUB;
	}
}
