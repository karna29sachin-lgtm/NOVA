import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import fs from 'fs';

// Mock browser globals for Three.js GLTFLoader in Node
global.window = {};
global.document = {
  createElementNS: () => ({
    style: {}
  })
};

const toArrayBuffer = (buffer) => {
  const ab = new ArrayBuffer(buffer.length);
  const view = new Uint8Array(ab);
  for (let i = 0; i < buffer.length; ++i) {
    view[i] = buffer[i];
  }
  return ab;
};

const loader = new GLTFLoader();
const data = fs.readFileSync('v:/Control-system/frontend/NOVA-UI/public/models/Koenigsegg.glb');
const arrayBuffer = toArrayBuffer(data);

loader.parse(arrayBuffer, '', (gltf) => {
  let meshCount = 0;
  gltf.scene.traverse((child) => {
    if (child.isMesh) meshCount++;
  });
  console.log('Meshes:', meshCount);
  const box = new THREE.Box3().setFromObject(gltf.scene);
  console.log('Size:', box.getSize(new THREE.Vector3()));
}, (error) => {
  console.error('Error:', error);
});
