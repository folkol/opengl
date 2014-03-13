import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public abstract class OpenGL {

    public static void setup() throws Exception {
        createGLContext();
        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
    }

    private static void createGLContext() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(800, 600));
        Display.create(new PixelFormat(),
                       new ContextAttribs(3, 2).withProfileCore(true));
    }

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void main(String[] args) throws Exception {
        setup();

        while (!Display.isCloseRequested()) {
            render();
            Display.update();
        }
        Display.destroy();
    }

}