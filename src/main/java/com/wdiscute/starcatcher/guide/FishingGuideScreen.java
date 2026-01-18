package com.wdiscute.starcatcher.guide;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.wdiscute.libtooltips.Tooltips;
import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.FishSort;
import com.wdiscute.starcatcher.io.Units;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.registry.blocks.ModBlocks;
import com.wdiscute.starcatcher.compat.EclipticSeasonsCompat;
import com.wdiscute.starcatcher.compat.SereneSeasonsCompat;
import com.wdiscute.starcatcher.compat.TerraFirmaCraftSeasonsCompat;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.io.SingleStackContainer;
import com.wdiscute.starcatcher.io.network.FPsSeenPayload;
import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.secretnotes.NoteContainer;
import com.wdiscute.starcatcher.secretnotes.SecretNoteScreen;
import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.storage.FishProperties.WorldRestrictions.Seasons;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.renderer.RenderPipelines;

import java.awt.*;
import java.util.*;
import java.util.List;

public class FishingGuideScreen extends Screen
{
    //todo fix fishes in area to not be shit
    private static final Identifier BACKGROUND_INDEX_FIRST = Starcatcher.rl("textures/gui/guide/background_index_first.png");
    private static final Identifier BACKGROUND_INDEX_SECOND = Starcatcher.rl("textures/gui/guide/background_index_second.png");
    private static final Identifier BACKGROUND_ENTRY = Starcatcher.rl("textures/gui/guide/background_entry.png");
    private static final Identifier BACKGROUND_BASICS = Starcatcher.rl("textures/gui/guide/background_basics.png");

    private static final Identifier HIGHLIGHT_LEFT = Starcatcher.rl("textures/gui/guide/highlight_page_left.png");
    private static final Identifier HIGHLIGHT_RIGHT = Starcatcher.rl("textures/gui/guide/highlight_page_right.png");

    private static final Identifier FISHES_IN_AREA_TOP_RIGHT_DECORATION = Starcatcher.rl("textures/gui/guide/fishes_in_area_top_right_decoration.png");
    private static final Identifier FISHES_IN_AREA_BOTTOM_LEFT_DECORATION = Starcatcher.rl("textures/gui/guide/fishes_in_area_bottom_left_decoration.png");
    private static final Identifier FISHES_IN_AREA_BOTTOM_DECORATION = Starcatcher.rl("textures/gui/guide/fishes_in_area_bottom_decoration.png");
    private static final Identifier FISHES_IN_AREA_FISH_DECORATION = Starcatcher.rl("textures/gui/guide/fishes_in_area_fish_decoration.png");

    private static final Identifier HELP_PAGE_BASICS = Starcatcher.rl("textures/gui/guide/help_basics.png");
    private static final Identifier HELP_PAGE_SWEETSPOTS = Starcatcher.rl("textures/gui/guide/help_sweetspots.png");
    private static final Identifier HELP_PAGE_TREASURE = Starcatcher.rl("textures/gui/guide/help_treasure.png");
    private static final Identifier HELP_PAGE_LAVA_FISHING = Starcatcher.rl("textures/gui/guide/help_lava_fishing.png");
    private static final Identifier HELP_PAGE_HOOKS_BOBBERS_BAITS = Starcatcher.rl("textures/gui/guide/help_hooks_bobbers_baits.png");
    private static final Identifier HELP_PAGE_GADGETS_COSMETICS = Starcatcher.rl("textures/gui/guide/help_gadgets_cosmetics.png");
    private static final Identifier HELP_PAGE_TEMPLATES_EQUIPMENT = Starcatcher.rl("textures/gui/guide/help_templates_equipment.png");
    private static final Identifier HELP_PAGE_TROPHIES = Starcatcher.rl("textures/gui/guide/help_trophies.png");
    private static final Identifier HELP_PAGE_TOURNAMENTS = Starcatcher.rl("textures/gui/guide/help_tournaments.png");

    private static final Identifier ARROW_PREVIOUS = Starcatcher.rl("textures/gui/guide/arrow_previous.png");
    private static final Identifier ARROW_PREVIOUS_PRESSED = Starcatcher.rl("textures/gui/guide/arrow_previous_pressed.png");
    private static final Identifier ARROW_PREVIOUS_HIGHLIGHT = Starcatcher.rl("textures/gui/guide/arrow_previous_highlight.png");

    private static final Identifier ARROW_NEXT = Starcatcher.rl("textures/gui/guide/arrow_next.png");
    private static final Identifier ARROW_NEXT_PRESSED = Starcatcher.rl("textures/gui/guide/arrow_next_pressed.png");
    private static final Identifier ARROW_NEXT_HIGHLIGHT = Starcatcher.rl("textures/gui/guide/arrow_next_highlight.png");

    private static final Identifier ARROW_INDEX = Starcatcher.rl("textures/gui/guide/arrow_index.png");
    private static final Identifier ARROW_INDEX_PRESSED = Starcatcher.rl("textures/gui/guide/arrow_index_pressed.png");
    private static final Identifier ARROW_INDEX_HIGHLIGHT = Starcatcher.rl("textures/gui/guide/arrow_index_highlight.png");

    private static final Identifier NEW_FISH = Starcatcher.rl("textures/gui/guide/new_fish.png");
    private static final Identifier STAR = Starcatcher.rl("textures/gui/guide/star.png");
    private static final Identifier GLOW = Starcatcher.rl("textures/gui/guide/glow.png");
    private static final Identifier SEASONS = Starcatcher.rl("textures/gui/guide/seasons.png");

    private static final int MAX_HELP_PAGES = 8;


    private final List<ItemStack> hooksAndBobbers = new ArrayList<>();
    private final List<ItemStack> baits = new ArrayList<>();
    private final List<ItemStack> gadgets = new ArrayList<>();
    private final List<ItemStack> templates = new ArrayList<>();
    private final List<ItemStack> equipments = new ArrayList<>();


    private final ItemStack basicsIndexIcon;
    private final ItemStack upgradeIndexIcon;
    private final ItemStack addonsIndexIcon;
    private final ItemStack cosmeticsIndexIcon;
    private final ItemStack tournamentIndexIcon;
    private final ItemStack trophiesIndexIcon;
    private final ItemStack settingsIndexIcon;

    private final ItemStack sweetspotsIcon;
    private final ItemStack treasureIcon;
    private final ItemStack equipmentIcon;

    private final ItemStack hookIcon;
    private final ItemStack baitIcon;
    private final ItemStack secretsIcon;

    private final ItemStack templateIcon;


    private List<Pair<ItemStack, String>> indexEntries;

    int uiX;
    int uiY;

    int imageWidth;
    int imageHeight;

    int clickedX;
    int clickedY;

    float highlightLeftAlpha = 0;
    float highlightRightAlpha = 0;

    // Temporary storage for guiGraphics during rendering
    private GuiGraphics currentGuiGraphics;

    boolean arrowPreviousPressed;
    boolean arrowNextPressed;
    boolean arrowIndexPressed;

    boolean hasNextPage = false;
    int lastIndexPage = 0;

    int menu = 0;
    int page = 0;

    ClientLevel level;
    LocalPlayer player;

    List<Identifier> fpsSeen = new ArrayList<>();
    List<FishProperties> entries = new ArrayList<>(999);
    List<TrophyProperties> trophiesTps = new ArrayList<>();
    List<TrophyProperties> secretsTps = new ArrayList<>();
    List<FishProperties> fishInArea = new ArrayList<>();
    Map<Identifier, FishCaughtCounter> fishCaughtCounterMap = new HashMap<>();

    TrophyProperties.RarityProgress all = TrophyProperties.RarityProgress.DEFAULT;
    private final Map<FishProperties.Rarity, TrophyProperties.RarityProgress> progressMap = new EnumMap<FishProperties.Rarity, TrophyProperties.RarityProgress>(Map.of(
            FishProperties.Rarity.COMMON, new TrophyProperties.RarityProgress(0, -1),
            FishProperties.Rarity.UNCOMMON, TrophyProperties.RarityProgress.DEFAULT,
            FishProperties.Rarity.RARE, TrophyProperties.RarityProgress.DEFAULT,
            FishProperties.Rarity.EPIC, TrophyProperties.RarityProgress.DEFAULT,
            FishProperties.Rarity.LEGENDARY, TrophyProperties.RarityProgress.DEFAULT
    ));

