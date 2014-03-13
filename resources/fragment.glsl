#version 150 core

in vec4 pass_Color;
out vec4 out_Color;

uniform float time;
uniform vec2 mouse;

void main(void) {
     out_Color = pass_Color + sin(time);
}