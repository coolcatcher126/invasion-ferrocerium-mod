package io.github.coolcatcher126.ferrocerium.item.custom;

import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AlienBeaconItem extends Item {
    public AlienBeaconItem(Settings settings) {
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
        // TODO: add the actual invasion.

        //Use up the item
        ItemStack heldStack = user.getStackInHand(hand);
        heldStack.decrement(1);
        return TypedActionResult.success(heldStack);
    }
}
