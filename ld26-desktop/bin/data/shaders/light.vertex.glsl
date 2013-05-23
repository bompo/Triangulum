attribute vec3 a_position; 
attribute vec4 a_color;

uniform vec4 camPos;
uniform mat4 u_projectionViewMatrix;
uniform mat4 u_modelMatrix;

uniform float u_time;

varying vec4 v_color;

float wrapLight(vec3 nor, vec3 direction) {
	return dot(nor, direction) * 0.5 + 0.5;
}

vec4 wobble(vec4 pos) {
	pos.y = pos.y + sin(pos.y + u_time) * 0.1;
	pos.z = pos.z + cos(pos.x + u_time) * 0.1;
	pos.x = pos.x + sin(pos.x + u_time) * 0.1;
return pos;
}

void main() {
	v_color = wobble(a_color);
	vec4 worldPos = u_modelMatrix * vec4(a_position,1.0);
	gl_Position = u_projectionViewMatrix * worldPos;
}
