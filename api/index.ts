import { Server } from "socket.io";

const io = new Server().listen(3000);
const games: Game = {};

io.on("connection", function (socket) {
  console.log(
    `[${socket.handshake.query.CustomId}] User connected from ${socket.request.socket.remoteAddress}`
  );

  socket.on("newGame", function (name) {
    if (games[name]) {
      console.log(`[${socket.handshake.query.CustomId}] Join Game ${name}`);
      socket.emit("newGame", {});
    } else {
      console.log(
        `[${socket.handshake.query.CustomId}] Created a new Game ${name}`
      );
      newGame(name);
      socket.emit("newGame", {});
    }
  });

  socket.on("getGames", function () {
    socket.emit("getGames", Object.keys(games));
  });
});

function newGame(gameId: string) {
  games[gameId] = {};

  games[gameId] = {};
  const nsp = io.of("/game/" + gameId);
  nsp.on("connection", function (socket) {
    const username = socket.handshake.query.CustomId as string;
    console.log(
      `[${gameId} - ${username}] User connected from ${socket.request.socket.remoteAddress}`
    );
    addScore(gameId, username, 0);

    socket.on("score", function (score) {
      addScore(gameId, username, score);
      nsp.emit("score", games[gameId]);
    });

    socket.on("won", function (score) {
      addScore(gameId, username, score, false);
      nsp.emit("score", games[gameId]);
    });

    socket.on("over", function (score) {
      addScore(gameId, username, score, false);
      nsp.emit("score", games[gameId]);
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
