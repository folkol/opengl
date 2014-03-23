#version 150 core

out vec4 out_Color;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

#define SPEED 10.0
#define WAVELENGTH 0.1 // Higher number = shorter wavelength :P
#define AMPLITUDE 0.2

float wave(vec2 origin)
{
  float r = distance(gl_FragCoord.xy, origin);
  return 0.5 + AMPLITUDE * sin(-SPEED * time + WAVELENGTH * r);
}

void main( void ) {
  float color = 0.5 * (wave(mouse) + wave(resolution - mouse));
  out_Color = vec4( vec3(color), 1);
}
