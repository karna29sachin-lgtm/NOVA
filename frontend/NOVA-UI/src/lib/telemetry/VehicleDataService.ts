import type { VehicleDataProvider } from './VehicleDataProvider';
import { WebSocketProvider } from './providers/WebSocketProvider';

/**
 * Service to manage which provider is used.
 * Keeps the UI unaware of specific provider implementations (e.g. Mock vs Bluetooth).
 */
export class VehicleDataService {
  private static activeProvider: VehicleDataProvider | null = null;

  static getProvider(): VehicleDataProvider {
    if (!this.activeProvider) {
      // Return WebSocketProvider to connect to backend
      this.activeProvider = new WebSocketProvider();
    }
    return this.activeProvider;
  }
}
