package com.mygame.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Content {
    private HashMap<String, Texture> textures;
    private HashMap<String, Sound> sounds;

    public Content() {
        textures = new HashMap<String, Texture>();
        sounds = new HashMap<String, Sound>();
    }

    public void loadTexture(String path, String key) {
        Texture tex = new Texture(Gdx.files.internal(path));
        textures.put(key, tex);
    }

    public Texture getTexture(String key) {
        return textures.get(key);
    }

    public void loadSound(String path, String key) {
        Sound s = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(key, s);
    }

    public Sound getSound(String key) {
        return sounds.get(key);
    }

    public void disposeTexture(String key) {
        Texture tex = textures.get(key);
        if(tex != null) tex.dispose();
    }

    public void disposeAll() {
        for(Texture i : textures.values()) {
            i.dispose();
        }

        for(Sound i : sounds.values()) {
            i.dispose();
        }
    }
}
