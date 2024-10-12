package com.slope.game.objs;

import com.slope.game.ObjectLoader;
import com.slope.game.utils.Model;

public interface IObject {

    // Create an Object via shape enums (meaning VAO, VBO, and EBO)
    static IObject withShape(ObjectLoader loader, Model m) {
        loader.loadVertexObject(m, 3);
        return new ModelObject(m);
    }

    // Set and load the texture applies
    void setTexture(ObjectLoader loader, String filename);
}