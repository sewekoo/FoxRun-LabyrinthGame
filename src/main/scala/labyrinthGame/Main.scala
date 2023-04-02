package labyrinthGame

import javafx.geometry.Pos
import labyrinthGame.Main.stage
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem, TextField}
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.paint.Color.{Blue, Green, Red, White, Yellow, rgb}
import scalafx.scene.shape.Rectangle
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.image.ImageView
import scalafx.Includes.*
import scalafx.animation.AnimationTimer
import scalafx.scene.text.Font
import scalafx.event.ActionEvent

import java.awt.GraphicsEnvironment
import java.awt.DisplayMode
import java.io.{BufferedWriter, FileNotFoundException, FileWriter, IOException, File}
import scala.collection.mutable.Buffer

object Main extends JFXApp3 {

  var levelLoaded: Boolean = false
  var gameStarted: Boolean = false
  var gameOn: Boolean = false
  var game: Game = _

  def initializeGame(): Unit =
    game = new Game
    game.generateLevel(0)

  def loadLevel(stage: PrimaryStage): Unit =
    game = new Game
    game.levelLoaded = false
    gameOn = false
    var fileName = ""
    val inputScene = new Scene(400, 400) {
      val label = new Label("Type the name of the level file to load")
      label.layoutX = 20
      label.layoutY = 20

      val textField = new TextField
      textField.layoutX = 20
      textField.layoutY = 50
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

  def saveLevel(stage: PrimaryStage): Unit =
    var fileName = ""
    val inputScene = new Scene(400, 400) {
      val label = new Label("Type a name for the level")
      label.layoutX = 20
      label.layoutY = 20

      val textField = new TextField
      textField.layoutX = 20
      textField.layoutY = 50
      content = List(label, textField)

      textField.onAction = (e:ActionEvent) => {
        fileName = "levels/" + textField.text.apply() + ".txt"
        game.saveLevel(fileName)
        start()
      }
    }
    stage.delegate.setScene(inputScene)

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


  def addScore(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val scoreScene = new Scene(400, 400) {
      val label = new Label("Type in your name to save score:")
      label.layoutX = midPointX
      label.layoutY = 20
      val textField = new TextField
      textField.layoutX = 20
      textField.layoutY = 50
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



  def tick(graphics: GraphicsContext, stage: PrimaryStage): Unit =
      if (game.levelLoaded) then
        game.update()
        graphics.fill = rgb(0, 0, 0, 1.0)
        graphics.fillRect(0, 0, game.mapSizeX * (game.squareSize * 1.5), game.mapSizeY * (game.squareSize * 1.5))
        for (i <- game.level.wallGridHorizontal) do
          for (j <- i) do
            if (!j.isBroken) then
              graphics.fill = Red
              graphics.fillRect(j.getPosX(), j.getPosY(), j.getLength(), j.getWidth())
        for (i <- game.level.wallGridVertical) do
          for (j <- i) do
            if (!j.isBroken) then
              graphics.fill = Red
              graphics.fillRect(j.getPosX(), j.getPosY(), j.getWidth(), j.getLength())

        for (i <- game.level.grid) do
          for (j <- i) do
            graphics.fill = rgb(0, 0, 0, 1.0)
            graphics.fillRect(j.getPosX(), j.getPosY(), j.getSize(), j.getSize())
            if (j.hasHorizontalOverpass) then
              graphics.fill = Green
              graphics.fillRect(j.getPosX() - j.getSize() / 4, j.getPosY() + j.getSize() / 4, j.getSize() + j.getSize() / 2, j.getSize() / 2)
            if (j.hasVecticalOverpass) then
              graphics.fill = Green
              graphics.fillRect(x = j.getPosX() + j.getSize() / 4, y = j.getPosY() - j.getSize() / 4, j.getSize() / 2, j.getSize() + j.getSize() / 2)

        if (game.level.showSolution) then
          for (i <- game.level.solution) do
            graphics.fill = Yellow
            graphics.fillRect(i.getPosX() + i.getSize() / 4, i.getPosY() + i.getSize() / 4, i.getSize() / 2, i.getSize() / 2)

        graphics.fill = Blue
        graphics.fillRect(game.player.getPosX, game.player.getPosY, game.player.size, game.player.size)

        if (game.player.currentSquare.hasVecticalOverpass && !game.player.onVerticalOverpass) then
          val j = game.player.currentSquare
          graphics.fill = Green
          graphics.fillRect(x = j.getPosX() + j.getSize() / 4, y = j.getPosY() - j.getSize() / 4, j.getSize() / 2, j.getSize() + j.getSize() / 2)
        if (game.player.currentSquare.hasHorizontalOverpass && !game.player.onHorizontalOverpass) then
          val j = game.player.currentSquare
          graphics.fill = Green
          graphics.fillRect(j.getPosX() - j.getSize() / 4, j.getPosY() + j.getSize() / 4, j.getSize() + j.getSize() / 2, j.getSize() / 2)

        graphics.fill = Blue
        graphics.font = Font("", 20)
        graphics.fillText("Timer: " + game.level.timer, game.mapSizeX * (game.squareSize * 1.5) - game.squareSize * 3, game.squareSize * 2)
        graphics.fillText("Points: " + game.points, game.mapSizeX * (game.squareSize * 1.5) - game.squareSize * 3, game.squareSize * 5)

        if (game.level.levelWon) then
          game.generateLevel(game.round)
        if (game.level.levelLost) then
          start()

  def formatScore(score: String): (Int, String) =
    val splittedString = score.split(" ")
    if splittedString.length == 2 then
      (splittedString.head.toInt, splittedString(1))
    else
      (splittedString.head.toInt, "-")

  def showScores(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    if (!gameOn) then
      game = new Game
    val scores: Seq[String] = game.readFile("scoreboard/scores.txt")
    val scoresFormat: Seq[(Int, String)] = scores.map(formatScore(_))
    val sortedScores = scoresFormat.sortWith(_._1 > _._1)
    var currentY = 50
    val scoreScene = new Scene(400, 400) {
      val label = new Label("Scoreboard")
      label.layoutX = midPointX
      label.layoutY = 20
      val exitButton = new Button("Exit")
      exitButton.layoutX = midPointX + 100
      exitButton.layoutY = 20
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
      content = List(label, exitButton) ::: scoreBuffer.toList
      exitButton.onAction = (e:ActionEvent) => {
        showMenu(stage)
      }
    }
    stage.delegate.setScene(scoreScene)

  def showCustoms(stage: JFXApp3.PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val dir = new File("levels")
    val levels = dir.listFiles.filter(_.isFile).toList
    var currentY = 50
    val customScene = new Scene(400, 400) {
      val label = new Label("Saved levels:")
      label.layoutX = midPointX
      label.layoutY = 20
      val buttonBuffer: Buffer[Button] = Buffer[Button]()
      for i <- levels do
        val currentButton = new Button(i.getName.dropRight(4))
        currentButton.layoutX = midPointX
        currentButton.layoutY = currentY
        currentY += 30
        currentButton.onAction = (e:ActionEvent) => {
          game = new Game
          val fileName = "levels/" + i.getName
          levelLoaded = true
          game.loadLevel(fileName)
          gameOn = true
          start()
        }
        buttonBuffer += currentButton
      content = List(label) ::: buttonBuffer.toList

    }
    stage.delegate.setScene(customScene)

  def showHowTo(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val howToScene = new Scene(400, 400) {
      val instructions = new Label("Find exit within the time limit. Move with W, A, S, D keys.")
      instructions.layoutX = midPointX
      instructions.layoutY = 20
      val close = new Button("Close")
      close.layoutX = midPointX
      close.layoutY = 50
      content = List(instructions, close)
      close.onAction = (e:ActionEvent) => {
        showMenu(stage)
      }
    }
    stage.delegate.setScene(howToScene)

  def showMenu(stage: PrimaryStage): Unit =
    val midPointX = stage.width.toInt / 2
    val menuScene = new Scene(400, 400) {
      val startGame = new Button("Start game")
      startGame.layoutX = midPointX
      startGame.layoutY = 20
      val howTo = new Button("How to?")
      howTo.layoutX = midPointX
      howTo.layoutY = 50
      val scoreboard = new Button("Scoreboard")
      scoreboard.layoutX = midPointX
      scoreboard.layoutY = 80
      val options = new Button("Options")
      options.layoutX = midPointX
      options.layoutY = 110
      val customLevel = new Button("Custom levels")
      customLevel.layoutX = midPointX
      customLevel.layoutY = 140
      val exit = new Button("Exit game")
      exit.layoutX = midPointX
      exit.layoutY = 170

      content = List(startGame, howTo, scoreboard, options, customLevel, exit)

      startGame.onAction = (e:ActionEvent) => {
        gameOn = true
        gameStarted = true
        initializeGame()
        start()
        stage.fullScreen = true
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
      customLevel.onAction= (e:ActionEvent) => {
        showCustoms(stage)
      }
    }
    stage.delegate.setScene(menuScene)


  override def start(): Unit =
    var graphics: GraphicsContext = null

    val second = 1_000_000_000L
    var lastTick = 0L
    var roundTimer = 0L
    val timer: AnimationTimer = AnimationTimer(now => {
      if lastTick == 0L || (now - lastTick > second / 64) then {
        lastTick = now
        tick(graphics, stage)
      }
      if gameOn && (roundTimer == 0L || (now - roundTimer > second)) then {
        roundTimer = now
        game.decreaseTimer()
      }
    })

    val gd = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
    stage = new PrimaryStage {
      title = "Labyrinth Game"
      width = gd.getDisplayMode.getWidth()
      height = gd.getDisplayMode.getHeight()
      scene = new Scene {
        val rootPane = new BorderPane
        rootPane.top = new MenuBar {
          menus = List {
            new Menu("Game") {
              items = List (
                new MenuItem("New Game") {
                  onAction = { _ =>
                    gameOn = true
                    initializeGame()
                    timer.start()
                    stage.fullScreen = true
                  }
                },
                new MenuItem("Solve") {
                  onAction = { _ =>
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
                    loadLevel(stage)
                  }
                },
                new MenuItem("Quit") {
                  onAction = { _ =>
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
        onKeyPressed = (key: KeyEvent) =>
          //println("Recognized key input")
          key.code match
            case KeyCode.W =>
              //println("Up key pressed")
              if (gameOn) then
                game.player.moveUp()
            case KeyCode.S =>
              if (gameOn) then
                game.player.moveDown()
            case KeyCode.D =>
              if (gameOn) then
                game.player.moveRight()
            case KeyCode.A =>
              if (gameOn) then
                game.player.moveLeft()
            case KeyCode.Y =>
              if (gameOn) then
                game.solveLevel()
            case _ => println("Unknown input")
      }
      if levelLoaded then
        levelLoaded = false
        timer.start()

    }
    if (gameStarted) then
      gameStarted = false
      timer.start()

    if (!gameOn)
        showMenu(stage)

    if (gameOn && game.level.levelLost) then
      addScore(stage)
}
