#define normalsFlag
//#define specularColorFlag
//#define emissiveColorFlag
//#define diffuseColorFlag
//#define translucentFlag
//#define fogColorFlag
//#define LIGHTS_NUM 4
attribute vec3 a_position; 
attribute vec2 a_texCoord0;

#ifdef normalsFlag
attribute vec3 a_normal;
uniform mat3 u_normalMatrix;
#endif

#ifdef diffuseColorFlag
uniform vec4 diffuseColor;
#endif

#ifdef emissiveColorFlag
uniform vec4 emissiveColor;
#endif

#ifdef rimColorFlag
uniform vec4 rimColor;
#endif

#ifdef envmapFlag
varying vec3 v_reflect;
#endif

//#ifdef specularColor
//uniform vec4 specularCol;
//#endif

#if LIGHTS_NUM > 0
uniform vec3  lightsPos[LIGHTS_NUM];
uniform vec3  lightsCol[LIGHTS_NUM];
uniform float lightsInt[LIGHTS_NUM];
#endif

uniform vec4 camPos;
uniform vec3 camDir;	
uniform vec3 dirLightDir;
uniform vec3 dirLightCol;
uniform mat4 u_projectionViewMatrix;
uniform mat4 u_modelMatrix;
uniform vec3 ambient;

#ifdef fogColorFlag
varying float v_fog;
#endif

#ifdef waterFlag
uniform float time;
#endif

varying vec2 v_texCoords;
varying vec4 v_diffuse;

//wrap light. this is fastest light model
float wrapLight(vec3 nor, vec3 direction){
	return dot(nor, direction) * 0.5 + 0.5;
}

#ifdef waterFlag
vec4 wobble(vec4 pos){
	pos.y = 0.01 * pos.y + sin(pos.y+time);
	pos.z = 0.1 * pos.z + cos(pos.x+time);
	pos.x = 0.1 * pos.x + sin(pos.x+time);
return pos;
}
#endif

void main()
{	
	v_texCoords = a_texCoord0;
	vec4 worldPos = u_modelMatrix * vec4(a_position,1.0);
	
    #ifdef waterFlag
        worldPos = wobble(worldPos);
    #endif
	
	gl_Position = u_projectionViewMatrix * worldPos; 
	vec3 pos  = worldPos.xyz;
	
	#ifdef envmapFlag
        vec3 nWorld = mat3( u_modelMatrix[0].xyz, u_modelMatrix[1].xyz, u_modelMatrix[2].xyz ) * a_normal;
        v_reflect = reflect( normalize( camPos.xyz - pos.xyz ), normalize( nWorld.xyz ) );
    #endif
		
	vec3  aggCol = dirLightCol;
	
	#ifdef normalsFlag
	    vec3 normal = u_normalMatrix * normalize(a_normal);	
	    aggCol *= wrapLight(normal, -dirLightDir);
	#endif

#if LIGHTS_NUM > 0		
	for ( int i = 0; i < LIGHTS_NUM; i++ ){	
		vec3 dif  = lightsPos[i] - pos;
		//fastest way to calculate inverse of length				
		float invLen = inversesqrt(dot(dif, dif));
		float weight = invLen * lightsInt[i];
				
		#ifdef normalsFlag
		    vec3 L = invLen * dif;// normalize
		    float lambert = wrapLight(normal, L);
		    weight *= lambert;		
		#endif
		aggCol += lightsCol[i] * weight;
		
	}
#endif
#ifdef diffuseColorFlag
	aggCol *= diffuseColor.rgb;
	#ifdef translucentFlag
		v_diffuse.a = diffuseColor.a;	
	#endif
#endif

#ifdef emissiveColorFlag
	aggCol += emissiveColor.rgb;
#endif
	
	aggCol += ambient.rgb;


#ifdef normalsFlag
	#ifdef rimColorFlag
	aggCol.rgb +=  pow( 1.0 - dot( normal, -camDir ), 2.0 ) * rimColor.rgb;
	#endif
#endif



#ifdef fogColorFlag
	float fog  =  (distance(pos, camPos.xyz) * camPos.w);
	fog *=fog;	
	v_fog = min(fog, 1.0);	
#endif

	v_diffuse.rgb = aggCol;
	
}
