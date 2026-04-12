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
  playerId: string = '';
  symbol: string = '';
  opponentId: string = '';
  opponentSymbol: string = '';
  gameId: number = 0;
  showBoard: boolean = false;
  showAvailableGames: boolean = false;
  availableGames: any[] = [];

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
      messageType: 'MOVE_ATTEMPT',
      data: { x: i, y: j }
    };
    this.websocket.send(JSON.stringify(message));
  }

  startGame() {
    const message: Message = {
      messageType: 'START_NEW',
      data: '' // Empty string for start new game data
    };
    this.websocket.send(JSON.stringify(message));
  }

  joinGame(gameId: number) {
    const message: Message = {
      messageType: 'JOIN_GAME',
      data: { gameId: gameId }
    };
    this.websocket.send(JSON.stringify(message));
    this.showBoard = true;
    this.showAvailableGames = false;
  }

  refreshGames() {
    const message: Message = {
      messageType: 'LIST_GAMES',
      data: '' // Empty string for list games data
    };
    this.websocket.send(JSON.stringify(message));
  }

  sendNewPlayerMessage() {
    console.log('Sending new player message');
    const message: Message = {
      messageType: 'NEW_PLAYER',
      data: '' // Empty string for new player data
    };
    this.websocket.send(JSON.stringify(message));
  }

  startListening() {
    this.websocket.onmessage = (event: MessageEvent) => {
      const message: Message = JSON.parse(event.data);

      if (message.messageType === 'ONLINE_ACK') {
        console.log('Received ONLINE_ACK:', message.data);
        this.sendNewPlayerMessage(); 
      } else if (message.messageType === 'NEW_PLAYER_CREATED') {
        console.log('Received NEW_PLAYER_CREATED:', message.data);
        const d: any = message.data;
        if (d.playerInfo) {
          this.playerId = String(d.playerInfo.playerId);
          this.gameId = d.playerInfo.gameId;
          this.symbol = d.playerInfo.symbol;
          sessionStorage.setItem('playerInfo', JSON.stringify(d.playerInfo));
        }
      } else if (message.messageType === 'AVAILABLE_GAMES') {
        console.log('Received AVAILABLE_GAMES:', message.data);
        const d: any = message.data;
        if (d.availableGames) {
          this.availableGames = d.availableGames;
          this.showAvailableGames = true;
        }
      } else if (message.messageType === 'NEW_GAME_STARTED') {
        console.log('Received NEW_GAME_STARTED:', message.data);
        const d: any = message.data;
        if (d.gameInfo) {
          this.gameId = d.gameInfo.gameId;
          if (d.gameInfo.players && d.gameInfo.players.length > 0) {
            // Find current player and opponent by matching stored playerId
            const currentPlayerId = parseInt(this.playerId);
            const currentPlayer = d.gameInfo.players.find((p: any) => p.playerId === currentPlayerId);
            const opponent = d.gameInfo.players.find((p: any) => p.playerId !== currentPlayerId);
            
            if (currentPlayer) {
              this.symbol = currentPlayer.symbol;
            }
            if (opponent) {
              this.opponentId = String(opponent.playerId);
              this.opponentSymbol = opponent.symbol;
            }
          }
          this.showBoard = true;
          // Update session storage with new gameId
          const storedPlayerInfo = JSON.parse(sessionStorage.getItem('playerInfo') || '{}');
          storedPlayerInfo.gameId = this.gameId;
          sessionStorage.setItem('playerInfo', JSON.stringify(storedPlayerInfo));
        }
      } else if (message.messageType === 'JOINED_GAME') {
        console.log('Received JOINED_GAME:', message.data);
        const d: any = message.data;
        if (d.gameInfo) {
          this.gameId = d.gameInfo.gameId;
          if (d.gameInfo.players && d.gameInfo.players.length > 0) {
            // Get stored player info to identify current player
            const storedPlayerInfo = JSON.parse(sessionStorage.getItem('playerInfo') || '{}');
            const currentPlayerId = storedPlayerInfo.playerId;
            
            // Find current player and opponent
            const currentPlayer = d.gameInfo.players.find((p: any) => p.playerId === currentPlayerId);
            const opponent = d.gameInfo.players.find((p: any) => p.playerId !== currentPlayerId);
            
            if (currentPlayer) {
              this.playerId = String(currentPlayer.playerId);
              this.symbol = currentPlayer.symbol;
            }
            if (opponent) {
              this.opponentId = String(opponent.playerId);
              this.opponentSymbol = opponent.symbol;
            }
          }
          this.showBoard = true;
          // Update session storage with new gameId
          const storedPlayerInfo = JSON.parse(sessionStorage.getItem('playerInfo') || '{}');
          storedPlayerInfo.gameId = this.gameId;
          sessionStorage.setItem('playerInfo', JSON.stringify(storedPlayerInfo));
        }
      } else if (message.messageType === 'PLAYER_MOVE') {
        const d: any = message.data;
        const x = Number(d.x);
        const y = Number(d.y);
        const symbol = d.playerSymbol ?? d.symbol ?? 'X';
        this.board[x][y] = symbol;
      } else if (message.messageType === 'GAME_ERROR') {
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

  getGameCardBackground(gameId: number): string {
    // Generate a translucent color based on game ID hash
    const hash = this.simpleHash(gameId.toString());
    const hue = hash % 360;
    return `hsla(${hue}, 70%, 85%, 0.8)`;
  }

  getPlayerCardBackground(playerId: number): string {
    // Generate a random color based on player ID hash
    const hash = this.simpleHash(playerId.toString());
    const hue = hash % 360;
    return `hsl(${hue}, 65%, 60%)`;
  }

  private simpleHash(str: string): number {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32-bit integer
    }
    return Math.abs(hash);
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
