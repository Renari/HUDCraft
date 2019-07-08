package com.arimil.hudcraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {

    @Shadow
    @Final
    @Mutable
    private List<DefaultedList<ItemStack>> combinedInventory;

    private DefaultedList<ItemStack> hudcraftEquipment;

    public DefaultedList<ItemStack> getHudcraftEquipment() {
        return hudcraftEquipment;
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onConstructed(PlayerEntity playerEntity, CallbackInfo ci) {
        hudcraftEquipment = DefaultedList.ofSize(1, ItemStack.EMPTY);
        // Change combinedInventory to a mutable type
        combinedInventory = new ArrayList<>(combinedInventory);
        combinedInventory.add(hudcraftEquipment);
    }

    @Inject(method = "serialize", at = @At("TAIL"))
    public void onSerialize(ListTag listTag_1, CallbackInfoReturnable<ListTag> cir) {
        // Item slots 0-35, 100-103, 150 claimed by default inventory
        // Cosmetic armor using item slots adjacent to regular armor slots

        for (int i = 0; i < this.hudcraftEquipment.size(); i++) {
            if (!this.hudcraftEquipment.get(i).isEmpty()) {
                CompoundTag ct = new CompoundTag();
                ct.putByte("Slot", (byte) (i + 80));
                this.hudcraftEquipment.get(i).toTag(ct);
                listTag_1.add(ct);
            }
        }
    }

    @Inject(method = "deserialize", at = @At("TAIL"))
    public void onDeserialize(ListTag listTag_1, CallbackInfo ci) {
        this.hudcraftEquipment.clear();
        for (int i = 0; i < listTag_1.size(); ++i) {
            CompoundTag ct = listTag_1.getCompoundTag(i);
            int slot = ct.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromTag(ct);
            if (!itemStack.isEmpty()) {
                if (slot >= 80 && slot < this.hudcraftEquipment.size() + 80) {
                    this.hudcraftEquipment.set(slot - 80, itemStack);
                }
            }
        }
    }

    @Inject(method = "isInvEmpty", at = @At("HEAD"), cancellable = true)
    public void onIsInvEmpty(CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack itemStack : this.hudcraftEquipment) {
            if (!itemStack.isEmpty()) {
                cir.setReturnValue(false);
            }
        }
    }
}