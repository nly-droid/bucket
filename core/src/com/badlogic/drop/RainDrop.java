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

  public float getX(){
    return drop.getX();
  }

  public float getY(){
    return drop.getY();
  }

  @Override
  public void reset() {
    drop.setPosition(0,0);
    alive = false;
  }

  private boolean outOfScreen(){
    return (drop.y + 64 < 0);
  }

  public boolean isAlive(){
    return alive;
  }

  public boolean overlaps(Rectangle rec){
    return drop.overlaps(rec);
  }

  public void update () {
    // Move at a constant speed of 200 pixels/units per second
    drop.y -= 200 * Gdx.graphics.getDeltaTime();
    // If the raindrop is beneath the bottom edge of the screen, we remove it
    if (outOfScreen()) alive = false;
  }
}
