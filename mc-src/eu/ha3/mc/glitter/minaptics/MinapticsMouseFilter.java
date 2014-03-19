package eu.ha3.mc.glitter.minaptics;

/* xw-placeholder */

public class MinapticsMouseFilter extends MouseFilter
{
	private float field_22388_a;
	private float field_22387_b;
	private float field_22389_c;
	
	private boolean forceMode;
	private float forceValue;
	
	public MinapticsMouseFilter()
	{
		this.forceMode = false;
		this.forceValue = 0F;
	}
	
	public void force(float value)
	{
		this.forceMode = true;
		this.forceValue = value;
		
	}
	
	public void let()
	{
		this.forceMode = false;
		
	}
	
	@Override
	public float smooth(float f, float f1)
	{
		if (this.forceMode)
		{
			f1 = this.forceValue;
		}
		
		this.field_22388_a += f;
		f = (this.field_22388_a - this.field_22387_b) * f1;
		this.field_22389_c = this.field_22389_c + (f - this.field_22389_c) * 0.5F;
		if (f > 0.0F && f > this.field_22389_c || f < 0.0F && f < this.field_22389_c)
		{
			f = this.field_22389_c;
		}
		this.field_22387_b += f;
		return f;
		
	}
}