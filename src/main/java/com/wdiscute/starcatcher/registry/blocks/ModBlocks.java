package com.wdiscute.starcatcher.registry.blocks;

import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ModBlocks
{
    DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Starcatcher.MOD_ID);

    // NeoForge 1.21.11: Use registerBlock with constructor reference and properties
    DeferredBlock<TrophyBlock> TROPHY_GOLD = BLOCKS.registerBlock("trophy_gold", TrophyBlock::new, TrophyBlock.createProperties());
    DeferredBlock<TrophyBlock> TROPHY_SILVER = BLOCKS.registerBlock("trophy_silver", TrophyBlock::new, TrophyBlock.createProperties());
    DeferredBlock<TrophyBlock> TROPHY_BRONZE = BLOCKS.registerBlock("trophy_bronze", TrophyBlock::new, TrophyBlock.createProperties());

    DeferredBlock<StandBlock> STAND = registerStand("tournament_stand");

    private static DeferredBlock<StandBlock> registerStand(String name)
    {
        DeferredBlock<StandBlock> toReturn = BLOCKS.registerBlock(name, StandBlock::new, StandBlock.createProperties());
        // In 1.21.11, register() lambda receives Identifier (ResourceLocation), convert to ResourceKey for setId
        ModItems.BLOCKITEMS_REGISTRY.register(name, id -> new StandBlockItem(toReturn.get(),
            new net.minecraft.world.item.Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
        return toReturn;
    }

    // Register block items for trophy blocks
    static void registerBlockItems()
    {
        ModItems.BLOCKITEMS_REGISTRY.registerSimpleBlockItem(TROPHY_GOLD);
        ModItems.BLOCKITEMS_REGISTRY.registerSimpleBlockItem(TROPHY_SILVER);
        ModItems.BLOCKITEMS_REGISTRY.registerSimpleBlockItem(TROPHY_BRONZE);
    }

    static void register(IEventBus eventBus)
    {
        registerBlockItems();
        BLOCKS.register(eventBus);
    }
}
