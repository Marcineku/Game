package com.mygame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygame.game.MyGame;

public class Hud {
    private Player player;

    private BitmapFont font;
    private TextureRegion coin;

    public Hud(Player player) {
        this.player = player;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 8;
        parameter.color = Color.GOLD;
        font = generator.generateFont(parameter);

        generator.dispose();

        TextureRegion[][] cn = TextureRegion.split(MyGame.assets.getTexture("coin"), 9, 9);
        coin = cn[0][0];
    }

    public void render(SpriteBatch sb) {
        String gold = Integer.toString(player.getGold());
        String arrows = Integer.toString(player.getArrows());

        sb.begin();
        sb.draw(coin, 20, MyGame.V_HEIGHT - 35);
        font.draw(sb, gold, 32, MyGame.V_HEIGHT - 28);
        font.draw(sb, "Arrows: " + arrows, 32, MyGame.V_HEIGHT - 48);
        sb.end();
    }

    public void dispose() {
        font.dispose();
    }
}
