package io.github.coolcatcher126.ferrocerium.item.custom;

import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CrudeAlienBeaconItem extends Item {
    public CrudeAlienBeaconItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //Play sound of invasion start
        user.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);

        //Don't do anything unless server
        if (world.isClient()){
            return super.use(world, user, hand);
        }

        //Activate an invasion nearby
        InvasionFerroceriumComponents.progressInvasion(world);
        int invLevel = InvasionFerroceriumComponents.getInvasionLevel(world);
        user.sendMessage(Text.literal("Invasion level is: %s".formatted(invLevel)));

        //Use up the item
        ItemStack heldStack = user.getStackInHand(hand);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        heldStack.decrementUnlessCreative(1, user);
        return TypedActionResult.success(heldStack, world.isClient());
    }
}
