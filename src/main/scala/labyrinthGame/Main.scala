package labyrinthGame

import javafx.geometry.Pos
import labyrinthGame.Main.stage
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.paint.Color.{Blue, Green, Red, rgb}
import scalafx.scene.shape.Rectangle
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.image.ImageView
import scalafx.Includes.*
import scalafx.animation.AnimationTimer

object Main extends JFXApp3 {

  var gameOn: Boolean = false
  var game: Game = _

  def initializeGame(): Unit =
    game = new Game
    game.generateLevel(0)

  def tick(graphics: GraphicsContext): Unit =
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

        graphics.fill = Blue
        graphics.fillRect(game.player.getPosX, game.player.getPosY, game.player.size, game.player.size)

        if (game.level.levelWon) then
          game.generateLevel(game.round)




  override def start(): Unit =
    var graphics: GraphicsContext = null

    val second = 1_000_000_000L
    var lastTick = 0L
    val timer: AnimationTimer = AnimationTimer(now => {
      if lastTick == 0L || (now - lastTick > second / 64) then {
        lastTick = now
        tick(graphics)
      }
    })

    stage = new PrimaryStage {
      title = "Labyrinth Game"
      width = 1900
      height = 1080
      scene = new Scene {
        val rootPane = new BorderPane
        rootPane.top = new MenuBar {
          menus = List {
            new Menu("Game") {
              items = List {
                new MenuItem("Start game") {
                  onAction = { _ =>
                    gameOn = true
                    initializeGame()
                    timer.start()
                  }
                }
              }
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
          println("Recognized key input")
          key.code match
            case KeyCode.W =>
              println("Up key pressed")
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
            case _ => println("Unknown input")
      }
    }

    //val root = Pane() // Simple pane component
    //val scene = Scene(parent = root) // Scene acts as a container for the scene graph
    //stage.scene = scene // Assigning the new scene as the current scene for the stage

/**
    def drawGrid() =


      for (i <- game.level.wallGridHorizontal) do
        for (j <- i) do
          val wall = new Rectangle :
            x = j.getPosX()
            y = j.getPosY()
            width = j.getLength()
            height = j.getWidth()
            if (!j.isBroken) then
              fill = Red
            else
              fill = rgb(0, 0, 0, 0)
          root.children += wall

      for (i <- game.level.wallGridVertical) do
        for (j <- i) do
          val wall = new Rectangle :
            x = j.getPosX()
            y = j.getPosY()
            width = j.getWidth()
            height = j.getLength()
            if (!j.isBroken) then
              fill = Red
            else
              fill = rgb(0, 0, 0, 0)
          root.children += wall

      for (i <- game.level.grid) do
        for (j <- i) do
          val square = new Rectangle :
            x = j.getPosX()
            y = j.getPosY()
            width = j.getSize()
            height = j.getSize()
          root.children += square

          if (j.hasHorizontalOverpass) then
            val overpass = new Rectangle :
              x = j.getPosX() - j.getSize() / 4
              y = j.getPosY() + j.getSize() / 4
              width = j.getSize() + j.getSize() / 2
              height = j.getSize() / 2
              fill = Green
            root.children += overpass
          if (j.hasVecticalOverpass) then
            val overpass = new Rectangle :
              x = j.getPosX() + j.getSize() / 4
              y = j.getPosY() - j.getSize() / 4
              width = j.getSize() / 2
              height = j.getSize() + j.getSize() / 2
              fill = Green
            root.children += overpass
      player.x = game.player.getPosX
      player.y = game.player.getPosY
      player.width = game.player.size
      player.height = game.player.size
      player.fill = Blue
      root.children += player
*/



}
