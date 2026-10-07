import type { GPSLocation, GPSState, RouteOption } from '../types/navigation';

let watchId: number | null = null;
let demoInterval: ReturnType<typeof setInterval> | null = null;

export const startGPS = (
  isDemo: boolean,
  onLocationUpdate: (loc: GPSLocation) => void,
  onStateChange: (state: GPSState) => void,
  getDemoActiveRoute?: () => RouteOption | null
) => {
  stopGPS();

  if (isDemo) {
    onStateChange('SEARCHING');
    
    let lat = 28.6139;
    let lon = 77.2090;
    let heading = 0;
    let demoRouteProgressIndex = 0;
    let demoRouteId: string | null = null;
    let currentDistanceAlongSegment = 0;
    const DEMO_SPEED_KMH = 60; // 60 km/h
    const DEMO_SPEED_MS = DEMO_SPEED_KMH / 3.6; // ~16.6 m/s
    const INTERVAL_MS = 100; // Update 10 times per second for smooth movement
    const DISTANCE_PER_INTERVAL = DEMO_SPEED_MS * (INTERVAL_MS / 1000);
    
    // Simple distance helper (Haversine)
    const getDist = (lat1: number, lon1: number, lat2: number, lon2: number) => {
      const R = 6371e3;
      const p1 = lat1 * Math.PI/180;
      const p2 = lat2 * Math.PI/180;
      const dp = (lat2-lat1) * Math.PI/180;
      const dl = (lon2-lon1) * Math.PI/180;
      const a = Math.sin(dp/2) * Math.sin(dp/2) + Math.cos(p1) * Math.cos(p2) * Math.sin(dl/2) * Math.sin(dl/2);
      return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    };

    const startDemoLoop = () => {
      onStateChange('CONNECTED');
      demoInterval = setInterval(() => {
        const activeRoute = getDemoActiveRoute ? getDemoActiveRoute() : null;
        
        if (activeRoute && activeRoute.geometry.coordinates.length > 1) {
          if (demoRouteId !== activeRoute.id) {
              demoRouteId = activeRoute.id;
              demoRouteProgressIndex = 0;
              currentDistanceAlongSegment = 0;
          }
          
          const coords = activeRoute.geometry.coordinates;
          if (demoRouteProgressIndex < coords.length - 1) {
              const p1 = coords[demoRouteProgressIndex];
              const p2 = coords[demoRouteProgressIndex + 1];
              
              const segmentLength = getDist(p1[1], p1[0], p2[1], p2[0]);
              
              if (currentDistanceAlongSegment + DISTANCE_PER_INTERVAL >= segmentLength) {
                  // Move to next segment
                  currentDistanceAlongSegment = (currentDistanceAlongSegment + DISTANCE_PER_INTERVAL) - segmentLength;
                  demoRouteProgressIndex++;
                  
                  // If we reached the end
                  if (demoRouteProgressIndex >= coords.length - 1) {
                      lat = coords[coords.length - 1][1];
                      lon = coords[coords.length - 1][0];
                  } else {
                      // We will interpolate on the next tick, for now just snap to p2 to avoid overshoot
                      lat = p2[1];
                      lon = p2[0];
                  }
              } else {
                  currentDistanceAlongSegment += DISTANCE_PER_INTERVAL;
                  const ratio = segmentLength === 0 ? 0 : currentDistanceAlongSegment / segmentLength;
                  
                  // Interpolate
                  lon = p1[0] + (p2[0] - p1[0]) * ratio;
                  lat = p1[1] + (p2[1] - p1[1]) * ratio;
                  
                  // Calculate bearing
                  const lat1Rad = p1[1] * Math.PI / 180;
                  const lat2Rad = p2[1] * Math.PI / 180;
                  const dLonRad = (p2[0] - p1[0]) * Math.PI / 180;
                  const y = Math.sin(dLonRad) * Math.cos(lat2Rad);
                  const x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLonRad);
                  heading = (Math.atan2(y, x) * 180 / Math.PI + 360) % 360;
              }
          }
        }
        
        onLocationUpdate({
          latitude: lat,
          longitude: lon,
          accuracy: 2.5,
          heading: heading,
          speed: DEMO_SPEED_KMH,
          timestamp: Date.now()
        });
      }, INTERVAL_MS);
    };

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          lat = pos.coords.latitude;
          lon = pos.coords.longitude;
          startDemoLoop();
        },
        () => startDemoLoop(),
        { timeout: 5000, maximumAge: 0 }
      );
    } else {
      startDemoLoop();
    }
    return;
  }

  if (!navigator.geolocation) {
    onStateChange('ERROR');
    return;
  }

  onStateChange('SEARCHING');

  watchId = navigator.geolocation.watchPosition(
    (position) => {
      onStateChange('CONNECTED');
      onLocationUpdate({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        accuracy: position.coords.accuracy,
        heading: position.coords.heading,
        speed: position.coords.speed !== null ? position.coords.speed * 3.6 : 0, // m/s to km/h
        timestamp: position.timestamp
      });
    },
    (error) => {
      if (error.code === error.PERMISSION_DENIED) {
        onStateChange('DENIED');
      } else {
        onStateChange('ERROR');
      }
    },
    {
      enableHighAccuracy: true,
      maximumAge: 0,
      timeout: 10000
    }
  );
};

export const stopGPS = () => {
  if (watchId !== null) {
    navigator.geolocation.clearWatch(watchId);
    watchId = null;
  }
  if (demoInterval !== null) {
    clearInterval(demoInterval);
    demoInterval = null;
  }
};
