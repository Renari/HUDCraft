package com.arimil.hudcraft.mixin;

import com.arimil.hudcraft.client.gui.screen.ingame.HudcraftInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryButtonMixin extends AbstractInventoryScreen<PlayerContainer> implements ButtonWidget.PressAction {
    private static final Identifier HUDCRAFT_BUTTON_TEX = new Identifier("hudcraft", "textures/gui/hudcraft_button.png");

    public InventoryButtonMixin(PlayerContainer container_1, PlayerInventory playerInventory_1, Text text_1) {
        super(container_1, playerInventory_1, text_1);
    }

    class HudcraftButtonWidget extends TexturedButtonWidget {
        HudcraftButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture,
                             int textureWidth, int textureHeight, ButtonWidget.PressAction buttonAction) {
            super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, buttonAction);
        }

        @Override
        public void renderButton(int int_1, int int_2, float float_1) {
            super.renderButton(int_1, int_2, float_1);
            this.setPos(x, y);
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void onInit(CallbackInfo ci) {
        if (!this.minecraft.interactionManager.hasCreativeInventory()) {
            int ModOffset = 0;
            // cosmetic armor slots adds an icon at the same location, so if this
            // mod is loaded move our icon up a bit so they don't overlap
            if (FabricLoader.getInstance().isModLoaded("cosmeticarmorslots")) {
                ModOffset += 8;
            }
            this.addButton(new HudcraftButtonWidget(
                    this.left + 66,
                    this.height / 2 - (14 + ModOffset),
                    8,
                    8,
                    0,
                    0,
                    8,
                    HUDCRAFT_BUTTON_TEX,
                    8,
                    16,
                    this));
        }
    }

    @Override
    public void onPress(ButtonWidget var1) {
        this.minecraft.openScreen(new HudcraftInventoryScreen(this.playerInventory.player));
    }
}
