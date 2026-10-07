import type { VehicleDataProvider, TelemetrySubscriber } from '../VehicleDataProvider';
import type { TelemetryData, DriveMode } from '../types';

const defaultData: TelemetryData = {
  vehicleId: 'Vehicle 042',
  batteryLevel: 78,
  rangeKm: 312,
  speed: 0,
  status: 'HEALTHY',
  autonomousMode: true,
  activeAlertsCount: 0,
  openIssuesCount: 0,
  osVersion: '4.8.2',
  
  motorTemperature: 68,
  batteryTemperature: 34,
  powerOutput: 12.4,
  efficiency: 14.2,
  gpsHeading: 0,
  driveMode: 'NORMAL',
  sensorHealth: 'NOMINAL',
};

export class WebSocketProvider implements VehicleDataProvider {
  private socket: WebSocket | null = null;
  private subscribers: Set<TelemetrySubscriber> = new Set();
  private data: TelemetryData = { ...defaultData };

  private simulationInterval: ReturnType<typeof setInterval> | null = null;

  async connect(): Promise<void> {
    if (this.socket) return;
    return new Promise((resolve) => {
      this.socket = new WebSocket('ws://localhost:8080/ws/telemetry/websocket');
      
      this.socket.onopen = () => {
        console.log('Connected to Backend WebSocket');
        this.startSimulation(); // Fallback simulation
        resolve();
      };
      
      this.socket.onmessage = (event) => {
        try {
          const newData = JSON.parse(event.data);
          this.data = { ...this.data, ...newData };
          this.notifySubscribers();
        } catch(e) {
          console.error('Error parsing telemetry', e);
        }
      };
      
      this.socket.onerror = (err) => {
        console.error('WebSocket Error:', err);
        this.startSimulation(); // Start simulation even on error so UI isn't dead
        resolve();
      };
      
      this.socket.onclose = () => {
        console.log('WebSocket Closed');
        this.socket = null;
        this.stopSimulation();
      };
    });
  }

  async disconnect(): Promise<void> {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
    this.stopSimulation();
  }

  private startSimulation() {
    if (this.simulationInterval) return;
    
    this.simulationInterval = setInterval(() => {
      const isDriving = this.data.speed > 0 || this.data.autonomousMode;
      const newSpeed = this.data.autonomousMode ? 40 + (Math.random() * 4 - 2) : this.data.speed;
      
      this.data = {
        ...this.data,
        speed: isDriving ? newSpeed : this.data.speed,
        batteryLevel: Math.max(0, this.data.batteryLevel - (isDriving ? 0.001 : 0)),
        rangeKm: Math.max(0, this.data.rangeKm - (isDriving ? 0.005 : 0)),
        powerOutput: isDriving ? 12.0 + (Math.random() * 2) : 0,
        motorTemperature: isDriving ? 68 + (Math.random() * 2 - 1) : Math.max(30, this.data.motorTemperature - 0.5),
      };
      
      this.notifySubscribers();
    }, 1000);
  }

  private stopSimulation() {
    if (this.simulationInterval) {
      clearInterval(this.simulationInterval);
      this.simulationInterval = null;
    }
  }

  subscribe(callback: TelemetrySubscriber): () => void {
    this.subscribers.add(callback);
    if (this.data) callback(this.data);
    return () => this.subscribers.delete(callback);
  }

  private notifySubscribers() {
    if (!this.data) return;
    this.subscribers.forEach(cb => cb(this.data!));
  }

  async setDriveMode(mode: DriveMode): Promise<void> {
    this.socket?.send(JSON.stringify({ type: 'setDriveMode', mode }));
  }

  async setAutonomousMode(enabled: boolean): Promise<void> {
    this.socket?.send(JSON.stringify({ type: 'setAutonomousMode', enabled }));
  }

  async requestDiagnostics(): Promise<void> {
    this.socket?.send(JSON.stringify({ type: 'requestDiagnostics' }));
  }

  async acknowledgeAlert(alertId: string): Promise<void> {
    this.socket?.send(JSON.stringify({ type: 'acknowledgeAlert', alertId }));
  }

  async updateSoftware(version: string): Promise<void> {
    this.socket?.send(JSON.stringify({ type: 'updateSoftware', version }));
  }
}
