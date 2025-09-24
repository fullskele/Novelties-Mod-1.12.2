package com.fullskele.eruptionspawner.world.container.gui;

import com.fullskele.eruptionspawner.EruptionSpawner;
import com.fullskele.eruptionspawner.blocks.tile.TileOven;
import com.fullskele.eruptionspawner.world.container.ContainerOven;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiOven extends GuiContainer {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EruptionSpawner.MODID, "textures/gui/oven.png");

    private final TileOven tile;
    private final InventoryPlayer playerInventory;

    public GuiOven(ContainerOven container, InventoryPlayer playerInventory, TileOven tile) {
        super(container);
        this.tile = tile;
        this.playerInventory = playerInventory;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BG_TEXTURE);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        //relative to UI, pushing right and down
        int cookIconX = x + 105;
        int cookIconY = y + 34;
        int burnIconX = x + 17;
        int burnIconY = y + 26;

        /*
        fontRenderer.drawString(
                "CookTotal: " + tile.cookTimeTotal +
                        " CookTime: " + tile.cookTime +
                        " BurnTime: " + tile.burnTime +
                        " CurrentBurn: " + tile.currentItemBurnTime,
                x + 8, y + 6, 0xFFFFFF
        );
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        */

                        //Burn progress
        int burnHeight = getBurnLeftScaled(14);
        this.drawTexturedModalRect(burnIconX, burnIconY + 14 - burnHeight, 176, 14 - burnHeight, 14, burnHeight);

                        //Cook progress
        int cookWidth = getCookProgressScaled(24);
        this.drawTexturedModalRect(cookIconX, cookIconY, 176, 14, cookWidth, 16);
    }

    private int getCookProgressScaled(int pixels) {
        return tile.cookTimeTotal == 0 ? 0 : tile.cookTime * pixels / tile.cookTimeTotal;
    }

    private int getBurnLeftScaled(int pixels) {
        if (tile.currentItemBurnTime == 0) return 0;
        return tile.burnTime * pixels / tile.currentItemBurnTime;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("tile.oven.name"), 50, 6, 0x404040);

        fontRenderer.drawString(
                playerInventory.getDisplayName().getUnformattedText(),
                8, ySize - 96 + 2,
                0x404040
        );
    }
}