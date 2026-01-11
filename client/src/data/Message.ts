export interface MoveAttemptData { x: number; y: number; }
export interface PlayerMoveData { x: number; y: number; playerId: number; playerSymbol: string; }
export interface OnlineAckData { id: string; symbol: string; }
export interface GameErrorData { message: string; }

export type MessageData = string | MoveAttemptData | PlayerMoveData | OnlineAckData | GameErrorData;

export interface Message {
  eventType: string;
  data: MessageData;
} 