package de.zaryon.nohunger.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class DamageAdjustMixin {

    @ModifyVariable(
            method = "damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At("HEAD"),
            ordinal = 0
    )
    private float adjustDamage(float amount, ServerWorld world, DamageSource source) {
        RegistryEntry<DamageType> type = source.getTypeRegistryEntry();

        if (type.matchesKey(DamageTypes.CACTUS)
                || type.matchesKey(DamageTypes.HOT_FLOOR)
                || type.matchesKey(DamageTypes.IN_FIRE)
                || type.matchesKey(DamageTypes.CAMPFIRE)
                || type.matchesKey(DamageTypes.WITHER)
                || type.matchesKey(DamageTypes.SWEET_BERRY_BUSH)) {
            return 2.3F;
        }

        if (type.matchesKey(DamageTypes.ON_FIRE)) {
            return 3.0F;
        }

        if (type.matchesKey(DamageTypes.DROWN)) {
            return 3.5F;
        }

        if (type.matchesKey(DamageTypes.FREEZE)) {
            return 2.5F;
        }

        if (type.matchesKey(DamageTypes.FALL) && amount < 2.0F) {
            return 2.5F;
        }

        if (type.matchesKey(DamageTypes.STALAGMITE)) {
            return Math.max(amount, 3.5F);
        }

        if (type.matchesKey(DamageTypes.WIND_CHARGE)) {
            return Math.max(amount, 3.0F);
        }

        return amount;
    }
}