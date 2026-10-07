import React, { useEffect, useRef } from 'react';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { RoomEnvironment } from 'three/examples/jsm/environments/RoomEnvironment.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { Battery, Car, Gauge, Thermometer, Zap } from 'lucide-react';
import { cn } from '../../lib/utils';
import { useTelemetry } from '../../context/TelemetryContext';
import { Badge } from '../../components/ui/Badge';

export const VehiclePage = () => {
  const mountRef = useRef<HTMLDivElement>(null);
  const { data } = useTelemetry();

  useEffect(() => {
    // 3D Visualizer removed as requested
  }, []);

  const Card = ({ title, children, className }: { title: string, children: React.ReactNode, className?: string }) => (
    <div className={cn("glass-panel-elevated p-6 rounded-2xl border border-border backdrop-blur-md flex flex-col", className)}>
      <h3 className="text-xs font-bold tracking-widest text-primary-muted uppercase mb-6 flex items-center gap-2">
        <div className="w-1.5 h-1.5 rounded-full bg-accent"></div>
        {title}
      </h3>
      <div className="flex-1">
        {children}
      </div>
    </div>
  );

  const StatusRow = ({ label, value, status }: { label: string, value?: string, status?: string }) => (
    <div className="flex justify-between items-center py-2.5 border-b border-border/50 last:border-0">
      <span className="text-sm font-medium text-primary-muted">{label}</span>
      <div className="flex items-center gap-3">
        {value && <span className="font-semibold text-primary">{value}</span>}
        {status && (
          <Badge variant={status === 'nominal' ? 'NOMINAL' : status === 'active' ? 'ACTIVE' : status.toUpperCase()}>
            {status}
          </Badge>
        )}
      </div>
    </div>
  );

  const SensorRow = ({ label, ratio, status }: { label: string, ratio: string, status: string }) => (
    <div className="flex justify-between items-center py-2.5 border-b border-border/50 last:border-0">
      <span className="text-sm font-medium text-primary-muted">{label}</span>
      <div className="flex items-center gap-4">
        <span className="text-sm font-mono text-primary/80">{ratio}</span>
        <Badge variant={status === 'ONLINE' || status === 'CONNECTED' ? 'ACTIVE' : 'CRITICAL'}>
          {status}
        </Badge>
      </div>
    </div>
  );

  return (
    <div className="w-full h-full bg-[#080A0D] p-6 lg:p-8 text-primary overflow-y-auto hide-scrollbar">
      <div className="max-w-7xl mx-auto flex flex-col gap-6">
        
        {/* Header */}
        <div className="flex items-center justify-between mb-2">
          <div>
            <h1 className="text-3xl font-light tracking-widest text-white uppercase flex items-center gap-3">
              <Car className="w-8 h-8 text-accent" />
              Vehicle Command Center
            </h1>
            <p className="text-sm text-primary-muted mt-2 tracking-wide">NOVA Autonomous Fleet · Unit 042</p>
          </div>
          <div className="text-right">
            <div className="text-2xl font-light tracking-tight text-accent">KOENIGSEGG</div>
            <div className="text-[10px] font-bold tracking-widest text-primary-muted mt-1 uppercase">Jesko Absolut - Custom</div>
          </div>
        </div>

        {/* Main Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 h-[calc(100vh-140px)] min-h-[700px]">
          
          {/* Right Column: Status Panels (Now Full Width) */}
          <div className="lg:col-span-12 grid grid-cols-1 md:grid-cols-2 gap-6 overflow-y-auto hide-scrollbar">
            
            {/* VEHICLE STATUS */}
            <Card title="Vehicle Status">
              <StatusRow label="Drive System" status={data.status === 'HEALTHY' ? 'nominal' : 'degraded'} />
              <StatusRow label="Autonomous System" status={data.autonomousMode ? 'active' : 'OFFLINE'} />
              <StatusRow label="Battery" value={`${data.batteryLevel.toFixed(0)}%`} />
              <StatusRow label="Range" value={`${data.rangeKm.toFixed(0)} km`} />
              <StatusRow label="Speed" value={`${data.speed.toFixed(0)} km/h`} />
            </Card>

            {/* SENSOR NETWORK */}
            <Card title="Sensor Network">
              <SensorRow label="LiDAR Array" ratio="4 / 4" status="ONLINE" />
              <SensorRow label="Vision Cameras" ratio="8 / 8" status="ONLINE" />
              <SensorRow label="Radar System" ratio="5 / 5" status="ONLINE" />
              <SensorRow label="GPS Receiver" ratio="1 / 1" status="CONNECTED" />
              <SensorRow label="IMU & Odometry" ratio="2 / 2" status="ONLINE" />
            </Card>

            {/* POWERTRAIN */}
            <Card title="Powertrain">
              <div className="grid grid-cols-2 gap-4 mb-4">
                <div className="bg-black/20 p-4 rounded-xl border border-border/30">
                  <div className="flex items-center gap-2 text-primary-muted mb-2">
                    <Thermometer className="w-4 h-4" />
                    <span className="text-[10px] uppercase font-bold tracking-wider">Motor Temp</span>
                  </div>
                  <div className="text-xl font-semibold">68°C</div>
                </div>
                <div className="bg-black/20 p-4 rounded-xl border border-border/30">
                  <div className="flex items-center gap-2 text-primary-muted mb-2">
                    <Battery className="w-4 h-4" />
                    <span className="text-[10px] uppercase font-bold tracking-wider">Bat Temp</span>
                  </div>
                  <div className="text-xl font-semibold">34°C</div>
                </div>
                <div className="bg-black/20 p-4 rounded-xl border border-border/30">
                  <div className="flex items-center gap-2 text-primary-muted mb-2">
                    <Zap className="w-4 h-4" />
                    <span className="text-[10px] uppercase font-bold tracking-wider">Output</span>
                  </div>
                  <div className="text-xl font-semibold">12.4 <span className="text-xs text-primary-muted font-medium">kW</span></div>
                </div>
                <div className="bg-black/20 p-4 rounded-xl border border-border/30">
                  <div className="flex items-center gap-2 text-primary-muted mb-2">
                    <Gauge className="w-4 h-4" />
                    <span className="text-[10px] uppercase font-bold tracking-wider">Efficiency</span>
                  </div>
                  <div className="text-xl font-semibold">14.2 <span className="text-xs text-primary-muted font-medium">kWh/100km</span></div>
                </div>
              </div>
            </Card>

            {/* SAFETY */}
            <Card title="Safety Systems" className="mb-6">
              <StatusRow label="Brake System" status="nominal" />
              <StatusRow label="Steering Actuators" status="nominal" />
              <StatusRow label="Airbags & Restraints" status="nominal" />
              <StatusRow label="Emergency Cutoff" status="nominal" />
            </Card>

          </div>
        </div>
      </div>
    </div>
  );
};
