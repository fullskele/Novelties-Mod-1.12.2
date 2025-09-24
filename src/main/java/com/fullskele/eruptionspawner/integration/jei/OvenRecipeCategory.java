package com.fullskele.eruptionspawner.integration.jei;

import com.fullskele.eruptionspawner.EruptionSpawner;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class OvenRecipeCategory implements IRecipeCategory<OvenRecipeWrapper> {

    public static final String UID = "NB_Oven";
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(EruptionSpawner.MODID, "textures/gui/oven.png");

    private final String localizedName;
    private final IDrawable background;
    private final IDrawable gridDrawable;


    private float lastXp = 0f;

    public OvenRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(148, 54);
        this.localizedName = "Oven";

        this.gridDrawable = guiHelper.createDrawable(BG_TEXTURE, 15, 14, 150, 55);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return EruptionSpawner.MODID;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, OvenRecipeWrapper wrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        int index = 0;
        // 3x3 input inputs
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                stacks.init(index, true, (x * 18) + 27, y * 18);
                index++;
            }
        }

        // Output slot
        stacks.init(index, false, 125, 19);

        stacks.set(ingredients);

        this.lastXp = wrapper.getRecipe().getExperience();
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        gridDrawable.draw(minecraft, -1, -1);

        long seconds = System.currentTimeMillis() / 1000L;
        long halfSeconds = (System.currentTimeMillis() / 500L);

        int fullFireW = 14;
        int fullFireH = 14;
        int fireHeight = (int)(fullFireH - (seconds % fullFireH));
        fireHeight = Math.max(0, fireHeight);
        int fireX = 2;
        int fireY = 11 + (fullFireH - fireHeight);

        minecraft.getTextureManager().bindTexture(BG_TEXTURE);
        if (fireHeight > 0) {
            Gui.drawModalRectWithCustomSizedTexture(
                    fireX, fireY,
                    176, fullFireH - fireHeight,
                    fullFireW, fireHeight,
                    256, 256
            );
        }

        int fullArrowW = 24;
        int fullArrowH = 24;
        int arrowWidth = (int)((halfSeconds % fullArrowW) + 1);
        int arrowX = 89;
        int arrowY = 19;

        Gui.drawModalRectWithCustomSizedTexture(
                arrowX, arrowY,
                176, 14,
                arrowWidth, fullArrowH,
                256, 256
        );

        if (lastXp > 0.0f) {
            minecraft.fontRenderer.drawString("XP: " + lastXp, 115, 45, 0x404040);
        }
    }
}