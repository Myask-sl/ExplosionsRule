package invalid.myask.explosionsrule.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityLargeFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityLargeFireball.class)
public class MixinLargeFireball {
    @ModifyArg(method = "onImpact",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"),
    index = 0)
    private Entity explosionsRule$blame (Entity blame) {
        return (Entity) (Object) this;
    }
}
