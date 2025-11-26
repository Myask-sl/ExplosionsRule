package invalid.myask.explosionsrule.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Explosion.class)
public class MixinExplosion {
    @Shadow
    public Entity exploder;

    @Unique Object explosionsRule$lastExplode = null;
    @Unique boolean explosionsRule$creeper = true;
    @Unique boolean explosionsRule$mob = true;
    @Unique boolean explosionsRule$block = true;
    @Unique boolean explosionsRule$tnt = false;
    @Unique boolean explosionsRule$playerfireball = false;

    @Unique float explosionsRule$chance = 1;

    @ModifyArg(method = "doExplosionB",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropBlockAsItemWithChance(Lnet/minecraft/world/World;IIIIFI)V"), index = 5)
    private float explosionsRule$NewChance(float chance) {
        if (this != explosionsRule$lastExplode) {
            explosionsRule$lastExplode = this;
            GameRules rules = exploder.worldObj.getWorldInfo().getGameRulesInstance();

            explosionsRule$creeper = rules.getGameRuleBooleanValue("creeperExplosionDropDecay");
            explosionsRule$mob = rules.getGameRuleBooleanValue("mobExplosionDropDecay");
            explosionsRule$block = rules.getGameRuleBooleanValue("blockExplosionDropDecay");
            explosionsRule$tnt = rules.getGameRuleBooleanValue("tntExplosionDropDecay");
            explosionsRule$playerfireball = rules.getGameRuleBooleanValue("playerFireballExplosionDropDecay");
            explosionsRule$chance = chance;

            if (!explosionsRule$mob && (exploder instanceof EntityLiving || exploder instanceof EntityWitherSkull))
                explosionsRule$chance = 1;
            else if (exploder instanceof EntityLargeFireball fireball) {
                if (!explosionsRule$mob && fireball.shootingEntity instanceof EntityLiving)
                    explosionsRule$chance = 1;
                else if (!explosionsRule$playerfireball && fireball.shootingEntity instanceof EntityPlayer)
                    explosionsRule$chance = 1;
            }
            else if (!explosionsRule$creeper && exploder instanceof EntityCreeper)
                explosionsRule$chance = 1;
            else if (!explosionsRule$tnt && (exploder instanceof EntityMinecartTNT || exploder instanceof EntityTNTPrimed))
                explosionsRule$chance = 1;
            else if (!explosionsRule$block && exploder == null)
                explosionsRule$chance = 1;
        }

        return explosionsRule$chance;
    }
}
