import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class OpenGL {

    public static void main(String[] args) throws Exception {
        new OpenGL().run();
    }

    public void run() throws Exception {
        setupOpenGL();
        setupQuad();
        setupShaders();

        while (!Display.isCloseRequested()) {
            render();
            Display.sync(60);
            Display.update();
        }

        Display.destroy();
    }

    // OpenGL variables
    private int vaoId = 0;
    private int vboId = 0;
    private int vbocId = 0;
    private int vboiId = 0;
    private int indicesCount = 0;
    private int pId = 0;

    public void setupOpenGL() throws LWJGLException {
        PixelFormat pixelFormat = new PixelFormat();
        ContextAttribs contextAtrributes = new ContextAttribs(3, 2).withProfileCore(true);

        Display.setDisplayMode(new DisplayMode(800, 600));
        Display.create(pixelFormat, contextAtrributes);

        GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
        GL11.glViewport(0, 0, 800, 600);
    }

    public void setupQuad() {
        // Vertices, the order is not important. XYZW instead of XYZ
        float[] vertices = { -0.5f, 0.5f, 0f, 1f, -0.5f, -0.5f, 0f, 1f, 0.5f, -0.5f, 0f, 1f, 0.5f, 0.5f, 0f, 1f };
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        float[] colors = { 1f, 0f, 0f, 1f, 0f, 1f, 0f, 1f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, };
        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors);
        colorsBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = { 0, 1, 2, 2, 3, 0 };
        indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // Vertices
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Colors
        vbocId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbocId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorsBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(0);

        // Indices
        vboiId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void setupShaders() throws Exception {
        int vsId = loadShader("resources/vertex.glsl", GL_VERTEX_SHADER);
        int fsId = loadShader("resources/fragment.glsl", GL_FRAGMENT_SHADER);

        pId = GL20.glCreateProgram();
        glAttachShader(pId, vsId);
        glAttachShader(pId, fsId);

        glBindAttribLocation(pId, 0, "in_Position");
        glBindAttribLocation(pId, 1, "in_Color");

        glLinkProgram(pId);
        glValidateProgram(pId);
    }

    public void render() {
        GL11.glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(pId);

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // DRAW!
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    public int loadShader(String filename, int type) throws Exception {
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, slurp(filename));
        GL20.glCompileShader(shaderID);

        return shaderID;
    }

    private String slurp(String filename) throws FileNotFoundException {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(new FileInputStream(new File(filename))).useDelimiter("\\A");
        String filedata = sc.next();
        sc.close();

        return filedata;
    }
}