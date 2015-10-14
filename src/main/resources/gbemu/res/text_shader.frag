#version 330 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D image;
uniform vec3 textureColor;
uniform mat3 texCoordTransform;

void main()
{
	vec3 realTexCoord = texCoordTransform * vec3(TexCoords, 1.0f);
    color = vec4(textureColor, texture(image, realTexCoord.xy).r);
}