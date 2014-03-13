import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class OpenGL {

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void setup() {
    }

    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        setup();
        while (!Display.isCloseRequested()) {
            render();
            Display.update();
        }
        Display.destroy();
    }

}