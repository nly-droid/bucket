package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class RainDrop implements Pool.Poolable{

  private Rectangle drop;
  private static final float width = 64;
  private static final float height = 64;
  private boolean alive;

  public RainDrop() {
    drop = new Rectangle();
    drop.setHeight(height);
    drop.setWidth(width);
    this.alive = false;
  }

  public void init(float posX, float posY) {
    drop.setPosition(posX, posY);
    alive = true;
  }

  @Override
  public void reset() {
    drop.setPosition(0,0);
    alive = false;
  }

  private boolean outOfScreen(){
    return (drop.y + 64 < 0);
  }

  public void update () {
    drop.y -= 200 * Gdx.graphics.getDeltaTime();
    if (outOfScreen()) alive = false;
  }
}
