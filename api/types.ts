import { Namespace } from "socket.io";
import { DefaultEventsMap } from "socket.io/dist/typed-events";

export type Game = {
  [gameId: string]: {
    [username: string]: {
      score: number;
      alive: boolean;
    };
  };
};

export type Lobby = {
  [gameId: string]: {
    owner: string;
    currentUsers: number;
    maxUsers: number;
    running: boolean;
  };
};

export type SocketNamespace = Namespace<
  DefaultEventsMap,
  DefaultEventsMap,
  DefaultEventsMap,
  any
>;
