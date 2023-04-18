import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.mutable.Buffer
import labyrinthGame._

class Tests extends AnyFlatSpec with Matchers {
  "Game" should "generate labyrinth succesfully." in {
    val game = new Game(20, 20, 180)
    assume(game.mapSizeX === 20)
    assume(game.mapSizeY === 20)
    assume(game.roundLength === 180)

    val map: Buffer[Buffer[Square]] = game.generateLevel(0)
    assert(game.levelLoaded === true)
  }
  "Game" should "generate a labyrinth with squares." in {
    val game = new Game(20, 20, 180)
    assume(game.mapSizeX === 20)
    assume(game.mapSizeY === 20)
    assume(game.roundLength === 180)

    val map: Buffer[Buffer[Square]] = game.generateLevel(0)
    assert(game.level.grid.nonEmpty)
  }
  "Game" should "be able to generate a solution." in {
    val game = new Game(20, 20, 180)
    assume(game.mapSizeX === 20)
    assume(game.mapSizeY === 20)
    assume(game.roundLength === 180)

    val map: Buffer[Buffer[Square]] = game.generateLevel(0)
    game.solveLevel()
    assert(game.level.solution.nonEmpty)
  }
  "Wall" should "recognize that it's on the edge." in {
    val game = new Game(20, 20, 180)
    assume(game.mapSizeX === 20)
    assume(game.mapSizeY === 20)
    assume(game.roundLength === 180)

    val map: Buffer[Buffer[Square]] = game.generateLevel(0)
    assert(game.level.wallGridHorizontal(0)(0).isEdge)
  }
}
