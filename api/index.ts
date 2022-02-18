import { Server } from "socket.io";
import shortUUID from "short-uuid";

const io = new Server().listen(3000);
const games: Game = {};
const lobbys: Lobby = {};

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
    const username = socket.handshake.query.CustomId as string;
    newGame(gameId, username);
    socket.emit("newGame", getLobbyDetails(gameId));
    socket.broadcast.emit("getLobbys", getLobbys());
  });

  socket.on("getLobbys", function () {
    console.log(`[${socket.handshake.query.CustomId}] requested Lobbylist`);
    socket.emit("getLobbys", getLobbys());
  });
});

function newGame(gameId: string, username: string) {
  lobbys[gameId] = {
    owner: username,
    currentUsers: 0,
    maxUsers: 48,
    running: false,
  };

  games[gameId] = {};
  const nsp = io.of("/game/" + gameId);
  nsp.on("connection", function (socket) {
    const username = socket.handshake.query.CustomId as string;
    console.log(
      `[${gameId} - ${username}] User connected from ${socket.request.socket.remoteAddress}`
    );
    addScore(gameId, username, 0);
    nsp.emit("score", getScore(gameId));

    socket.on("score", function (score) {
      addScore(gameId, username, score);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("won", function (score) {
      addScore(gameId, username, score, false);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("over", function (score) {
      addScore(gameId, username, score, false);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("start", function (score) {
      nsp.emit("start", {});
    });
  });
}

function addScore(gameId, username, score, alive = true) {
  console.log(`[${gameId} - ${username}] ${alive ? "Alive" : "Dead"} ${score}`);

  if (!games[gameId]) {
    games[gameId] = {};
  }
  if (!games[gameId][username]) {
    games[gameId][username] = {
      score: 0,
      alive: true,
    };
  }

  games[gameId][username].score = score;
  games[gameId][username].alive = alive;
}

function getScore(gameId: string) {
  var score = Object.keys(games[gameId]).map((key) => {
    return {
      ...games[gameId][key],
      username: key,
    };
  });

  score.sort((a, b) => b.score - a.score);

  return score;
}

function getLobbys() {
  var lobbyList = Object.keys(lobbys).map((gameId) => {
    return getLobbyDetails(gameId);
  });

  lobbyList = lobbyList.filter((lobby) => !lobby.running);

  lobbyList.sort((a, b) => b.currentUsers - a.currentUsers);

  return lobbyList;
}

function getLobbyDetails(gameId) {
  return {
    ...lobbys[gameId],
    currentUsers: Object.keys(games[gameId]).length,
    id: gameId,
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
