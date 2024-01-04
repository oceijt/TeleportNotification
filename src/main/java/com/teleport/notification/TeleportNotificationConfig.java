package com.teleport.notification;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("teleportNotification")
public interface TeleportNotificationConfig extends Config
{
	@ConfigItem(
		keyName = "warningTimer",
		name = "Warning timer ticks",
		description = "ticks until warning"
	)
	default int warningTicks()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "timerUnit",
		name = "Timer unit",
		description = "unit of the timer"
	)
	default TimerUnit timerUnit()
	{
		return TimerUnit.TICKS;
	}

}
