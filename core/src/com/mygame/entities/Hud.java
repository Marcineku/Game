package com.mygame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.game.MyGame;
import com.mygame.handlers.Constants;
import com.mygame.handlers.MyInput;

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
        if(player.getWeaponEquipped() != null && player.getWeaponEquipped().toString().equals(Constants.ITEM_BOW) && player.isWeaponDrawn()) {
            font.draw(sb, "Arrows: " + arrows, 32, MyGame.V_HEIGHT - 48);
        }
        if(MyInput.isDown(MyInput.EQ)) {
            for(Item i : player.getItems()) {
                String tmp = i.getItemName();
                String name = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
                Vector2 v = new Vector2(MyGame.V_WIDTH - 52, MyGame.V_HEIGHT - 52);
                sb.draw(i.getCurrentAnimation().getFrame(), v.x, v.y);
                font.draw(sb, name, v.x + 14, v.y + 4);
            }
        }
        sb.end();
    }

    public void dispose() {
        font.dispose();
    }
}
