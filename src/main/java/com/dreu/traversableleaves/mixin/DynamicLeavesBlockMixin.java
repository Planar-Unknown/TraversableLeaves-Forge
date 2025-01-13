package com.dreu.traversableleaves.mixin;

import com.ferreusveritas.dynamictrees.block.leaves.DynamicLeavesBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.dreu.traversableleaves.config.TLConfig.*;

@SuppressWarnings({"NullableProblems", "deprecation", "unused"})
@Mixin(value = DynamicLeavesBlock.class, remap = false)
public abstract class DynamicLeavesBlockMixin extends LeavesBlock {

    static {
        if (!ModList.get().isLoaded("dynamictrees"))
            throw new RuntimeException("Traversable Leaves: Dynamic Trees is not loaded; skipping DynamicLeavesBlockMixin.");
    }

    public DynamicLeavesBlockMixin() {super(Properties.of(Material.AIR));}

    @Overwrite
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null)
            return this.isTraversable() && IS_ENTITIES_WHITELIST == ENTITIES.contains(ForgeRegistries.ENTITY_TYPES.getKey(entityContext.getEntity().getType()))
                    && !(context.isAbove(Shapes.block(), blockPos, true)
                    && !context.isDescending())
                    ? Shapes.empty() : Shapes.block();
        return Shapes.block();
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!isTraversable()) return;
        if (entity instanceof LivingEntity livingEntity) {
            createAmbience(entity, blockPos);
            livingEntity.resetFallDistance();
            entity.setDeltaMovement(entity.getDeltaMovement().multiply((MOVEMENT_PENALTY + getArmorBonus(livingEntity)) * 0.5f, livingEntity.isCrouching() ? 0.5 : 1, (MOVEMENT_PENALTY + getArmorBonus(livingEntity)) * 0.5f));
        }
    }

    private float getArmorBonus(LivingEntity livingEntity) {
        return ARMOR_HELPS ? ARMOR_SCALE_FACTOR * Mth.clamp(livingEntity.getArmorValue(), 0, 20) : 0;
    }

    private void createAmbience(Entity entity, BlockPos blockPos){
        if (!entity.position().equals(new Vec3(entity.xOld, entity.yOld, entity.zOld))) {
            if (entity.level.getGameTime() % 15 == 1) {
                entity.playSound(SoundEvents.GRASS_BREAK, 0.1f, 0.6f);
            }
            if (entity.level.getGameTime() % 4 == 1){
                double d0 = (double) blockPos.getX() + entity.level.random.nextDouble();
                double d1 = (double) blockPos.getY() + 1;
                double d2 = (double) blockPos.getZ() + entity.level.random.nextDouble();
                entity.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.defaultBlockState()), d0, d1, d2, 0, 0, 0);
            }
        }
    }

    public boolean isTraversable() {
        return IS_LEAVES_WHITELIST == LEAVES.contains(ForgeRegistries.BLOCKS.getKey(this));
    }
}
