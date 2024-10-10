package com.slope.game.objs;

import com.slope.game.IComponent;
import com.slope.game.ObjectLoader;
import com.slope.game.Shape;
import com.slope.game.Texture;

public interface IObject {

    // Create an Object via shape enums (meaning VAO, VBO, and EBO)
    static IObject withShape(ObjectLoader loader, Shape shape) {
        loader.loadVertexObject(shape);
        return new ShapeObject(shape);
    }

    // Set and load the texture applies
    void setTexture(ObjectLoader loader, String filename);
}