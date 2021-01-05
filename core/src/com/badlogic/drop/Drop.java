package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Drop extends ApplicationAdapter {
	private Texture bucketImg;
	private Texture drop;
	private Music rain;
	private Sound waterDrop;
	private SpriteBatch batch;

	private OrthographicCamera camera;

  private Vector3 touchPos;
	private Rectangle bucket;

	private Array<Rectangle> drops;
	private long lastDropTime;

	@Override
	public void create () {
    // load the images for the droplet and the bucket, 64x64 pixels each
    drop = new Texture(Gdx.files.internal("drop.png"));
    bucketImg = new Texture(Gdx.files.internal("bucket.png"));

    // load the drop sound effect and the rain background "music"
    waterDrop = Gdx.audio.newSound(Gdx.files.internal("waterdrop.mp3"));
    rain = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

    // start the playback of the background music immediately
    rain.setLooping(true);
    rain.play();

    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);

    batch = new SpriteBatch();

    bucket = new Rectangle();
    bucket.x = (float) (800/2 - 64/2);
    bucket.y = 20;
    bucket.width = 64;
    bucket.height = 64;

    touchPos = new Vector3();

    drops = new Array<>();
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
    for(Rectangle raindrop: drops) {
      batch.draw(drop, raindrop.x, raindrop.y);
    }
    batch.end();

    if (Gdx.input.isTouched()) {
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      bucket.x = touchPos.x - (float) 64/2;
    }

    if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
    if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

    if(bucket.x < 0) bucket.x = 0;
    if(bucket.x > 800 - 64) bucket.x = 800 - 64;

    if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

    for (Iterator<Rectangle> iter = drops.iterator(); iter.hasNext(); ) {
      Rectangle raindrop = iter.next();
      raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
      if(raindrop.y + 64 < 0) iter.remove();
      if(raindrop.overlaps(bucket)) {
        waterDrop.play();
        iter.remove();
      }
    }
  }

  private void spawnRaindrop() {
    Rectangle raindrop = new Rectangle();
    raindrop.x = MathUtils.random(0, 800-64);
    raindrop.y = 480;
    raindrop.width = 64;
    raindrop.height = 64;
    drops.add(raindrop);
    lastDropTime = TimeUtils.nanoTime();
  }

	@Override
	public void dispose () {
		batch.dispose();
		bucketImg.dispose();
		drop.dispose();
		rain.dispose();
		waterDrop.dispose();
	}
}
