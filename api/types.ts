type Game = {
  [gameId: string]: {
    [username: string]: {
      score: number;
      alive: boolean;
    };
  };
};

type Lobby = {
  [gameId: string]: {
    owner: string;
    currentUsers: number;
    maxUsers: number;
    running: boolean;
  };
};
