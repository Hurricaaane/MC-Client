package eu.ha3.mc.misc.logblockvisual;

import java.util.ArrayList;
import java.util.List;

/* xw-placeholder */

public class LBReport
{
	private boolean valid;
	
	private int page;
	private int pageCount;
	private int changedTotal;
	
	private List<LBChange> changes;
	
	public LBReport()
	{
		this.valid = false;
		this.page = 0;
		this.pageCount = 0;
		this.changedTotal = -1;
		this.changes = new ArrayList<LBChange>();
		
	}
	
	public void setPageData(int page, int pageCount)
	{
		this.page = page;
		this.pageCount = pageCount;
		validate();
		
	}
	
	public void setChangedTotal(int changedTotal)
	{
		this.changedTotal = changedTotal;
		validate();
		
	}
	
	public int getChangedTotal()
	{
		return this.changedTotal;
		
	}
	
	public int getPage()
	{
		return this.page;
		
	}
	
	public int getPageCount()
	{
		return this.pageCount;
		
	}
	
	public boolean isValid()
	{
		return this.valid;
		
	}
	
	public synchronized List<LBChange> getStoredChanges()
	{
		return this.changes;
		
	}
	
	public synchronized void addChange(LBChange change)
	{
		this.changes.add(change);
		
	}
	
	private void validate()
	{
		this.valid = this.page != 0 && this.page < this.pageCount && this.changedTotal >= 0;
		
	}
	
}
