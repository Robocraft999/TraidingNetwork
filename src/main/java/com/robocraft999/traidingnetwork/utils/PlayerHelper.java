package com.robocraft999.traidingnetwork.utils;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.math.BigInteger;

public class PlayerHelper {
    public final static ObjectiveCriteria SCOREBOARD_RP = new ReadOnlyScoreCriteria(TraidingNetwork.MODID + ":rp_score");

    public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, BigInteger value) {
        updateScore(player, objective, value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ? Integer.MAX_VALUE : value.intValueExact());
    }

    public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, int value) {
        player.getScoreboard().forAllObjectives(objective, player.getScoreboardName(), score -> score.setScore(value));
    }

    private static class ReadOnlyScoreCriteria extends ObjectiveCriteria{
        protected ReadOnlyScoreCriteria(String pName) {
            super(pName, true, ObjectiveCriteria.RenderType.INTEGER);
        }
    }
}
