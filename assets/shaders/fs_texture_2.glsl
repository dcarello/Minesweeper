#version 430 core

in vec2 fTexCoords;      // Interpolated texture coordinates from the vertex shader
uniform sampler2D uTexture; // Texture sampler
uniform vec4 COLOR_FACTOR;  // Optional color multiplier

out vec4 color;

void main()
{
    vec4 textureColor = texture(uTexture, fTexCoords); // Fetch the texture color
    color = textureColor * COLOR_FACTOR;             // Combine with COLOR_FACTOR
    // color = COLOR_FACTOR;
}
