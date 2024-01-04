package com.teleport.notification;

import com.google.inject.Provides;
import java.awt.TrayIcon.MessageType;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
@PluginDescriptor(
	name = "TeleportNotification"
)
public class TeleportNotificationPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TeleportNotificationConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Notifier notifier;

	public int afkTimeRemaining;

	private TeleportNotificationInfoBox infoBox;
	private boolean sentNotification = false;

	private static final int TICKS_PER_GAME_TICK = 30;

	private static final int ESCAPE_CRYSTAL_ACTIVATION_DELAY_VARBIT_ID = 14849;
	private static final int GAUNTLET_MAZE_VARBIT_ID = 9178;
	private static final int GAUNTLET_BOSS_VARBIT_ID = 9177;

	@Subscribe
	public void onGameTick(GameTick gameTick) {

		var itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		if (Arrays.stream(itemContainer.getItems()).noneMatch(x -> x.getId() == ItemID.ESCAPE_CRYSTAL)
				&& client.getVarbitValue(GAUNTLET_MAZE_VARBIT_ID) != 1
				&& client.getVarbitValue(GAUNTLET_BOSS_VARBIT_ID) != 1
		) {
			removeInfoBoxIfExists();
			return;
		}

		addInfoBoxIfNotExists();

		var afkMouseTicks = client.getMouseIdleTicks() / TICKS_PER_GAME_TICK;
		var afkKeyboardTicks = client.getKeyboardIdleTicks() / TICKS_PER_GAME_TICK;
		var escapeCrystalActivationDelay = client.getVarbitValue(ESCAPE_CRYSTAL_ACTIVATION_DELAY_VARBIT_ID);

		afkTimeRemaining = escapeCrystalActivationDelay - Math.min(afkKeyboardTicks, afkMouseTicks);
		if (afkTimeRemaining < 0) afkTimeRemaining = 0;
		if (afkTimeRemaining < config.warningTicks() && !sentNotification) {
			notifier.notify("Your escape crystal will teleport you upon taking damage", MessageType.WARNING);
			sentNotification = true;
		} else {
			sentNotification = false;
		}
	}

	private void addInfoBoxIfNotExists() {
		if (infoBox == null) {
			infoBox = new TeleportNotificationInfoBox(client, this, config);
			infoBox.setImage(itemManager.getImage(ItemID.ESCAPE_CRYSTAL));
			infoBoxManager.addInfoBox(infoBox);
		}
	}

	private void removeInfoBoxIfExists() {
		if (infoBox != null)
		{
			infoBoxManager.removeInfoBox(infoBox);
			infoBox = null;
		}
	}

	@Provides
	TeleportNotificationConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TeleportNotificationConfig.class);
	}
}
