import { Namespace, Server } from "socket.io";
import shortUUID from "short-uuid";
import { Game, Lobby, SocketNamespace } from "./types";

const io = new Server().listen(3000);
const games: Game = {};
const lobbys: Lobby = {};
const OPENING_TEXT = "Opening...";

const uuid = shortUUID(shortUUID.constants.flickrBase58, {
  consistentLength: true,
});

io.on("connection", function (socket) {
  console.log(
    `[${socket.handshake.query.CustomId}] User connected from ${socket.request.socket.remoteAddress}`
  );

  socket.on("newGame", function () {
    var gameId = generateLobbyName();

    console.log(
      `[${socket.handshake.query.CustomId}] Created a new Game ${gameId}`
    );
    const userId = socket.handshake.query.CustomId as string;
    const nsp = newGame(gameId, userId);
    socket.emit("newGame", getLobbyDetails(gameId));
    socket.broadcast.emit("getLobbys", getLobbys());
    setTimeout(() => {
      // Update once lobby is open
      socket.broadcast.emit("getLobbys", getLobbys());
    }, 1500);

    setTimeout(() => {
      // Cheanup if not connected
      if (lobbys[gameId] && lobbys[gameId].owner == OPENING_TEXT) {
        closeLobby(gameId, nsp);
      }
    }, 60000);
  });

  socket.on("getLobbys", function () {
    console.log(`[${socket.handshake.query.CustomId}] requested Lobbylist`);
    socket.emit("getLobbys", getLobbys());
  });

  socket.on("disconnect", (reason) => {
    const userId = socket.handshake.query.CustomId as string;
    console.log(`[${userId}] User disconnected because of ${reason}`);
  });
});

