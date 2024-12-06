package com.dreu.traversableleaves.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

import static com.dreu.traversableleaves.config.TLConfig.*;
import static net.minecraft.world.level.block.LeavesBlock.*;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block {
    public LeavesBlockMixin(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(WATERLOGGED, false));
    }

    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null){
            return IS_ENTITIES_WHITELIST == ENTITIES.contains(ForgeRegistries.ENTITY_TYPES.getKey(entityContext.getEntity().getType())) && !(context.isAbove(Shapes.block(), blockPos, true) && !context.isDescending()) ? Shapes.empty() : Shapes.block();
        }

        return Shapes.block();

    }
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!isTraversable()) return;
        if (entity instanceof Player player){
            if (!level.getBlockState(new BlockPos(player.position())).is(BlockTags.LEAVES)){
                entity.resetFallDistance();
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0, 1, 0));
            } else {
                player.makeStuckInBlock(blockState, new Vec3(0.6, 1.0, 0.6));
            }
        } else if (entity instanceof LivingEntity livingEntity) {
            entity.resetFallDistance();
            entity.setDeltaMovement(entity.getDeltaMovement().subtract(0, entity.getDeltaMovement().y + 1, 0));
        }
    }

    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return isTraversable() && entity instanceof Player player && !player.isCrouching();
    }
    public boolean isTraversable(){
        return IS_LEAVES_WHITELIST == LEAVES.contains(ForgeRegistries.BLOCKS.getKey(this));
    }
}
