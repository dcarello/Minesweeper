

#version 430 core

layout (location=0) in vec2 aPos;
//layout (location=1) in vec4 aColor;

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

//out vec4 fColor;

void main()
{
    //fColor = aColor;
    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0, 1.0);
    //gl_Position = uProjMatrix * uViewMatrix * gl_Vertex;

}







