package com.slope.game;

import com.slope.game.utils.TriContainer;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL43.*;

public class ShaderManager implements IGraphics {
    private static final int BUFFER_SIZE = 1024;

    //private final List<Integer> programIDs;
    private final List<TriContainer<Integer, Integer, Integer>> IDs;
    private final Object2IntMap<CharSequence> uniformBlocks;
    private final Object2IntMap<CharSequence> storageBlocks;
    private final Object2IntMap<CharSequence> uniforms;

    public ShaderManager() {
        this.IDs = new ArrayList<>();
        this.uniformBlocks = new Object2IntArrayMap<>();
        this.storageBlocks = new Object2IntArrayMap<>();
        this.uniforms = new Object2IntArrayMap<>();
        createShaderProgram();

        if(IDs.get(0).getFirst() == 0) {
            throw new IllegalStateException("Wasn't able to create shaders.");
        }
    }

    public void createShaderProgram() {
        IDs.add(new TriContainer<>(GL20.glCreateProgram(), -1, -1));
    }

    public void createUniform(CharSequence name) throws Exception {
        createUniform(0, name);
    }

    public void createUniform(int programIndex, CharSequence name) throws Exception {
        int location = GL20.glGetUniformLocation(IDs.get(programIndex).getFirst(), name);

        if(location < 0) {
            throw new Exception("Could not find uniform: " + name);
        }

        uniforms.put(name, location);
    }

    public void setVec2Uniform(CharSequence name, Vector2f value) {
        GL20.glUniform2f(uniforms.getOrDefault(name, -1), value.x, value.y);
    }

    public void setVec3Uniform(CharSequence name, Vector3f value) {
        GL20.glUniform3f(uniforms.getOrDefault(name, -1), value.x, value.y, value.z);
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
        createVertexShader(0, shaderCode);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        createFragmentShader(0, shaderCode);
    }

    public void createVertexShader(int index, String shaderCode) throws Exception {
        IDs.get(index).setSecond(createShader(shaderCode, GL20.GL_VERTEX_SHADER));
    }

    public void createFragmentShader(int index, String shaderCode) throws Exception {
        IDs.get(index).setThird(createShader(shaderCode, GL20.GL_FRAGMENT_SHADER));
    }

    public void link() throws Exception {
        link(0);
    }

    public void link(int programIndex) throws Exception {
        int programID = IDs.get(programIndex).getFirst();
        int vertexShaderID = IDs.get(programIndex).getSecond();
        int fragmentShaderID = IDs.get(programIndex).getThird();

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
        int programID = this.IDs.get(programIndex).getFirst();

        if(programID == 0) {
            return GL_INVALID_INDEX;
        }

        return this.uniformBlocks.computeIfAbsent(name, k -> glGetUniformBlockIndex(programID, name));
    }

    public int getStorageBlock(int programIndex, CharSequence name) {
        int programID = this.IDs.get(programIndex).getFirst();

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
        int programID = this.IDs.get(programIndex).getFirst();
        int index = this.getUniformBlock(name);

        if(index != GL_INVALID_INDEX) {
            glUniformBlockBinding(programID, index, binding);
        }
    }

    public void setStorageBlock(int programIndex, CharSequence name, int binding) {
        int programID = this.IDs.get(programIndex).getFirst();
        int index = this.getStorageBlock(name);

        if (index != GL_INVALID_INDEX) {
            glShaderStorageBlockBinding(programID, index, binding);
        }
    }

    public void bind() {
        bind(0);
    }

    public void bind(int programIndex) {
        GL20.glUseProgram(this.IDs.get(programIndex).getFirst());
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

        while(IDs.size() != 0) {
            GL20.glDeleteShader(IDs.get(0).getThird());
            GL20.glDeleteShader(IDs.get(0).getSecond());
            GL20.glDeleteProgram(IDs.get(0).getFirst());
            IDs.remove(0);
        }

        this.uniformBlocks.clear();
        this.storageBlocks.clear();
    }
}