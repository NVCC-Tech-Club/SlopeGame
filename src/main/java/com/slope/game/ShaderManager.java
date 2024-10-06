package com.slope.game;

import org.lwjgl.opengl.GL20;

public class ShaderManager implements IGraphics {
    private static final int BUFFER_SIZE = 1024;

    private final int programID;
    private int vertexShaderID, fragmentShaderID;

    public ShaderManager() {
        this.programID = GL20.glCreateProgram();

        if(programID == 0) {
            throw new IllegalStateException("Wasn't able to create shaders.");
        }
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
    }
}