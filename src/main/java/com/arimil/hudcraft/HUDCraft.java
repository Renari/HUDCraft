package com.arimil.hudcraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HUDCraft implements ModInitializer {
    private static final Block EXAMPLE_BLOCK = new Block(FabricBlockSettings.of(Material.WOOD).build().strength(5, 0));
	@Override
	public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("hudcraft", "example_block"), new BlockItem(EXAMPLE_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
        Registry.register(Registry.BLOCK, new Identifier("hudcraft", "example_block"), EXAMPLE_BLOCK);
	}
}
