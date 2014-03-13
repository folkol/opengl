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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

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

        glClearColor(0.4f, 0.6f, 0.9f, 0f);
        glViewport(0, 0, 800, 600);
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

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Vertices
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Colors
        vbocId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbocId);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        // Indices
        vboiId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void setupShaders() throws Exception {
        int vsId = loadShader("resources/vertex.glsl", GL_VERTEX_SHADER);
        int fsId = loadShader("resources/fragment.glsl", GL_FRAGMENT_SHADER);

        pId = glCreateProgram();
        glAttachShader(pId, vsId);
        glAttachShader(pId, fsId);

        glBindAttribLocation(pId, 0, "in_Position");
        glBindAttribLocation(pId, 1, "in_Color");

        glLinkProgram(pId);
        glValidateProgram(pId);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(pId);

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        int timeUniformLocation = glGetUniformLocation(pId, "time");
        glUniform1f(timeUniformLocation, getTime());
        int mouseUniformLocation = glGetUniformLocation(pId, "mouse");
        glUniform2f(mouseUniformLocation, (Mouse.getX() - 400)/800f, (Mouse.getY() - 300)/600f);

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
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, slurp(filename));
        glCompileShader(shaderID);

        return shaderID;
    }

    private String slurp(String filename) throws FileNotFoundException {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(new FileInputStream(new File(filename))).useDelimiter("\\A");
        String filedata = sc.next();
        sc.close();

        return filedata;
    }

    long startTime = System.nanoTime();
    private float getTime() {
        return (System.nanoTime() - startTime) / 1e9f;
    }
}