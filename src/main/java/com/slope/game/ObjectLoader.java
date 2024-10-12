package com.slope.game;

import com.slope.game.utils.Model;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader implements IGraphics {
    private static final int BIT_32_CAPACITY = 32;
    private static boolean RE = false;

    private List<Long> vaoList = new ArrayList<Long>();
    private List<Integer> vboList = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();
    private List<Long> eboList = new ArrayList<Long>();

    public Model loadOBJModel(String fileName) {
        List<String> lines = ResourceLoader.readAllLines(fileName);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for(String line: lines) {
            String[] tokens = line.split("\\s+");

            switch(tokens[0]) {
                case "v":
                    //Vertices
                    Vector3f verticesVec = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );

                    // System.out.println("Line: " + tokens[0]);
                    vertices.add(verticesVec);

                    break;
                case "vt":

                    //Texture Coordinates
                    Vector2f texCoor = new Vector2f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2])
                    );
                    texCoords.add(texCoor);

                    break;
                case "vn":
                    // Vertex Normal
                    Vector3f normal = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );
                    normals.add(normal);

                    break;
                case "f":
                    //Faces
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);

                    break;
                default:
                    break;
            }
        }

        List<Integer> indices = new ArrayList<>();
        float[] vertexArray = new float[vertices.size() * 3];
        int i = 0;

        for(Vector3f pos: vertices) {
            vertexArray[i * 3 + 0] = pos.x;
            vertexArray[i * 3 + 1] = pos.y;
            vertexArray[i * 3 + 2] = pos.z;

            i++;
        }

        float[] texCoordArr = new float[texCoords.size() * 2];
        float[] normalArray = new float[vertices.size() * 3];
        int j = 0;

        for(Vector3i face: faces) {
            processVertex(face.x, face.y, face.z, texCoords, normals, indices, texCoordArr, normalArray);
        }

        for(Vector2f tex: texCoords) {
            texCoordArr[j * 2 + 0] = tex.x;
            texCoordArr[j * 2 + 1] = tex.y;

            j++;
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        return new Model(vertexArray, indicesArr, texCoordArr);
    }

    public void loadVertexObject(Shape sp) {
        loadVertexObject(sp.getModel(), sp.getVertexCount());
    }

    public void loadVertexObject(Model model, int count) {
        long vertexAmount = (long) model.getVertices().length / 3;
        int VAO = createVAO();
        int EBO = storeIndexInAttribList(model);
        storeDataInAttribList(model.storeVerticesInBuffer(),0, count);
        storeDataInAttribList(model.storeTexCoordsInBuffer(), 1, 2);

        // Store VAO and vertex count in a 64-bit long (32 bits each)
        long vaoWithCount = ((long) VAO << BIT_32_CAPACITY) | (vertexAmount & 0xFFFFFFFFL);
        long eboWithCount = ((long) EBO << BIT_32_CAPACITY) | (model.getIndices().length & 0xFFFFFFFFL);

        // Store both VAO and vertex count in a 64-bit datatype since 32 + 32 = 64.
        // Also include the VBO.
        vaoList.add(vaoWithCount);
        eboList.add(eboWithCount);
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

    private int storeIndexInAttribList(Model model) {
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
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        STBImage.stbi_image_free(buffer);

        return textureID;
    }

    public int getCapacity() {
        return vaoList.size();
    }

    public int getTextureCapacity() { return textures.size(); }

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