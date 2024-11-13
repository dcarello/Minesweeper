package pkgSlRenderer;

import static org.lwjgl.opengl.GL30.*;

public class DCShaderObject {

    public DCShaderObject(String vs_shader_program_string, String fs_shader_program_string){
        int shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, vs_shader_program_string);
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, fs_shader_program_string);
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);

        glUseProgram(shader_program);
    }

}
