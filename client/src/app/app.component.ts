import { Component } from '@angular/core';
import { Message } from '../data/Message';


const WEBSOCKET_URL = (() => {
  const host = window.location.hostname || 'localhost';
  const port = '8185';
  const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
  return `${protocol}://${host}:${port}/websocket`;
})();

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: false
})
export class AppComponent {

  websocket = new WebSocket(WEBSOCKET_URL);
  board: string[][] = [];
  id: string = '';
  symbol: string = '';

  constructor() {
    // register ToastService on window so the component can delegate to it without import cycles
    try {
      (window as any).__toastService = (window as any).__toastService || null;
    } catch (e) {
      // noop
    }

    this.initializeBoard();
    this.startListening();
  }

  initializeBoard() {
    this.board = [];
    for (let i = 0; i < 3; i++) {
      this.board[i] = [];
      for (let j = 0; j < 3; j++) {
        this.board[i][j] = "-";
      }
    }
  }

  move(i: number, j: number) {
    console.log(i + " " + j);
    const message: Message = {
      eventType: 'MOVE_ATTEMPT',
      data: { x: i, y: j }
    };
    this.websocket.send(JSON.stringify(message));
  }

  startListening() {
    this.websocket.onmessage = (event: MessageEvent) => {
      const message: Message = JSON.parse(event.data);

      if (message.eventType === 'ONLINE_ACK') {
        // ONLINE_ACK may still be a legacy string like "id_symbol" or a structured object
        if (typeof message.data === 'string') {
          const parts = message.data.split("_");
          this.id = parts[0];
          this.symbol = parts[1];
        } else if (typeof message.data === 'object' && message.data !== null) {
          // structured { id, symbol }
          const d: any = message.data;
          this.id = String(d.id ?? this.id);
          this.symbol = String(d.symbol ?? this.symbol);
        }

      } else if (message.eventType === 'PLAYER_MOVE') {
        // Expected structured payload:
        // { data: { x: number, y: number, playerId: number, playerSymbol: string } }
        if (typeof message.data === 'object' && message.data !== null && 'x' in message.data && 'y' in message.data) {
          const d: any = message.data;
          const x = Number(d.x);
          const y = Number(d.y);
          const symbol = d.playerSymbol ?? d.symbol ?? 'X';
          this.board[x][y] = symbol;
        } else if (typeof message.data === 'string') {
          // handle legacy string formats (e.g., "x_y_..._symbol")
          const parts = message.data.split("_");
          const x = parseInt(parts[0], 10);
          const y = parseInt(parts[1], 10);
          const symbol = parts[3] ?? parts[2] ?? 'X';
          this.board[x][y] = symbol;
        } else {
          console.error('Malformed PLAYER_MOVE message', message);
        }

      } else if (message.eventType === 'GAME_ERROR') {
        // show message in toast
        let errMsg = 'Unknown error';
        if (typeof message.data === 'object' && message.data !== null && 'message' in message.data) {
          errMsg = String((message.data as any).message);
        } else if (typeof message.data === 'string') {
          errMsg = message.data;
        } else {
          try { errMsg = JSON.stringify(message.data); } catch (e) { /* keep fallback */ }
        }
        this.toast(errMsg);
      }
    };
  }

  toast(message: string) {
    // delegate to ToastService if available
    try {
      // import lazily to avoid circular deps in some setups
      const svc = (window as any).__toastService as any;
      if (svc && typeof svc.show === 'function') {
        svc.show(message);
        return;
      }
    } catch (e) {
      // ignore
    }
    // fallback: simple alert
    console.warn('Toast fallback:', message);
  }

}
