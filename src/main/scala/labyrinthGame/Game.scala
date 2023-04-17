package labyrinthGame

import scala.collection.mutable.Buffer
import scala.math.max
import java.io.{FileReader, FileNotFoundException, BufferedReader, IOException, FileWriter, BufferedWriter}
import scala.util.Random
class Game(sizeX: Int, sizeY: Int, time: Int) {
  // Game round tracker
  var round = 0
  var level: Level = _
  var levelLoaded = false
  var player: Player = _
  var points: Int = 0

  var mapSizeX: Int = sizeX
  var mapSizeY: Int = sizeY
  var squareSize: Int = 50
  var roundLength: Int = time


  def generateLevel(round: Int): Buffer[Buffer[Square]] =
    this.round = round
    this.level = new Level(mapSizeX, mapSizeY, squareSize, roundLength)
    level.initalizeGrid()
    level.pickStartingPosition()
    level.generateLabyrinth()
    initializeLevel()
    levelLoaded = true
    println(level.seed)
    level.grid


  def readFile(fileName: String): Seq[String] =
    try
      val fileIn = FileReader(fileName)
      val linesIn = BufferedReader(fileIn)
      try
        var resList = LazyList
                      .continually(linesIn.readLine())
                      .takeWhile(_ != null)
                      .toSeq
        for i <- resList do
          println(i)
        resList
      finally
        fileIn.close()
        linesIn.close()
      end try
    catch
      case notFound: FileNotFoundException =>
        println("File not found")
        Seq[String]()
      case e: IOException =>
        println("Reading finished with an error")
        Seq[String]()

  def loadLevel(fileName: String): Buffer[Buffer[Square]] =
    this.round = 0
    val levelInfo = readFile(fileName)
    if levelInfo.length != 5 then
      println("Incorrect level format")
    this.level = new Level(levelInfo(0).toInt, levelInfo(1).toInt, levelInfo(2).toInt, levelInfo(3).toInt)
    level.seed = levelInfo(4).toLong
    level.rand = new Random(level.seed)
    level.initalizeGrid()
    level.pickStartingPosition()
    level.generateLabyrinth()
    initializeLevel()
    levelLoaded = true
    level.grid

  def saveLevel(fileName: String) =
    val arr: Seq[String] = Seq(level.sizeX.toString, level.sizeY.toString, level.squareLength.toString, level.roundLength.toString, level.seed.toString)
    try
      val filesOut = FileWriter(fileName)
      val linesOut = BufferedWriter(filesOut)
      try
        for
          i <- arr
          if i != null
        do
          // println(i)
          linesOut.write(i)
          linesOut.newLine()
      finally
        linesOut.close()
        println("filesOut closed")
        filesOut.close()
        println("linesOut closed")
      end try
    catch
      case notFound: FileNotFoundException =>
        println("File not found")
      case e: IOException =>
        println("Error writing a file: " + e)

  def placePlayer(level: Level): Unit =
    player = new Player(level)

  def initializeLevel(): Unit =
    level.pickEdges()
    level.pickGoals(max(5 - round, 1))
    placePlayer(level)

  def update(): Unit =
    updateWinLoseCondition()
    
  def decreaseTimer(): Unit =
    level.timer -= 1


  def updateWinLoseCondition(): Unit =
    for i <- level.goals do
      if i.isHorizontal then
        if (player.posX >= i.getPosX() && player.posX <= i.getPosX() + i.getLength() && player.posY >= i.getPosY() && player.posY <= i.getPosY() + i.getWidth()) then
          round += 1
          points += level.timer + 100 + round * 10
          roundLength -= 10
          level.levelWon = true
          levelLoaded = false
      else
        if (player.posX >= i.getPosX() && player.posX <= i.getPosX() + i.getWidth() && player.posY >= i.getPosY() && player.posY <= i.getPosY() + i.getLength()) then
          round += 1
          points += level.timer + 100 + round * 10
          roundLength -= 10
          level.levelWon = true
          levelLoaded = false
    if level.timer <= 0 then
      round = 0
      level.levelLost = true
      
      
  def solveLevel(): Unit =
    player.currentSquare = level.startingPoint
    player.resetPos()
    level.solution = level.solve()
    level.showSolution = true
}




