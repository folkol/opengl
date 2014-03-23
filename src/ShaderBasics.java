import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class ShaderBasics {

    public static void main(String[] args) throws Exception {
        GlUtil.setupOpenGL(false);
        pId = GlUtil.loadShaders("resources/vertex.glsl", "resources/fragment.glsl");
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
        final float size = 0.2f;
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

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        int timeUniformLocation = glGetUniformLocation(pId, "time");
        glUniform1f(timeUniformLocation, getTime());
        int mouseUniformLocation = glGetUniformLocation(pId, "mouse");
        glUniform2f(mouseUniformLocation, Mouse.getX(), Mouse.getY());
        int resolutionUniformLocation = glGetUniformLocation(pId, "resolution");
        glUniform2f(resolutionUniformLocation, Display.getWidth(), Display.getHeight());

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

}