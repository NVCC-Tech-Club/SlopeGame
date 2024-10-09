package com.slope.game.utils

import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL31
import org.lwjgl.opengl.GL32
import org.lwjgl.opengl.GLCapabilities


// Made for managing specific resource limits.
// Programmed in Kotlin since it's much nicer.

/*
* @param maxUniformComponents -> The number of active componets of uniform variable that
* can be defined outside of a uniform block.
* The term "component" refers to a basic component of a vector or matrix. Since a 3D vector takes up 3 components,
* the minimum value for this limit is 1024. Which means we can have 256 4D vectors.
*
* @param maxUniformBlocks -> The number of uniform blocks that can be used by a shader. The OpenGL-required is 12 blocks.
* The minimum value for this limit is 12 in GL 3.3, and 14 in GL 4.3.
*
* @param maxInputComponents -> The number of components of input variables that can be defined in a shader.
* Note: Vertex shaders use attributes multiplied by the number of components in the attribute, this is given
* by the Shape enum.
*
* @param maxShaderStorageBlocks -> The number of shader storage blocks that can be used by a shader. For
* fragment shaders and compute shaders, the minimum value is 4.
* For other shader types, the minimum value is 0.
*
* https://www.khronos.org/opengl/wiki/Shader#Resource_limitations
 */
data class ShaderLimits(
    val maxUniformComponents: Int,
    val maxUniformBlocks: Int,
    val maxInputCogetCapabilitiesponents: Int,
    val maxShaderStorageBlocks: Int
) {
    // Our current implementaion of our shader's limts.'
    companion object {
        val VERTEX_SHADER_LIMITS: () -> ShaderLimits = {
            val caps: GLCapabilities = GL.getCapabilities()
            ShaderLimits(
                caps,
                GL31.glGetInteger(GL31.GL_MAX_VERTEX_UNIFORM_COMPONENTS),
                GL31.glGetInteger(GL31.GL_MAX_VERTEX_UNIFORM_BLOCKS),
                GL31.glGetInteger(GL31.GL_MAX_VERTEX_ATTRIBS * 4),
                GL31.glGetInteger(GL32.GL_MAX_VERTEX_OUTPUT_COMPONENTS)
            )
        };
    }

    // Our Constructor
    constructor(
        caps: GLCapabilities,
        maxUniformComponents: Int,
        maxUniformBlocks: Int,
        maxInputComponents: Int,
        maxShaderStorageBlocks: Int) : this(
        maxUniformComponents,
        maxUniformBlocks,
        maxInputComponents,
        maxShaderStorageBlocks
    ) {}
}