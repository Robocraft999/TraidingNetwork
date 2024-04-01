package com.robocraft999.amazingtrading.utils;

import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.mixin.accessor.ObjectiveCriteriaAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.math.BigInteger;

public class PlayerHelper {
    //public final static ObjectiveCriteria SCOREBOARD_RP = new ReadOnlyScoreCriteria(AmazingTrading.MODID + ":rp_score");
    public final static ObjectiveCriteria SCOREBOARD_RP = ObjectiveCriteriaAccessor.amazingtrading$registerCustom(AmazingTrading.MODID + ":rp_score", true, ObjectiveCriteria.RenderType.INTEGER);

    public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, BigInteger value) {
        updateScore(player, objective, value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ? Integer.MAX_VALUE : value.intValueExact());
    }

    public static void updateScore(ServerPlayer player, ObjectiveCriteria objective, int value) {
        player.getScoreboard().forAllObjectives(objective, player.getScoreboardName(), score -> score.setScore(value));
    }

    /*private static class ReadOnlyScoreCriteria extends ObjectiveCriteria{
        protected ReadOnlyScoreCriteria(String pName) {
            super(pName, true, ObjectiveCriteria.RenderType.INTEGER);
        }
    }*/
}
