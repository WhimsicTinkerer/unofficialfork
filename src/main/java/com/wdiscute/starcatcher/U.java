package com.wdiscute.starcatcher;

import com.mojang.logging.LogUtils;
import com.wdiscute.starcatcher.bob.FishingBobEntity;
import com.wdiscute.starcatcher.util.TrustedHolder;
import com.wdiscute.starcatcher.fishentity.FishEntity;
import com.wdiscute.starcatcher.io.*;
import com.wdiscute.starcatcher.registry.custom.catchmodifiers.AbstractCatchModifier;
import com.wdiscute.starcatcher.registry.ModCriterionTriggers;
import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.registry.custom.tackleskin.ModTackleSkins;
import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import com.wdiscute.starcatcher.tournament.TournamentHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class U
{
    public static final Random r = new Random();

    public static void spawnFishFromPlayerFishing(ServerPlayer player, int time, boolean completedTreasure, boolean perfectCatch, int hits)
    {
        ServerLevel level = ((ServerLevel) player.level());

        if (ModDataAttachments.get(player, ModDataAttachments.FISHING_BOB).isEmpty()) return;

        Entity levelEntity = level.getEntity(ModDataAttachments.get(player, ModDataAttachments.FISHING_BOB).getUuid());
        if (levelEntity instanceof FishingBobEntity fbe)
        {
            if (time != -1)
            {
                FishProperties fp = fbe.fpToFish;

                ModCriterionTriggers.MINIGAME_COMPLETED.get().trigger(player, hits, perfectCatch, completedTreasure, time, fp.catchInfo().fish());

                //trigger modifiers
                fbe.modifiers.forEach(m -> m.onSuccessfulMinigameCompletion(player, time, completedTreasure, perfectCatch, hits));

                //play sound
                ModTackleSkins.get(level, fbe.rod).onSuccessfulMinigame(player);

                //if should cancel because of modifier, return
                if(fbe.modifiers.stream().anyMatch(m -> m.shouldCancelAfterSuccessfulMinigameCompletion(
                        player, time, completedTreasure, perfectCatch, hits))) return;

                //pick size and weight
                int size = getRandomSize(fp);
                int weight = getRandomWeight(fp);

                //award fish counter
                FishCaughtCounter.awardFishCaughtCounter(fp, player, time, size, weight, perfectCatch, true);

                //add score to tournaments
                TournamentHandler.addScore(player, fp, perfectCatch, size, weight);

                //award exp
                int exp = fp.rarity().getXp();

                player.giveExperiencePoints(exp);

                //SPAWN ENTITY if ⏬⏬⏬
                if (fp.catchInfo().alwaysSpawnEntity() ||
                        ModList.get().isLoaded("fishingreal") ||
                        fbe.modifiers.stream().anyMatch(AbstractCatchModifier::forceSpawnEntity))
                {

                    Vec3 objPos = player.position().subtract(fbe.position());

                    double x = objPos.x / 25;
                    double y = objPos.y / 20;
                    double z = objPos.z / 25;

                    x = Math.clamp(x, -1, 1);
                    y = Math.clamp(y, -1, 1);
                    z = Math.clamp(z, -1, 1);

                    x *= 2.5;
                    y *= 2;
                    z *= 2.5;

                    Entity entity = fp.catchInfo().entityToSpawn().value().create(level, null, fbe.blockPosition(), net.minecraft.world.entity.EntitySpawnReason.TRIGGERED, false, false);

                    if (entity == null)
                    {
                        LogUtils.getLogger().warn("starcatcher doesnt like when the flag or whatever is not enabled");
                        return;
                    }

                    //set fish item if it's a starcatcher fish entity
                    if (entity instanceof FishEntity fe) fe.setFish(getFishedItemstackFromFP(fp, size, weight));

                    entity.setPos(fbe.position().add(0, 1.2f, 0));

                    Vec3 vec3 = new Vec3(x, 0.7 + y, z);
                    entity.setDeltaMovement(vec3);
                    level.addFreshEntity(entity);
                }
                else
                {
                    //SPAWN ITEMSTACK
                    ItemStack bait = ModDataComponents.get(fbe.rod, ModDataComponents.BAIT).stack().copy();
                    boolean isStarcaught = fp.catchInfo().bucketedFish().is(ModItems.STARCAUGHT_BUCKET.getKey()) && bait.is(Items.BUCKET);
                    boolean isBucketed = !fp.catchInfo().bucketedFish().is(ModItems.MISSINGNO.getKey()) && !isStarcaught && bait.is(Items.BUCKET);

                    ItemStack is;
                    //create itemStack
                    if (isBucketed)
                    {
                        is = new ItemStack(fp.catchInfo().bucketedFish());
                    }
                    else
                    {
                        //make fish itemstack
                        is = new ItemStack(fp.catchInfo().fish());

                        //store size and weight data component
                        ModDataComponents.set(is, ModDataComponents.SIZE_AND_WEIGHT, new SizeAndWeightInstance(size, weight));

                        //store fp in itemstack for name color change
                        ModDataComponents.set(is, ModDataComponents.FISH_PROPERTIES, fp);

                        //call modify stack on modifiers (split hook behaviour)
                        for (AbstractCatchModifier acm : fbe.modifiers) is = acm.modifyItemStack(is);

                        //set starcaught bucket data stuff
                        if (isStarcaught)
                        {
                            ItemStack starcaughtBucket = new ItemStack(fp.catchInfo().bucketedFish());
                            ModDataComponents.set(starcaughtBucket,ModDataComponents.BUCKETED_FISH, new SingleStackContainer(is.copy()));
                            is = starcaughtBucket;
                        }
                    }


                    //make ItemEntities for fish item stack
                    ItemEntity itemFished = new ItemEntity(level, fbe.position().x, fbe.position().y + 1.2f, fbe.position().z, is);

                    //assign delta movement so fish flies towards player
                    double x = Math.clamp((player.position().x - fbe.position().x) / 25, -1, 1);
                    double y = Math.clamp((player.position().y - fbe.position().y) / 20, -1, 1);
                    double z = Math.clamp((player.position().z - fbe.position().z) / 25, -1, 1);
                    Vec3 vec3 = new Vec3(x, 0.7 + y, z);
                    itemFished.setDeltaMovement(vec3);

                    //add item entity to level
                    level.addFreshEntity(itemFished);
                }

                //spawn treasure item
                if (completedTreasure || fbe.modifiers.stream().anyMatch(m -> m.forceAwardTreasure(fbe, time, completedTreasure, perfectCatch, hits)))
                {
                    ItemStack treasure = new ItemStack(fp.catchInfo().treasure());
                    ItemEntity treasureFished = new ItemEntity(level, fbe.position().x, fbe.position().y + 1.2f, fbe.position().z, treasure);
                    double x = Math.clamp((player.position().x - fbe.position().x) / 25, -1, 1);
                    double y = Math.clamp((player.position().y - fbe.position().y) / 20, -1, 1);
                    double z = Math.clamp((player.position().z - fbe.position().z) / 25, -1, 1);
                    Vec3 vec3 = new Vec3(x, 0.7 + y, z);
                    treasureFished.setDeltaMovement(vec3);
                    level.addFreshEntity(treasureFished);
                }

            }
            else
            {
                //if fish minigame failed/canceled
                fbe.modifiers.forEach(AbstractCatchModifier::onFailedMinigame);

                //play sound from tackle skin
                ModTackleSkins.get(level, fbe.rod).onFailedMinigame(player);
            }

            fbe.kill(level);
        }

        ModDataAttachments.remove(player, ModDataAttachments.FISHING_BOB.get());
    }

    public static ItemStack getFishedItemstackFromFP(FishProperties fp)
    {
        int size = getRandomSize(fp);
        int weight = getRandomWeight(fp);
        return getFishedItemstackFromFP(fp, size, weight);
    }


    public static int getRandomSize(FishProperties fp)
    {
        return ((int) Starcatcher.truncatedNormal(fp.sizeWeight().sizeAverage(), fp.sizeWeight().sizeDeviation()));

    }

    public static int getRandomWeight(FishProperties fp)
    {
        return ((int) Starcatcher.truncatedNormal(fp.sizeWeight().weightAverage(), fp.sizeWeight().weightDeviation()));

    }

    public static ItemStack getFishedItemstackFromFP(FishProperties fp, int size, int weight)
    {
        ItemStack is = new ItemStack(fp.catchInfo().fish());
        ModDataComponents.set(is, ModDataComponents.FISH_PROPERTIES, fp);
        ModDataComponents.set(is, ModDataComponents.SIZE_AND_WEIGHT, new SizeAndWeightInstance(size, weight));
        return is;
    }

    //List<TrophyProperties> -> List<Identifier>
    public static List<TrophyProperties> getTpsFromRls(Registry<TrophyProperties> registry, List<Identifier> resourceLocations)
    {
        List<TrophyProperties> tps = new ArrayList<>();

        for (Identifier rl : resourceLocations)
        {
            // 1.21.11: registry.get() now returns Optional<Reference<T>>
            registry.get(rl).map(ref -> ref.value()).ifPresent(tps::add);
        }
        return tps;
    }

    public static List<TrophyProperties> getTpsFromRls(RegistryAccess registryAccess, List<Identifier> rls)
    {
        return getTpsFromRls(registryAccess.lookupOrThrow(Starcatcher.TROPHY_REGISTRY), rls);
    }

    public static List<TrophyProperties> getTpsFromRls(Level level, List<Identifier> rls)
    {
        return getTpsFromRls(level.registryAccess(), rls);
    }


    //List<FishProperties> -> List<Identifier>
    public static List<Identifier> getRlsFromFps(Registry<FishProperties> registry, List<FishProperties> fishProperties)
    {
        List<Identifier> rls = new ArrayList<>();

        for (FishProperties fp : fishProperties)
        {
            Identifier resourceLocation = registry.getKey(fp);
            if (resourceLocation != null) rls.add(resourceLocation);
        }
        return rls;
    }

    public static List<Identifier> getRlsFromFps(RegistryAccess registryAccess, List<FishProperties> fps)
    {
        return getRlsFromFps(registryAccess.lookupOrThrow(Starcatcher.FISH_REGISTRY), fps);
    }

    public static List<Identifier> getRlsFromFps(Level level, List<FishProperties> fps)
    {
        return getRlsFromFps(level.registryAccess(), fps);
    }


    //List<TrophyProperties> -> List<Identifier>
    public static List<Identifier> getRlsFromTps(Registry<TrophyProperties> registry, List<TrophyProperties> trophyProperties)
    {
        List<Identifier> rls = new ArrayList<>();

        for (TrophyProperties tp : trophyProperties)
        {
            Identifier resourceLocation = registry.getKey(tp);
            if (resourceLocation != null) rls.add(resourceLocation);
        }
        return rls;
    }

    public static List<Identifier> getRlsFromTps(RegistryAccess registryAccess, List<TrophyProperties> tps)
    {
        return getRlsFromTps(registryAccess.lookupOrThrow(Starcatcher.TROPHY_REGISTRY), tps);
    }

    public static List<Identifier> getRlsFromTps(Level level, List<TrophyProperties> tps)
    {
        return getRlsFromTps(level.registryAccess(), tps);
    }


    //Identifier -> TrophyProperties
    public static TrophyProperties getTpFromRl(Registry<TrophyProperties> registry, Identifier resourceLocation)
    {
        // 1.21.11: registry.get() now returns Optional<Reference<T>>
        return registry.get(resourceLocation)
                .map(ref -> ref.value())
                .orElseGet(() -> TrophyProperties.builder().build());
    }

    public static TrophyProperties getTpFromRl(RegistryAccess registryAccess, Identifier rl)
    {
        return getTpFromRl(registryAccess.lookupOrThrow(Starcatcher.TROPHY_REGISTRY), rl);
    }

    public static TrophyProperties getTpFromRl(Level level, Identifier rl)
    {
        return getTpFromRl(level.registryAccess(), rl);
    }


    //TrophyProperties -> Identifier
    public static Identifier getRlFromTp(Registry<TrophyProperties> registry, TrophyProperties tp)
    {
        Identifier rl = registry.getKey(tp);
        return rl == null ? Starcatcher.rl("missingno_rl") : rl;
    }

    public static Identifier getRlFromTp(RegistryAccess registryAccess, TrophyProperties tp)
    {
        return getRlFromTp(registryAccess.lookupOrThrow(Starcatcher.TROPHY_REGISTRY), tp);
    }

    public static Identifier getRlFromTp(Level level, TrophyProperties tp)
    {
        return getRlFromTp(level.registryAccess(), tp);
    }


    //List<Identifier> -> List<FishProperties>
    public static List<FishProperties> getFpsFromRls(Registry<FishProperties> registry, List<Identifier> resourceLocations)
    {
        List<FishProperties> fps = new ArrayList<>();

        for (Identifier rl : resourceLocations)
        {
            // 1.21.11: registry.get() now returns Optional<Reference<T>>
            registry.get(rl).map(ref -> ref.value()).ifPresent(fps::add);
        }
        return fps;
    }

    public static List<FishProperties> getFpsFromRls(RegistryAccess registryAccess, List<Identifier> rls)
    {
        return getFpsFromRls(registryAccess.lookupOrThrow(Starcatcher.FISH_REGISTRY), rls);
    }

    public static List<FishProperties> getFpsFromRls(Level level, List<Identifier> rls)
    {
        return getFpsFromRls(level.registryAccess(), rls);
    }


    //Identifier -> FishProperties
    public static FishProperties getFpFromRl(Registry<FishProperties> registry, Identifier resourceLocation)
    {
        // 1.21.11: registry.get() now returns Optional<Reference<T>>
        return registry.get(resourceLocation)
                .map(ref -> ref.value())
                .orElseGet(() -> FishProperties.builder().build());
    }

    public static FishProperties getFpFromRl(RegistryAccess registryAccess, Identifier rl)
    {
        return getFpFromRl(registryAccess.lookupOrThrow(Starcatcher.FISH_REGISTRY), rl);
    }

    public static FishProperties getFpFromRl(Level level, Identifier rl)
    {
        return getFpFromRl(level.registryAccess(), rl);
    }


    //resource location from fish properties
    public static Identifier getRlFromFp(Registry<FishProperties> registry, FishProperties fp)
    {
        Identifier rl = registry.getKey(fp);
        return rl == null ? Starcatcher.rl("missingno_rl") : rl;
    }

    public static Identifier getRlFromFp(RegistryAccess registryAccess, FishProperties tp)
    {
        return getRlFromFp(registryAccess.lookupOrThrow(Starcatcher.FISH_REGISTRY), tp);
    }

    public static Identifier getRlFromFp(Level level, FishProperties tp)
    {
        return getRlFromFp(level.registryAccess(), tp);
    }

    public static String calculateRealLifeTimeFromTicks(long ticks)
    {
        long ticksRemainingToCalculate = ticks / 20;
        String finalString = "";

        //days
        if(ticksRemainingToCalculate > 86400)
        {
            finalString += ticksRemainingToCalculate / 86400 + "d ";
            ticksRemainingToCalculate = ticksRemainingToCalculate % 86400;
        }

        //hours
        if(ticksRemainingToCalculate > 3600)
        {
            finalString += ticksRemainingToCalculate / 3600 + "h ";
            ticksRemainingToCalculate = ticksRemainingToCalculate % 3600;
        }

        //minutes
        if(ticksRemainingToCalculate > 60)
        {
            finalString += ticksRemainingToCalculate / 60 + "m ";
            ticksRemainingToCalculate = ticksRemainingToCalculate % 60;
        }

        //seconds
        if(ticksRemainingToCalculate > 0)
        {
            finalString += ticksRemainingToCalculate + "s";
        }
        return finalString;
    }

    @SafeVarargs
    public static <T> boolean containsAny(List<T> list, T... contains)
    {
        for (T s : contains)
            if (list.contains(s)) return true;

        return false;
    }

    @SafeVarargs
    public static <T> boolean containsAll(List<T> list, T... contains)
    {
        for (T s : contains)
            if (!list.contains(s)) return false;
        return true;
    }

    @SafeVarargs
    public static <T> boolean containsNone(List<T> list, T... contains)
    {
        return !containsAny(list, contains);
    }

    public static Identifier rl(String ns, String path)
    {
        return Identifier.fromNamespaceAndPath(ns, path);
    }

    public static Holder<Item> holderItem(String ns, String path)
    {
        return BuiltInRegistries.ITEM.get(rl(ns, path))
                .map(ref -> (Holder<Item>) ref)
                .orElseGet(() -> Holder.direct(net.minecraft.world.item.Items.AIR));
    }

    public static Holder<Item> holderItem(DeferredItem<Item> item)
    {
        return Holder.direct(item.get());
    }

    public static Holder<Item> holderItem(Item item)
    {
        return Holder.direct(item);
    }

    public static Holder<EntityType<?>> holderEntity(EntityType<?> entityType)
    {
        return Holder.direct(entityType);
    }

    @SuppressWarnings("unchecked")
    public static Holder<EntityType<?>> holderEntity(String ns, String path)
    {
        return BuiltInRegistries.ENTITY_TYPE.get(rl(ns, path))
                .map(ref -> (Holder<EntityType<?>>) (Holder<?>) ref)
                .orElseGet(() -> Holder.direct(EntityType.PIG));
    }

    public static Holder<EntityType<?>> holderEntity(Supplier<EntityType<FishEntity>> entity)
    {
        return Holder.direct(entity.get());
    }

    public static int intToRed(int packedColor)
    {
        return packedColor >> 16 & 255;
    }

    public static int intToGreen(int packedColor)
    {
        return packedColor >> 8 & 255;
    }

    public static int intToBlue(int packedColor)
    {
        return packedColor & 255;
    }

    public static int sign(float x)
    {
        return x >= 0 ? 1 : -1;
    }
}
