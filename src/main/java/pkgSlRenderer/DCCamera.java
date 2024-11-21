package pkgSlRenderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static pkgDriver.slSpot.*;

public class DCCamera {

    Vector3f lookFrom, lookAt, upVector;

    public DCCamera(){
    }

    public Matrix4f getViewMatrix(){
        lookFrom = new Vector3f(0f, 0f, 100f);
        lookAt = new Vector3f(0f, 0f, -1.0f);
        upVector = new Vector3f(0f, 1.0f, 0f);

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.lookAt(lookFrom, lookAt.add(lookFrom), upVector);
        return viewMatrix;

    }

    public Matrix4f getProjectionMatrix(){
        Matrix4f projMatrix = new Matrix4f();
        projMatrix.identity();
        projMatrix.ortho(FRUSTUM_LEFT, FRUSTUM_RIGHT, FRUSTUM_BOTTOM, FRUSTUM_TOP, Z_NEAR, Z_FAR);

        return projMatrix;
    }

}