    @Override
    protected void init()
    {
        super.init();

        entries = new ArrayList<>();
        trophiesTps = new ArrayList<>();
        secretsTps = new ArrayList<>();

        imageWidth = 420;
        imageHeight = 260;

        uiX = (width - imageWidth) / 2;
        uiY = (height - imageHeight) / 2;

        level = Minecraft.getInstance().level;
        player = Minecraft.getInstance().player;

        fishInArea = FishProperties.getFpsWithGuideEntryForArea(player);
        fishCaughtCounterMap = FishingGuideAttachment.getFishesCaught(player);

        for (FishProperties fp : FishProperties.getFPs(level)) if (fp.hasGuideEntry()) entries.add(fp);
        entries = sortEntries(Config.SORT.get(), entries, player);
        fishInArea = sortEntries(Config.SORT.get(), fishInArea, player);

        for (TrophyProperties tp : level.registryAccess().lookupOrThrow(Starcatcher.TROPHY_REGISTRY))
            if (tp.trophyType() == TrophyProperties.TrophyType.TROPHY) trophiesTps.add(tp);

        for (TrophyProperties tp : level.registryAccess().lookupOrThrow(Starcatcher.TROPHY_REGISTRY))
        {
            if (tp.trophyType() == TrophyProperties.TrophyType.SECRET
                    && FishingGuideAttachment.getTrophiesCaught(player).containsKey(level.registryAccess().lookupOrThrow(Starcatcher.TROPHY_REGISTRY).getKey(tp)))
                secretsTps.add(tp);
        }

        all = TrophyProperties.RarityProgress.fromAttachment(player);

        fishCaughtCounterMap.forEach((loc, counter) ->
        {
            all = new TrophyProperties.RarityProgress(all.total() + counter.count(), all.unique());

            this.progressMap.computeIfPresent(U.getFpFromRl(level, loc).rarity(), (r, p) -> new TrophyProperties.RarityProgress(p.total() + counter.count(), p.unique() + 1));
        });
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent keyEvent)
    {
        InputConstants.Key key = InputConstants.getKey(keyEvent);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(key))
        {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event)
    {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        double x = mouseX - uiX;
        double y = mouseY - uiY;

        arrowIndexPressed = false;
        arrowNextPressed = false;
        arrowPreviousPressed = false;

        //previous arrow
        if (x > 49 && x < 69 && y > 203 && y < 217)
        {
            switch (menu)
            {
                case 0 ->
                {
                    //index <- previous page of index
                    if (page != 0)
                    {
                        minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                        page--;
                        return true;
                    }
                }
                case 1 ->
                {
                    minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                    //help -> index
                    if (page == 0)
                    {
                        menu = 0;
                        page = lastIndexPage;
                        return true;
                    }
                    //help -> previous help page
                    page--;
                    return true;
                }
                case 2 ->
                {
                    minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                    //entries -> last page of help
                    if (page == 0)
                    {
                        menu = 1;
                        page = MAX_HELP_PAGES;
                        return true;
                    }
                    //entries -> previous entry
                    page--;
                    return true;

                }
            }
        }

        //next arrow
        if (x > 336 && x < 356 && y > 202 && y < 216)
        {
            switch (menu)
            {
                case 0 ->
                {
                    //index -> next page of index
                    minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                    if (hasNextPage)
                    {
                        page++;
                        return true;
                    }
                    //index -> first page of help
                    menu = 1;
                    page = 0;
                    return true;
                }
                case 1 ->
                {
                    //help -> next page of help
                    minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                    if (page != MAX_HELP_PAGES)
                    {
                        minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                        page++;
                        return true;
                    }
                    //index -> first page of help
                    menu = 2;
                    page = 0;
                    return true;
                }
                case 2 ->
                {
                    //entries -> next entry
                    if (entries.size() > page * 2 + 2)
                    {
                        minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                        page++;
                        return true;
                    }
                    //entries -> leaderboards??
                    //currentMenu = 3;
                    //currentPage = 0;
                    return true;
                }
            }
        }

        //index arrow
        if (x > 174 && x < 196 && y > 202 && y < 216)
        {
            minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
            menu = 0;
            page = 0;
            return true;
        }

        if (button == 0)
        {
            clickedX = (int) mouseX;
            clickedY = (int) mouseY;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean focused)
    {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        double x = mouseX - uiX;
        double y = mouseY - uiY;

//        System.out.println("clicked on x :" + x);
//        System.out.println("clicked on x :" + y);

        //sort
        if (x > 51 && x < 116 && y > 67 && y < 76)
        {
            if (button == 0) Config.SORT.set(Config.SORT.get().next());
            if (button == 1) Config.SORT.set(Config.SORT.get().previous());
            Config.SORT.save();
            entries = sortEntries(Config.SORT.get(), entries, player);
            fishInArea = sortEntries(Config.SORT.get(), fishInArea, player);
        }

        //previous arrow
        if (x > 49 && x < 69 && y > 203 && y < 217)
        {
            if (!(menu == 0 && page == 0))
            {
                arrowPreviousPressed = true;
            }
        }

        //next arrow
        if (x > 336 && x < 356 && y > 202 && y < 216)
        {
            if (entries.size() > page * 2 + 2)
            {
                arrowNextPressed = true;
            }
        }

        //index arrow
        if (x > 174 && x < 196 && y > 202 && y < 216)
        {
            arrowIndexPressed = true;
        }

        return super.mouseClicked(event, focused);
    }

    @Override
    public void tick()
    {
        super.tick();
        highlightLeftAlpha -= 0.025f;
        highlightRightAlpha -= 0.025f;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.currentGuiGraphics = guiGraphics;
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        switch (menu)
        {
            //render settings screen
            case -1 ->
            {
                // Create a rod with empty components to avoid NPE
                ItemStack settingsRod = new ItemStack(ModItems.ROD.get());
                ModDataComponents.set(settingsRod, ModDataComponents.BOBBER, SingleStackContainer.EMPTY);
                ModDataComponents.set(settingsRod, ModDataComponents.BAIT, SingleStackContainer.EMPTY);
                ModDataComponents.set(settingsRod, ModDataComponents.HOOK, SingleStackContainer.EMPTY);
                Minecraft.getInstance().setScreen(
                        new NewSettingsScreen(
                                FishProperties.builder().withFish(ModItems.AURORA).build(),
                                settingsRod
                        ));
                return;
            }
            //render index
            case 0 ->
            {
                if (page == 0) renderImage(guiGraphics, BACKGROUND_INDEX_FIRST);
                else renderImage(guiGraphics, BACKGROUND_INDEX_SECOND);
                renderIndex(guiGraphics, mouseX, mouseY);
            }
            //render help pages
            case 1 ->
            {
                renderImage(guiGraphics, BACKGROUND_BASICS);
                renderTheBasics(guiGraphics, mouseX, mouseY);
            }
            //render entries
            case 2 ->
            {
                renderImage(guiGraphics, BACKGROUND_ENTRY);
                renderEntry(guiGraphics, mouseX, mouseY, 52, page * 2);
                renderEntry(guiGraphics, mouseX, mouseY, 212, page * 2 + 1);
            }
        }

        double x = mouseX - uiX;
        double y = mouseY - uiY;

        //previous arrow and index should not render on first page of the book
        if (!(menu == 0 && page == 0))
        {
            //previous arrow
            if (x > 49 && x < 69 && y > 203 && y < 217)
                renderImage(guiGraphics, ARROW_PREVIOUS_HIGHLIGHT);
            renderImage(guiGraphics, arrowPreviousPressed ? ARROW_PREVIOUS_PRESSED : ARROW_PREVIOUS);

            //index
            if (x > 174 && x < 196 && y > 202 && y < 216)
                renderImage(guiGraphics, ARROW_INDEX_HIGHLIGHT);
            renderImage(guiGraphics, arrowIndexPressed ? ARROW_INDEX_PRESSED : ARROW_INDEX);
        }

        //next arrow
        if (entries.size() > page * 2 + 2)
        {
            if (x > 336 && x < 356 && y > 202 && y < 216)
                renderImage(guiGraphics, ARROW_NEXT_HIGHLIGHT);
            renderImage(guiGraphics, arrowNextPressed ? ARROW_NEXT_PRESSED : ARROW_NEXT);
        }

        clickedX = 0;
        clickedY = 0;
    }

    private void renderHelpText(GuiGraphics guiGraphics, String pageName)
    {
        for (int i = 0; i < 40; i++)
        {
            if (!I18n.exists("gui.guide.page." + pageName + ".left." + i)) break;
            Component comp = Tooltips.decodeTranslationKey("gui.guide.page." + pageName + ".left." + i).copy().withColor(0xff635040);
            guiGraphics.drawString(this.font, comp, uiX + 52, uiY + 10 * i + 13, 0xff000000, false);
        }

        for (int i = 0; i < 40; i++)
        {
            if (!I18n.exists("gui.guide.page." + pageName + ".right." + i)) break;
            Component comp = Tooltips.decodeTranslationKey("gui.guide.page." + pageName + ".right." + i).copy().withColor(0xff635040);
            guiGraphics.drawString(this.font, comp, uiX + 213, uiY + 10 * i + 13, 0xff000000, false);
        }
    }

    private void renderSecrets(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        for (int i = 0; i < secretsTps.size(); i++)
        {
            int rowSize = Math.min(6, (secretsTps.size() - i / 6 * 6));
            int x = 70 - rowSize * 23 / 2;

            int xrender = x + (i % 6) * 23;
            int y = i / 6 * 25;

            //offset to page
            xrender += uiX + 223;
            y += uiY + 110;

            TrophyProperties tp = secretsTps.get(i);

            ItemStack is;

            is = new ItemStack(tp.fish());
            ModDataComponents.set(is, ModDataComponents.TROPHY, tp);

            guiGraphics.renderOutline(xrender - 10, y - 2, 20, 20, 0xff000000);
            renderItem(is, xrender - 8, y, 1);

            if (mouseX > xrender - 10 && mouseX < xrender + 10 && mouseY > y - 2 && mouseY < y + 18)
            {
                guiGraphics.setTooltipForNextFrame(this.font, is, mouseX, mouseY);
            }

            if (clickedX > xrender - 10 && clickedX < xrender + 10 && clickedY > y - 2 && clickedY < y + 18)
            {
                if (is.getItem() instanceof NoteContainer nc)
                {
                    Minecraft.getInstance().setScreen(new SecretNoteScreen(nc.note));
                }
            }
        }
    }

    private void renderTrophies(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        for (int i = 0; i < trophiesTps.size(); i++)
        {
            int rowSize = Math.min(6, (trophiesTps.size() - i / 6 * 6));
            int x = 60 - rowSize * 23 / 2;

            int xrender = x + (i % 6) * 23;
            int y = i / 6 * 25;

            //offset to page
            xrender += uiX + 73;
            y += uiY + 120;

            TrophyProperties tp = trophiesTps.get(i);

            ItemStack is;
            boolean isMouseOnTop = mouseX > xrender - 10 && mouseX < xrender + 10 && mouseY > y - 2 && mouseY < y + 18;

            //if caught
            if (FishingGuideAttachment.getTrophiesCaught(player).containsKey(level.registryAccess().lookupOrThrow(Starcatcher.TROPHY_REGISTRY).getKey(tp)))
            {
                is = new ItemStack(tp.fish());
                ModDataComponents.set(is, ModDataComponents.TROPHY, tp);
                if (isMouseOnTop)
                {
                    guiGraphics.setTooltipForNextFrame(this.font, is, mouseX, mouseY);
                }
            } else
            {
                if (isMouseOnTop)
                {
                    List<Component> list = new ArrayList<>(List.of(Component.literal("Requirements:")));

                    if (tp.all().total() != 0)
                        list.add(Component.empty().append(Tooltips.decodeTranslationKey("gui.guide.trophy.all.total")).append("[" + all.total() + "/" + tp.all().total() + "]"));
                    if (tp.all().unique() != 0)
                        list.add(Component.empty().append(Tooltips.decodeTranslationKey("gui.guide.trophy.all.unique")).append("[" + all.unique() + "/" + tp.all().unique() + "]"));

                    for (FishProperties.Rarity value : FishProperties.Rarity.values())
                    {
                        TrophyProperties.RarityProgress progress = tp.getProgress(value);
                        TrophyProperties.RarityProgress active = this.progressMap.get(value);
                        if (progress.total() != 0)
                        {
                            list.add(Component.empty().append(Tooltips.decodeTranslationKey("gui.guide.trophy." + value.getSerializedName() + ".total")).append("[" + active.total() + "/" + progress.total() + "]"));
                        }
                        if (progress.unique() != 0)
                        {
                            list.add(Component.empty().append(Tooltips.decodeTranslationKey("gui.guide.trophy." + value.getSerializedName() + ".unique")).append("[" + active.unique() + "/" + progress.unique() + "]"));
                        }
                    }

                    guiGraphics.setTooltipForNextFrame(this.font, list, Optional.empty(), mouseX, mouseY);
                }
                is = new ItemStack(ModItems.MISSINGNO.get());
            }

            guiGraphics.renderOutline(xrender - 10, y - 2, 20, 20, 0xff000000);
            renderItem(is, xrender - 8, y, 1);
        }
    }

    private void renderTheBasics(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {

        guiGraphics.drawString(this.font, page + "/" + MAX_HELP_PAGES, uiX + 213, uiY + 206, 0xff9c897c, false);

        switch (page)
        {
            //the basics
            case 0 ->
            {
                renderHelpText(guiGraphics, "basics");
                renderImage(guiGraphics, HELP_PAGE_BASICS);
                renderItem(basicsIndexIcon, uiX + 166, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.basics"), uiX + 80, uiY + 45, 0xff635040, false);
            }

            //sweetspots
            case 1 ->
            {
                renderHelpText(guiGraphics, "sweetspots");
                renderImage(guiGraphics, HELP_PAGE_SWEETSPOTS);
                renderItem(sweetspotsIcon, uiX + 166, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.sweetspots"), uiX + 80, uiY + 45, 0xff635040, false);
            }

            //treasures
            case 2 ->
            {
                renderHelpText(guiGraphics, "treasure");
                renderImage(guiGraphics, HELP_PAGE_TREASURE);
                renderItem(treasureIcon, uiX + 166, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.treasures"), uiX + 80, uiY + 45, 0xff635040, false);
            }

            //upgrades
            case 3 ->
            {
                renderHelpText(guiGraphics, "upgrades");
                renderImage(guiGraphics, HELP_PAGE_LAVA_FISHING);
                renderItem(upgradeIndexIcon, uiX + 166, uiY + 39, 1);
                renderItem(equipmentIcon, uiX + 321, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.lava_fishing"), uiX + 80, uiY + 45, 0xff635040, false);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.modifiers"), uiX + 230, uiY + 45, 0xff635040, false);
            }

            //hooks bobbers and baits
            case 4 ->
            {
                renderHelpText(guiGraphics, "hooks");
                renderImage(guiGraphics, HELP_PAGE_HOOKS_BOBBERS_BAITS);

                //hooks & bobbers
                renderItem(hookIcon, uiX + 166, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.hooks_and_bobbers"), uiX + 60, uiY + 45, 0xff635040, false);


                //dont ask me whats going on here ⏬

                int s1 = 5;
                int d1 = 23;
                int h1 = 0;

                //baits max column size
                if (hooksAndBobbers.size() > 15) s1 = 6;
                if (hooksAndBobbers.size() > 18) s1 = 7;
                if (hooksAndBobbers.size() > 18) d1 = 21;
                if (hooksAndBobbers.size() > 18) h1 = 6;

                for (int i = 0; i < Math.min(hooksAndBobbers.size(), 21); i++)
                {
                    int x = 70 - Math.min(s1, (hooksAndBobbers.size() - i / s1 * s1)) * 23 / 2;
                    int xrender = x + (i % s1) * d1;
                    int y = i / s1 * 25;

                    //offset to page
                    xrender += uiX + 60 + h1;
                    y += uiY + 130;

                    //render item and background
                    guiGraphics.fill(xrender - 10, y - 2, xrender + 10, y + 18, 0xffb4a697);
                    renderItem(hooksAndBobbers.get(i), xrender - 8, y, 1);

                    //render when hover
                    if (mouseX > xrender - 10 && mouseX < xrender + 10 && mouseY > y - 2 && mouseY < y + 18)
                        guiGraphics.setTooltipForNextFrame(this.font, hooksAndBobbers.get(i), mouseX, mouseY);
                }

                //baits
                renderItem(baitIcon, uiX + 321, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.baits"), uiX + 230, uiY + 45, 0xff635040, false);

                int s2 = 5;
                int d2 = 23;
                int h2 = 0;

                //baits max column size
                if (baits.size() > 15) s2 = 6;
                if (baits.size() > 18) s2 = 7;
                if (baits.size() > 18) d2 = 21;
                if (baits.size() > 18) h2 = 6;

                for (int i = 0; i < Math.min(baits.size(), 18); i++)
                {
                    int x = 70 - Math.min(s2, (baits.size() - i / s2 * s2)) * 23 / 2;
                    int xrender = x + (i % s2) * d2;
                    int y = i / s2 * 25;

                    //offset to page
                    xrender += uiX + 223 + h2;
                    y += uiY + 130;

                    //render item and background
                    guiGraphics.fill(xrender - 10, y - 2, xrender + 10, y + 18, 0xffb4a697);
                    renderItem(baits.get(i), xrender - 8, y, 1);

                    //render when hover
                    if (mouseX > xrender - 10 && mouseX < xrender + 10 && mouseY > y - 2 && mouseY < y + 18)
                        guiGraphics.setTooltipForNextFrame(this.font, baits.get(i), mouseX, mouseY);
                }
            }

            //gadgets & cosmetics-
            case 5 ->
            {
                renderHelpText(guiGraphics, "cosmetics");
                renderImage(guiGraphics, HELP_PAGE_GADGETS_COSMETICS);
                renderItem(upgradeIndexIcon, uiX + 166, uiY + 39, 1);
                renderItem(equipmentIcon, uiX + 321, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.gadgets"), uiX + 80, uiY + 45, 0xff635040, false);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.cosmetics"), uiX + 230, uiY + 45, 0xff635040, false);

                //dont ask me whats going on here ⏬

                int s1 = 5;
                int d1 = 23;
                int h1 = 0;

                //baits max column size
                if (gadgets.size() > 15) s1 = 6;
                if (gadgets.size() > 18) s1 = 7;
                if (gadgets.size() > 18) d1 = 21;
                if (gadgets.size() > 18) h1 = 6;

                for (int i = 0; i < Math.min(gadgets.size(), 21); i++)
                {
                    int x = 70 - Math.min(s1, (gadgets.size() - i / s1 * s1)) * 23 / 2;
                    int xrender = x + (i % s1) * d1;
                    int y = i / s1 * 25;

                    //offset to page
                    xrender += uiX + 60 + h1;
                    y += uiY + 130;

                    //render item and background
                    guiGraphics.fill(xrender - 10, y - 2, xrender + 10, y + 18, 0xffb4a697);
                    renderItem(gadgets.get(i), xrender - 8, y, 1);

                    //render when hover
                    if (mouseX > xrender - 10 && mouseX < xrender + 10 && mouseY > y - 2 && mouseY < y + 18)
                        guiGraphics.setTooltipForNextFrame(this.font, gadgets.get(i), mouseX, mouseY);
                }
            }

            //cosmetic templates
            case 6 ->
            {
                renderHelpText(guiGraphics, "templates");
                //templates
                renderImage(guiGraphics, HELP_PAGE_TEMPLATES_EQUIPMENT);
                renderItem(templateIcon, uiX + 166, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.templates"), uiX + 80, uiY + 45, 0xff635040, false);
                //dont ask me whats going on here ⏬

                int s1 = 5;
                int d1 = 23;
                int h1 = 0;

                //templates max column size
                for (int i = 0; i < Math.min(templates.size(), 12); i++)
                {
                    int x = 70 - Math.min(s1, (templates.size() - i / s1 * s1)) * 23 / 2;
                    int xrender = x + (i % s1) * d1;
                    int y = i / s1 * 25;

                    //offset to page
                    xrender += uiX + 60 + h1;
                    y += uiY + 157;

                    //render item and background
                    guiGraphics.fill(xrender - 10, y - 2, xrender + 10, y + 18, 0xffb4a697);
                    renderItem(templates.get(i), xrender - 8, y, 1);

                    //render when hover
                    if (mouseX > xrender - 10 && mouseX < xrender + 10 && mouseY > y - 2 && mouseY < y + 18)
                        guiGraphics.setTooltipForNextFrame(this.font, templates.get(i), mouseX, mouseY);
                }

                //Equipment
                renderItem(equipmentIcon, uiX + 321, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.equipment"), uiX + 230, uiY + 45, 0xff635040, false);

                int s2 = 7;
                int d2 = 21;
                int h2 = 0;

                for (int i = 0; i < Math.min(equipments.size(), 18); i++)
                {
                    int x = 70 - Math.min(s2, (equipments.size() - i / s2 * s2)) * 23 / 2;
                    int xrender = x + (i % s2) * d2;
                    int y = i / s2 * 25;

                    //offset to page
                    xrender += uiX + 229 + h2;
                    y += uiY + 157;

                    //render item and background
                    guiGraphics.fill(xrender - 10, y - 2, xrender + 10, y + 18, 0xffb4a697);
                    renderItem(equipments.get(i), xrender - 8, y, 1);

                    //render when hover
                    if (mouseX > xrender - 10 && mouseX < xrender + 10 && mouseY > y - 2 && mouseY < y + 18)
                        guiGraphics.setTooltipForNextFrame(this.font, equipments.get(i), mouseX, mouseY);
                }
            }


            case 7 ->
            {
                //tournaments
                renderHelpText(guiGraphics, "tournaments");
                renderImage(guiGraphics, HELP_PAGE_TOURNAMENTS);
                renderItem(tournamentIndexIcon, uiX + 166, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.tournaments"), uiX + 60, uiY + 45, 0xff635040, false);

            }

            case 8 ->
            {
                //trophies
                renderHelpText(guiGraphics, "trophies");
                renderImage(guiGraphics, HELP_PAGE_TROPHIES);
                renderItem(trophiesIndexIcon, uiX + 166, uiY + 39, 1);
                renderItem(secretsIcon, uiX + 321, uiY + 39, 1);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.trophies"), uiX + 60, uiY + 45, 0xff635040, false);
                renderTrophies(guiGraphics, mouseX, mouseY);
                guiGraphics.drawString(this.font, Component.translatable("gui.guide.secrets"), uiX + 230, uiY + 45, 0xff635040, false);
                renderSecrets(guiGraphics, mouseX, mouseY);
            }

        }
    }

    private void renderIndex(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        int topLeftCorner = uiX + 53;

//        if (player.level().getDayTime() % 5 == 0)
//        {
//            System.out.println(player.level().getDayTime());
//            fishInArea.add(FishProperties.DEFAULT);
//        }

        //render top index
        if (page == 0)
        {
            int x = topLeftCorner;
            for (int i = 0; i < 7; i++)
            {
                renderItem(indexEntries.get(i).getFirst(), x + 2, uiY + 47, 1);
                if (mouseX > x - 2 && mouseX < x + 17 && mouseY > uiY + 47 - 2 && mouseY < uiY + 47 + 17)
                    guiGraphics.setTooltipForNextFrame(this.font, Component.translatable(indexEntries.get(i).getSecond()), mouseX, mouseY);

                if (clickedX > x - 2 && clickedX < x + 17 && clickedY > uiY + 47 - 2 && clickedY < uiY + 47 + 17)
                {
                    minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
                    menu = 1;
                    switch (i)
                    {
                        case 0 -> page = 0;
                        case 1 -> page = 3;
                        case 2 -> page = 4;
                        case 3 -> page = 5;
                        case 4 -> page = 7;
                        case 5 -> page = 8;
                    }
                    if (i == 6) menu = -1;
                }
                x += 20;
            }
        }

        //[sort] text
        guiGraphics.drawString(this.font, Component.translatable("gui.guide.sort"), uiX + 52, uiY + 67, 0xffe4e0d8, false);

        //render fishes in area
        {
            if (page == 0)
            {
                //render fishes in area clickable squares and stuff
                for (int i = 0; i < fishInArea.size(); i++)
                {
                    if (i >= 42) break;
                    FishProperties fp = fishInArea.get(i);

                    int xpos = topLeftCorner + (i % 7) * 20;
                    int ypos = uiY + 47 + (i / 7 * 20) + 38;

                    renderFishIndex(guiGraphics, xpos, ypos, mouseX, mouseY, fp, 0xffc6bdaf);
                }

                //render decorations and stuff
                {
                    //render top right deco unless theres no fish in the top right slot
                    if (fishInArea.size() > 6) renderImage(guiGraphics, FISHES_IN_AREA_TOP_RIGHT_DECORATION);

                    int numberOfRows = (fishInArea.size() - 1) / 7 + 1;

                    int x = mouseX - uiX;
                    int y = mouseY - uiY;

                    if (x > 51 && x < 116 && y > 67 && y < 76)
                    {
                        guiGraphics.setTooltipForNextFrame(this.font, Component.translatable(Config.SORT.get().getTranslationKey()), mouseX, mouseY);
                    }

                    //render bottom decoration if theres space
                    if (numberOfRows < 4)
                        renderImage(guiGraphics, FISHES_IN_AREA_BOTTOM_DECORATION);

                    if (numberOfRows == 4 && fishInArea.size() % 7 < 5 && fishInArea.size() % 7 != 0)
                        renderImage(guiGraphics, FISHES_IN_AREA_BOTTOM_DECORATION);


                    //render bottom left thingy, offset by the number of rows
                    if (!fishInArea.isEmpty())
                        renderImage(guiGraphics, FISHES_IN_AREA_BOTTOM_LEFT_DECORATION, 0, (Math.min(numberOfRows, 6) - 1) * 20);

                    //render fish skeleton unless theres no space for it
                    int xFishSkeletonOffset = 0;
                    if (fishInArea.size() % 7 > 4 || fishInArea.size() % 7 == 0) xFishSkeletonOffset = 20;
                    if(numberOfRows < 6)
                        renderImage(guiGraphics, FISHES_IN_AREA_FISH_DECORATION, 0, (numberOfRows - 1) * 20 + xFishSkeletonOffset);
                    if(numberOfRows == 6 && fishInArea.size() % 7 < 5 && fishInArea.size() % 7 != 0)
                        renderImage(guiGraphics, FISHES_IN_AREA_FISH_DECORATION, 0, (numberOfRows - 1) * 20 + xFishSkeletonOffset);
                }
            }

        }

        hasNextPage = false;
        //render all fishes
        if (page == 0)
        {
            for (int i = 0; i < 49; i++)
            {
                if (i > entries.size() - 1) return;
                renderFishIndex(guiGraphics, topLeftCorner + 160 + (i % 7 * 20), uiY + 56 + (i / 7 * 20), mouseX, mouseY, entries.get(i), 0xffc6bdaf);
            }
        } else
        {
            //render second page, left
            for (int i = 0; i < 49; i++)
            {
                int order = i + 49 * (page * 2 - 1);
                if (order > entries.size() - 1) break;
                renderFishIndex(guiGraphics, topLeftCorner + (i % 7 * 20), uiY + 56 + (i / 7 * 20), mouseX, mouseY, entries.get(order), 0xffc6bdaf);
            }

            //render second page, right
            for (int i = 0; i < 49; i++)
            {
                int order = i + 49 + 49 * (page * 2 - 1);
                if (order > entries.size() - 1) return;
                renderFishIndex(guiGraphics, topLeftCorner + 160 + (i % 7 * 20), uiY + 56 + (i / 7 * 20), mouseX, mouseY, entries.get(order), 0xffc6bdaf);
            }
        }
        lastIndexPage = Math.max(page + 1, lastIndexPage);
        hasNextPage = true;
    }

    private void renderFishIndex(GuiGraphics guiGraphics, int xOffset, int yOffset, int mouseX, int mouseY, FishProperties fp, int backgroundFillColor)
    {
        Map<Identifier, FishCaughtCounter> fishesCaught = FishingGuideAttachment.getFishesCaught(player);
        FishCaughtCounter fishCaughtCounter = FishCaughtCounter.get(player, fp);
        ItemStack is = new ItemStack(fp.catchInfo().fish());

        //calculate caught counter
        int caught = fishCaughtCounter == null ? 0 : fishCaughtCounter.count();

        //handle click
        if (clickedX > xOffset - 3 && clickedX < xOffset + 21 - 3 && clickedY > yOffset - 3 && clickedY < yOffset + 21 - 3)
        {
            minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
            menu = 2;
            page = entries.indexOf(fp) / 2;

            if (entries.indexOf(fp) % 2 == 0)
                highlightLeftAlpha = 0.5f;
            else
                highlightRightAlpha = 0.5f;
        }

        //render fill
        guiGraphics.fill(xOffset - 1, yOffset - 1, xOffset + 17, yOffset + 17, backgroundFillColor);

        //glow color
        int color = switch (fp.rarity())
        {
            case FishProperties.Rarity.COMMON -> ARGB.color(0, -1);
            case FishProperties.Rarity.UNCOMMON -> ARGB.color(255, 0x92f28d);
            case FishProperties.Rarity.RARE -> ARGB.color(255, 0x78c8ff);
            case FishProperties.Rarity.EPIC -> ARGB.color(255, 0xc060ff);
            case FishProperties.Rarity.LEGENDARY -> ARGB.color(175, Color.HSBtoRGB(Tooltips.hue * 2, 1, 1));
        };

        float red = ARGB.red(color) / 255f;
        float green = ARGB.green(color) / 255f;
        float blue = ARGB.blue(color) / 255f;
        float alpha = ARGB.alpha(color) / 255f;

        // In 1.21.11, color tinting is handled via RenderPipeline or blitWithColor
        // For now, render the glow with the ARGB color applied
        int tintedColor = ARGB.color((int)(alpha * 255), (int)(red * 255), (int)(green * 255), (int)(blue * 255));
        // Use blit instead of blitSprite since GLOW is a texture path, not a sprite atlas entry
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GLOW, xOffset - 1, yOffset - 1, 18, 18, 0, 0, 18, 18, 18, 18, tintedColor);

        //render fish with missingno if not caught
        if (caught != 0)
            renderItem(is, xOffset, yOffset, 1);
        else
            renderItem(new ItemStack(ModItems.MISSINGNO.get()), xOffset, yOffset, 1);

        //render fish notification icon
        if (fishCaughtCounter != null && fishCaughtCounter.hasGuideNotification())
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, STAR, xOffset + 10, yOffset + 7, 0, 0, 10, 10, 10, 10);


        //render tooltip
        if (mouseX > xOffset - 3 && mouseX < xOffset + 21 - 3 && mouseY > yOffset - 3 && mouseY < yOffset + 21 - 3)
        {
            List<Component> components = new ArrayList<>();

            if (caught == 0)
            {
                components.add(Component.translatable("gui.guide.not_caught_fish_name"));
                components.add(Tooltips.decodeTranslationKey("gui.guide.rarity." + fp.rarity().getSerializedName()));
                components.add(Component.translatable("gui.guide.not_caught_yet").withColor(0xffa34536));
            } else
            {
                components.add(Component.translatable(fp.catchInfo().fish().value().getDescriptionId()));

                components.add(Tooltips.decodeTranslationKey("gui.guide.rarity." + fp.rarity().getSerializedName()));
                components.add(Component.translatable("gui.guide.caught").append(Component.literal(" [" + caught + "]")).withColor(0xff40752c));
            }

            components.add(Component.literal(""));

            String check = "";
            color = FishProperties.isDimensionCorrect(player, fp) ? 0xff40752c : 0xffa34536;
            check = FishProperties.isDimensionCorrect(player, fp) ? "✅" : "❌";
            components.add(Component.translatable("gui.guide.dimension").append(Component.literal(check)).withColor(color));

            color = FishProperties.isBiomeCorrect(player, fp) ? 0xff40752c : 0xffa34536;
            check = FishProperties.isBiomeCorrect(player, fp) ? "✅" : "❌";
            components.add(Component.translatable("gui.guide.biome").append(Component.literal(check)).withColor(color));

            color = FishProperties.isWeatherCorrect(player, fp, ItemStack.EMPTY) ? 0xff40752c : 0xffa34536;
            check = FishProperties.isWeatherCorrect(player, fp, ItemStack.EMPTY) ? "✅" : "❌";
            components.add(Component.translatable("gui.guide.weather").append(Component.literal(check)).withColor(color));

            color = FishProperties.isDaytimeCorrect(player, fp) ? 0xff40752c : 0xffa34536;
            check = FishProperties.isDaytimeCorrect(player, fp) ? "✅" : "❌";
            components.add(Component.translatable("gui.guide.daytime").append(Component.literal(check)).withColor(color));

            color = FishProperties.isElevationCorrect(player, fp) ? 0xff40752c : 0xffa34536;
            check = FishProperties.isElevationCorrect(player, fp) ? "✅" : "❌";
            components.add(Component.translatable("gui.guide.elevation").append(Component.literal(check)).withColor(color));

            components.add(Component.literal(""));

            //Serene Seasons compat
            if (ModList.get().isLoaded("sereneseasons") && Config.ENABLE_SEASONS.get())
                if (SereneSeasonsCompat.canCatch(fp, level))
                    components.add(Component.translatable("gui.guide.seasons.in_season").withStyle(Style.EMPTY.withColor(0xff40752c)));
                else
                    components.add(Component.translatable("gui.guide.seasons.not_in_season").withStyle(Style.EMPTY.withColor(0xffa34536)));

            //Ecliptic Seasons compat
            if (ModList.get().isLoaded("eclipticseasons") && Config.ENABLE_SEASONS.get())
                if (EclipticSeasonsCompat.canCatch(fp, level))
                    components.add(Component.translatable("gui.guide.seasons.in_season").withStyle(Style.EMPTY.withColor(0xff40752c)));
                else
                    components.add(Component.translatable("gui.guide.seasons.not_in_season").withStyle(Style.EMPTY.withColor(0xffa34536)));

            //TerraFirmaCraft Seasons compat
            if (ModList.get().isLoaded("tfc") && Config.ENABLE_SEASONS.get())
                if (TerraFirmaCraftSeasonsCompat.canCatch(fp, level))
                    components.add(Component.translatable("gui.guide.seasons.in_season").withStyle(Style.EMPTY.withColor(0xff40752c)));
                else
                    components.add(Component.translatable("gui.guide.seasons.not_in_season").withStyle(Style.EMPTY.withColor(0xffa34536)));


            guiGraphics.setTooltipForNextFrame(this.font, components, Optional.empty(), mouseX, mouseY);
        }

    }

    private void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, int xOffset, int entry)
    {

        if (level == null) level = getMinecraft().level;

        double x = mouseX - uiX;
        double y = mouseY - uiY;

        if (entries.size() <= entry) return;

        ItemStack is = new ItemStack(entries.get(entry).catchInfo().fish());
        FishProperties fp = entries.get(entry);

        Identifier loc = fp.toLoc(level);
        FishCaughtCounter fishCaughtCounter = fishCaughtCounterMap.get(loc);
        if (fishCaughtCounter != null && !fpsSeen.contains(loc) && fishCaughtCounter.hasGuideNotification())
            fpsSeen.add(loc);

        //get fishCaughtCount
        FishCaughtCounter fcc = FishCaughtCounter.get(player, fp);

        //render caught:
        //caught:
        guiGraphics.drawString(
                this.font, Component.translatable("gui.guide.caught"),
                uiX + xOffset + 73, uiY + 68, 0xff9c897c, false);

        //render caught count
        if (fcc == null)
        {
            //------
            guiGraphics.drawString(
                    this.font, Component.translatable("gui.guide.not_caught"),
                    uiX + xOffset + 73, uiY + 78, 0xff9c897c, false);
        } else
        {
            //[324]
            Component c = Component.literal("[" + fcc.count() + "]").withColor(0xff635040);
            guiGraphics.drawString(this.font, c, uiX + xOffset + 73, uiY + 78, 0xff635040, false);
        }

        //render rarity (always shown)
        //rarity:
        guiGraphics.drawString(
                this.font, Component.translatable("gui.guide.rarity"),
                uiX + xOffset + 73, uiY + 90, 0xff9c897c, false);
        //common
        guiGraphics.drawString(
                this.font, Tooltips.decodeTranslationKey("gui.guide.rarity." + fp.rarity().getSerializedName()),
                uiX + xOffset + 73, uiY + 100, 0xff635040, false);


        //render seasons
        if ((ModList.get().isLoaded("sereneseasons") || ModList.get().isLoaded("eclipticseasons") || ModList.get().isLoaded("tfc")) && Config.ENABLE_SEASONS.get())
        {

            int seasonX = 79;
            int seasonY = 48;
            int spacing = 15;

            List<Seasons> seasons = fp.wr().seasons();

            //spring
            if (U.containsAny(seasons, Seasons.ALL, Seasons.SPRING, Seasons.EARLY_SPRING, Seasons.MID_SPRING, Seasons.LATE_SPRING))
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SEASONS, uiX + xOffset + seasonX, uiY + seasonY, 8, 8, 0, 0, 8, 8, 32, 8);

            //summer
            if (U.containsAny(seasons, Seasons.ALL, Seasons.SUMMER, Seasons.EARLY_SUMMER, Seasons.MID_SUMMER, Seasons.LATE_SUMMER))
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SEASONS, uiX + xOffset + seasonX + spacing * 1, uiY + seasonY, 8, 8, 8, 0, 8, 8, 32, 8);

            //autumn
            if (U.containsAny(seasons, Seasons.ALL, Seasons.AUTUMN, Seasons.EARLY_AUTUMN, Seasons.MID_AUTUMN, Seasons.LATE_AUTUMN))
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SEASONS, uiX + xOffset + seasonX + spacing * 2, uiY + seasonY, 8, 8, 16, 0, 8, 8, 32, 8);

            //winter
            if (U.containsAny(seasons, Seasons.ALL, Seasons.WINTER, Seasons.EARLY_WINTER, Seasons.MID_WINTER, Seasons.LATE_WINTER))
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SEASONS, uiX + xOffset + seasonX + spacing * 3, uiY + seasonY, 8, 8, 24, 0, 8, 8, 32, 8);


            if (x > xOffset + 70 && x < xOffset + 140 && y > 46 && y < 57)
            {
                List<Component> seasonsComp = new ArrayList<>();
                seasonsComp.add(Component.translatable("gui.guide.seasons"));

                if (fp.wr().seasons().contains(Seasons.ALL))
                {
                    seasonsComp.add(Component.translatable("gui.guide.seasons.all"));
                } else
                {
                    for (Seasons s : seasons)
                        seasonsComp.add(Component.translatable("gui.guide.seasons." + s.getSerializedName()));
                }
                guiGraphics.setTooltipForNextFrame(this.font, seasonsComp, Optional.empty(), mouseX, mouseY);
            }
        }

        //rarity
        guiGraphics.drawString(
                this.font, Component.translatable("gui.guide.rarity"),
                uiX + xOffset + 73, uiY + 90, 0xff9c897c, false);


        //render fish name
        if (fcc == null)
        {
            guiGraphics.drawString(
                    this.font, Component.translatable("gui.guide.not_caught_fish_name"),
                    uiX + xOffset + 30, uiY + 36, 0xff635040, false);
        } else
        {
            MutableComponent compName = Component.translatable(fp.catchInfo().fish().value().getDescriptionId());

            //todo fix this holy shit this has to be the worse hard coded offset possible omg wd why did you code it like this
            if (xOffset > 200)
                guiGraphics.drawString(this.font, compName, uiX + xOffset + 15, uiY + 36, 0xff635040, false);
            else
                guiGraphics.drawString(this.font, compName, uiX + xOffset + 30, uiY + 36, 0xff635040, false);
        }

        //render fish
        if (fcc != null) renderItem(is, uiX + xOffset + 26, uiY + 70);

        int color = switch (fp.rarity())
        {
            case FishProperties.Rarity.COMMON -> ARGB.color(0, -1);
            case FishProperties.Rarity.UNCOMMON -> ARGB.color(200, 0x92f28d);
            case FishProperties.Rarity.RARE -> ARGB.color(200, 0x78c8ff);
            case FishProperties.Rarity.EPIC -> ARGB.color(200, 0xc060ff);
            case FishProperties.Rarity.LEGENDARY -> ARGB.color(175, Color.HSBtoRGB(Tooltips.hue * 2, 1, 1));
        };

        float red = ARGB.red(color) / 255f;
        float green = ARGB.green(color) / 255f;
        float blue = ARGB.blue(color) / 255f;
        float alpha = ARGB.alpha(color) / 255f;

        // In 1.21.11, color tinting is handled via RenderPipeline
        int tintedColor2 = ARGB.color((int)(alpha * 255), (int)(red * 255), (int)(green * 255), (int)(blue * 255));
        // Use blit instead of blitSprite since GLOW is a texture path, not a sprite atlas entry
        if (fcc != null) guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GLOW, uiX + xOffset + 10, uiY + 55, 48, 48, 0, 0, 18, 18, 18, 18, tintedColor2);

        //render new fish icon
        FishCaughtCounter counter = fishCaughtCounterMap.get(U.getRlFromFp(level, fp));
        if (counter != null && counter.hasGuideNotification())
            renderImage(guiGraphics, NEW_FISH, xOffset - 52, 0);

        //render fish tooltip
        if (mouseX > uiX + xOffset + 0 && mouseX < uiX + xOffset + 65 && mouseY > uiY + 45 && mouseY < uiY + 110 && fcc != null)
            guiGraphics.setTooltipForNextFrame(this.font, is, mouseX, mouseY);

        //render stats tooltip
        if (mouseX > uiX + xOffset + 66 && mouseX < uiX + xOffset + 140 && mouseY > uiY + 57 && mouseY < uiY + 110 && fcc != null)
        {
            List<Component> components = new ArrayList<>();
            float averageTicks = (int) ((fcc.averageTicks() / 20) * 100) / 100.0f;

            Units unit = Config.UNIT.get();
            String size = com.wdiscute.starcatcher.client.ClientHelper.getSizeAsString(unit, fcc.size());
            String weight = com.wdiscute.starcatcher.client.ClientHelper.getWeightAsString(unit, fcc.weight());

            components.add(Component.literal("Fastest Catch: ").append(Component.literal((((float) fcc.fastestTicks()) / 20) + "s").withStyle(ChatFormatting.BOLD)));
            components.add(Component.literal("Average Catch: ").append(Component.literal(averageTicks + "s").withStyle(ChatFormatting.BOLD)));
            components.add(Component.literal(""));
            components.add(Component.literal("Biggest Catch: ").append(Component.literal(size).withStyle(ChatFormatting.BOLD)));
            components.add(Component.literal("Heaviest Catch: ").append(Component.literal(weight).withStyle(ChatFormatting.BOLD)));

            guiGraphics.setTooltipForNextFrame(this.font, components, Optional.empty(), mouseX, mouseY);
        }

        int yOffset = 121;

        //dimension
        {
            Component comp;

            if (fp.wr().dims().isEmpty())
            {
                comp = Component.translatable("gui.guide.no_restriction");
            } else
            {
                //if theres only one dimension
                if (fp.wr().dims().size() == 1)
                {
                    comp = Component.translatable("dimension." + fp.wr().dims().getFirst().toLanguageKey());
                } else
                {
                    comp = Component.translatable("gui.guide.hover");

                    //show tooltip while hovering
                    if (x > xOffset && x < xOffset + 100 && y > yOffset - 2 && y < yOffset + 10)
                    {
                        List<Component> c = new ArrayList<>();

                        c.add(Component.translatable("gui.guide.dimensions"));

                        for (int i = 0; i < fp.wr().dims().size(); i++)
                        {
                            c.add(Component.translatable("dimension." + fp.wr().dims().get(i).toLanguageKey()));
                        }
                        guiGraphics.setTooltipForNextFrame(this.font, c, Optional.empty(), mouseX, mouseY);
                    }
                }
            }

            if (fp.wr().dims().isEmpty())
            {
                comp = comp.copy().withColor(0xff40752c);
            } else
            {
                if (fp.wr().dims().contains(level.dimension().identifier()))
                {
                    comp = comp.copy().withColor(0xff40752c);
                } else
                {
                    comp = comp.copy().withColor(0xffa34536);
                }
            }


            Component start = Component.translatable("gui.guide.dimension");

            guiGraphics.drawString(this.font, start.copy().append(comp), uiX + xOffset, uiY + yOffset, 0xff635040, false);
        }

        //dimension blacklist
        {
            if (!fp.wr().dimsBlacklist().isEmpty())
            {
                guiGraphics.drawString(this.font, Component.literal("[!]"), uiX + xOffset + 160, uiY + yOffset, 0xffa34536, false);

                //show tooltip while hovering
                if (x > xOffset + 155 && x < xOffset + 175 && y > yOffset - 4 && y < yOffset + 12)
                {
                    List<Component> c = new ArrayList<>();

                    c.add(Component.translatable("gui.guide.blacklisted_dimensions"));

                    for (int i = 0; i < fp.wr().dimsBlacklist().size(); i++)
                    {
                        c.add(Component.literal(fp.wr().dimsBlacklist().get(i).toString()));
                    }
                    guiGraphics.setTooltipForNextFrame(this.font, c, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        yOffset += 12;

        List<Identifier> biomesBL = FishProperties.getBiomesBlacklistAsList(fp, level);
        List<Identifier> biomes = FishProperties.getBiomesAsList(fp, level);
        //biome:
        {
            MutableComponent comp;
            if (biomes.isEmpty())
            {
                comp = Component.translatable("gui.guide.no_restriction");

                if (fp.wr().biomesBlacklistTags().equals(List.of(StarcatcherTags.IS_OCEAN, StarcatcherTags.IS_RIVER)))
                {
                    comp = Component.translatable("gui.guide.lakes");
                    if (x > 25 + xOffset && x < 120 + xOffset && y > 133 && y < 140)
                    {
                        Component c = Component.translatable("gui.guide.lakes.hover");
                        guiGraphics.setTooltipForNextFrame(this.font, c, mouseX, mouseY);
                    }
                }
            } else
            {
                //if theres only one biome
                if (biomes.size() == 1)
                {
                    comp = Component.translatable("biome." + biomes.getFirst().toLanguageKey());
                } else if (fp.wr().biomesTags().size() == 1)
                {
                    comp = Component.translatable("tag." + fp.wr().biomesTags().getFirst().toLanguageKey());

                    //show tooltip while hovering
                    if (x > xOffset && x < xOffset + 100 && y > yOffset - 2 && y < yOffset + 10)
                    {
                        List<Component> c = new ArrayList<>();

                        if (!fp.wr().biomesTags().isEmpty())
                        {
                            c.add(Component.translatable("gui.guide.biome_tags").withStyle(Style.EMPTY.withBold(true)));

                            for (Identifier rl : fp.wr().biomesTags())
                                c.add(Component.translatable("tag." + rl.toLanguageKey()));
                            c.add(Component.empty());
                        }


                        c.add(Component.translatable("gui.guide.biomes").withStyle(Style.EMPTY.withBold(true)));

                        for (Identifier rl : biomes)
                            c.add(Component.translatable("biome." + rl.toLanguageKey()));

                        guiGraphics.setTooltipForNextFrame(this.font, c, Optional.empty(), mouseX, mouseY);
                    }
                } else
                {
                    comp = Component.translatable("gui.guide.hover");

                    //show tooltip while hovering
                    if (x > xOffset && x < xOffset + 100 && y > yOffset - 2 && y < yOffset + 10)
                    {
                        List<Component> c = new ArrayList<>();
                        c.add(Component.translatable("gui.guide.biome"));

                        for (Identifier rl : biomes)
                        {
                            c.add(Component.translatable("biome." + rl.toLanguageKey()));
                        }

                        guiGraphics.setTooltipForNextFrame(this.font, c, Optional.empty(), mouseX, mouseY);
                    }
                }
            }


            Identifier rl = Identifier.parse(level.getBiome(Minecraft.getInstance().player.blockPosition()).getRegisteredName());

            comp = comp.copy().withColor(0xff40752c);

            if (!biomes.contains(rl) && !biomes.isEmpty())
            {
                comp = comp.copy().withColor(0xffa34536);
            }

            if (biomesBL.contains(rl))
            {
                comp = comp.copy().withColor(0xffa34536);
            }

            Component start = Component.translatable("gui.guide.biome");

            guiGraphics.drawString(this.font, start.copy().append(comp), uiX + xOffset, uiY + yOffset, 0xff635040, false);
        }

        //biome blacklist
        {
            if (!biomesBL.isEmpty())
            {
                guiGraphics.drawString(this.font, Component.literal("[!]"), uiX + xOffset + 130, uiY + yOffset - 1, 0xffa34536, false);

                //show tooltip while hovering
                if (x > xOffset + 129 && x < xOffset + 140 && y > yOffset - 3 && y < yOffset + 8)
                {
                    List<Component> c = new ArrayList<>();

                    if (!fp.wr().biomesBlacklistTags().isEmpty())
                    {
                        c.add(Component.translatable("gui.guide.blacklisted_biome_tags").withStyle(Style.EMPTY.withBold(true)));

                        for (Identifier rl : fp.wr().biomesBlacklistTags())
                            c.add(Component.translatable("tag." + rl.toLanguageKey()));
                        c.add(Component.empty());
                    }

                    c.add(Component.translatable("gui.guide.blacklisted_biomes").withStyle(Style.EMPTY.withBold(true)));

                    for (Identifier rl : biomesBL)
                        c.add(Component.translatable("biome." + rl.toLanguageKey()));

                    guiGraphics.setTooltipForNextFrame(this.font, c, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        yOffset += 12;

        //baits
        if (fp.baseChance() == 0)
        {
            guiGraphics.drawString(this.font, Component.literal("[!]"), uiX + xOffset + 130, uiY + yOffset - 1, 0xffa34536, false);
            //show tooltip while hovering
            if (x > xOffset + 129 && x < xOffset + 140 && y > yOffset - 3 && y < yOffset + 8)
            {
                guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.guide.bait_required"), mouseX, mouseY);
            }
        }
        if (fp.br().correctBait().isEmpty())
        {
            guiGraphics.drawString(
                    this.font,
                    Component.translatable("gui.guide.bait").append(Component.translatable("gui.guide.no_restriction")),
                    uiX + xOffset, uiY + yOffset, 0xff635040, false);
        } else
        {
            ItemStack bait = BuiltInRegistries.ITEM.get(fp.br().correctBait().getFirst())
                    .map(holder -> new ItemStack(holder.value()))
                    .orElse(ItemStack.EMPTY);

            if (bait.is(ModItems.LEGENDARY_BAIT.get()))
            {
                guiGraphics.drawString(
                        this.font,
                        Component.translatable("gui.guide.bait")
                                .append(Tooltips.RGBEachLetter(I18n.get(bait.getItem().getDescriptionId()))),
                        uiX + xOffset, uiY + yOffset, 0xff635040, false);
            } else
            {
                guiGraphics.drawString(
                        this.font,
                        Component.translatable("gui.guide.bait")
                                .append(Component.translatable(bait.getItem().getDescriptionId())),
                        uiX + xOffset, uiY + yOffset, 0xff635040, false);
            }


            if (x > xOffset && x < xOffset + 100 && y > yOffset - 2 && y < yOffset + 10)
            {
                guiGraphics.setTooltipForNextFrame(this.font, bait, mouseX, mouseY);
            }
        }

        yOffset += 12;

        //weather
        {
            Component comp;

            if (fp.weather() == FishProperties.Weather.ALL)
            {
                comp = Component.translatable("gui.guide.no_restriction").withColor(0xff635040);
            } else
            {
                comp = Component.translatable("gui.guide.no_restriction");
                if (fp.weather() == FishProperties.Weather.RAIN)
                {
                    if (level.getRainLevel(0) > 0.5)
                        comp = Component.translatable("gui.guide.raining").withColor(0xff40752c);
                    else
                        comp = Component.translatable("gui.guide.raining").withColor(0xffa34536);
                }

                if (fp.weather() == FishProperties.Weather.THUNDER)
                {
                    if (level.getThunderLevel(0) > 0.5)
                        comp = Component.translatable("gui.guide.thundering").withColor(0xff40752c);
                    else
                        comp = Component.translatable("gui.guide.thundering").withColor(0xffa34536);
                }

                if (fp.weather() == FishProperties.Weather.CLEAR)
                {
                    if (level.getRainLevel(0) > 0.5 || level.getThunderLevel(0) > 0.5)
                        comp = Component.translatable("gui.guide.clear").withColor(0xffa34536);
                    else
                        comp = Component.translatable("gui.guide.clear").withColor(0xff40752c);
                }
            }

            Component start = Component.translatable("gui.guide.weather");

            guiGraphics.drawString(this.font, start.copy().append(comp), uiX + xOffset, uiY + yOffset, 0xff635040, false);

        }

        yOffset += 12;

        //daytime
        {
            Component comp;

            if (fp.daytime() == FishProperties.Daytime.ALL)
            {
                comp = Component.translatable("gui.guide.no_restriction").withColor(0xff635040);
            } else
            {
                long time = level.getDayTime() % 24000;

                comp = switch (fp.daytime())
                {
                    case FishProperties.Daytime.DAY:
                        if (!(time > 23000 || time < 12700))
                            yield Component.translatable("gui.guide.day").withColor(0xffa34536);
                        else
                            yield Component.translatable("gui.guide.day").withColor(0xff40752c);

                    case FishProperties.Daytime.NOON:
                        if (!(time > 3500 && time < 8500))
                            yield Component.translatable("gui.guide.noon").withColor(0xffa34536);
                        else
                            yield Component.translatable("gui.guide.noon").withColor(0xff40752c);

                    case FishProperties.Daytime.NIGHT:
                        if (!(time < 23000 && time > 12700))
                            yield Component.translatable("gui.guide.night").withColor(0xffa34536);
                        else
                            yield Component.translatable("gui.guide.night").withColor(0xff40752c);

                    case FishProperties.Daytime.MIDNIGHT:
                        if (!(time > 16500 && time < 19500))
                            yield Component.translatable("gui.guide.midnight").withColor(0xffa34536);
                        else
                            yield Component.translatable("gui.guide.midnight").withColor(0xff40752c);

                    default:
                        yield Component.empty();
                };


            }

            Component start = Component.translatable("gui.guide.daytime");

            guiGraphics.drawString(this.font, start.copy().append(comp), uiX + xOffset, uiY + yOffset, 0xff635040, false);
        }

        yOffset += 12;

        //elevation
        {
            int above = fp.wr().mustBeCaughtAboveY();
            int below = fp.wr().mustBeCaughtBelowY();

            String aboveBelow = above + ", " + below;

            //aboveBelow = "50, 100";
            MutableComponent hardCodedTranslations = switch (aboveBelow)
            {
                case "100, 2147483647" -> Component.translatable("gui.guide.mountain");
                case "50, 100", "50, 2147483647" -> Component.translatable("gui.guide.surface");
                case "-2147483648, 50" -> Component.translatable("gui.guide.underground");
                case "0, 50" -> Component.translatable("gui.guide.caves");
                case "-2147483648, 0" -> Component.translatable("gui.guide.deepslate");
                case "-2147483648, 2147483647" -> Component.translatable("gui.guide.no_restriction");

                default -> Component.literal("> " + above + ", < " + below);
            };

            //color the text
            if (player.getY() > above && player.getY() < below)
                hardCodedTranslations.withColor(0xff40752c);
            else
                hardCodedTranslations.withColor(0xffa34536);

            //tooltip only shows if a pre-defined named for the elevation range is used
            List<Component> hoverTooltip = new ArrayList<>(List.of());

            if (above > Integer.MIN_VALUE)
                hoverTooltip.add(Component.translatable("gui.guide.above").append("" + above));
            if (below < Integer.MAX_VALUE)
                hoverTooltip.add(Component.translatable("gui.guide.below").append("" + below));

            if (x > xOffset && x < xOffset + 140 && y > yOffset - 2 && y < yOffset + 10)
                guiGraphics.setTooltipForNextFrame(this.font, hoverTooltip, Optional.empty(), mouseX, mouseY);

            guiGraphics.drawString(this.font, Component.translatable("gui.guide.elevation").append(hardCodedTranslations), uiX + xOffset, uiY + yOffset, 0xff635040, false);
        }

        yOffset += 12;

        //fluid
        List<Identifier> fluids = fp.wr().fluids();
        //if (!)
        {
            MutableComponent comp;
            if (fluids.size() == 1)
            {
                comp = Component.translatable("block." + fluids.getFirst().toLanguageKey());
            } else
            {
                comp = Component.translatable("gui.guide.hover");

                //show tooltip while hovering
                if (x > xOffset && x < xOffset + 100 && y > yOffset - 2 && y < yOffset + 10)
                {
                    List<Component> c = new ArrayList<>();
                    c.add(Component.translatable("gui.guide.fluid"));

                    for (Identifier rl : fluids)
                    {
                        c.add(Component.translatable("block." + rl.toLanguageKey()));
                    }

                    guiGraphics.setTooltipForNextFrame(this.font, c, Optional.empty(), mouseX, mouseY);
                }
            }
            guiGraphics.drawString(this.font, Component.translatable("gui.guide.fluid").append(comp), uiX + xOffset, uiY + yOffset, 0xff635040, false);

        }

        if (highlightRightAlpha > 0)
        {
            // In 1.21.11, alpha blending is handled via RenderPipeline
            int highlightColorRight = ARGB.color((int)(highlightRightAlpha * 255), 255, 255, 255);
            renderImageWithColor(guiGraphics, HIGHLIGHT_RIGHT, 0, 0, highlightColorRight);
        }

        if (highlightLeftAlpha > 0)
        {
            // In 1.21.11, alpha blending is handled via RenderPipeline
            int highlightColorLeft = ARGB.color((int)(highlightLeftAlpha * 255), 255, 255, 255);
            renderImageWithColor(guiGraphics, HIGHLIGHT_LEFT, 0, 0, highlightColorLeft);
        }
    }

    private void renderImage(GuiGraphics guiGraphics, Identifier rl)
    {
        renderImage(guiGraphics, rl, 0, 0);
    }

    private void renderImage(GuiGraphics guiGraphics, Identifier rl, int xOffset, int yOffset)
    {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, rl, uiX + xOffset, uiY + yOffset, 0, 0, 420, 260, 420, 260);
    }

    private void renderImageWithColor(GuiGraphics guiGraphics, Identifier rl, int xOffset, int yOffset, int color)
    {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, rl, uiX + xOffset, uiY + yOffset, 0, 0, 420, 260, 420, 260, color);
    }

    private void renderItem(ItemStack stack, int x, int y)
    {
        renderItem(stack, x, y, 3);
    }

    private void renderItem(ItemStack stack, int x, int y, float scale)
    {
        if (!stack.isEmpty() && currentGuiGraphics != null)
        {
            // In 1.21.11, guiGraphics.pose() returns Matrix3x2fStack for 2D transforms
            org.joml.Matrix3x2fStack pose = currentGuiGraphics.pose();
            pose.pushMatrix();

            // Apply scaling around the item center
            float centerX = x + 8;
            float centerY = y + 8;
            pose.translate(centerX, centerY);
            pose.scale(scale, scale);
            pose.translate(-8, -8);

            // Use the standard GUI item rendering which handles the new render state system
            currentGuiGraphics.renderItem(stack, 0, 0);

            pose.popMatrix();
        }
    }

    @Override
    public void onClose()
    {
        // In NeoForge 1.21.11, use connection directly to send to server
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(new FPsSeenPayload(fpsSeen));
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    protected boolean shouldNarrateNavigation()
    {
        return false;
    }

    // Sort enum has been moved to com.wdiscute.starcatcher.io.FishSort

    public static List<FishProperties> sortEntries(FishSort sort, List<FishProperties> entriesToSort, Player player)
    {
        //rarity
        if (sort.equals(FishSort.RARITY_DOWN) || sort.equals(FishSort.RARITY_UP))
        {
            //sort alphabetical first
            entriesToSort = entriesToSort.stream().sorted(Comparator.comparing(o -> o.catchInfo().fish().unwrapKey().get().identifier().getPath())).toList();

            List<FishProperties> entriesSorted = new ArrayList<>();

            entriesToSort.forEach(e ->
            {
                if (e.rarity().equals(FishProperties.Rarity.COMMON)) entriesSorted.add(e);
            });
            entriesToSort.forEach(e ->
            {
                if (e.rarity().equals(FishProperties.Rarity.UNCOMMON)) entriesSorted.add(e);
            });
            entriesToSort.forEach(e ->
            {
                if (e.rarity().equals(FishProperties.Rarity.RARE)) entriesSorted.add(e);
            });
            entriesToSort.forEach(e ->
            {
                if (e.rarity().equals(FishProperties.Rarity.EPIC)) entriesSorted.add(e);
            });
            entriesToSort.forEach(e ->
            {
                if (e.rarity().equals(FishProperties.Rarity.LEGENDARY)) entriesSorted.add(e);
            });

            return sort.equals(FishSort.RARITY_UP) ? entriesSorted : entriesSorted.reversed();
        }

        //alphabetical
        if (sort.equals(FishSort.ALPHABETICAL_DOWN) || sort.equals(FishSort.ALPHABETICAL_UP))
        {
            List<FishProperties> entriesSorted = entriesToSort.stream().sorted(Comparator.comparing(o -> o.catchInfo().fish().unwrapKey().get().identifier().getPath())).toList();
            return sort.equals(FishSort.ALPHABETICAL_UP) ? entriesSorted : entriesSorted.reversed();
        }

        //mod
        if (sort.equals(FishSort.MOD_DOWN) || sort.equals(FishSort.MOD_UP))
        {
            //sort alphabetical first
            entriesToSort = entriesToSort.stream().sorted(Comparator.comparing(o -> o.catchInfo().fish().unwrapKey().get().identifier().getPath())).toList();

            List<FishProperties> entriesSorted = new ArrayList<>();
            List<String> allNamespaces = new ArrayList<>();

            for (FishProperties fp : entriesToSort)
            {
                String namespace = fp.catchInfo().fish().unwrapKey().get().identifier().getNamespace();
                if (!allNamespaces.contains(namespace)) allNamespaces.add(namespace);
            }

            for (String s : allNamespaces)
            {
                for (FishProperties fp : entriesToSort)
                {
                    String namespace = fp.catchInfo().fish().unwrapKey().get().identifier().getNamespace();
                    if (namespace.equals(s)) entriesSorted.add(fp);
                }

            }

            entriesToSort = sort.equals(FishSort.MOD_UP) ? entriesSorted : entriesSorted.reversed();
        }

        //fluid
        if (sort.equals(FishSort.FLUID_DOWN) || sort.equals(FishSort.FLUID_UP))
        {
            //sort alphabetical first
            entriesToSort = entriesToSort.stream().sorted(Comparator.comparing(o -> o.catchInfo().fish().unwrapKey().get().identifier().getPath())).toList();

            List<FishProperties> entriesSorted = new ArrayList<>();
            List<FishProperties> entriesRemaining = new ArrayList<>(entriesToSort);

            while (!entriesRemaining.isEmpty())
            {
                Identifier rlBeingSorted = entriesRemaining.getFirst().wr().fluids().getFirst();
                List<FishProperties> temp = new ArrayList<>(entriesRemaining);
                temp.forEach(e ->
                {
                    if (e.wr().fluids().getFirst().equals(rlBeingSorted))
                    {
                        entriesSorted.add(e);
                        entriesRemaining.remove(e);
                    }
                });
            }

            entriesToSort = sort.equals(FishSort.FLUID_UP) ? entriesSorted : entriesSorted.reversed();
        }

        //caught
        if (sort.equals(FishSort.CAUGHT_UP) || sort.equals(FishSort.CAUGHT_DOWN))
        {
            //sort alphabetical first
            entriesToSort = entriesToSort.stream().sorted(Comparator.comparing(o -> o.catchInfo().fish().unwrapKey().get().identifier().getPath())).toList();


            //add all fishes caught to start
            Map<Identifier, FishCaughtCounter> fishesCaught = FishingGuideAttachment.getFishesCaught(player);

            List<FishProperties> hasCaught = new ArrayList<>();
            List<FishProperties> hasNotCaught = new ArrayList<>();
            List<FishProperties> toReturn = new ArrayList<>();

            //populate hasCaught and hasNotCaught
            entriesToSort.forEach(e ->
            {
                if (e.hasGuideEntry() && fishesCaught.containsKey(U.getRlFromFp(player.level(), e)))
                    hasCaught.add(e);
                else
                    hasNotCaught.add(e);
            });


            if (sort.equals(FishSort.CAUGHT_UP))
            {
                toReturn.addAll(hasCaught);
                toReturn.addAll(hasNotCaught);
            } else
            {
                toReturn.addAll(hasNotCaught);
                toReturn.addAll(hasCaught);
            }

            return toReturn;
        }

        //SEASONS
        if (sort.equals(FishSort.SEASON_DOWN) || sort.equals(FishSort.SEASON_UP))
        {
            //sort alphabetical first
            entriesToSort = entriesToSort.stream().sorted(Comparator.comparing(o -> o.catchInfo().fish().unwrapKey().get().identifier().getPath())).toList();

            List<FishProperties> entriesSorted = new ArrayList<>();
            List<FishProperties> entriesUnsorted = new ArrayList<>(entriesToSort);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.ALL)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.SPRING)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.EARLY_SPRING)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.MID_SPRING)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.LATE_SPRING)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.SUMMER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.EARLY_SUMMER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.MID_SUMMER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.LATE_SUMMER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.AUTUMN)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.EARLY_AUTUMN)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.MID_AUTUMN)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.LATE_AUTUMN)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.WINTER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.EARLY_WINTER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.MID_WINTER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            for (FishProperties fp : entriesUnsorted)
                if (fp.wr().seasons().contains(Seasons.LATE_WINTER)) entriesSorted.add(fp);
            entriesUnsorted.removeAll(entriesSorted);

