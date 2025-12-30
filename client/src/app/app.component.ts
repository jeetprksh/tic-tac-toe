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
      type: 'MOVE_ATTEMPT',
      message: i + "_" + j
    };
    this.websocket.send(JSON.stringify(message));
  }

  startListening() {
    this.websocket.onmessage = (event: MessageEvent) => {
      const message: Message = JSON.parse(event.data);
      if (message.type === 'ONLINE_ACK') {
        this.id = message.message.split("_")[0];
        this.symbol = message.message.split("_")[1];
      } else if (message.type === 'PLAYER_MOVE') {
        const x = parseInt(message.message.split("_")[0], 10);
        const y = parseInt(message.message.split("_")[1], 10);
        const symbol = message.message.split("_")[3];
        this.board[x][y] = symbol;
      } else if (message.type === 'GAME_ERROR') {
        console.error(message);
      }
    };
  }

}
