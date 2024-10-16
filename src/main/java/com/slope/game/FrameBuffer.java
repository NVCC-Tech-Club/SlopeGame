package com.slope.game;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FrameBuffer {
    private int fbo;
    private int texture;

    public FrameBuffer() {
        fbo = 0;
        texture = 0;
    }

    public void init(int width, int height) {
        // Create FBO
        fbo = GL30.glGenFramebuffers();
        // GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo); // Commented out to disable binding

        // Create texture to attach to FBO
        texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Attach texture to FBO
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);

        // Check if FBO is complete
        // GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo); // Commented out to disable binding
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Framebuffer not complete!");
            System.exit(1);
        }

        unbind();
    }

    public int getTextureID() {
        return texture;
    }

    public void bind() {
        // GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo); // Commented out to disable binding
    }

    public void destroy() {
        GL30.glDeleteFramebuffers(fbo);
    }

    public static void unbind() {
        // GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0); // Commented out to disable binding
    }
}
