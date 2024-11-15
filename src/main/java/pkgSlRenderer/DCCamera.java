package pkgSlRenderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DCCamera {

    Vector3f lookFrom, lookAt, upVector;


    public DCCamera(){

        lookFrom = new Vector3f(0f, 0f, 100f);
        lookAt = new Vector3f(0f, 0f, -1.0f);
        upVector = new Vector3f(0f, 1.0f, 0f);

    }

    public Matrix4f getViewMatrix(){

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.lookAt(lookFrom, lookAt.add(lookFrom), upVector);
        return viewMatrix;

    }

    public Matrix4f getProjectionMatrix(){
        float zNear = 0f, zFar = 100f, screen_left = -1.0f, screen_right = 1.0f, screen_bottom = -1.0f, screen_top = 1.0f;

        Matrix4f projMatrix = new Matrix4f();
        projMatrix.identity();
        projMatrix.ortho(screen_left, screen_right, screen_bottom, screen_top, zNear, zFar);

        return projMatrix;
    }

}
