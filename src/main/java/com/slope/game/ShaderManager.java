package com.slope.game;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL43.*;

public class ShaderManager implements IGraphics {
    private static final int BUFFER_SIZE = 1024;

    private final int programID;
    private final Object2IntMap<CharSequence> uniformBlocks;
    private final Object2IntMap<CharSequence> storageBlocks;
    private final Object2IntMap<CharSequence> uniforms;
    private int vertexShaderID, fragmentShaderID;

    public ShaderManager() {
        this.uniformBlocks = new Object2IntArrayMap<>();
        this.storageBlocks = new Object2IntArrayMap<>();
        this.uniforms = new Object2IntArrayMap<>();
        this.programID = GL20.glCreateProgram();

        if(programID == 0) {
            throw new IllegalStateException("Wasn't able to create shaders.");
        }
    }

    public void createUniform(CharSequence name) throws Exception {
        int location = GL20.glGetUniformLocation(programID, name);

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
        if(this.programID == 0) {
            return GL_INVALID_INDEX;
        }

        return this.uniformBlocks.computeIfAbsent(name, k -> glGetUniformBlockIndex(this.programID, name));
    }

    public int getStorageBlock(CharSequence name) {
        if(this.programID == 0) {
            return GL_INVALID_INDEX;
        }

        return this.storageBlocks.computeIfAbsent(name, k -> glGetProgramResourceIndex(this.programID, GL_SHADER_STORAGE_BLOCK, name));
    }

    public void setUniformBlock(CharSequence name, int binding) {
        int index = this.getUniformBlock(name);

        if(index != GL_INVALID_INDEX) {
            glUniformBlockBinding(this.programID, index, binding);
        }
    }

    public void setStorageBlock(CharSequence name, int binding) {
        int index = this.getStorageBlock(name);

        if (index != GL_INVALID_INDEX) {
            glShaderStorageBlockBinding(this.programID, index, binding);
        }
    }

    public void bind() {
        GL20.glUseProgram(programID);
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

        if(programID != 0) {
            GL20.glDeleteShader(fragmentShaderID);
            GL20.glDeleteShader(vertexShaderID);
            GL20.glDeleteProgram(programID);
        }

        this.uniformBlocks.clear();
        this.storageBlocks.clear();
    }
}