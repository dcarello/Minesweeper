package pkgSlUtils;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import pkgKeyListener.DCKeyListener;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.*;


public class slWindowManager {
    private static long glfw_win = 0;
    private static slWindowManager my_window = null;

    private slWindowManager(){
    }

    public int[] getCurrentWindowSize() {
        int[][] win_size = new int[2][1];
        glfwGetWindowSize(glfw_win, win_size[0], win_size[1]);
        return new int[] {win_size[0][0], win_size[1][0]};
    }

    public static slWindowManager get(){
        if (my_window == null){
            my_window = new slWindowManager();
        }
        return my_window;
    }

    public void destroyGlfwWindow(){
        glfwDestroyWindow(glfw_win);
    }

    public void swapBuffers(){
        glfwSwapBuffers(glfw_win);
    }

    public boolean isGlfwWindowClosed(){
        return glfwWindowShouldClose(glfw_win);
    }

    public void initGLFWWindow(int win_width, int win_height, String title){
        if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
        }
        if (glfw_win == 0){
            glfw_win = glfwCreateWindow(win_width, win_height, title, NULL, NULL);
        }
        if (glfw_win == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
    }

//    public int[] getWindowSize(){
//        glfwGetWindowSize();
//    }

    public void updateContextToThis(){
        glfwMakeContextCurrent(glfw_win);
    }

    private static GLFWFramebufferSizeCallback resizeWindow =
            new GLFWFramebufferSizeCallback(){
                @Override
                public void invoke(long window, int width, int height){
                    glViewport(0,0,width, height);
                }
            };

    public void enableResizeWindowCallback() {
        glfwSetFramebufferSizeCallback(glfw_win, resizeWindow);
    } // public void enableResizeWindowCallback(...)

    public void setKeyCallback(){
        glfwSetKeyCallback(glfw_win, DCKeyListener::keyCallback);
    }



}
