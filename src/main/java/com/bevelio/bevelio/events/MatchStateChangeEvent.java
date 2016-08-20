package com.bevelio.bevelio.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bevelio.bevelio.core.MatchState;

public class MatchStateChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private MatchState to
					, from;
	private boolean isCancelled;
	
	public MatchStateChangeEvent(MatchState to, MatchState from)
	{
		this.to = to;
		this.from = from;
		
		this.isCancelled = false;
	}

	public MatchState getTo()
	{
		return to;
	}

	public void setTo(MatchState to)
	{
		this.to = to;
	}

	public MatchState getFrom() 
	{
		return from;
	}
	
	@Override
	public boolean isCancelled() 
	{
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) 
	{
		this.isCancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
}
