package vazkii.akashictomeoftools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import vazkii.akashictomeoftools.config.ConfigManager;

public class AkashicTome implements ModInitializer {

	public static final Item TOME_ITEM = new TomeItem(new FabricItemSettings()
			.group(ItemGroup.TOOLS)
			.maxCount(1)
			.rarity(Rarity.EPIC));
	public static final Identifier AkashicChannel = new Identifier("akashictomeoftools");
	public static final RecipeType<AttachmentRecipe> ATTACHMENT_RECIPE_TYPE;
	public static final RecipeSerializer<AttachmentRecipe> ATTACHMENT_RECIPE_SERIALIZER;

	@Override
	public void onInitialize() {
		ConfigManager.registerAutoConfig();
		Registry.register(Registry.ITEM, new Identifier("akashictomeoftools", "akashic_tome"), TOME_ITEM);
		ServerPlayNetworking.registerGlobalReceiver(AkashicChannel, (server, player, handler, buf, responseSender) -> {
			ItemStack itemStack = ItemStackWrap.tryConvert(player.getMainHandStack());
			Hand hand;
			if (!(itemStack instanceof ItemStackWrap)) {
				itemStack = ItemStackWrap.tryConvert(player.getOffHandStack());
				if (!(itemStack instanceof ItemStackWrap)) return;
				else {player.setStackInHand(Hand.OFF_HAND, itemStack);hand = Hand.OFF_HAND;}
			}
			else {player.setStackInHand(Hand.MAIN_HAND, itemStack); hand = Hand.MAIN_HAND;}
			int pos = buf.readInt();
			if (pos != -2)
			((ItemStackWrap) itemStack).morph(pos);
			player.setStackInHand(hand, itemStack);
		});
	}

	static {
		ATTACHMENT_RECIPE_SERIALIZER = RecipeSerializer.register((new Identifier("akashictomeoftools", "add_page")).toString(), new SpecialRecipeSerializer<>(AttachmentRecipe::new));
		ATTACHMENT_RECIPE_TYPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("akashictomeoftools", "add_page"), new RecipeType<AttachmentRecipe>() {
			@Override
			public String toString() {return "akashictomeoftools:add_page";}
		});
	}
}