package infinitealloys.client.model.entity;

import net.minecraft.client.model.ModelBlaze;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public final class ModelBossBlaze extends ModelBlaze {

  @Override
  public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6,
                     float par7) {
    GL11.glPushMatrix();
    GL11.glScalef(4F, 4F, 4F);
    GL11.glTranslatef(0F, -1.13F, 0F);
    super.render(par1Entity, par2, par3, par4, par5, par6, par7);
    GL11.glPopMatrix();
  }
}