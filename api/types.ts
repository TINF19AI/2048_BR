type Game = {
  [gameId: string]: {
    [username: string]: {
      score: number;
      alive: boolean;
    };
  };
};
