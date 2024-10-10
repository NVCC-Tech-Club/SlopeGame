package com.slope.game.objs;

import com.slope.game.ObjectLoader;
import com.slope.game.Shape;
import com.slope.game.Texture;

public class ShapeObject implements IObject {
    private Texture texture;
    private Shape shape;

    protected ShapeObject(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void setTexture(ObjectLoader loader, String filename) {
        // texture = new Texture(loader.loadT);
    }
}