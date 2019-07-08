package com.arimil.hudcraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HudcraftInventoryScreen extends AbstractContainerScreen<HudcraftInventoryScreen.HudcraftSlotsContainer> {

    private static final Identifier BACKGROUND_TEXTURE = new Identifier("hudcraft", "textures/gui/container/inventory.png");
    private static final Identifier HUDCRAFT_BUTTON_TEX = new Identifier("hudcraft", "textures/gui/hudcraft_button.png");

    private float mouseX;
    private float mouseY;

    public HudcraftInventoryScreen(PlayerEntity playerEntity) {
        super(new HudcraftSlotsContainer(playerEntity.inventory), playerEntity.inventory, new TranslatableText("container.cosmeticarmorslots"));
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new TexturedButtonWidget(this.left + 66, this.height / 2 - 14, 8, 8, 0, 0, 8, HUDCRAFT_BUTTON_TEX, 8, 16, (buttonWidget) -> {
            this.minecraft.openScreen(new InventoryScreen(this.playerInventory.player));
        }));
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        this.renderBackground();
        super.render(int_1, int_2, float_1);
        this.drawMouseoverTooltip(int_1, int_2);

        this.mouseX = (float) int_1;
        this.mouseY = (float) int_2;
    }

    @Override
    protected void drawForeground(int int_1, int int_2) {
        super.drawForeground(int_1, int_2);
    }

    @Override
    protected void drawBackground(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);
        InventoryScreen.drawEntity(this.left + 51, this.top + 75, 30, (float) (this.left + 51) - this.mouseX, (float) (this.top + 75 - 50) - this.mouseY, this.minecraft.player);
    }

    static class ProxySlot extends Slot {
        private final Slot slot;

        ProxySlot(Slot slot_1, int int_1) {
            super(slot_1.inventory, int_1, 0, 0);
            this.slot = slot_1;
        }

        public ItemStack onTakeItem(PlayerEntity playerEntity_1, ItemStack itemStack_1) {
            this.slot.onTakeItem(playerEntity_1, itemStack_1);
            return itemStack_1;
        }

        public boolean canInsert(ItemStack itemStack_1) {
            return this.slot.canInsert(itemStack_1);
        }

        public ItemStack getStack() {
            return this.slot.getStack();
        }

        public boolean hasStack() {
            return this.slot.hasStack();
        }

        public void setStack(ItemStack itemStack_1) {
            this.slot.setStack(itemStack_1);
        }

        public void markDirty() {
            this.slot.markDirty();
        }

        public int getMaxStackAmount() {
            return this.slot.getMaxStackAmount();
        }

        public int getMaxStackAmount(ItemStack itemStack_1) {
            return this.slot.getMaxStackAmount(itemStack_1);
        }

        public String getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }

        public ItemStack takeStack(int int_1) {
            return this.slot.takeStack(int_1);
        }

        public boolean doDrawHoveringEffect() {
            return this.slot.doDrawHoveringEffect();
        }

        public boolean canTakeItems(PlayerEntity playerEntity_1) {
            return this.slot.canTakeItems(playerEntity_1);
        }
    }

    static class HudcraftSlotsContainer extends Container {
        HudcraftSlotsContainer(PlayerInventory playerInventory) {
            super(null, 0);

            for (int i = 0; i < playerInventory.player.playerContainer.slotList.size(); ++i) {
                Slot originalSlot = playerInventory.player.playerContainer.slotList.get(i);
                Slot slot = new ProxySlot(originalSlot, i);
                this.addSlot(slot);

                // System.out.println(String.valueOf(i));

                if (i >= 46 && i < 50) {
                    // cosmetic armor slots
                    slot.xPosition = 77;
                    slot.yPosition = 8 + (i - 46) * 18;
                } else {
                    // everything else
                    slot.xPosition = originalSlot.xPosition;
                    slot.yPosition = originalSlot.yPosition;
                }
            }
        }

        public boolean canUse(PlayerEntity playerEntity_1) {
            return true;
        }

        public ItemStack transferSlot(PlayerEntity playerEntity_1, int int_1) {
            ItemStack itemStack_1 = ItemStack.EMPTY;
            Slot slot_1 = (Slot) this.slotList.get(int_1);
            if (slot_1 != null && slot_1.hasStack()) {
                ItemStack itemStack_2 = slot_1.getStack();
                itemStack_1 = itemStack_2.copy();
                EquipmentSlot equipmentSlot_1 = MobEntity.getPreferredEquipmentSlot(itemStack_1);
                if (int_1 == 0) {
                    if (!this.insertItem(itemStack_2, 9, 45, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot_1.onStackChanged(itemStack_2, itemStack_1);
                } else if (int_1 >= 1 && int_1 < 5) {
                    if (!this.insertItem(itemStack_2, 9, 45, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (int_1 >= 5 && int_1 < 9) {
                    if (!this.insertItem(itemStack_2, 9, 45, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (equipmentSlot_1.getType() == EquipmentSlot.Type.ARMOR && !((Slot) this.slotList.get(8 - equipmentSlot_1.getEntitySlotId())).hasStack()) {
                    int int_2 = 8 - equipmentSlot_1.getEntitySlotId();
                    if (!this.insertItem(itemStack_2, int_2, int_2 + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (equipmentSlot_1 == EquipmentSlot.OFFHAND && !((Slot) this.slotList.get(45)).hasStack()) {
                    if (!this.insertItem(itemStack_2, 45, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (int_1 >= 9 && int_1 < 36) {
                    if (!this.insertItem(itemStack_2, 36, 45, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (int_1 >= 36 && int_1 < 45) {
                    if (!this.insertItem(itemStack_2, 9, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStack_2, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }

                if (itemStack_2.isEmpty()) {
                    slot_1.setStack(ItemStack.EMPTY);
                } else {
                    slot_1.markDirty();
                }

                if (itemStack_2.getCount() == itemStack_1.getCount()) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemStack_3 = slot_1.onTakeItem(playerEntity_1, itemStack_2);
                if (int_1 == 0) {
                    playerEntity_1.dropItem(itemStack_3, false);
                }
            }

            return itemStack_1;
        }
    }

}