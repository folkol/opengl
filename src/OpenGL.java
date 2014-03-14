import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class OpenGL {

    private static final int _WIDTH = 800;
    private static final int _HEIGHT = 600;

    public static void main(String[] args) throws Exception {
        new OpenGL().run();
    }

    public void run() throws Exception {
        GfxUtil.setupOpenGL(_WIDTH, _HEIGHT);
        pId = GfxUtil.loadShaders("resources/vertex.glsl", "resources/fragment.glsl");
        setupQuad();

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

    public void setupQuad() {
        // Vertices, the order is not important. XYZW instead of XYZ
        float[] vertices = { -1f, 1f, 0f, 1f, -1f, -1f, 0f, 1f, 1f, -1f, 0f, 1f, 1f, 1f, 0f, 1f };
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

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(pId);

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        int timeUniformLocation = glGetUniformLocation(pId, "time");
        glUniform1f(timeUniformLocation, getTime());
        int mouseUniformLocation = glGetUniformLocation(pId, "mouse");
        glUniform2f(mouseUniformLocation, getMouseX(), getMouseY());

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // DRAW!
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    long startTime = System.nanoTime();
    private float getTime() {
        return (System.nanoTime() - startTime) / 1e9f;
    }

    private float getMouseY() {
        return Mouse.getY()/(float)_HEIGHT;
    }

    private float getMouseX() {
        return Mouse.getX()/(float)_WIDTH;
    }
}