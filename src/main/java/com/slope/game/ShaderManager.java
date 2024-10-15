package com.slope.game;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL43.*;

public class ShaderManager implements IGraphics {
    private static final int BUFFER_SIZE = 1024;

    private final List<Integer> programIDs;
    private final Object2IntMap<CharSequence> uniformBlocks;
    private final Object2IntMap<CharSequence> storageBlocks;
    private final Object2IntMap<CharSequence> uniforms;
    private int vertexShaderID, fragmentShaderID;

    public ShaderManager() {
        this.programIDs = new ArrayList<>();
        this.uniformBlocks = new Object2IntArrayMap<>();
        this.storageBlocks = new Object2IntArrayMap<>();
        this.uniforms = new Object2IntArrayMap<>();
        createShaderProgram();

        if(programIDs.get(0) == 0) {
            throw new IllegalStateException("Wasn't able to create shaders.");
        }
    }

    public void createShaderProgram() {
        programIDs.add(GL20.glCreateProgram());
    }

    public void createUniform(CharSequence name) throws Exception {
        createUniform(0, name);
    }

    public void createUniform(int programIndex, CharSequence name) throws Exception {
        int location = GL20.glGetUniformLocation(programIDs.get(programIndex), name);

        if(location < 0) {
            throw new Exception("Could not find uniform: " + name);
        }

        uniforms.put(name, location);
    }

    public void setVec2Uniform(CharSequence name, Vector2f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniform2f(uniforms.getOrDefault(name, -1), value.x, value.y);
        }
    }

    public void setMatrixUniform(CharSequence name, Matrix4fc value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.getOrDefault(name,-1), false,
                value.get(stack.mallocFloat(16)));
        }
    }

    public void setIntUniform(CharSequence name, int value) {
        GL20.glUniform1i(uniforms.getOrDefault(name,-1), value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public void link() throws Exception {
        link(0);
    }

    public void link(int programIndex) throws Exception {
        int programID = programIDs.get(programIndex);

        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        GL20.glLinkProgram(programID);

        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking shader: INFO: " + GL20.glGetProgramInfoLog(programID, BUFFER_SIZE));
        }

        if(vertexShaderID != 0) {
            GL20.glDetachShader(programID, vertexShaderID);
        }

        if(fragmentShaderID != 0) {
            GL20.glDetachShader(programID, fragmentShaderID);
        }

        GL20.glValidateProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) {
            throw new Exception("Unable to validate the current shader code: "
                + GL20.glGetProgramInfoLog(programID, BUFFER_SIZE));
        }
    }

    public int getUniformBlock(CharSequence name) {
        return getUniformBlock(0, name);
    }

    public int getStorageBlock(CharSequence name) {
        return getStorageBlock(0, name);
    }

    public int getUniformBlock(int programIndex, CharSequence name) {
        int programID = this.programIDs.get(programIndex);

        if(programID == 0) {
            return GL_INVALID_INDEX;
        }

        return this.uniformBlocks.computeIfAbsent(name, k -> glGetUniformBlockIndex(programID, name));
    }

    public int getStorageBlock(int programIndex, CharSequence name) {
        int programID = this.programIDs.get(programIndex);

        if(programID == 0) {
            return GL_INVALID_INDEX;
        }

        return this.storageBlocks.computeIfAbsent(name, k -> glGetProgramResourceIndex(programID, GL_SHADER_STORAGE_BLOCK, name));
    }

    public void setUniformBlock(CharSequence name, int binding) {
        setUniformBlock(0, name, binding);
    }

    public void setStorageBlock(CharSequence name, int binding) {
        setStorageBlock(0, name, binding);
    }

    public void setUniformBlock(int programIndex, CharSequence name, int binding) {
        int programID = this.programIDs.get(programIndex);
        int index = this.getUniformBlock(name);

        if(index != GL_INVALID_INDEX) {
            glUniformBlockBinding(programID, index, binding);
        }
    }

    public void setStorageBlock(int programIndex, CharSequence name, int binding) {
        int programID = this.programIDs.get(programIndex);
        int index = this.getStorageBlock(name);

        if (index != GL_INVALID_INDEX) {
            glShaderStorageBlockBinding(programID, index, binding);
        }
    }

    public void bind() {
        bind(0);
    }

    public void bind(int programIndex) {
        GL20.glUseProgram(programIDs.get(programIndex));
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL20.glCreateShader(shaderType);

        if(shaderID == 0) {
            throw new IllegalStateException("Error creating shader type: " + shaderType);
        }

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling shader: TYPE: " + shaderType
            + "INFO: " + GL20.glGetShaderInfoLog(shaderID, BUFFER_SIZE));
        }

        return shaderID;
    }

    @Override
    public void unbind() {
        GL20.glUseProgram(0);
    }

    @Override
    public void destroy() {
        unbind();

        while(programIDs.size() != 0) {
            GL20.glDeleteShader(fragmentShaderID);
            GL20.glDeleteShader(vertexShaderID);
            GL20.glDeleteProgram(programIDs.get(0));
            programIDs.remove(0);
        }

        this.uniformBlocks.clear();
        this.storageBlocks.clear();
    }
}