function newGame(gameId: string, userId: string) {
  lobbys[gameId] = {
    owner: userId,
    currentUsers: 0,
    maxUsers: 48,
    running: false,
    round: -1,
    duration: -1,
    roundDurations: [35000, 25000, 15000],
  };

  games[gameId] = {};
  const nsp = io.of("/game/" + gameId);
  nsp.on("connection", function (socket) {
    const userId = socket.handshake.query.CustomId as string;
    const username = socket.handshake.query.CustomUsername as string;
    console.log(
      `[${gameId} - ${userId} - ${username}] User connected from ${socket.request.socket.remoteAddress}`
    );
    addScore(gameId, userId, username, 0);
    nsp.emit("score", getScore(gameId));

    socket.on("score", function (score) {
      addScore(gameId, userId, username, score);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("won", function (score) {
      addScore(gameId, userId, username, score, false);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("over", function (score) {
      addScore(gameId, userId, username, score, false);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("lobbyDetails", function (_) {
      nsp.emit("lobbyDetails", getLobbyDetails(gameId));
    });

    socket.on("start", function (_) {
      nsp.emit("start", {});
      setTimeout(() => {
        startRound(gameId, 0, nsp);
      }, 1000);
    });

    socket.on("disconnect", (reason) => {
      const userId = socket.handshake.query.CustomId as string;
      leaveLobby(gameId, userId, nsp);
      console.log(
        `[${gameId} - ${userId} - ${username}] User disconnected because of ${reason}`
      );
      if (lobbys[gameId]) {
        nsp.emit("lobbyDetails", getLobbyDetails(gameId));
        nsp.emit("score", getScore(gameId));
      }
    });
  });

  return nsp;
}

function addScore(
  gameId: string,
  userId: string,
  username: string,
  score: number,
  alive = true
) {
  console.log(`[${gameId} - ${userId}] ${alive ? "Alive" : "Dead"} ${score}`);

  if (!games[gameId]) {
    games[gameId] = {};
  }
  if (!games[gameId][userId]) {
    games[gameId][userId] = {
      username,
      score: 0,
      alive: true,
    };
  }

  games[gameId][userId].score = score;
  games[gameId][userId].alive = alive;
}

function getScore(gameId: string) {
  if (!(gameId in games)) {
    console.warn(`[WARN] Requested empty score for ${gameId}`);
    return [];
  }

  var score = Object.keys(games[gameId]).map((key) => {
    return {
      ...games[gameId][key],
      userId: key,
    };
  });

  score.sort((a, b) => b.score - a.score);

  for (const [i, key] of enumerate(Object.keys(score))) {
    score[key].position = i + 1;
  }

  return score;
}

function* enumerate(iterable) {
  let i = 0;

  for (const x of iterable) {
    yield [i, x];
    i++;
  }
}

function getLobbys() {
  var lobbyList = Object.keys(lobbys).map((gameId) => {
    var details = getLobbyDetails(gameId);
    const owner = games[gameId][details.owner];

    return {
      ...details,
      owner: owner ? owner.username : OPENING_TEXT,
    };
  });

  lobbyList = lobbyList.filter((lobby) => !lobby.running);

  lobbyList.sort((a, b) => b.currentUsers - a.currentUsers);

  return lobbyList;
}

function getLobbyDetails(gameId: string) {
  return {
    ...lobbys[gameId],
    currentUsers: Object.keys(games[gameId]).length,
    id: gameId,
    duration: -1,
  };
}

function generateLobbyName(): string {
  var gameId = uuid.generate().toString();

  gameId = gameId.substring(0, 12);

  gameId = [
    gameId.substring(0, 4),
    gameId.substring(4, 8),
    gameId.substring(8, 12),
  ].join("-");

  if (lobbys[gameId]) {
    gameId = generateLobbyName();
  }

  return gameId;
}

function leaveLobby(
  gameId: string,
  userId: string,
  namespace: SocketNamespace
) {
  console.log(`[${gameId} - ${userId}] Left the lobby`);

  if (!games[gameId] || !games[gameId][userId]) {
    return;
  }

  delete games[gameId][userId];

  if (Object.keys(games[gameId]).length == 0) {
    closeLobby(gameId, namespace);
    return;
  }

  if (lobbys[gameId].owner == userId) {
    lobbys[gameId].owner = Object.keys(games[gameId])[0];
  }
}

function closeLobby(gameId: string, namespace: SocketNamespace) {
  console.log(`[${gameId} - SYSTEM] Lobby is empty, closing...`);

  delete lobbys[gameId];
  delete games[gameId];

  namespace.disconnectSockets(false);
  namespace.removeAllListeners();
  delete io._nsps["/game/" + gameId];
}

function startRound(gameId: string, round: number, namespace: SocketNamespace) {
  lobbys[gameId].round = round;
  const duration = lobbys[gameId].roundDurations[round];
  console.log(`[${gameId} - SYSTEM] round ${round} started`);

  namespace.emit("lobbyDetails", {
    ...lobbys[gameId],
    currentUsers: Object.keys(games[gameId]).length,
    id: gameId,
    duration: duration ? duration : -1,
  });
  namespace.emit("score", getScore(gameId));

  setTimeout(() => {
    endRound(gameId, round, namespace);
  }, duration);
}

function endRound(gameId: string, round: number, namespace: SocketNamespace) {
  if (!lobbys[gameId]) {
    return;
  }

  if (lobbys[gameId].roundDurations.length == round - 1) {
    endGame(gameId, namespace);
    return;
  }
  const playerCount = getScore(gameId).filter((score) => score.alive).length;
  const playersToRemove = Math.floor(playerCount / 2);

  for (let i = 0; i < playersToRemove; i++) {
    if (removePlayerByScore(gameId, namespace)) {
      endGame(gameId, namespace);
      return;
    }
  }

  console.log(
    `[${gameId} - SYSTEM] round ${round} over, starting ${round + 1}`
  );

  startRound(gameId, round + 1, namespace);
}

function removePlayerByScore(gameId: string, namespace: SocketNamespace) {
  const remainingPlayers = getScore(gameId).filter((score) => score.alive);
  const removePlayer = remainingPlayers[remainingPlayers.length - 1];

  if (!removePlayer) {
    // No player to remove
    return true;
  }

  games[gameId][removePlayer.userId].alive = false;

  console.log(
    `[${gameId} - SYSTEM] removed ${removePlayer.userId} ${
      remainingPlayers.length - 1
    } players remaining`
  );

  if (remainingPlayers.length - 1 == 1) {
    // Only 1 player remaining
    return true;
  }
}

function endGame(gameId: string, namespace: SocketNamespace) {
  console.log(`[${gameId} - SYSTEM] Game end`);

  namespace.emit(
    "score",
    getScore(gameId).map((score) => {
      score.alive = false;
      return score;
    })
  );

  setTimeout(() => {
    closeLobby(gameId, namespace);
  }, 500);

  return;
}
