package labyrinthGame

import javafx.geometry.Pos
import labyrinthGame.Main.stage
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, CheckBox, Label, Menu, MenuBar, MenuItem, TextField}
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.paint.Color.{Black, Blue, Green, Red, White, Yellow, rgb}
import scalafx.scene.shape.Rectangle
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.image.{Image, ImageView}
import scalafx.Includes.*
import scalafx.animation.AnimationTimer
import scalafx.scene.text.Font
import scalafx.event.ActionEvent

import java.awt.GraphicsEnvironment
import java.awt.DisplayMode
import java.io.{BufferedWriter, File, FileInputStream, FileNotFoundException, FileWriter, IOException, InputStream}
import scala.collection.mutable.Buffer

object Main extends JFXApp3 {
  
  // Flag to check if level has been loaded
  var levelLoaded: Boolean = false
  // Flag to check whether game has been launched from many
  var gameStarted: Boolean = false
  // Flag used to check whether game is on or not
  var gameOn: Boolean = false
  // Flag to check initally whether game class is still null
  var gameObjectCreated = false
  // Flag to set game on fullscreen
  var fullScreenOn = false
  // Game object that runs labyrinth game
  var game: Game = _
  // GraphicsContext variable that the game is drawn on
  var graphics: GraphicsContext = null
  // Long for second. Used in timer
  val second = 1_000_000_000L
  // Tracks current tick
  var lastTick = 0L
  // Tracks round time
  var roundTimer = 0L
  // Number to determine state of animation
  var animationNum = 0
  // Variable that sets map size for width
  var mapSizeX = 20
  // Variable that sets map size for height
  var mapSizeY = 15
  // Round length for a first round of new game
  var roundLength = 300
  // Timer that updates tick and animation number and also round timer
  val timer: AnimationTimer = AnimationTimer(now => {
      if lastTick == 0L || (now - lastTick > second / 64) then {
        lastTick = now
        tick(graphics, stage)
        animationNum += 1
        if (animationNum >= 30) then
          animationNum = 0
      }
      if gameOn && (roundTimer == 0L || (now - roundTimer > second)) then {
        roundTimer = now
        game.decreaseTimer()
      }
    })

  // Offsets for moving zoomed out map
  var xOffset = 0
  var yOffset = 0

  // Flag to enable player camera follow (turned off for debug to show whole map)
  var playerCentered: Boolean = true

  // Flag for player movement animation
  var playerMoving: Boolean = false

  // Flag for player facing direction
  var facingRight: Boolean = true

  // Menu banner
  val menuPic = new Image(new FileInputStream("assets/menupic.png"))

  // Background image
  val backgroundStream: InputStream = new FileInputStream("assets/background.png")
  val background = new Image(backgroundStream)

  // Player idle images. 15 different animation pics
  val plrstream: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_00.png")
  val plrimg = new Image(plrstream)
  val plrstream1: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_01.png")
  val plrimg1 = new Image(plrstream1)
  val plrstream2: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_02.png")
  val plrimg2 = new Image(plrstream2)
  val plrstream3: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_03.png")
  val plrimg3 = new Image(plrstream3)
  val plrstream4: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_04.png")
  val plrimg4 = new Image(plrstream4)
  val plrstream5: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_05.png")
  val plrimg5 = new Image(plrstream5)
  val plrstream6: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_06.png")
  val plrimg6 = new Image(plrstream6)
  val plrstream7: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_07.png")
  val plrimg7 = new Image(plrstream7)
  val plrstream8: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_08.png")
  val plrimg8 = new Image(plrstream8)
  val plrstream9: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_09.png")
  val plrimg9 = new Image(plrstream9)
  val plrstream10: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_10.png")
  val plrimg10 = new Image(plrstream10)
  val plrstream11: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_11.png")
  val plrimg11 = new Image(plrstream11)
  val plrstream12: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_12.png")
  val plrimg12 = new Image(plrstream12)
  val plrstream13: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_13.png")
  val plrimg13 = new Image(plrstream13)
  val plrstream14: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_14.png")
  val plrimg14 = new Image(plrstream14)

  // Player idle flipped image
  val plrstreamF: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_00F.png")
  val plrimgF = new Image(plrstreamF)
  val plrstream1F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_01F.png")
  val plrimg1F = new Image(plrstream1F)
  val plrstream2F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_02F.png")
  val plrimg2F = new Image(plrstream2F)
  val plrstream3F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_03F.png")
  val plrimg3F = new Image(plrstream3F)
  val plrstream4F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_04F.png")
  val plrimg4F = new Image(plrstream4F)
  val plrstream5F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_05F.png")
  val plrimg5F = new Image(plrstream5F)
  val plrstream6F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_06F.png")
  val plrimg6F = new Image(plrstream6F)
  val plrstream7F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_07F.png")
  val plrimg7F = new Image(plrstream7F)
  val plrstream8F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_08F.png")
  val plrimg8F = new Image(plrstream8F)
  val plrstream9F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_09F.png")
  val plrimg9F = new Image(plrstream9F)
  val plrstream10F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_10F.png")
  val plrimg10F = new Image(plrstream10F)
  val plrstream11F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_11F.png")
  val plrimg11F = new Image(plrstream11F)
  val plrstream12F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_12F.png")
  val plrimg12F = new Image(plrstream12F)
  val plrstream13F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_13F.png")
  val plrimg13F = new Image(plrstream13F)
  val plrstream14F: InputStream = new FileInputStream("assets/foxy/animation/idle/foxy-idle_14F.png")
  val plrimg14F = new Image(plrstream14F)

