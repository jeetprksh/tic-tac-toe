export interface MoveAttemptData { x: number; y: number; }
export interface PlayerMoveData { x: number; y: number; playerId: number; playerSymbol: string; }
export interface OnlineAckData { id: string; symbol: string; }
export interface GameErrorData { message: string; }
export interface Player { playerId: number; symbol: string; gameId?: number; }
export interface GameInfo { gameId: number; players: Player[]; }
export interface NewGameStartedData { gameInfo: GameInfo; }
export interface NewPlayerCreatedData { playerInfo: { playerId: number; gameId: number; symbol: string; }; }
export interface JoinedGameData { gameInfo: GameInfo; }
export interface AvailableGamePlayer { playerId: number; symbol: string; }
export interface AvailableGame { gameId: number; players: AvailableGamePlayer[]; }
export interface AvailableGamesData { availableGames: AvailableGame[]; }
export interface JoinGameData { gameId: number; }

export type MessageData = string | MoveAttemptData | PlayerMoveData | OnlineAckData | GameErrorData | NewGameStartedData | NewPlayerCreatedData | JoinedGameData | AvailableGamesData | JoinGameData;

export interface Message {
  messageType: string;
  data: MessageData;
} 