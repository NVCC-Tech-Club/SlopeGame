package com.slope.game;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class FrameBuffer {
    private int fbo;
    private int rbo;
    private int texture;
    private int depthTexture;

    public FrameBuffer() {
        fbo = 0;
        texture = 0;
        depthTexture = 0;
    }

    public void init(int width, int height) {

        // Create FBO
        fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

        // Create texture to attach to FBO
        texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Attach texture to FBO
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);

        // Create RBO
        rbo = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL21.GL_DEPTH_COMPONENT32, width, height);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

        // Attach RBO to the depth attachment of our FBO
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo);

        // Create texture to attach to RBO
        depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL21.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Attach FBO depth to the actual depth texture
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);

        // Check if FBO is complete
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Framebuffer not complete!");
            System.exit(1);
        }

        // Unbind everything
        GL21.glBindTexture(GL21.GL_TEXTURE_2D, 0);
        unbind();
    }

    public void onWindowResize(int width, int height) {
        destroy();
        init(width, height);
    }

    public int getTextureID() {
        return texture;
    }

    public int getDepthTexture() { return depthTexture; }

    public void bind() {
        GL30.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        //GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
    }

    public void destroy() {
        GL30.glDeleteTextures(texture);
        GL30.glDeleteFramebuffers(fbo);
    }

    public static void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}