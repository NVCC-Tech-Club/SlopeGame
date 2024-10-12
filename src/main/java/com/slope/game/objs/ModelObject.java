package com.slope.game.objs;

import com.slope.game.ObjectLoader;
import com.slope.game.Texture;
import com.slope.game.utils.Model;

public class ModelObject implements IObject {
    private Texture texture;
    private Model model;

    protected ModelObject(Model model) {
        this.model = model;
    }

    @Override
    public void setTexture(ObjectLoader loader, String filename) {
        texture = new Texture(loader.loadTexture(filename));
    }
}