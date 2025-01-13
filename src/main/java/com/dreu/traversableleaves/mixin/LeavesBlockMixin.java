package com.dreu.traversableleaves.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.dreu.traversableleaves.config.TLConfig.*;
import static net.minecraft.block.LeavesBlock.DISTANCE;
import static net.minecraft.block.LeavesBlock.PERSISTENT;

@Mixin(LeavesBlock.class) @SuppressWarnings({"deprecation", "unused", "NullableProblems"})
public abstract class LeavesBlockMixin extends Block {
    public LeavesBlockMixin(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, IBlockReader blockReader, BlockPos blockPos, ISelectionContext context) {
        if (context instanceof EntitySelectionContext && context.getEntity() != null) {
            return isTraversable() && IS_ENTITIES_WHITELIST == ENTITIES.contains(ForgeRegistries.ENTITIES.getKey(context.getEntity().getType())) && !(context.isAbove(VoxelShapes.block(), blockPos, true) && !context.isDescending()) ? VoxelShapes.empty() : VoxelShapes.block();
        }
        return VoxelShapes.block();
    }
    
    @ParametersAreNonnullByDefault
    public void entityInside(BlockState blockState, World level, BlockPos blockPos, Entity entity) {
        if (!isTraversable()) return;
        if (entity instanceof LivingEntity) {
            createAmbience(entity, blockPos);
            entity.fallDistance = 0;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply((MOVEMENT_PENALTY + getArmorBonus((LivingEntity) entity)) * 0.5f, entity.isCrouching() ? 0.5 : 1, (MOVEMENT_PENALTY + getArmorBonus((LivingEntity) entity)) * 0.5f));
        }
    }

    private float getArmorBonus(LivingEntity livingEntity) {
        return ARMOR_HELPS ? ARMOR_SCALE_FACTOR * MathHelper.clamp(livingEntity.getArmorValue(), 0, 20) : 0;
    }

    private void createAmbience(Entity entity, BlockPos blockPos){
        if (!entity.position().equals(new Vector3d(entity.xOld, entity.yOld, entity.zOld))) {
            if (entity.level.getGameTime() % 15 == 1) {
                entity.playSound(SoundEvents.GRASS_BREAK, 0.1f, 0.6f);
            }
            if (entity.level.getGameTime() % 4 == 1){
                double d0 = (double) blockPos.getX() + entity.level.random.nextDouble();
                double d1 = (double) blockPos.getY() + 1;
                double d2 = (double) blockPos.getZ() + entity.level.random.nextDouble();
                entity.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, this.defaultBlockState()), d0, d1, d2, 0, 0, 0);
            }
        }
    }

    public boolean isLadder(BlockState state, IWorldReader worldReader, BlockPos pos, LivingEntity livingEntity) {
        return isTraversable() && livingEntity instanceof PlayerEntity && !livingEntity.isCrouching() && livingEntity.jumping;
    }

    public boolean isTraversable(){
        return IS_LEAVES_WHITELIST == LEAVES.contains(ForgeRegistries.BLOCKS.getKey(this));
    }
}
