import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Window {

    public static void main(String[] argv) throws Exception {
        Display.setDisplayMode(new DisplayMode(800, 600));
        Display.create();
        setup();
        while (!Display.isCloseRequested()) {
            render();
            Display.update();
        }
        Display.destroy();
    }

    protected static void setup() {
    }

    protected static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
    }
}