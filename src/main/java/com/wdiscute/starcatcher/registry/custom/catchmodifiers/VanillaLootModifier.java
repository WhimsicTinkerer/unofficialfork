package com.wdiscute.starcatcher.registry.custom.catchmodifiers;

import com.wdiscute.starcatcher.bob.FishingBobEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VanillaLootModifier extends AbstractCatchModifier
{

    @Override
    public boolean shouldCancelBeforeSkipsMinigameCheck()
    {
        return true;
    }

    @Override
    public void onReelAfterTreasureCheck()
    {
        super.onReelAfterTreasureCheck();
        Player player = instance.player;
        Level level = instance.level();

        LootParams lootparams = new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.ORIGIN, instance.position())
                .withParameter(LootContextParams.TOOL, instance.rod)
                .withParameter(LootContextParams.THIS_ENTITY, instance)
                .withParameter(LootContextParams.ATTACKING_ENTITY, instance.getOwner())
                .withLuck(player.getLuck())
                .create(LootContextParamSets.FISHING);

        LootTable table = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
        List<ItemStack> items = table.getRandomItems(lootparams);

        if (items.isEmpty()) return;

        //make ItemEntity for item stack
        ItemEntity itemFished = new ItemEntity(level, instance.position().x, instance.position().y + 1.2f, instance.position().z, items.get(0));

        //assign delta movement so fish flies towards player
        double x = Math.clamp((player.position().x - instance.position().x) / 25, -1, 1);
        double y = Math.clamp((player.position().y - instance.position().y) / 20, -1, 1);
        double z = Math.clamp((player.position().z - instance.position().z) / 25, -1, 1);
        Vec3 vec3 = new Vec3(x, 0.7 + y, z);
        itemFished.setDeltaMovement(vec3);

        //add item entity to level
        level.addFreshEntity(itemFished);

        instance.kill((ServerLevel) level);
    }
}
