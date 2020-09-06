package greekfantasy.entity;

import java.util.EnumSet;

import greekfantasy.entity.ai.GoToBlockGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CerastesEntity extends CreatureEntity {
  
  private static final byte STANDING_START = 4;
  private static final byte STANDING_END = 5;
  private static final byte HIDING_START = 6;
  private static final byte HIDING_END = 7;
  
  private final EntitySize hiddenSize;
  
  private final int MAX_TONGUE_TIME = 10;
  private final float STANDING_SPEED = 0.18F;
  private int tongueTime;
  private float standingTime;
  private float hidingTime;
  
  private boolean isHiding;
  private boolean isStanding;
  
  public CerastesEntity(final EntityType<? extends CerastesEntity> type, final World worldIn) {
    super(type, worldIn);
    this.hiddenSize = EntitySize.flexible(0.8F, 0.2F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.31D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 5.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new HideGoal(this));
    this.goalSelector.addGoal(2, new GoToSandGoal(this, 10, 0.8D));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    //this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 4.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false, false));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, RabbitEntity.class, false, false));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // tongue-flick counter
    if(this.tongueTime == 0) {
      if(!this.isStanding() && rand.nextInt(100) == 0) {
        tongueTime = 1;
      }
    } else if (++this.tongueTime > MAX_TONGUE_TIME) {
      this.tongueTime = 0;
    }
    // standing counter
    if(this.isStanding()) {
      standingTime = Math.min(1.0F, standingTime + STANDING_SPEED);
    } else if(standingTime > 0.0F) {
      standingTime = Math.max(0.0F, standingTime - STANDING_SPEED);
    }
    // hiding counter
    if(this.isHiding()) {
      hidingTime = Math.min(1.0F, hidingTime + STANDING_SPEED);
    } else if(hidingTime > 0.0F) {
      hidingTime = Math.max(0.0F, hidingTime - STANDING_SPEED);
    }
    // standing logic
    if(this.isServerWorld()) {
      if(this.getAttackTarget() != null) {
        this.setStanding(true);
      } else if(this.isStanding() && this.rand.nextInt(60) == 0){
        this.setStanding(false);
      }
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case STANDING_START:
      this.isStanding = true;
      this.isHiding = false;
      break;
    case STANDING_END:
      this.isStanding = false;
      break;
    case HIDING_START:
      this.isHiding = true;
      this.isStanding = false;
      this.recalculateSize();
      break;
    case HIDING_END:
      this.isHiding = false;
      this.recalculateSize();
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  @Override
  public boolean attackEntityFrom(final DamageSource source, final float amount) {
    this.setHiding(false);
    this.setStanding(true);
    return super.attackEntityFrom(source, amount);
  }

  @Override
  protected void collideWithEntity(final Entity entityIn) {
    if (entityIn instanceof LivingEntity && this.canAttack(entityIn.getType()) && this.getRNG().nextInt(20) == 0) {
      this.setAttackTarget((LivingEntity) entityIn);
    }
    if(this.isServerWorld()) {
      this.setHiding(false);
      this.setStanding(true);
    }

    super.collideWithEntity(entityIn);
  }
  
  @Override
  public boolean canAttack(EntityType<?> typeIn) {
    if (typeIn == this.getType() || typeIn == EntityType.CREEPER) {
       return false;
    }
    return super.canAttack(typeIn);
 }

  @Override
  public EntitySize getSize(Pose poseIn) {
    return this.isHiding() ? hiddenSize : super.getSize(poseIn);
  }
  
  public void setStanding(final boolean standing) {
    this.isStanding = standing;
    if(standing) this.isHiding = false;
    this.world.setEntityState(this, standing ? STANDING_START : STANDING_END);
  }
  
  public boolean isStanding() {
    return isStanding;
  }
  
  public float getTongueTime() {
    return ((float) this.tongueTime) / ((float) MAX_TONGUE_TIME);
  }
  
  public float getStandingTime(final float partialTick) {
    return standingTime;
  }
  
  public void setHiding(final boolean hiding) {
    this.isHiding = hiding;
    if(hiding) {
      this.isStanding = false;
    }
    this.world.setEntityState(this, hiding ? HIDING_START : HIDING_END);
    this.recalculateSize();
  }
  
  public boolean isHiding() {
    return isHiding;
  }
  
  public float getHidingTime(final float partialTick) {
    return hidingTime;
  }
  
  static class GoToSandGoal extends GoToBlockGoal {
    
    final CerastesEntity cerastes;

    public GoToSandGoal(final CerastesEntity entity, final int radius, final double speed) {
      super(entity, radius, speed, b -> BlockTags.SAND.contains(b.getBlock()));
      this.cerastes = entity;
    }
    
    @Override
    public boolean shouldExecute() {
      return !this.cerastes.isHiding() && this.cerastes.getAttackTarget() == null 
          && this.cerastes.getRNG().nextInt(100) == 0 && super.shouldExecute();
    }
  }
  
  static class HideGoal extends Goal {
    
    final CerastesEntity entity;
    final int MAX_HIDE_TIME = 500;
    final int MAX_COOLDOWN = 500;
    int cooldown;

    public HideGoal(final CerastesEntity entityIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      this.entity = entityIn;
      this.cooldown = entityIn.getRNG().nextInt(MAX_COOLDOWN);
    }

    @Override
    public boolean shouldExecute() {
      if(this.cooldown > 0) {
        cooldown--;
      } else if (this.entity.getAttackTarget() != null || !this.entity.getNavigator().noPath() || this.entity.isHiding()) {
        return false;
      } else if (this.entity.getRNG().nextInt(10) == 0) {
        BlockPos blockpos = (new BlockPos(this.entity.getPosX(), this.entity.getPosY() - 0.5D, this.entity.getPosZ()));
        BlockState blockstate = this.entity.world.getBlockState(blockpos);
        return BlockTags.SAND.contains(blockstate.getBlock());
      }
      return false;
    }
    
    @Override
    public void startExecuting() {
      this.entity.setHiding(true);
      this.cooldown = MAX_COOLDOWN + MAX_HIDE_TIME;
    }
    
    @Override
    public void tick() {
      super.tick();
      if(this.entity.isHiding()) {
        if(this.entity.getAttackTarget() != null || this.entity.getRNG().nextInt(MAX_HIDE_TIME) == 0) {
          this.resetTask();
          return;
        }
        this.entity.getNavigator().clearPath();
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return this.entity.isHiding();
    }
    
    @Override
    public void resetTask() {
      super.resetTask();
      this.entity.setHiding(false);
      this.cooldown = MAX_COOLDOWN;
    }
    
  }
  
}