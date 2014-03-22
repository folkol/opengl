#version 150 core

in vec4 pass_Color;
out vec4 out_Color;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

#define SPEED 10.0
#define WAVELENGTH .05 // Higher number = shorter wavelength :P
#define AMPLITUDE 0.5

float wave(vec2 origin)
{
  return 0.5 + AMPLITUDE * sin(-SPEED * time + WAVELENGTH * distance(gl_FragCoord.xy, origin));
}

void main( void ) {
  float color = wave(mouse) + wave(resolution - mouse);
  out_Color = vec4( color, 0., 0., 1);
}
