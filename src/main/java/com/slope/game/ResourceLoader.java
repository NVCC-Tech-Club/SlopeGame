package com.slope.game;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ResourceLoader {
    public static String loadShader(String fileName) {
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(fileName);
        StringBuilder shaderSource = new StringBuilder();

        if(useShaderReader(is, shaderSource) == 0) {
            throw new IllegalStateException("Was not able to read this shader file!");
        }

        return shaderSource.toString();
    }

    private static int useShaderReader(InputStream is, StringBuilder shaderSource) {
        try {
            BufferedReader shaderReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = shaderReader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }

            shaderReader.close();
            return 1;
        }catch (Exception e) {
            return 0;
        }
    }
}