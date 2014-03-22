#version 150 core

in vec4 in_Position;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main(void) {
    float Angle = 2. * mouse.x/resolution.x;
    mat4 rotz = mat4(cos( Angle ), -sin( Angle ), 0.0, 0.0,
                    sin( Angle ),  cos( Angle ), 0.0, 0.0,
                    0.0,           0.0,          1.0, 0.0,
                    0.0,           0.0,          0.0, 1.0 );

    gl_Position = rotz * in_Position;
}
