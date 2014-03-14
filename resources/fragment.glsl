#version 150 core

in vec4 pass_Color;
out vec4 out_Color;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

#define SPEED 30.0
#define WAVELENGTH 0.1 // Higher number = shorter wavelength :P
#define AMPLITUDE .2

float wave(vec2 origin)
{
  return 0.5 + AMPLITUDE * sin(-SPEED * time + WAVELENGTH * distance(gl_FragCoord.xy, origin));
}

void main( void ) {
  float color = wave(mouse) + wave(resolution - mouse);
  out_Color = vec4( color, color, color, 1 );
}
