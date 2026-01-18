package com.wdiscute.starcatcher.event;

import com.wdiscute.libtooltips.Tooltips;
import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.bob.FishingBobRenderer;
import com.wdiscute.starcatcher.fishentity.FishRenderer;
import com.wdiscute.starcatcher.fishentity.fishmodels.*;
import com.wdiscute.starcatcher.fishspotter.FishRadarLayer;
import com.wdiscute.starcatcher.client.CastProperty;
import com.wdiscute.starcatcher.client.ClientHelper;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.io.SizeAndWeightInstance;
import com.wdiscute.starcatcher.io.Units;
import com.wdiscute.starcatcher.items.BucketTooltipRenderer;
import com.wdiscute.starcatcher.items.StarcaughtBucket;
import com.wdiscute.starcatcher.particles.FishingBitingLavaParticles;
import com.wdiscute.starcatcher.particles.FishingBitingParticles;
import com.wdiscute.starcatcher.particles.FishingNotificationParticles;
import com.wdiscute.starcatcher.registry.*;
import com.wdiscute.starcatcher.registry.custom.tackleskin.*;
import com.wdiscute.starcatcher.rod.FishingRodScreen;
import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import com.wdiscute.starcatcher.tournament.StandScreen;
import com.wdiscute.starcatcher.tournament.TournamentOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = Starcatcher.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents
{

    @SubscribeEvent
    public static void keyPressed(InputEvent.Key event)
    {
        if(event.getAction() == 0 && event.getKey() == ModKeymappings.EXPAND_TOURNAMENT.getKey().getValue())
        {
            TournamentOverlay.expandedType = TournamentOverlay.expandedType.next();
        }
    }

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event)
    {
        List<Component> comp = event.getToolTip();
        ItemStack stack = event.getItemStack();

        if (ModDataComponents.has(stack, ModDataComponents.MINIGAME_MODIFIERS) || ModDataComponents.has(stack, ModDataComponents.CATCH_MODIFIERS))
        {
            List<Identifier> modifiers = new ArrayList<>();

            if (ModDataComponents.has(stack, ModDataComponents.CATCH_MODIFIERS))
                modifiers.addAll(Objects.requireNonNull(ModDataComponents.get(stack, ModDataComponents.CATCH_MODIFIERS)));
            if (ModDataComponents.has(stack, ModDataComponents.MINIGAME_MODIFIERS))
                modifiers.addAll(Objects.requireNonNull(ModDataComponents.get(stack, ModDataComponents.MINIGAME_MODIFIERS)));

            if (!modifiers.isEmpty())
            {
                comp.add(Component.translatable("tooltip.starcatcher.modifiers").withStyle(ChatFormatting.GRAY));

                for (Identifier rl : modifiers)
                {
                    for (int i = 0; i < 100; i++)
                    {
                        if (I18n.exists("tooltip.modifier." + rl.toLanguageKey() + "." + i))
                        {
                            MutableComponent start = i == 0 ? Component.literal("- ") : Component.literal("");
                            comp.add(start.append(Component.translatable("tooltip.modifier." + rl.toLanguageKey() + "." + i)).withStyle(ChatFormatting.DARK_GRAY));
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
        }

        //size and weight
        if (ModDataComponents.has(stack, ModDataComponents.SIZE_AND_WEIGHT))
        {
            SizeAndWeightInstance sw = ModDataComponents.get(stack, ModDataComponents.SIZE_AND_WEIGHT);

            Units units = Config.UNIT.get();

            String size = ClientHelper.getSizeAsString(units, sw.sizeInCentimeters());
            String weight = ClientHelper.getWeightAsString(units, sw.weightInGrams());

            comp.add(1, Component.literal(size + " - " + weight).withColor(0x888888));
        }

        //tackle skin
        if (ModDataComponents.has(stack, ModDataComponents.TACKLE_SKIN))
        {
            Identifier rl = ModDataComponents.get(stack, ModDataComponents.TACKLE_SKIN);
            comp.add(Component.translatable("tooltip.starcatcher.tackle").withStyle(ChatFormatting.GRAY));

            for (int i = 0; i < 100; i++)
            {
                if (I18n.exists("tooltip.tackle." + rl.toLanguageKey() + "." + i))
                {
                    MutableComponent start = i == 0 ? Component.literal("- ") : Component.literal("");
                    comp.add(start.append(Component.translatable("tooltip.tackle." + rl.toLanguageKey() + "." + i)).withStyle(ChatFormatting.DARK_GRAY));
                }
                else break;
            }
        }

        //Netherite Upgrade
        if (ModDataComponents.has(stack, ModDataComponents.NETHERITE_UPGRADE))
        {
            if (ModDataComponents.get(stack, ModDataComponents.NETHERITE_UPGRADE))
            {
                comp.add(1, Tooltips.decodeTranslationKey("tooltip.starcatcher.rod.netherite"));
            }
        }

        //rarity name color
        if (ModDataComponents.has(stack, ModDataComponents.FISH_PROPERTIES))
        {
            FishProperties fp = ModDataComponents.get(stack, ModDataComponents.FISH_PROPERTIES);

            String s = fp.rarity().getPre() + comp.get(0).getString(100) + fp.rarity().getPost();

            comp.remove(0);
            comp.add(0, Tooltips.decodeString(s));
        }

        //trophy stuff
        if (ModDataComponents.has(stack, ModDataComponents.TROPHY))
        {
            TrophyProperties tp = ModDataComponents.get(stack, ModDataComponents.TROPHY);

            if (tp.trophyType() == TrophyProperties.TrophyType.TROPHY)
                if (event.getFlags().hasShiftDown())
                {
                    comp.add(Component.translatable("tooltip.libtooltips.generic.shift_down"));
                    comp.add(Component.translatable("tooltip.libtooltips.generic.empty"));
                    comp.add(Component.translatable("tooltip.starcatcher.trophy.0"));
                    comp.add(Component.translatable("tooltip.starcatcher.trophy.1"));

                    List<Component> list = new java.util.ArrayList<>();

                    //all
                    if (tp.all().total() != 0) list.add(Tooltips.decodeString(
                            I18n.get("tooltip.starcatcher.trophy.total")
                                    .replace("&", tp.all().total() + "")
                                    .replace("$", I18n.get("tooltip.starcatcher.trophy.all"))
                    ));

                    if (tp.all().unique() != 0) list.add(
                            Tooltips.decodeString(I18n.get("tooltip.starcatcher.trophy.unique")
                                    .replace("&", tp.all().unique() + "")
                                    .replace("$", I18n.get("tooltip.starcatcher.trophy.all"))));

                    for (FishProperties.Rarity value : FishProperties.Rarity.values())
                    {
                        TrophyProperties.RarityProgress progress = tp.getProgress(value);
                        if (progress.total() != 0) list.add(
                                Tooltips.decodeString(I18n.get("tooltip.starcatcher.trophy.total")
                                        .replace("&", progress.total() + "")
                                        .replace("$", I18n.get("tooltip.starcatcher.trophy." + value.getSerializedName()))));

                        if (progress.unique() != 0) list.add(
                                Tooltips.decodeString(I18n.get("tooltip.starcatcher.trophy.unique")
                                        .replace("&", progress.unique() + "")
                                        .replace("$", I18n.get("tooltip.starcatcher.trophy." + value.getSerializedName()))));
                    }

                    if (list.size() == 1)
                    {
                        comp.add(Component.translatable("tooltip.starcatcher.trophy.once")
                                .append(list.getFirst())
                                .append(Component.translatable("tooltip.starcatcher.trophy.have_been_caught")));
                    }
                    else
                    {
                        comp.add(Component.translatable("tooltip.starcatcher.trophy.2"));
                        comp.addAll(list);
                    }

                }
                else
                {
                    comp.add(Component.translatable("tooltip.libtooltips.generic.shift_up"));
                }

        }
    }

    // NOTE: Mod bus events (FMLClientSetupEvent, RegisterMenuScreensEvent, etc.)
    // have been moved to ModClientModBusEvents.java which properly specifies Bus.MOD

}
