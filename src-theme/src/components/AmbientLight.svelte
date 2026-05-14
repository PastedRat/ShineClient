<script lang="ts">
  import { onMount, onDestroy } from 'svelte';

  export let color: string = '#ffffff';
  export let intensity: number = 1;
  export let backgroundColor: string = '#000000';
  export let fogColor: string = '#000000';
  export let fogNear: number = 1;
  export let fogFar: number = 100;
  export let enableFog: boolean = true;

  let scene: any = null;
  let camera: any = null;
  let ambientLight: any = null;

  // Function to get Three.js scene from the game context
  function getScene() {
    // This will be called by the game integration to inject the scene
    return scene;
  }

  export function init(threeScene: any, threeCamera: any) {
    scene = threeScene;
    camera = threeCamera;
    
    // Create ambient light
    if (scene && !ambientLight) {
      const THREE = (window as any).THREE;
      if (THREE) {
        ambientLight = new THREE.AmbientLight(color, intensity);
        scene.add(ambientLight);
        
        // Set background color
        if (backgroundColor) {
          scene.background = new THREE.Color(backgroundColor);
        }
        
        // Setup fog
        if (enableFog && fogColor) {
          scene.fog = new THREE.Fog(fogColor, fogNear, fogFar);
        }
      }
    }
  }

  export function updateColors(newColor: string, newIntensity: number, newBgColor: string, newFogColor: string) {
    const THREE = (window as any).THREE;
    if (!THREE || !ambientLight || !scene) return;
    
    // Update ambient light
    ambientLight.color.set(newColor);
    ambientLight.intensity = newIntensity;
    
    // Update background
    if (newBgColor) {
      scene.background = new THREE.Color(newBgColor);
    }
    
    // Update fog
    if (enableFog && newFogColor && scene.fog) {
      scene.fog.color.set(newFogColor);
    } else if (enableFog && newFogColor && !scene.fog) {
      scene.fog = new THREE.Fog(newFogColor, fogNear, fogFar);
    }
  }

  onDestroy(() => {
    if (scene && ambientLight) {
      scene.remove(ambientLight);
      ambientLight = null;
    }
  });

  // Expose for external use
  $: if (scene && ambientLight) {
    ambientLight.color.set(color);
    ambientLight.intensity = intensity;
  }
</script>

<!-- This component is controlled programmatically via the init function -->
<div style="display: none;">Ambient Light Controller</div>
