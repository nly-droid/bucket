package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Drop extends ApplicationAdapter {
  public static final int dropRate = 1000000000;
  public static final int VIEWPORT_WIDTH = 800;
  public static final int VIEWPORT_HEIGHT = 480;
  public static final int initialHeight = 20;
  public static final int bucketMoveSpeed = 550;
  private Texture bucketImg;
	private Music rain;
	private SpriteBatch batch;

	private OrthographicCamera camera;

  private Vector3 touchPos;
	private Rectangle bucket;

	private final Array<RainDrop> activeDrops = new Array<>();
  private final Pool<RainDrop> dropPool = new Pool<RainDrop>() {
    @Override
    protected RainDrop newObject() {
      return new RainDrop();
    }
  };
	private long lastDropTime;

	@Override
	public void create () {
    // load the images for the the bucket, 64x64 pixels each
    bucketImg = new Texture(Gdx.files.internal("bucket.png"));

    // load the the rain background "music"
    rain = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

    // start the playback of the background music immediately
    rain.setLooping(true);
    rain.play();

    camera = new OrthographicCamera();
    camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

    batch = new SpriteBatch();

    bucket = new Rectangle();
    bucket.x = (float) (VIEWPORT_WIDTH/2 - 64/2);
    bucket.y = initialHeight;
    bucket.width = 64;
    bucket.height = 64;

    touchPos = new Vector3();

    spawnRaindrop();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(bucketImg, bucket.x, bucket.y);
    for(RainDrop raindrop: activeDrops) {
      batch.draw(RainDrop.getSprite(), raindrop.getX(), raindrop.getY());
    }
    batch.end();

    if (Gdx.input.isTouched()) {
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      bucket.x = touchPos.x - (float) 64/2;
    }

    if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= bucketMoveSpeed * Gdx.graphics.getDeltaTime();
    if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += bucketMoveSpeed * Gdx.graphics.getDeltaTime();
    
    // make sure our bucket stays within the screen limits
    if(bucket.x < 0) bucket.x = 0;
    if(bucket.x > VIEWPORT_WIDTH - 64) bucket.x = VIEWPORT_WIDTH - 64;

    if(TimeUtils.nanoTime() - lastDropTime > dropRate) spawnRaindrop();

    for (Iterator<RainDrop> iter = activeDrops.iterator(); iter.hasNext(); ) {
      RainDrop rainDrop = iter.next();
      rainDrop.update();
      if (!rainDrop.isAlive() || rainDrop.overlaps(bucket)){
        if (rainDrop.overlaps(bucket)) {
          RainDrop.playDropSound();
        }
        iter.remove();
        dropPool.free(rainDrop);
      }
    }
  }

  private void spawnRaindrop() {
    RainDrop rainDrop = dropPool.obtain();
    rainDrop.init(MathUtils.random(0, VIEWPORT_WIDTH - 64), VIEWPORT_HEIGHT);
    activeDrops.add(rainDrop);
    lastDropTime = TimeUtils.nanoTime();
  }

	@Override
	public void dispose () {
    RainDrop.dispose();
		batch.dispose();
		bucketImg.dispose();
		rain.dispose();
	}
}