            return sort.equals(FishSort.SEASON_UP) ? entriesSorted : entriesSorted.reversed();
        }

        return entriesToSort;
    }

    public FishingGuideScreen()
    {
        super(Component.empty());

        //get all items in bobbers/hooks/baits tags
        BuiltInRegistries.ITEM.get(StarcatcherTags.BOBBERS).ifPresent(o -> o.stream().forEach(i -> hooksAndBobbers.add(i.value().getDefaultInstance())));
        BuiltInRegistries.ITEM.get(StarcatcherTags.HOOKS).ifPresent(o -> o.stream().forEach(i -> hooksAndBobbers.add(i.value().getDefaultInstance())));
        BuiltInRegistries.ITEM.get(StarcatcherTags.BAITS).ifPresent(o -> o.stream().forEach(i -> baits.add(i.value().getDefaultInstance())));
        BuiltInRegistries.ITEM.get(StarcatcherTags.GADGETS).ifPresent(o -> o.stream().forEach(i -> gadgets.add(i.value().getDefaultInstance())));
        BuiltInRegistries.ITEM.get(StarcatcherTags.TEMPLATES).ifPresent(o -> o.stream().forEach(i -> templates.add(i.value().getDefaultInstance())));
        BuiltInRegistries.ITEM.get(StarcatcherTags.EQUIPMENTS).ifPresent(o -> o.stream().forEach(i -> equipments.add(i.value().getDefaultInstance())));

        //index
        basicsIndexIcon = new ItemStack(ModItems.ROD.get());
        upgradeIndexIcon = new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        addonsIndexIcon = new ItemStack(ModItems.HOOK.get());
        cosmeticsIndexIcon = new ItemStack(ModItems.PEARL_SMITHING_TEMPLATE.get());
        tournamentIndexIcon = new ItemStack(ModBlocks.STAND.get());
        trophiesIndexIcon = new ItemStack(ModBlocks.TROPHY_GOLD.get());
        settingsIndexIcon = new ItemStack(ModItems.SETTINGS.get());
        indexEntries = new ArrayList<>(List.of(
                Pair.of(basicsIndexIcon, "gui.guide.index.basics"),
                Pair.of(upgradeIndexIcon, "gui.guide.index.upgrades"),
                Pair.of(addonsIndexIcon, "gui.guide.index.addons"),
                Pair.of(cosmeticsIndexIcon, "gui.guide.index.cosmetics"),
                Pair.of(tournamentIndexIcon, "gui.guide.index.tournament"),
                Pair.of(trophiesIndexIcon, "gui.guide.index.trophies"),
                Pair.of(settingsIndexIcon, "gui.guide.index.settings")
        ));

        //other items
        sweetspotsIcon = new ItemStack(ModItems.AURORA.get());
        treasureIcon = new ItemStack(ModItems.WATERLOGGED_SATCHEL.get());
        equipmentIcon = new ItemStack(ModItems.AZURE_CRYSTAL_ROD.get());

        hookIcon = new ItemStack(ModItems.HOOK.get());
        baitIcon = new ItemStack(ModItems.CHERRY_BAIT.get());
        secretsIcon = new ItemStack(ModItems.HOPEFUL_BOTTLE.get());

        templateIcon = new ItemStack(ModItems.PEARL_SMITHING_TEMPLATE.get());
    }
}
