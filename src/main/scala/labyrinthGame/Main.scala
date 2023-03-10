package labyrinthGame

import javafx.geometry.Pos
import labyrinthGame.Main.stage
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color.{Blue, Green, Red, rgb}
import scalafx.scene.shape.Rectangle

object Main extends JFXApp3 {

  var gameOn: Boolean = false
  var game: Game = _

  def initializeGame(): Unit =
    game = new Game
    game.generateLevel(0)

  def start(): Unit =
    stage = new JFXApp3.PrimaryStage :
      title = "Labyrinth Game"
      width = 1900
      height = 1080

    val root = Pane() // Simple pane component
    val scene = Scene(parent = root) // Scene acts as a container for the scene graph
    stage.scene = scene // Assigning the new scene as the current scene for the stage

    val button = Button("Generate labyrinth")
    button.setLayoutX(1500)
    button.setLayoutY(1000)
    button.onAction = (event) =>
      gameOn = true
      initializeGame()
      drawGrid()
    root.children += button //Needs scalafx.Includes._ import

    def drawGrid() =
      val backgroundColor = new Rectangle :
        x = 0
        y = 0
        width = 1500
        height = 1000
      root.children += backgroundColor

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






    def drawSquare() =
        val rectangle = new Rectangle :
          x = 100
          y = 100
          width = 50
          height = 50
          fill = Blue //scalafx.scene.paint.Color

        root.children += rectangle




}