  // Player moving right image
  val plrMoveRstream: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_00.png")
  val plrMoveRimg = new Image(Main.plrMoveRstream)
  val plrMoveRstream1: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_01.png")
  val plrMoveRimg1 = new Image(Main.plrMoveRstream1)
  val plrMoveRstream2: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_02.png")
  val plrMoveRimg2 = new Image(Main.plrMoveRstream2)
  val plrMoveRstream3: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_03.png")
  val plrMoveRimg3 = new Image(Main.plrMoveRstream3)
  val plrMoveRstream4: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_04.png")
  val plrMoveRimg4 = new Image(Main.plrMoveRstream4)
  val plrMoveRstream5: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_05.png")
  val plrMoveRimg5 = new Image(Main.plrMoveRstream5)
  val plrMoveRstream6: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_06.png")
  val plrMoveRimg6 = new Image(Main.plrMoveRstream6)
  val plrMoveRstream7: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_07.png")
  val plrMoveRimg7 = new Image(Main.plrMoveRstream7)
  val plrMoveRstream8: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_08.png")
  val plrMoveRimg8 = new Image(Main.plrMoveRstream8)
  val plrMoveRstream9: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_09.png")
  val plrMoveRimg9 = new Image(Main.plrMoveRstream9)
  val plrMoveRstream10: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_10.png")
  val plrMoveRimg10 = new Image(Main.plrMoveRstream10)
  val plrMoveRstream11: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_11.png")
  val plrMoveRimg11 = new Image(Main.plrMoveRstream11)
  val plrMoveRstream12: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_12.png")
  val plrMoveRimg12 = new Image(Main.plrMoveRstream12)
  val plrMoveRstream13: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_13.png")
  val plrMoveRimg13 = new Image(Main.plrMoveRstream13)
  val plrMoveRstream14: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_14.png")
  val plrMoveRimg14 = new Image(Main.plrMoveRstream14)

  // Player moving right flipped image
  val plrMoveRstreamF: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_00F.png")
  val plrMoveRimgF = new Image(Main.plrMoveRstreamF)
  val plrMoveRstream1F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_01F.png")
  val plrMoveRimg1F = new Image(Main.plrMoveRstream1F)
  val plrMoveRstream2F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_02F.png")
  val plrMoveRimg2F = new Image(Main.plrMoveRstream2F)
  val plrMoveRstream3F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_03F.png")
  val plrMoveRimg3F = new Image(Main.plrMoveRstream3F)
  val plrMoveRstream4F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_04F.png")
  val plrMoveRimg4F = new Image(Main.plrMoveRstream4F)
  val plrMoveRstream5F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_05F.png")
  val plrMoveRimg5F = new Image(Main.plrMoveRstream5F)
  val plrMoveRstream6F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_06F.png")
  val plrMoveRimg6F = new Image(Main.plrMoveRstream6F)
  val plrMoveRstream7F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_07F.png")
  val plrMoveRimg7F = new Image(Main.plrMoveRstream7F)
  val plrMoveRstream8F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_08F.png")
  val plrMoveRimg8F = new Image(Main.plrMoveRstream8F)
  val plrMoveRstream9F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_09F.png")
  val plrMoveRimg9F = new Image(Main.plrMoveRstream9F)
  val plrMoveRstream10F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_10F.png")
  val plrMoveRimg10F = new Image(Main.plrMoveRstream10F)
  val plrMoveRstream11F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_11F.png")
  val plrMoveRimg11F = new Image(Main.plrMoveRstream11F)
  val plrMoveRstream12F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_12F.png")
  val plrMoveRimg12F = new Image(Main.plrMoveRstream12F)
  val plrMoveRstream13F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_13F.png")
  val plrMoveRimg13F = new Image(Main.plrMoveRstream13F)
  val plrMoveRstream14F: InputStream = new FileInputStream("assets/foxy/animation/run/foxy-run_14F.png")
  val plrMoveRimg14F = new Image(Main.plrMoveRstream14F)

  // Player debug image
  val dplrstream: InputStream = new FileInputStream("assets/player.png")
  val dplrimg = new Image(dplrstream)

  // Wall horizontal image
  val wallstreamh: InputStream = new FileInputStream("assets/wallhorizontal.png")
  val wallhimg = new Image(wallstreamh)

  // Wall horizontal large image
  val wallstreamhL: InputStream = new FileInputStream("assets/wallhorizontalL.png")
  val wallhimgL = new Image(wallstreamhL)

  // Wall vertical image
  val wallstreamv: InputStream = new FileInputStream("assets/wallvertical.png")
  val wallvimg = new Image(wallstreamv)

  // Wall vertical large image
  val wallstreamvL: InputStream = new FileInputStream("assets/wallverticalL.png")
  val wallvimgL = new Image(wallstreamvL)

  // Floor image
  val floorstream: InputStream = new FileInputStream("assets/floor.png")
  val floorimg = new Image(floorstream)

  // Floor large image
  val floorLstream: InputStream = new FileInputStream("assets/floorlarge.png")
  val floorLimg = new Image(floorLstream)

  // Horizontal overpass image
  val overphorstream: InputStream = new FileInputStream("assets/horizontalbridge.png")
  val overpasshimg = new Image(overphorstream)

  // Horizontal overpass large image
  val overphorstreamL: InputStream = new FileInputStream("assets/horizontalbridgeL.png")
  val overpasshimgL = new Image(overphorstreamL)

  // Vertical overpass image
  val overpverstream: InputStream = new FileInputStream("assets/verticalbridge.png")
  val overpassvimg = new Image(overpverstream)

