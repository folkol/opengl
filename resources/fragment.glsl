#version 150 core

in vec4 pass_Color;
out vec4 out_Color;

uniform float time;
uniform vec2 mouse;

#define SPEED 10.0
#define WAVELENGTH 75.0 // Higher number = shorter wavelength :P
#define AMPLITUDE 0.2
#define NUMPAIRS 1

void main( void ) {
  vec2 position = gl_FragCoord.xy;
  float color = 0.0;
  for(int c = 1; c <= NUMPAIRS; c++) {
    vec2 endpoint = mouse - vec2(0.5, 0.5);
    vec2 wave_one_pos = vec2(0.5, 0.5) + (endpoint/float(NUMPAIRS))*float(c);
    vec2 wave_two_pos = vec2(1.0, 1.0) - wave_one_pos;
    float wave_one_color = 0.5 + AMPLITUDE * sin(-SPEED * time + WAVELENGTH * distance(position, wave_one_pos));
    float wave_two_color = 0.5 + AMPLITUDE * sin(-SPEED * time + WAVELENGTH * distance(position, wave_two_pos));
    color += wave_one_color + wave_two_color;
  }
  color /= float(NUMPAIRS * 2);

  out_Color = vec4( color, color, color, 1 );
}
