import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

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
import org.lwjgl.opengl.PixelFormat;

public class ShaderBasics {

    public static void main(String[] args) throws Exception {
        setupOpenGL(false);
        pId = loadShaders("resources/vertex.glsl", "resources/fragment.glsl");
        setupQuad();

        while (!Display.isCloseRequested()) {
            render();
            Display.sync(60);
            Display.update();
            if (Display.wasResized()) {
                glViewport(0, 0, Display.getWidth(), Display.getHeight());
            }
        }

        Display.destroy();
    }

    // OpenGL variables
    private static int vaoId = 0;
    private static int pId = 0;
    private static int vboiId = 0;
    private static int indicesCount = 0;

    static public void setupQuad() {
        final float size = 0.6f;
        final float w = 1f;
        float[] vertices = { -size, size, 0f, w, -size, -size, 0f, w, size, -size, 0f, w, size, size, 0f, w };
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = { 0, 1, 2, 2, 3, 0 };
        indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Vertices
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        // Indices
        vboiId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    static public void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(pId);

        int timeUniformLocation = glGetUniformLocation(pId, "time");
        glUniform1f(timeUniformLocation, getTime());
        int mouseUniformLocation = glGetUniformLocation(pId, "mouse");
        glUniform2f(mouseUniformLocation, Mouse.getX(), Mouse.getY());
        int resolutionUniformLocation = glGetUniformLocation(pId, "resolution");
        glUniform2f(resolutionUniformLocation, Display.getWidth(), Display.getHeight());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // DRAW!
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    static long startTime = System.nanoTime();
    static private float getTime() {
        return (System.nanoTime() - startTime) / 1e9f;
    }

    public static void setupOpenGL(boolean fullscreen) throws LWJGLException {
        ContextAttribs contextAttributes = new ContextAttribs(3, 2).withProfileCore(true);

        Display.setFullscreen(true);
        Display.setVSyncEnabled(true);
        // Display.setDisplayMode(new DisplayMode(400, 400));
        Display.setResizable(true);
        Display.create(new PixelFormat(), contextAttributes);

        glClearColor(0.4f, 0.6f, 0.9f, 0f);
        glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());

        Mouse.setGrabbed(true);
    }

    public static int loadShader(String filename, int type) throws Exception {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, slurp(filename));
        glCompileShader(shaderID);

        return shaderID;
    }

    public static int loadShaders(String vs, String fs) throws Exception {
        int vsId = loadShader("resources/vertex.glsl", GL_VERTEX_SHADER);
        int fsId = loadShader("resources/fragment.glsl", GL_FRAGMENT_SHADER);

        int pId = glCreateProgram();
        glAttachShader(pId, vsId);
        glAttachShader(pId, fsId);

        glBindAttribLocation(pId, 0, "in_Position");
        glBindAttribLocation(pId, 1, "in_Color");

        glLinkProgram(pId);
        glValidateProgram(pId);

        return pId;
    }

    private static String slurp(String filename) throws FileNotFoundException {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(new FileInputStream(new File(filename))).useDelimiter("\\A");
        String filedata = sc.next();
        sc.close();

        return filedata;
    }

}