package com.slope.game;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public class Texture {
    private final int textureID;

    public Texture(int ID) {
        this.textureID = ID;
    }

    public int getID() {
        return this.textureID;
    }
}
