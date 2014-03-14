import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;


public class GfxUtil {

    public static void setupOpenGL(int width, int height) throws LWJGLException {
        PixelFormat pixelFormat = new PixelFormat();
        ContextAttribs contextAtrributes = new ContextAttribs(3, 2).withProfileCore(true);

        Display.setDisplayMode(new DisplayMode(width, height));
        Display.create(pixelFormat, contextAtrributes);
        Display.setFullscreen(true);

        glClearColor(0.4f, 0.6f, 0.9f, 0f);
        glViewport(0, 0, width, height);
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

}
