package com.mygame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygame.handlers.Constants;

/**
 * Is responsible for displaying information like current gold quantity of player's character on screen,
 * it uses it's own camera (hudCam), uses scene2d
 */
public class Hud {
    private Player player;

    private BitmapFont font;

    private Stage stage;
    private Viewport viewport;

    private Label goldText;
    private Label goldValue;

    private Label expText;
    private Label expValue;

    private Label arrowsText;
    private Label arrowsValue;

    /**
     * @param player player object
     * @param sb sprite batch
     * @param hudCam hud camera
     */
    public Hud(Player player, SpriteBatch sb, OrthographicCamera hudCam) {
        this.player = player;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.color = Color.GOLD;
        font = generator.generateFont(parameter);

        generator.dispose();

        viewport = new ScreenViewport(hudCam);
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.left();
        table.padTop(10);
        table.padLeft(5);
        table.setFillParent(true);

        goldText = new Label("GOLD: ", new Label.LabelStyle(font, Color.GOLD));
        table.add(goldText);
        goldValue = new Label("0", new Label.LabelStyle(font, Color.GOLD));
        table.add(goldValue);

        table.row().padTop(10);

        expText = new Label("EXP: ", new Label.LabelStyle(font, Color.WHITE));
        table.add(expText);
        expValue = new Label("0", new Label.LabelStyle(font, Color.WHITE));
        table.add(expValue);

        table.row().padTop(10);

        arrowsText = new Label("ARROWS: ", new Label.LabelStyle(font, Color.BROWN));
        table.add(arrowsText);
        arrowsValue = new Label("0", new Label.LabelStyle(font, Color.BROWN));
        table.add(arrowsValue);

        stage.addActor(table);
    }

    /**
     * Update's player's information and chooses which
     * information should be shown depending on current state of a player
     */
    public void update() {
        String gold = Integer.toString(player.getGold());
        String exp = Integer.toString(player.getExp());
        String arrows = Integer.toString(player.getArrows());

        goldValue.setText(gold);
        expValue.setText(exp);
        arrowsValue.setText(arrows);

        if(player.isWeaponDrawn() && player.getWeaponEquipped() != null && player.getWeaponEquipped().toString().equals(Constants.ITEM_BOW)) {
            arrowsText.setVisible(true);
            arrowsValue.setVisible(true);
        }
        else {
            arrowsText.setVisible(false);
            arrowsValue.setVisible(false);
        }
    }

    public void dispose() {
        font.dispose();
        stage.dispose();
    }

    /**
     * @return scene2d stage object
     */
    public Stage getStage() {
        return stage;
    }
}
