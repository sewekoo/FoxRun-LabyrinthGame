package labyrinthGame

import scala.collection.mutable.Buffer
class Game {
  // Game round tracker
  var round = 0
  var level: Level = _
  var player: Player = _

  var mapSizeX: Int = 20
  var mapSizeY: Int = 15
  var squareSize: Int = 50
  var roundLength: Int = 180


  def generateLevel(round: Int): Buffer[Buffer[Square]] =
    this.round = round
    this.level = new Level(mapSizeX, mapSizeY, squareSize, roundLength)
    level.initalizeGrid()
    level.pickStartingPosition()
    level.generateLabyrinth()
    initializeLevel()
    level.grid

  def placePlayer(level: Level): Unit =
    player = new Player(level)

  def initializeLevel(): Unit =
    placePlayer(level)

  def update(): Unit = ???
}
