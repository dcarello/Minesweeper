package pkgSlRenderer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;

public class DCShaderObject {

    private static String vs_shader_file, fs_shader_file;
    private static int shader_program;
    private static int vs;
    private static int fs;
    public static final int OGL_VEC4_SIZE = 4;

    public DCShaderObject(String vs_shader_file, String fs_shader_file){
        this.vs_shader_file = vs_shader_file;
        this.fs_shader_file = fs_shader_file;

        shader_program = glCreateProgram();

    }

    public int getShader_program(){
        return shader_program;
    }

    public void compileShader(){
        // Vertex Shader Compile
        vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, readFile(vs_shader_file));
        glCompileShader(vs);
//        checkCompileErrors(vs, "VERTEX");
        glAttachShader(shader_program, vs);

        // Fragment Shader Compile
        fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, readFile(fs_shader_file));
        glCompileShader(fs);
//        checkCompileErrors(fs, "FRAGMENT");
        glAttachShader(shader_program, fs);
    }

    public void setShaderProgram(){
        glLinkProgram(shader_program);
//        checkLinkErrors(shader_program);
        glDeleteShader(vs);
        glDeleteShader(fs);

    }

    private void checkCompileErrors(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String errorMessage = glGetShaderInfoLog(shader);
            System.out.println("Shader compilation error (" + type + "): " + errorMessage);
            throw new RuntimeException("Shader compilation error (" + type + ")");
        } else {
            System.out.println(type + " shader compiled successfully");
        }
    }

    private void checkLinkErrors(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            String errorMessage = glGetProgramInfoLog(program);
            System.out.println("Program linking error: " + errorMessage);
            throw new RuntimeException("Program linking error");
        } else {
            System.out.println("Program linked successfully");
        }
    }

    private String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
        return content.toString();
    }

    public void loadMatrix4f(String strMatrixName, Matrix4f my_mat4) {
        glUseProgram(shader_program);
        int var_location = glGetUniformLocation(shader_program, strMatrixName);
        if (var_location == -1) {
            throw new RuntimeException("Could not find uniform location for: " + strMatrixName);
        }
        final int OGL_MATRIX_SIZE = 16;
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
        my_mat4.get(matrixBuffer);
        glUniformMatrix4fv(var_location, false, matrixBuffer);

    } // public void loadMatrix4f(...)

    public void loadVector4f(String strVec4Name, Vector4f my_vec4) {
        int var_location = glGetUniformLocation(shader_program, strVec4Name);
        if (var_location == -1) {
            throw new RuntimeException("Could not find uniform location for: " + strVec4Name);
        }

        FloatBuffer vec4Buffer = BufferUtils.createFloatBuffer(OGL_VEC4_SIZE);
        my_vec4.get(vec4Buffer);
        glUniform4fv(var_location, vec4Buffer);

    } // public void loadVec4f(...)

}
