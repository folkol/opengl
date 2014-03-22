import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public class GlUtil {

    public static void setupOpenGL(boolean fullscreen) throws LWJGLException {
        PixelFormat pixelFormat = new PixelFormat();
        ContextAttribs contextAttributes = new ContextAttribs(3, 2).withProfileCore(true);

        Display.setFullscreen(fullscreen);
        Display.setVSyncEnabled(true);
        Display.setDisplayMode(new DisplayMode(400, 400));
        Display.setResizable(true);
        Display.create(pixelFormat, contextAttributes);

        glClearColor(0.4f, 0.6f, 0.9f, 0f);
        glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());

        // Mouse.setGrabbed(true);
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
