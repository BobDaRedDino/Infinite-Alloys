package infinitealloys.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import infinitealloys.item.IAItems;
import infinitealloys.util.EnumBoss;
import infinitealloys.util.EnumUpgrade;

public abstract class EntityIABoss extends EntityMob {

  public final EnumBoss bossType;

  /**
   * @param bossType the bossType that this entity pertains to
   */
  public EntityIABoss(World world, EnumBoss bossType) {
    super(world);
    this.bossType = bossType;
    setSize(2F, 8F);
    isImmuneToFire = true;
    experienceValue = 50;
    setHomeArea((int) posX, (int) posY, (int) posZ, 15); // Fix the entity to a certain area

    // All the AI tasks needed for proper operation
    tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
    tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
    tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
    tasks.addTask(7, new EntityAIWander(this, 1.0D));
    tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(8, new EntityAILookIdle(this));
    targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
    targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
  }

  @Override
  protected Item getDropItem() {
    return IAItems.upgrades[EnumUpgrade.ALLOY.ordinal()];
  }

  @Override
  protected void dropFewItems(boolean hitByPlayer, int lootingLevel) {
    if (hitByPlayer) {
      entityDropItem(new ItemStack(getDropItem(), 1, bossType.alloy.ordinal()), 0F);
    }
  }

  @Override
  protected boolean isValidLightLevel() {
    return true;
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();

    getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
    getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(bossType.health);
  }

  @Override
  protected boolean isAIEnabled() {
    return true;
  }
}
