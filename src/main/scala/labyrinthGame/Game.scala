package labyrinthGame

import scala.collection.mutable.Buffer
import scala.math.max
class Game {
  // Game round tracker
  var round = 0
  var level: Level = _
  var levelLoaded = false
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
    levelLoaded = true
    level.grid

  def placePlayer(level: Level): Unit =
    player = new Player(level)

  def initializeLevel(): Unit =
    level.pickEdges()
    level.pickGoals(max(5 - round, 1))
    placePlayer(level)

  def update(): Unit =
    updateWinLoseCondition()


  def updateWinLoseCondition(): Unit =
    for i <- level.goals do
      if i.isHorizontal then
        if (player.posX >= i.getPosX() && player.posX <= i.getPosX() + i.getLength() && player.posY >= i.getPosY() && player.posY <= i.getPosY() + i.getWidth()) then
          round += 1
          level.levelWon = true
          levelLoaded = false
      else
        if (player.posX >= i.getPosX() && player.posX <= i.getPosX() + i.getWidth() && player.posY >= i.getPosY() && player.posY <= i.getPosY() + i.getLength()) then
          round += 1
          level.levelWon = true
          levelLoaded = false
    if level.timer <= 0 then
      round = 0
      level.levelLost = true
      levelLoaded = false




}




