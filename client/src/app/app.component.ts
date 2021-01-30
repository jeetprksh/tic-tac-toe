import {Component} from '@angular/core';
import {Message} from '../data/Message';

const WEBSOCKET_URL = 'ws://localhost:8185/websocket';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  websocket = new WebSocket(WEBSOCKET_URL);
  board: string[][];
  id: string;
  symbol: string;

  constructor() {
    this.initializeBoard();
    this.startListening();
  }

  initializeBoard() {
    this.board = [];
    for(var i = 0; i < 3; i++) {
      this.board[i] = [];
      for (var j = 0; j < 3; j++) {
        this.board[i][j] = "-";
      }
    }
  }

  move(i, j) {
    console.log(i + " " + j);
    let message: Message = {
      type: 'MOVE_ATTEMPT',
      message: i + "_" + j
    }
    this.websocket.send(JSON.stringify(message));
  }

  startListening() {
    this.websocket.onmessage = (event: MessageEvent) => {
      let message: Message = JSON.parse(event.data);
      if (message.type == 'ONLINE_ACK') {
        this.id = message.message.split("_")[0];
        this.symbol = message.message.split("_")[1];
      } else if (message.type == 'PLAYER_MOVE') {
        var x = message.message.split("_")[0];
        var y = message.message.split("_")[1];
        var symbol = message.message.split("_")[3];
        this.board[x][y] = symbol;
      } else if (message.type == 'GAME_ERROR') {
        console.error(message);
      }
    };
  }

}