  // Vertical overpass large image
  val overpverstreamL: InputStream = new FileInputStream("assets/verticalbridgeL.png")
  val overpassvimgL = new Image(overpverstreamL)
  
  // Method to setup game class correctly
  def initializeGame(): Unit =
    game = new Game(mapSizeX, mapSizeY, roundLength)
    game.generateLevel(0)
  
  // Method for loading a level from in game menu. Asks for input at loads corresponding level
  def loadLevel(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    game = new Game(mapSizeX, mapSizeY, roundLength)
    game.levelLoaded = false
    gameOn = false
    var fileName = ""
    val inputScene = new Scene(400, 400) {
      val label = new Label("Type the name of the level file to load")
      label.layoutX = midPointX - 50
      label.layoutY = midPointY - 15

      val textField = new TextField
      textField.layoutX = midPointX - 50
      textField.layoutY = midPointY + 15
      content = List(label, textField)

      textField.onAction = (e:ActionEvent) => {
        fileName = "levels/" + textField.text.apply() + ".txt"
        levelLoaded = true
        game.loadLevel(fileName)
        gameOn = true
        start()
      }
    }
    stage.delegate.setScene(inputScene)
  
  // Method for changing scene to level save and asking input. Delegates actual saving to game class
  def saveLevel(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    var fileName = ""
    val inputScene = new Scene(400, 400) {
      val label = new Label("Type a name for the level")
      label.layoutX = midPointX - 50
      label.layoutY = midPointY - 15

      val textField = new TextField
      textField.layoutX = midPointX - 50
      textField.layoutY = midPointY + 15
      content = List(label, textField)

      textField.onAction = (e:ActionEvent) => {
        fileName = "levels/" + textField.text.apply() + ".txt"
        game.saveLevel(fileName)
        start()
      }
    }
    stage.delegate.setScene(inputScene)
  
  // Adds score to scoreboard file
  def addScoreToFile(player: String): Unit =
    val oldScores: Seq[String] = game.readFile("scoreboard/scores.txt")
    val plrScore = game.points.toString + " " + player
    val scores = oldScores :+ plrScore
    try
      val filesOut = FileWriter("scoreboard/scores.txt")
      val linesOut = BufferedWriter(filesOut)
      try
        for
          i <- scores
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

  // Method for drawing scene for saving score
  def addScore(stage: PrimaryStage): Unit =
    stage.fullScreen = fullScreenOn
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    val scoreScene = new Scene(400, 400) {
      val label = new Label("Type in your name and press ENTER to save score:")
      label.layoutX = midPointX
      label.layoutY = midPointY - 30
      val textField = new TextField
      textField.layoutX = midPointX
      textField.layoutY = midPointY
      content = List(label, textField)

      textField.onAction = (e:ActionEvent) => {
        val player = textField.text.apply()
        if player.nonEmpty then
          addScoreToFile(player)
        gameOn = false
        showMenu(stage)
      }
    }
    stage.delegate.setScene(scoreScene)

  // Method that updates each tick
  // Also handles drawing.
  def tick(graphics: GraphicsContext, stage: PrimaryStage): Unit =
      // Check if game is loaded
      if (game.levelLoaded) then
        game.update()
        graphics.drawImage(background, 0, 0)
        // If game mode on draw map centered around player.
        if (playerCentered) then
          val playersizeX: Int = plrimg.width.toInt
          val playersize: Int = plrimg.height.toInt
          val playerXtrue = (stage.width.toInt / 2) - (playersizeX / 2)
          val playerX = (stage.width.toInt / 2) - (playersize / 2)
          val playerY = (stage.height.toInt / 2) - (playersize / 2)
          val sizeMultiplier = playersize / game.player.size

          // Draw tiles relative to player
          for (i <- game.level.grid) do
            for (j <- i) do
              val diffToPlrX: Int = j.getPosX() - game.player.getPosX
              val diffToPlrY: Int = j.getPosY() - game.player.getPosY
              val relativeXdiff: Int = sizeMultiplier * diffToPlrX
              val relativeYdiff: Int = sizeMultiplier * diffToPlrY
              val relativeX: Int = playerX + relativeXdiff
              val relativeY: Int = playerY + relativeYdiff
              val minX = 0 - (floorLimg.width.toInt + 10)
              val minY = 0 - (floorLimg.height.toInt + 10)
              if ((relativeX > minX && relativeX < stage.width.toInt) && (relativeY > minY && relativeY < stage.height.toInt)) then
                graphics.drawImage(floorLimg, relativeX, relativeY)

          // Draw vertical walls relative to player
          for (i <- game.level.wallGridVertical) do
            for (j <- i) do
              if (!j.isBroken) then
                val diffToPlrX: Int = j.getPosX()  - game.player.getPosX
                val diffToPlrY: Int = j.getPosY() - game.player.getPosY
                val relativeXdiff: Int = sizeMultiplier * diffToPlrX
                val relativeYdiff: Int = sizeMultiplier * diffToPlrY
                val relativeX: Int = playerX + relativeXdiff
                val relativeY: Int = playerY + relativeYdiff
                val minX = 0 - (wallvimgL.width.toInt + 10)
                val minY = 0 - (wallvimgL.height.toInt + 10)
                if ((relativeX > minX && relativeX < stage.width.toInt) && (relativeY > minY && relativeY < stage.height.toInt)) then
                  graphics.drawImage(wallvimgL, relativeX, relativeY)

          // Draw horizontal walls relative to player
          for (i <- game.level.wallGridHorizontal) do
            for (j <- i) do
              if (!j.isBroken) then
                val diffToPlrX: Int = j.getPosX()  - game.player.getPosX
                val diffToPlrY: Int = j.getPosY() - game.player.getPosY
                val relativeXdiff: Int = sizeMultiplier * diffToPlrX
                val relativeYdiff: Int = sizeMultiplier * diffToPlrY
                val relativeX: Int = playerX + relativeXdiff
                val relativeY: Int = playerY + relativeYdiff
                val minX = 0 - (wallhimgL.width.toInt + 10)
                val minY = 0 - (wallhimgL.height.toInt + 10)
                if ((relativeX > minX && relativeX < stage.width.toInt) && (relativeY > minY && relativeY < stage.height.toInt)) then
                  graphics.drawImage(wallhimgL, relativeX, relativeY)

          // Draw overpass relative to player
          for (i <- game.level.grid) do
            for (j <- i) do
              if (j.hasHorizontalOverpass) then
                val diffToPlrX: Int = j.getPosX()  - game.player.getPosX
                val diffToPlrY: Int = j.getPosY() - game.player.getPosY
                val relativeXdiff: Int = sizeMultiplier * diffToPlrX
                val relativeYdiff: Int = sizeMultiplier * diffToPlrY
                val relativeX: Int = playerX + relativeXdiff
                val relativeY: Int = playerY + relativeYdiff
                val minX = 0 - (overpasshimgL.width.toInt + 10)
                val minY = 0 - (overpasshimgL.height.toInt + 10)
                if ((relativeX > minX && relativeX < stage.width.toInt + 300) && (relativeY > minY && relativeY < stage.height.toInt + 300)) then
                  graphics.drawImage(overpasshimgL, relativeX - wallvimgL.getWidth.toInt, relativeY + 50)
              if (j.hasVecticalOverpass) then
                val diffToPlrX: Int = j.getPosX() - game.player.getPosX
                val diffToPlrY: Int = j.getPosY() - game.player.getPosY
                val relativeXdiff: Int = sizeMultiplier * diffToPlrX
                val relativeYdiff: Int = sizeMultiplier * diffToPlrY
                val relativeX: Int = playerX + relativeXdiff
                val relativeY: Int = playerY + relativeYdiff
                val minX = 0 - (overpassvimgL.width.toInt + 10)
                val minY = 0 - (overpassvimgL.height.toInt + 10)
                if ((relativeX > minX && relativeX < stage.width.toInt + 300) && (relativeY > minY && relativeY < stage.height.toInt + 300)) then
                  graphics.drawImage(overpassvimgL, relativeX + 50, relativeY - floorLimg.getWidth.toInt / 4)

          // Draw player
          if (playerMoving) then
            if (facingRight) then
              if (animationNum < 2) then
                graphics.drawImage(plrMoveRimg, playerXtrue, playerY)
              else if (animationNum < 4) then
                graphics.drawImage(plrMoveRimg1, playerXtrue, playerY)
              else if (animationNum < 6) then
                graphics.drawImage(plrMoveRimg2, playerXtrue, playerY)
              else if (animationNum < 8) then
                graphics.drawImage(plrMoveRimg3, playerXtrue, playerY)
              else if (animationNum < 10) then
                graphics.drawImage(plrMoveRimg4, playerXtrue, playerY)
              else if (animationNum < 12) then
                graphics.drawImage(plrMoveRimg5, playerXtrue, playerY)
              else if (animationNum < 14) then
                graphics.drawImage(plrMoveRimg6, playerXtrue, playerY)
              else if (animationNum < 16) then
                graphics.drawImage(plrMoveRimg7, playerXtrue, playerY)
              else if (animationNum < 18) then
                graphics.drawImage(plrMoveRimg8, playerXtrue, playerY)
              else if (animationNum < 20) then
                graphics.drawImage(plrMoveRimg9, playerXtrue, playerY)
              else if (animationNum < 22) then
                graphics.drawImage(plrMoveRimg10, playerXtrue, playerY)
              else if (animationNum < 24) then
                graphics.drawImage(plrMoveRimg11, playerXtrue, playerY)
              else if (animationNum < 26) then
                graphics.drawImage(plrMoveRimg12, playerXtrue, playerY)
              else if (animationNum < 28) then
                graphics.drawImage(plrMoveRimg13, playerXtrue, playerY)
              else if (animationNum < 30) then
                graphics.drawImage(plrMoveRimg14, playerXtrue, playerY)
            else
              if (animationNum < 2) then
                graphics.drawImage(plrMoveRimgF, playerXtrue, playerY)
              else if (animationNum < 4) then
                graphics.drawImage(plrMoveRimg1F, playerXtrue, playerY)
              else if (animationNum < 6) then
                graphics.drawImage(plrMoveRimg2F, playerXtrue, playerY)
              else if (animationNum < 8) then
                graphics.drawImage(plrMoveRimg3F, playerXtrue, playerY)
              else if (animationNum < 10) then
                graphics.drawImage(plrMoveRimg4F, playerXtrue, playerY)
              else if (animationNum < 12) then
                graphics.drawImage(plrMoveRimg5F, playerXtrue, playerY)
              else if (animationNum < 14) then
                graphics.drawImage(plrMoveRimg6F, playerXtrue, playerY)
              else if (animationNum < 16) then
                graphics.drawImage(plrMoveRimg7F, playerXtrue, playerY)
              else if (animationNum < 18) then
                graphics.drawImage(plrMoveRimg8F, playerXtrue, playerY)
              else if (animationNum < 20) then
                graphics.drawImage(plrMoveRimg9F, playerXtrue, playerY)
              else if (animationNum < 22) then
                graphics.drawImage(plrMoveRimg10F, playerXtrue, playerY)
              else if (animationNum < 24) then
                graphics.drawImage(plrMoveRimg11F, playerXtrue, playerY)
              else if (animationNum < 26) then
                graphics.drawImage(plrMoveRimg12F, playerXtrue, playerY)
              else if (animationNum < 28) then
                graphics.drawImage(plrMoveRimg13F, playerXtrue, playerY)
              else if (animationNum < 30) then
                graphics.drawImage(plrMoveRimg14F, playerXtrue, playerY)
          else
            if (facingRight) then
              if (animationNum < 2) then
                graphics.drawImage(plrimg, playerXtrue, playerY)
              else if (animationNum < 4) then
                graphics.drawImage(plrimg1, playerXtrue, playerY)
              else if (animationNum < 6) then
                graphics.drawImage(plrimg2, playerXtrue, playerY)
              else if (animationNum < 8) then
                graphics.drawImage(plrimg3, playerXtrue, playerY)
              else if (animationNum < 10) then
                graphics.drawImage(plrimg4, playerXtrue, playerY)
              else if (animationNum < 12) then
                graphics.drawImage(plrimg5, playerXtrue, playerY)
              else if (animationNum < 14) then
                graphics.drawImage(plrimg6, playerXtrue, playerY)
              else if (animationNum < 16) then
                graphics.drawImage(plrimg7, playerXtrue, playerY)
              else if (animationNum < 18) then
                graphics.drawImage(plrimg8, playerXtrue, playerY)
              else if (animationNum < 20) then
                graphics.drawImage(plrimg9, playerXtrue, playerY)
              else if (animationNum < 22) then
                graphics.drawImage(plrimg10, playerXtrue, playerY)
              else if (animationNum < 24) then
                graphics.drawImage(plrimg11, playerXtrue, playerY)
              else if (animationNum < 26) then
                graphics.drawImage(plrimg12, playerXtrue, playerY)
              else if (animationNum < 28) then
                graphics.drawImage(plrimg13, playerXtrue, playerY)
              else if (animationNum < 30) then
                graphics.drawImage(plrimg14, playerXtrue, playerY)
            else
              if (animationNum < 2) then
                graphics.drawImage(plrimgF, playerXtrue, playerY)
              else if (animationNum < 4) then
                graphics.drawImage(plrimg1F, playerXtrue, playerY)
              else if (animationNum < 6) then
                graphics.drawImage(plrimg2F, playerXtrue, playerY)
              else if (animationNum < 8) then
                graphics.drawImage(plrimg3F, playerXtrue, playerY)
              else if (animationNum < 10) then
                graphics.drawImage(plrimg4F, playerXtrue, playerY)
              else if (animationNum < 12) then
                graphics.drawImage(plrimg5F, playerXtrue, playerY)
              else if (animationNum < 14) then
                graphics.drawImage(plrimg6F, playerXtrue, playerY)
              else if (animationNum < 16) then
                graphics.drawImage(plrimg7F, playerXtrue, playerY)
              else if (animationNum < 18) then
                graphics.drawImage(plrimg8F, playerXtrue, playerY)
              else if (animationNum < 20) then
                graphics.drawImage(plrimg9F, playerXtrue, playerY)
              else if (animationNum < 22) then
                graphics.drawImage(plrimg10F, playerXtrue, playerY)
              else if (animationNum < 24) then
                graphics.drawImage(plrimg11F, playerXtrue, playerY)
              else if (animationNum < 26) then
                graphics.drawImage(plrimg12F, playerXtrue, playerY)
              else if (animationNum < 28) then
                graphics.drawImage(plrimg13F, playerXtrue, playerY)
              else if (animationNum < 30) then
                graphics.drawImage(plrimg14F, playerXtrue, playerY)

          // Draw overpass again if player is under it
          if (game.player.currentSquare.hasVecticalOverpass && !game.player.onVerticalOverpass) then
            val j = game.player.currentSquare
            val diffToPlrX: Int = j.getPosX() - game.player.getPosX
                val diffToPlrY: Int = j.getPosY() - game.player.getPosY
                val relativeXdiff: Int = sizeMultiplier * diffToPlrX
                val relativeYdiff: Int = sizeMultiplier * diffToPlrY
                val relativeX: Int = playerX + relativeXdiff
                val relativeY: Int = playerY + relativeYdiff
                val minX = 0 - (overpassvimgL.width.toInt + 10)
                val minY = 0 - (overpassvimgL.height.toInt + 10)
                if ((relativeX > minX && relativeX < stage.width.toInt) && (relativeY > minY && relativeY < stage.height.toInt)) then
                  graphics.drawImage(overpassvimgL, relativeX + 50, relativeY - floorLimg.getWidth.toInt / 4)
          if (game.player.currentSquare.hasHorizontalOverpass && !game.player.onHorizontalOverpass) then
            val j = game.player.currentSquare
            val diffToPlrX: Int = j.getPosX()  - game.player.getPosX
                val diffToPlrY: Int = j.getPosY() - game.player.getPosY
                val relativeXdiff: Int = sizeMultiplier * diffToPlrX
                val relativeYdiff: Int = sizeMultiplier * diffToPlrY
                val relativeX: Int = playerX + relativeXdiff
                val relativeY: Int = playerY + relativeYdiff
                val minX = 0 - (overpasshimgL.width.toInt + 10)
                val minY = 0 - (overpasshimgL.height.toInt + 10)
                if ((relativeX > minX && relativeX < stage.width.toInt) && (relativeY > minY && relativeY < stage.height.toInt)) then
                  graphics.drawImage(overpasshimgL, relativeX - wallvimgL.getWidth.toInt, relativeY + 50)
          // HUD
          graphics.fill = White
          graphics.fillRect((stage.width.toInt / 2) - 120, (stage.height.toInt / 2) - (31 * stage.height.toInt / 64), 150, 40)
          graphics.fillRect((stage.width.toInt / 2) + 120, (stage.height.toInt / 2) - (31 * stage.height.toInt / 64), 170, 40)
          graphics.fill = Black
          graphics.font = Font("", 20)
          graphics.fillText("Timer: " + game.level.timer, (stage.width.toInt / 2) - 100, (stage.height.toInt / 2) - (31 * stage.height.toInt / 64) + 30)
          graphics.fillText("Points: " + game.points, (stage.width.toInt / 2) + 130, (stage.height.toInt / 2) - (31 * stage.height.toInt / 64) + 30)
      
        else
          // Solution "zoomed out" draw with whole map
          // Tile draw
          for (i <- game.level.grid) do
            for (j <- i) do
              //graphics.fill = rgb(0, 0, 0, 1.0)
              //graphics.fillRect(j.getPosX(), j.getPosY(), j.getSize(), j.getSize())
              graphics.drawImage(floorimg, j.getPosX() + xOffset, j.getPosY() + yOffset)

          // Vertical wall draw
          for (i <- game.level.wallGridVertical) do
            for (j <- i) do
              if (!j.isBroken) then
                graphics.drawImage(wallvimg, j.getPosX() + xOffset, j.getPosY() + yOffset)

          //Horizontal wall draw
          for (i <- game.level.wallGridHorizontal) do
            for (j <- i) do
              if (!j.isBroken) then
                graphics.drawImage(wallhimg, j.getPosX() + xOffset, j.getPosY() + yOffset)

          // Overpass draw
          for (i <- game.level.grid) do
            for (j <- i) do
              if (j.hasHorizontalOverpass) then
                graphics.drawImage(overpasshimg, (j.getPosX() - j.getSize() / 4) + xOffset, j.getPosY() + yOffset)
              if (j.hasVecticalOverpass) then
                graphics.drawImage(overpassvimg, j.getPosX() + xOffset, (j.getPosY() - j.getSize() / 4) + yOffset)

          if (game.level.showSolution) then
            for (i <- game.level.solution) do
              graphics.fill = Yellow
              graphics.fillOval((i.getPosX() +  3 * i.getSize() / 8) + xOffset, (i.getPosY() + 3 * i.getSize() / 8) + yOffset, i.getSize() / 4, i.getSize() / 4)

          // Player draw
          graphics.drawImage(dplrimg, game.player.getPosX + xOffset, game.player.getPosY + yOffset)

          if (game.player.currentSquare.hasVecticalOverpass && !game.player.onVerticalOverpass) then
            val j = game.player.currentSquare
            graphics.drawImage(overpassvimg, j.getPosX(), y = j.getPosY() - j.getSize() / 4)
          if (game.player.currentSquare.hasHorizontalOverpass && !game.player.onHorizontalOverpass) then
            val j = game.player.currentSquare
            graphics.drawImage(overpasshimg, j.getPosX() - j.getSize() / 4, j.getPosY())

          // HUD
          graphics.fill = rgb(255, 255, 255, 0.7)
          graphics.fillRect((stage.width.toInt / 2) - 200, (stage.height.toInt / 2) + (stage.height.toInt / 4), 500, 40)
          graphics.fill = Black
          graphics.font = Font("", 20)
          graphics.fillText("Move map with WASD-keys. Continue with SPACE", (stage.width.toInt / 2) - 180, (stage.height.toInt / 2) + (stage.height.toInt / 4) + 30)

        // Level win and lose handling
        if (game.level.levelWon) then
          game.generateLevel(game.round)
        if (game.level.levelLost) then
          playerCentered = false
          game.solveLevel()
          gameOn = false
  
  // Method for formatting score correctly to file
  def formatScore(score: String): (Int, String) =
    val splittedString = score.split(" ")
    if splittedString.length == 2 then
      (splittedString.head.toInt, splittedString(1))
    else
      (splittedString.head.toInt, "-")
  
  // Method for setting scene to show scores from scoreboard
  def showScores(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    if (!gameOn) then
      game = new Game(mapSizeX, mapSizeY, roundLength)
    val scores: Seq[String] = game.readFile("scoreboard/scores.txt")
    val scoresFormat: Seq[(Int, String)] = scores.map(formatScore(_))
    val sortedScores = scoresFormat.sortWith(_._1 > _._1)
    var currentY = midPointY - 120
    val scoreScene = new Scene(400, 400) {
      val banner = new ImageView(menuPic)
      banner.layoutX = midPointX - (menuPic.width.toInt / 2)
      banner.layoutY = midPointY - 185 - menuPic.height.toInt
      val label = new Label("Scoreboard")
      label.layoutX = midPointX
      label.layoutY = midPointY - 150
      val exitButton = new Button("Exit")
      exitButton.layoutX = midPointX + 100
      exitButton.layoutY = midPointY - 150
      val scoreBuffer: Buffer[Label] = Buffer[Label]()
      var placement = 1
      for i <- sortedScores do
        val currentScore = placement.toString + ": " + i._1.toString + " " + i._2
        scoreBuffer += new Label(currentScore)
        placement += 1
      for i <- scoreBuffer do
        i.layoutX = midPointX
        i.layoutY = currentY
        currentY += 30
      content = List(label, exitButton, banner) ::: scoreBuffer.toList
      exitButton.onAction = (e:ActionEvent) => {
        showMenu(stage)
      }
    }
    stage.delegate.setScene(scoreScene)
    stage.fullScreen = fullScreenOn
  
  // Method for setting scene to show custom maps.
  def showCustoms(stage: JFXApp3.PrimaryStage): Unit =
    playerCentered = true
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    val dir = new File("levels")
    val levels = dir.listFiles.filter(_.isFile).toList
    var currentY = midPointY - 120
    val customScene = new Scene(400, 400) {
      val banner = new ImageView(menuPic)
      banner.layoutX = midPointX - (menuPic.width.toInt / 2)
      banner.layoutY = midPointY - 185 - menuPic.height.toInt
      val label = new Label("Saved levels:")
      label.layoutX = midPointX
      label.layoutY = midPointY - 150
      val close = new Button("Close")
      close.layoutX = midPointX + 100
      close.layoutY = midPointY - 150
      close.onAction = (e:ActionEvent) => {
        showMenu(stage)
      }
      val buttonBuffer: Buffer[Button] = Buffer[Button]()
      for i <- levels do
        val currentButton = new Button(i.getName.dropRight(4))
        currentButton.layoutX = midPointX
        currentButton.layoutY = currentY
        currentY += 30
        currentButton.onAction = (e:ActionEvent) => {
          game = new Game(mapSizeX, mapSizeY, roundLength)
          val fileName = "levels/" + i.getName
          levelLoaded = true
          game.loadLevel(fileName)
          gameOn = true
          start()
          stage.fullScreen = fullScreenOn
        }
        buttonBuffer += currentButton
      content = List(label, close, banner) ::: buttonBuffer.toList

    }
    stage.delegate.setScene(customScene)
    stage.fullScreen = fullScreenOn
  
  // Method for showing tutorial
  def showHowTo(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    val howToScene = new Scene(400, 400) {
      val banner = new ImageView(menuPic)
      banner.layoutX = midPointX - (menuPic.width.toInt / 2)
      banner.layoutY = midPointY - 185 - menuPic.height.toInt
      val instructions = new Label("Find exit within the time limit. Move with W, A, S, D keys. Give up with Y key.")
      instructions.layoutX = midPointX - 150
      instructions.layoutY = midPointY - 150
      val close = new Button("Close")
      close.layoutX = midPointX
      close.layoutY = midPointY - 120
      content = List(instructions, close, banner)
      close.onAction = (e:ActionEvent) => {
        showMenu(stage)
      }
    }
    stage.delegate.setScene(howToScene)
    stage.fullScreen = fullScreenOn

  // Method for showing options
  def showOptions(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    val optionsScene = new Scene(400, 400) {
      val banner = new ImageView(menuPic)
      banner.layoutX = midPointX - (menuPic.width.toInt / 2)
      banner.layoutY = midPointY - 185 - menuPic.height.toInt
      val currentMap = new Label(s"Current map size: x: ${mapSizeX} y: ${mapSizeY}. Note: map sizes over 100 will cause lag.")
      currentMap.layoutX = midPointX - 250
      currentMap.layoutY = midPointY - 150
      val xLabel = new Label("Change map's width (enter integer):")
      xLabel.layoutX = midPointX - 250
      xLabel.layoutY = midPointY - 120
      val yLabel = new Label("Change map's height (enter integer):")
      yLabel.layoutX = midPointX - 250
      yLabel.layoutY = midPointY - 90
      val close = new Button("Close")
      close.layoutX = midPointX + 250
      close.layoutY = midPointY - 150
      val changeX = new TextField
      changeX.layoutX = midPointX
      changeX.layoutY = midPointY - 120
      val changeY = new TextField
      changeY.layoutX = midPointX
      changeY.layoutY = midPointY - 90
      val fullScreenLabel = new Label("Fullscreen:")
      fullScreenLabel.layoutX = midPointX - 250
      fullScreenLabel.layoutY = midPointY - 60
      val fullToggle = new CheckBox()
      fullToggle.layoutX = midPointX
      fullToggle.layoutY = midPointY - 60
      fullToggle.selected = fullScreenOn
      val currentTimer = new Label(s"Current starting round length: ${roundLength}")
      currentTimer.layoutX = midPointX - 250
      currentTimer.layoutY = midPointY - 30
      val timeLabel = new Label("Change starting round length (enter integer):")
      timeLabel.layoutX = midPointX - 250
      timeLabel.layoutY = midPointY
      val changeT = new TextField
      changeT.layoutX = midPointX
      changeT.layoutY = midPointY
      content = List(currentMap, close, changeX, xLabel, yLabel, changeY, fullScreenLabel, fullToggle, banner, currentTimer, timeLabel, changeT)
      close.onAction = (e:ActionEvent) => {
        showMenu(stage)
      }
      changeX.onAction = (e:ActionEvent) => {
        mapSizeX = changeX.text.apply().toIntOption.getOrElse(20)
        println(s"Map size changed to ${mapSizeX}")
        if (mapSizeX <= 0) then mapSizeX = 20
        showOptions(stage)
      }
      changeY.onAction = (e:ActionEvent) => {
        mapSizeY = changeY.text.apply().toIntOption.getOrElse(20)
        println(s"Map size changed to ${mapSizeX}")
        if (mapSizeX <= 0) then mapSizeX = 20
        showOptions(stage)
      }
      changeT.onAction = (e:ActionEvent) => {
        roundLength = changeT.text.apply().toIntOption.getOrElse(180)
        if (roundLength <= 0) then roundLength = 180
        showOptions(stage)
      }
      fullToggle.onAction = (e:ActionEvent) => {
        fullScreenOn = fullToggle.selected.apply()
        stage.fullScreen = fullScreenOn
      }
    }
    stage.delegate.setScene(optionsScene)
    stage.fullScreen = fullScreenOn
  
  // Method for showing main menu
  def showMenu(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val midPointY = stage.height.toInt / 2
    val menuScene = new Scene(400, 400) {
      val banner = new ImageView(menuPic)
      banner.layoutX = midPointX - (menuPic.width.toInt / 2)
      banner.layoutY = midPointY - 110 - menuPic.height.toInt
      val startGame = new Button("Start game")
      startGame.layoutX = midPointX
      startGame.layoutY = midPointY - 75
      val howTo = new Button("How to?")
      howTo.layoutX = midPointX
      howTo.layoutY = midPointY - 45
      val scoreboard = new Button("Scoreboard")
      scoreboard.layoutX = midPointX
      scoreboard.layoutY = midPointY - 15
      val options = new Button("Options")
      options.layoutX = midPointX
      options.layoutY = midPointY + 15
      val customLevel = new Button("Custom levels")
      customLevel.layoutX = midPointX
      customLevel.layoutY = midPointY + 45
      val exit = new Button("Exit game")
      exit.layoutX = midPointX
      exit.layoutY = midPointY + 75

      content = List(startGame, howTo, scoreboard, options, customLevel, exit, banner)

      startGame.onAction = (e:ActionEvent) => {
        xOffset = 0
        yOffset = 0
        playerCentered = true
        gameOn = true
        gameStarted = true
        initializeGame()
        start()
        stage.fullScreen = fullScreenOn
      }
      exit.onAction = (e:ActionEvent) => {
        sys.exit(0)
      }
      howTo.onAction = (e:ActionEvent) => {
        showHowTo(stage)
      }
      scoreboard.onAction = (e:ActionEvent) => {
        showScores(stage)
      }
      customLevel.onAction = (e:ActionEvent) => {
        showCustoms(stage)
      }
      options.onAction = (e: ActionEvent) => {
        showOptions(stage)
      }
    }
    stage.delegate.setScene(menuScene)
    stage.fullScreen = fullScreenOn

  // Starts the app and loads inputs and menu
  // Checks if game is loaded and loads menu
  override def start(): Unit =

    val gd = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
    stage = new PrimaryStage {
      title = "Labyrinth Game"
      width = gd.getDisplayMode.getWidth()
      height = gd.getDisplayMode.getHeight()
      fullScreen = fullScreenOn
      scene = new Scene {
        val rootPane = new BorderPane
        rootPane.top = new MenuBar {
          menus = List {
            new Menu("Game") {
              items = List (
                new MenuItem("New Game") {
                  onAction = { _ =>
                    playerCentered = true
                    gameOn = true
                    xOffset = 0
                    yOffset = 0
                    initializeGame()
                    timer.start()
                    stage.fullScreen = fullScreenOn
                  }
                },
                new MenuItem("Solve") {
                  onAction = { _ =>
                    playerCentered = false
                    game.level.levelLost = true
                    gameOn = false
                    game.solveLevel()
                  }
                },
                new MenuItem("Save labyrinth") {
                  onAction = { _ =>
                    game.levelLoaded = false
                    gameOn = false
                    saveLevel(stage)
                  }
                },
                new MenuItem("Load labyrinth") {
                  onAction = { _ =>
                    playerCentered = true
                    loadLevel(stage)
                  }
                },
                new MenuItem("Quit") {
                  onAction = { _ =>
                    game.level.timer = game.roundLength
                    gameOn = false
                    showMenu(stage)
                  }
                }
              )
            }
          }
        }
        rootPane.center = new HBox(1) {
          children = new Canvas {
            width <== rootPane.width
            height <== rootPane.height
            graphics = this.graphicsContext2D
          }
        }
        root = rootPane
        onKeyReleased = (key: KeyEvent) =>
          playerMoving = false

        onKeyPressed = (key: KeyEvent) =>
          //println("Recognized key input")
          key.code match
            case KeyCode.W =>
              //println("Up key pressed")
              if (gameOn) then
                playerMoving = true
                game.player.moveUp()
              else
                yOffset -= 3
            case KeyCode.S =>
              if (gameOn) then
                playerMoving = true
                game.player.moveDown()
              else
                yOffset += 3
            case KeyCode.D =>
              if (gameOn) then
                playerMoving = true
                facingRight = true
                game.player.moveRight()
              else
                xOffset += 3
            case KeyCode.A =>
              if (gameOn) then
                playerMoving = true
                facingRight = false
                game.player.moveLeft()
              else
                xOffset -= 3
            case KeyCode.Y =>
              if (gameOn) then
                playerCentered = false
                game.level.levelLost = true
                gameOn = false
                game.solveLevel()
            case KeyCode.Space =>
              if (game.level.levelLost) then
                addScore(stage)
            case _ => println("Unknown input")
      }
      if levelLoaded then
        levelLoaded = false
        timer.start()

    }
    if (gameStarted) then
      gameObjectCreated = true
      gameStarted = false
      timer.start()

    if (!gameOn)
        showMenu(stage)

    if (gameObjectCreated && game.level.levelLost) then
      addScore(stage)
}
