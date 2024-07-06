package com.robocraft999.amazingtrading.registry;

import com.robocraft999.amazingtrading.content.shop.ShopBlock;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderBlock;
import com.robocraft999.amazingtrading.content.shredder.hopping.CreateShredderHoppingBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;

import static com.robocraft999.amazingtrading.AmazingTrading.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ATBlocks {
    public static final BlockEntry<CreateShredderBlock> CREATE_SHREDDER = REGISTRATE
            .block("create_shredder", CreateShredderBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL).noOcclusion())
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .transform(BlockStressDefaults.setImpact(16))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CreateShredderHoppingBlock> CREATE_SHREDDER_HOPPING = REGISTRATE
            .block("create_shredder_hopping", CreateShredderHoppingBlock::new)
            .lang("Hopping Create Shredder")
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .transform(BlockStressDefaults.setImpact(16))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ShopBlock> SHOP = REGISTRATE
            .block("shop", ShopBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.METAL))
            .transform(axeOnly())
            .blockstate((c, p) -> {
                p.getVariantBuilder(c.getEntry()).forAllStates(state ->
                        switch (state.getValue(ShopBlock.HALF)){
                            case UPPER -> ConfiguredModel.builder()
                                    .modelFile(AssetLookup.partialBaseModel(c, p, "dummy"))
                                    .build();
                            case LOWER -> ConfiguredModel.builder()
                                    .modelFile(AssetLookup.partialBaseModel(c, p))
                                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360)
                                    .build();
                        }
                );
                //p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 90)
            })
            .loot((t, e) -> t.add(e, new LootTable.Builder()
                    /*.setParamSet(LootContextParamSet.builder()
                            .required(LootContextParams.BLOCK_STATE)
                            .build()
                    )*/
                    .withPool(LootPool.lootPool()
                            .name("main")
                            .add(LootItem
                                    .lootTableItem(e.asItem())
                                    .when(LootItemBlockStatePropertyCondition
                                            .hasBlockStateProperties(e)
                                            .setProperties(StatePropertiesPredicate.Builder
                                                    .properties()
                                                    .hasProperty(ShopBlock.HALF, DoubleBlockHalf.LOWER)
                                            )
                                    )
                            )
                    )
            ))
            .transform(BlockStressDefaults.setImpact(16))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
        // This method can be used to trigger class loading and ensure static initializers run
        /* Set the render layer for the SHOP block
        ItemBlockRenderTypes.setRenderLayer(SHOP.get(), RenderType.cutoutMipped());
        */
    }
}
