package hack;

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

public class BasicPipeline {

    public static void main(String[] args) throws Exception {
        setupOpenGL();
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

    static public void setupQuad() throws Exception {
        loadShaders();

        final float size = 0.6f;
        float[] vertices = { -size, size, 0f, 1f, -size, -size, 0f, 1f, size, -size, 0f, 1f, size, size, 0f, 1f };
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = { 0, 1, 2, 2, 3, 0 };
        Quad.indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(Quad.indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        Quad.vaoId = glGenVertexArrays();
        glBindVertexArray(Quad.vaoId);

        // Vertices
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        // Indices
        Quad.vboiId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, Quad.vboiId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    static public void render() {
        glClearColor(0.4f, 0.6f, 0.9f, 0f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(Quad.pId);

        setUniforms();

        glBindVertexArray(Quad.vaoId);
        glEnableVertexAttribArray(0);

        // DRAW!
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, Quad.vboiId);
        glDrawElements(GL_TRIANGLES, Quad.indicesCount, GL_UNSIGNED_BYTE, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    private static void setUniforms() {
        glUniform1f(glGetUniformLocation(Quad.pId, "time"), getElapsedTime());
        glUniform2f(glGetUniformLocation(Quad.pId, "mouse"), Mouse.getX(), Mouse.getY());
        glUniform2f(glGetUniformLocation(Quad.pId, "resolution"), Display.getWidth(), Display.getHeight());
    }

    static long startTime = System.nanoTime();
    static private float getElapsedTime() {
        return (System.nanoTime() - startTime) / 1e9f;
    }

    public static void setupOpenGL() throws LWJGLException {
        ContextAttribs contextAttributes = new ContextAttribs(3, 2).withProfileCore(true);

        Display.setVSyncEnabled(true);
        Display.setResizable(true);
        Display.create(new PixelFormat(), contextAttributes);
        glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
    }

    public static void loadShaders() throws Exception {
        int vsId = loadShader("resources/hack/vertex.glsl", GL_VERTEX_SHADER);
        int fsId = loadShader("resources/hack/fragment.glsl", GL_FRAGMENT_SHADER);

        Quad.pId = glCreateProgram();
        glAttachShader(Quad.pId, vsId);
        glAttachShader(Quad.pId, fsId);

        glBindAttribLocation(Quad.pId, 0, "in_Position");

        glLinkProgram(Quad.pId);
        glValidateProgram(Quad.pId);
    }

    public static int loadShader(String filename, int type) throws Exception {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, slurp(filename));
        glCompileShader(shaderID);

        return shaderID;
    }

    private static String slurp(String filename) throws FileNotFoundException {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(new FileInputStream(new File(filename))).useDelimiter("\\A");
        String filedata = sc.next();
        sc.close();

        return filedata;
    }

    static class Quad {
        static int vaoId = 0;
        static int pId = 0;
        static int vboiId = 0;
        static int indicesCount = 0;
    }

}
