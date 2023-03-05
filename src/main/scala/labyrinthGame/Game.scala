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
    level.grid

  def placePlayer(): Unit = ???

  def initializeLevel(): Unit = ???

  def update(): Unit = ???
}
