package pkgKeyListener;


import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class DCKeyListener {
    private static DCKeyListener my_listener = null;

    private final boolean[] keyPressed = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private DCKeyListener(){
    }

    public static DCKeyListener get(){
        if (my_listener == null){
            my_listener = new DCKeyListener();

        }
        return my_listener;
    }

    private void keyPressedReset(){
        Arrays.fill(keyPressed, false);
    }

    public static boolean isKeyPressed(int keyCode){
        if (keyCode < get().keyPressed.length) {
            return get().keyPressed[keyCode];
        } else {
            return false;
        }
    }

    // Call this function to receive one event for repeated presses:
    public static void resetKeypressEvent(int keyCode) {
        if (my_listener != null && keyCode < get().keyPressed.length) {
            my_listener.keyPressed[keyCode] = false;
        }
    }

    public static void keyCallback(long my_window, int key, int scancode, int action, int modifier_key) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
//            System.out.println("Key Pressed [" + key + "]");
        }
        else if (action == GLFW_RELEASE){
            get().keyPressed[key] = false;
//            System.out.println("Key Released");
        }
    }
}
