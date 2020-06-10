package com.neepsy.voxelmagica.blocks;

import com.mojang.blaze3d.platform.GlStateManager;
import com.neepsy.voxelmagica.VoxelMagica;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;


public class TestBlockScreen extends ContainerScreen<TestBlockContainer> {

    private ResourceLocation GUI = new ResourceLocation(VoxelMagica.MODID, "textures/gui/testblock_gui.png");
    public TestBlockScreen(TestBlockContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(screenContainer, inv, name);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1,1,1,1);
        minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int rely = (this.height - this.ySize) / 2;
        blit(relX, rely, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        font.drawString(this.title.getFormattedText(), 8f, 6f, 4210752);
        font.drawString("Stored: " + container.getEnergy() + "FE", 8f, 70f, 4210752);
    }
}
