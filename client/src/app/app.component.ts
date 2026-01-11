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
        // Expecting { data: { message: string } }
        let errMsg = 'Unknown error';
        if (typeof message.data === 'object' && message.data !== null && 'message' in message.data) {
          errMsg = String((message.data as any).message);
        } else if (typeof message.data === 'string') {
          errMsg = message.data;
        } else {
          try {
            errMsg = JSON.stringify(message.data);
          } catch (e) {
            // keep default
          }
        }
        console.error('GAME_ERROR:', errMsg);
        this.showToast(errMsg);
      }
    };
  }

  // Show a Bootstrap toast in the top-right corner
  showToast(message: string) {
    // Ensure a container exists
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.style.position = 'fixed';
      container.style.top = '1rem';
      container.style.right = '1rem';
      container.style.zIndex = '1060';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'toast align-items-center text-bg-danger border-0 mb-2';
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">${this.escapeHtml(message)}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    `;

    container.appendChild(toast);

    // Use Bootstrap's JS Toast if available
    const bs = (window as any).bootstrap;
    try {
      if (bs && bs.Toast) {
        const t = new bs.Toast(toast, { autohide: true, delay: 8000 });
        t.show();
        // remove element after hidden
        toast.addEventListener('hidden.bs.toast', () => toast.remove());
      } else {
        // fallback: auto remove after 8s
        setTimeout(() => toast.remove(), 8000);
      }
    } catch (e) {
      // ensure removal even on error
      setTimeout(() => toast.remove(), 8000);
    }
  }

  // basic HTML escape to avoid injection
  escapeHtml(unsafe: string) {
    return unsafe
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

}
