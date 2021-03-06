#version 150 core

in vec4 in_Position;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main(void) {
    float Angle = 0.1 * sin(time);
    mat4 rotz = mat4(cos( Angle ), -sin( Angle ), 0.0, 0.0,
                     sin( Angle ),  cos( Angle ),  0.0, 0.0,
                     0.0,           0.0,           1.0, 0.0,
                     0.0,           0.0,           0.0, 1.0);

    mat4 roty = mat4(cos( Angle ),  0.0, sin( Angle ), 0.0,
                     0.0,           1.0, 0.0,          0.0,
                     -sin( Angle ), 0.0, cos( Angle ), 0.0,
                     0.0,           0.0, 0.0,          1.0);

    vec4 pos = rotz * in_Position;

    float perspective_factor = pos.z * 0.5 + 1.0;
    gl_Position = vec4(pos.xyz/perspective_factor, 1.0);
}
