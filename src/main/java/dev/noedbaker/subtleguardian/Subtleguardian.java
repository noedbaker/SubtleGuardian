package dev.noedbaker.subtleguardian;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//added imports

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;


public class Subtleguardian implements ModInitializer {
	public static final String MOD_ID = "subtleguardian";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("SubtleGuardian is loading...");

		//1: prevent lethal damage

		ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity entity, DamageSource source, float damageAmount) -> {


			//only apply to players
			if (!(entity instanceof Player player)) return true;
			if (!canTrigger(player)) return true;

			if (ThreadLocalRandom.current().nextDouble() > DEATH_SAVE_CHANCE) return true;

			//bump health above 0 to stop death
			player.setHealth(1.0f);
			debug(player, "Death Prevented (Lethal Damage Intercepted)");


			markTriggered(player);
			//false = stop death
			return false;
		});

		//2: After damage micro-heals (make it feel like a random reduction)
		ServerLivingEntityEvents.AFTER_DAMAGE.register((LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken, boolean blocked) -> {
		
			if (!(entity instanceof Player player)) return;

			//dont trigger if shield blocks
			if (blocked) return;

			//random microheal
			if (ThreadLocalRandom.current().nextDouble() > MICRO_HEAL_CHANCE) return;

			//0.5 -> 2.5 health
			float heal = (float) (0.5f + ThreadLocalRandom.current().nextDouble() * 2.0f);
			float newHealth = Math.min(player.getMaxHealth(), player.getHealth() + heal);
			player.setHealth(newHealth);
			debug(player, String.format("Micro-Heal Applied (+%.1f)", heal));

			markTriggered(player);

		});

		LOGGER.info("SubtleGuardian hooks registered!");
	}

	//default fields

	//debug in chat
	private static final boolean DEBUG_CHAT = false;

	//cooldown so saves don't happen constantly (ms)
	private static final long SAVE_COOLDOWN_MS = 4162;

	//chance to prevent a death (0.0 to 1.0)
	private static final double DEATH_SAVE_CHANCE = 1.0;

	//chance to do a tiny "damage reduction" with micro-heal after hit
	private static final double MICRO_HEAL_CHANCE = 0.15;

	private static final Map<UUID, Long> lastSaveTime = new HashMap<>();


	//function to check if player can be triggered.
	private static boolean canTrigger(Player player) {

		long now = System.currentTimeMillis();
		long last = lastSaveTime.getOrDefault(player.getUUID(), 0L);
		return (now - last) >= SAVE_COOLDOWN_MS;

	}

	//markTriggered function
	private static void markTriggered(Player player) {

		lastSaveTime.put(player.getUUID(), System.currentTimeMillis());

	}

	private static void debug(Player player, String message) {

		if (!DEBUG_CHAT) return;

		if (player instanceof ServerPlayer serverPlayer) {

			serverPlayer.sendSystemMessage(Component.literal("[SG] " + message));

		}

	}

}