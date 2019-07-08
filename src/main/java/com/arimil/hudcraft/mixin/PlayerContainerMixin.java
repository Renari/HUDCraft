package com.arimil.hudcraft.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.ContainerType;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerContainer.class)
public abstract class PlayerContainerMixin extends CraftingContainer<CraftingInventory> {
    private static final Identifier HUDCRAFT_MAP_SLOT_BACKGROUND = new Identifier("hudcraft", "textures/gui/empty_map_slot.png");

    public PlayerContainerMixin(ContainerType<?> containerType_1, int int_1) {
        super(containerType_1, int_1);
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(PlayerInventory playerInventory, boolean boolean_1, PlayerEntity playerEntity, CallbackInfo ci) {
        Slot slot = new Slot(playerInventory, 80, -10000, -10000) {
            public int getMaxStackAmount() {
                return 1;
            }

            public boolean canInsert(ItemStack itemStack_1) {
                return itemStack_1.getName().asString().equals("Map");
            }

            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                ItemStack itemStack_1 = this.getStack();
                return (itemStack_1.isEmpty() || playerEntity_1.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack_1)) && super.canTakeItems(playerEntity_1);
            }

//            @Environment(EnvType.CLIENT)
//            public String getBackgroundSprite() {
//                MinecraftClient.getInstance().getTextureManager().bindTexture(HUDCRAFT_MAP_SLOT_BACKGROUND);
//                return "gui/map_slot_background";
//            }
        };
        this.addSlot(slot);
    }
}
