package com.slope.game;

import com.slope.game.utils.PropModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader implements IGraphics {
    private static final int BIT_32_CAPACITY = 32;
    public static final int BIT_16_CAPACITY = 16;

    private List<Long> vaoList = new ArrayList<Long>();
    private List<Integer> vboList = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();
    private List<Long> eboList = new ArrayList<Long>();

    public PropModel createScreen(int texIndex) {
        float[] vertices = {
                -1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f
        };

        // No indices needed.
        int[] indices = {};

        float[] texCoords = {
                0.0f, 1.0f,  // Top-left corner
                0.0f, 0.0f,  // Bottom-left corner
                1.0f, 0.0f,  // Bottom-right corner
                1.0f, 0.0f,  // Bottom-right corner (again, since it's reused)
                1.0f, 1.0f,  // Top-right corner
                0.0f, 1.0f   // Top-left corner (again, since it's reused)
        };

        float[] colors = {
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };

        PropModel m = new PropModel(texIndex, vertices, indices, texCoords, colors, 1);
        return m;
    }

    public PropModel loadGLTFModel(int texIndex, String filename) {
        return loadGLTFModel(1, texIndex, filename);
    }
    
    public PropModel loadGLTFModel(int amount, int texIndex, String filename) {
        if(amount < 1) {
            return null;
        }

        List<Float> positions = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Float> colors = new ArrayList<>();

        AIScene scene = Assimp.aiImportFile(filename, Assimp.aiProcess_Triangulate);
        PointerBuffer buffer = scene.mMeshes();

        for (int i = 0; i < buffer.limit(); i++){
            AIMesh mesh = AIMesh.create(buffer.get(i));
            processMesh(mesh, positions, texCoords, normals, indices, colors);
        }

        float[] vertexArray = new float[positions.size()];
        float[] texCoordArray = new float[texCoords.size()];
        float[] normalArray = new float[normals.size()];
        float[] colorArray = new float[colors.size()];
        int[] indicesArray = new int[indices.size()];

        for (int i = 0; i < positions.size(); i++) {
            vertexArray[i] = positions.get(i);
        }
        for (int i = 0; i < texCoords.size(); i++) {
            texCoordArray[i] = texCoords.get(i);
        }
        for (int i = 0; i < normals.size(); i++) {
            normalArray[i] = normals.get(i);
        }
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        for(int i = 0; i < colors.size(); i++){
            colorArray[i] = colors.get(i);
        }

        PropModel m = new PropModel(texIndex, vertexArray, indicesArray, texCoordArray, colorArray, amount);
        return m;
    }

    private void processMesh(AIMesh mesh,
                             List<Float> positions,
                             List<Float> texCoords,
                             List<Float> normals,
                             List<Integer> indices,
                             List<Float> colors) {

        AIVector3D.Buffer vectors = mesh.mVertices();

        for(int i = 0; i < vectors.limit(); i++){
            AIVector3D vector = vectors.get(i);

            positions.add(vector.x());
            positions.add(vector.y());
            positions.add(vector.z());

        }

        AIVector3D.Buffer coords = mesh.mTextureCoords(0);

        for (int i = 0; i < coords.limit(); i++) {
            AIVector3D coord = coords.get(i);

            texCoords.add(coord.x());
            texCoords.add(coord.y());

        }

        AIVector3D.Buffer norms = mesh.mNormals();

        for (int i = 0; i < norms.limit(); i++) {

            AIVector3D norm = norms.get(i);

            normals.add(norm.x());
            normals.add(norm.y());
            normals.add(norm.z());
        }

        AIFace.Buffer facesBuffer = mesh.mFaces();
        for (int i = 0; i < facesBuffer.limit(); i++) {
            AIFace face = facesBuffer.get(i);
            IntBuffer indexBuffer = face.mIndices();
            while (indexBuffer.remaining() > 0) {
                indices.add(indexBuffer.get());
            }
        }

        AIColor4D.Buffer vertexColors = mesh.mColors(0);
        int vertexLength = (vertexColors != null) ? vertexColors.limit() : 0;
        int nullVertexLength = (vertexColors == null) ? vectors.limit() : 0;

        for (int i = 0; i < nullVertexLength; i++) {
            colors.add(0.0f);
            colors.add(1.0f);
            colors.add(0.0f);
            colors.add(1.0f);
        }

        for (int i = 0; i < vertexLength; i++) {
            AIColor4D vertexColor = vertexColors.get(i);

            colors.add(vertexColor.r());
            colors.add(vertexColor.g());
            colors.add(vertexColor.b());
            colors.add(vertexColor.a());
        }
    }

    public void loadVertexObject(PropModel model, int count) {
        long vertexAmount = (long) model.getVertices().length / 3;
        int VAO = createVAO();
        int EBO = storeIndexInAttribList(model);
        storeDataInAttribList(model.storeVerticesInBuffer(),0, count);
        storeDataInAttribList(model.storeTexCoordsInBuffer(), 1, 2);
        storeDataInAttribList(model.storeColorsInBuffer(), 2, 4);

        // Store VAO and vertex count in a 64-bit long (32 bits each)
        long vaoWithCount = ((long) VAO << BIT_32_CAPACITY) | (vertexAmount & 0xFFFFFFFFL);
        long eboWithCount = ((long) EBO << BIT_32_CAPACITY) | (model.getIndices().length & 0xFFFFFFFFL);

        // Store both VAO and vertex count in a 64-bit datatype since 32 + 32 = 64.
        // Also include the VBO.
        vaoList.add(vaoWithCount);
        eboList.add(eboWithCount);

        model.setIndex(vaoList.size() - 1);
    }

    private int createVAO() {
        int VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAO);
        return VAO;
    }

    private void storeDataInAttribList(FloatBuffer buffer, int index, int size) {
        int VBO = GL15.glGenBuffers();
        vboList.add(VBO);

        // Bind buffer object to array
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        // Set vertex attribute pointer for the shape
        GL20.glVertexAttribPointer(index, size, GL21.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private int storeIndexInAttribList(PropModel model) {
        int EBO = GL15.glGenBuffers();

        // Bind buffer object to element array (Basically indices)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, model.storeIndicesInBuffer(), GL15.GL_STATIC_DRAW);
        return EBO;
    }

    public int loadTexture(String filename) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;

        STBImage.stbi_set_flip_vertically_on_load(true);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            // STBImage.
            buffer = STBImage.stbi_load(filename, w, h, c, 4);

            if(buffer == null) {
               System.err.println("Failed to load texture: " + STBImage.stbi_failure_reason());
               throw new Exception("Failed to load texture on phase two. IMAGE FILENAME: " + filename);
            }

            width = w.get();
            height = h.get();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        int textureID = GL11.glGenTextures();
        textures.add(textureID);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Set texture wrapping options (set this only once after loading)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);  // Horizontal wrap
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);  // Vertical wrap

        // Set texture filtering options (minification and magnification filters)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        STBImage.stbi_image_free(buffer);
        return textureID;
    }

    // Get capacity of VAO.
    public int getCapacity() {
        return vaoList.size();
    }

    // Get the left most 32-bit chunk which is our VAO.
    public int getID(int index) {
        return (int) (vaoList.get(index) >> BIT_32_CAPACITY);
    }

    // Get the VBO by extracting the left-most 32 bits.
    public int getVBO(int index) {
        return vboList.get(index);
    }

    public int getEBO(int index) {
        return (int) (eboList.get(index) >> BIT_32_CAPACITY);
    }

    public int getTextures(int index) { return textures.get(index); }

    public int getIndicesCount(int index) {
        return (int) (eboList.get(index) & 0xFFFFFFFFL);
    }

    @Override
    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    @Override
    public void destroy() {
        while(getCapacity() != 0) {
            GL30.glDeleteVertexArrays(getID(0));
            vaoList.remove(0);
        }

        while(vboList.size() != 0) {
            GL30.glDeleteBuffers(getVBO(0));
            vboList.remove(0);
        }

        while(eboList.size() != 0) {
            GL30.glDeleteBuffers(getEBO(0));
            eboList.remove(0);
        }

        while(textures.size() != 0) {
            GL30.glDeleteTextures(getTextures(0));
            textures.remove(0);
        }
    }

    private static void processFace(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;

        if(length > 1) {
            String texCoord = lineToken[1];
            coords = texCoord.length() > 0 ? Integer.parseInt(texCoord) - 1 : -1;

            if(length > 2) {
                normal = Integer.parseInt(lineToken[2]) - 1;
            }
        }

        Vector3i faceVec = new Vector3i(pos, coords, normal);
        faces.add(faceVec);
    }

    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList,
                                      List<Vector3f> normalList, List<Integer> indicesList,
                                      float[] texCoordArr, float[] normalArr) {
        indicesList.add(pos);

        if(normal >= 0) {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }
}