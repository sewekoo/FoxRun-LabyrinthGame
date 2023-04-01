package labyrinthGame
import scala.collection.mutable.Buffer
import scala.util.Random

class Level(mapSizeX: Int, mapSizeY: Int, squareSize: Int, startTime: Int) {

  val sizeX = mapSizeX
  val sizeY = mapSizeY
  val squareLength = squareSize
  val roundLength = startTime
  val grid: Buffer[Buffer[Square]] = Buffer[Buffer[Square]]()
  val wallGridHorizontal: Buffer[Buffer[Wall]] = Buffer[Buffer[Wall]]()
  val wallGridVertical: Buffer[Buffer[Wall]] = Buffer[Buffer[Wall]]()
  var timer = startTime
  var startingPoint: Square = _
  var connected: Buffer[Square] = Buffer[Square]()
  var unconnected: Buffer[Square] = Buffer[Square]()
  var weavable: Buffer[Square] = Buffer[Square]()
  var seed = System.currentTimeMillis()
  var rand = new Random(seed)
  val edges: Buffer[Wall] = Buffer[Wall]()
      val goals = Buffer[Wall]()
  var levelWon = false
  var levelLost = false
  var showSolution = false
  var solution: Buffer[Square] = Buffer[Square]()

  def initalizeGrid(): Unit =
    for(y <- 0 until mapSizeY) do
      grid.insert(y, Buffer[Square]())
      wallGridHorizontal.insert(y, Buffer[Wall]())
      wallGridVertical.insert(y, Buffer[Wall]())
      for(x <- 0 until mapSizeX) do
        //println(s"Generating square at pos x:$x y: $y")
        grid(y).insert(x, new Square(x, y, (x * squareSize) + ((x + 1) * (squareSize / 4)), (y * squareSize) + ((y + 1) * (squareSize / 4)), squareSize))
        wallGridHorizontal(y).insert(x, new Wall(x * (squareSize + (squareSize / 4)), y * (squareSize + (squareSize / 4)), squareSize + (squareSize / 2), squareSize / 4, true))
        if (y == 0) then
          wallGridHorizontal(y)(x).isEdge = true
        wallGridVertical(y).insert(x, new Wall(x * (squareSize + (squareSize / 4)), y * (squareSize + (squareSize / 4)), squareSize + (squareSize / 2), squareSize / 4, false))
        if (x == 0) then
          wallGridVertical(y)(x).isEdge = true

    wallGridHorizontal.insert(mapSizeY, Buffer[Wall]())
    for (x <- 0 until mapSizeX) do
      val y = mapSizeY
      wallGridHorizontal(y).insert(x, new Wall(x * (squareSize + (squareSize / 4)), y * (squareSize + (squareSize / 4)), squareSize + (squareSize / 2), squareSize / 4, true))
      wallGridHorizontal(y)(x).isEdge = true

    for (y <- 0 until mapSizeY) do
      val x = mapSizeX
      wallGridVertical(y).insert(x, new Wall(x * (squareSize + (squareSize / 4)), y * (squareSize + (squareSize / 4)), squareSize + (squareSize / 2), squareSize / 4, false))
      wallGridVertical(y)(x).isEdge = true


  def addNeighboursToUnconnected(square: Square): Unit =
    if(square.getGridPosX() != 0) then
      val leftNeighbour = grid(square.getGridPosY())(square.getGridPosX() - 1)
      if (!leftNeighbour.connected && !leftNeighbour.hasHorizontalOverpass) then
        unconnected.prepend(leftNeighbour)
        square.unconnectedNeighbours.prepend(leftNeighbour)
        if (leftNeighbour.getGridPosX() != 0 && leftNeighbour.getGridPosX() != mapSizeX - 1 && leftNeighbour.getGridPosY() != 0 && leftNeighbour.getGridPosY() != mapSizeY - 1) then
          leftNeighbour.weavable = true

    if(square.getGridPosX() != mapSizeX - 1) then
      val rightNeighbour = grid(square.getGridPosY())(square.getGridPosX() + 1)
      if (!rightNeighbour.connected && !rightNeighbour.hasHorizontalOverpass) then
        unconnected.prepend(rightNeighbour)
        square.unconnectedNeighbours.prepend(rightNeighbour)
        if (rightNeighbour.getGridPosX() != 0 && rightNeighbour.getGridPosX() != mapSizeX - 1 && rightNeighbour.getGridPosY() != 0 && rightNeighbour.getGridPosY() != mapSizeY - 1) then
          rightNeighbour.weavable = true

    if(square.getGridPosY() != 0) then
      val upNeighbour = grid(square.getGridPosY() - 1)(square.getGridPosX())
      if (!upNeighbour.connected && !upNeighbour.hasVecticalOverpass) then
        unconnected.prepend(upNeighbour)
        square.unconnectedNeighbours.prepend(upNeighbour)
        if (upNeighbour.getGridPosX() != 0 && upNeighbour.getGridPosX() != mapSizeX - 1 && upNeighbour.getGridPosY() != 0 && upNeighbour.getGridPosY() != mapSizeY - 1) then
          upNeighbour.weavable = true

    if (square.getGridPosY() != mapSizeY -1) then
      val downNeighbour = grid(square.getGridPosY() + 1)(square.getGridPosX())
      if (!downNeighbour.connected && !downNeighbour.hasVecticalOverpass) then
        unconnected.prepend(downNeighbour)
        square.unconnectedNeighbours.prepend(downNeighbour)
        if (downNeighbour.getGridPosX() != 0 && downNeighbour.getGridPosX() != mapSizeX - 1 && downNeighbour.getGridPosY() != 0 && downNeighbour.getGridPosY() != mapSizeY - 1) then
          downNeighbour.weavable = true


  def pickStartingPosition(): Unit =
    val x = rand.nextInt(mapSizeX)
    val y = rand.nextInt(mapSizeY)
    startingPoint = grid(y)(x)
    //println(s"Picked starting position to be x${x} y${y}")
    connected.prepend(startingPoint)
    startingPoint.connected = true
    addNeighboursToUnconnected(startingPoint)


  def generateLabyrinth(): Unit =
    var currentSquare = connected.head
    while (unconnected.nonEmpty && connected.nonEmpty) do
      if currentSquare.unconnectedNeighbours.nonEmpty then
        val targetSquare = currentSquare.unconnectedNeighbours(rand.nextInt(currentSquare.unconnectedNeighbours.length))
        
        //println(s"Current square: x${currentSquare.getGridPosX()} y${currentSquare.getGridPosY()},  Target: x${targetSquare.getGridPosX()} y${targetSquare.getGridPosY()}")
  
        // Neighbour orientation (from current)
        // 0 = up
        // 1 = right
        // 2 = down
        // 3 = left
        var whichNeighbour = 0
        if (targetSquare.getGridPosX() == currentSquare.getGridPosX() && targetSquare.getGridPosY() < currentSquare.getGridPosY()) then
          whichNeighbour = 0
        if (targetSquare.getGridPosX() > currentSquare.getGridPosX() && targetSquare.getGridPosY() == currentSquare.getGridPosY()) then
          whichNeighbour = 1
        if (targetSquare.getGridPosX() == currentSquare.getGridPosX() && targetSquare.getGridPosY() > currentSquare.getGridPosY()) then
          whichNeighbour = 2
        if (targetSquare.getGridPosX() < currentSquare.getGridPosX() && targetSquare.getGridPosY() == currentSquare.getGridPosY()) then
          whichNeighbour = 3
  
        var nextSquare = targetSquare
  
        if (targetSquare.weavable && !targetSquare.hasVecticalOverpass && !targetSquare.hasHorizontalOverpass) then
          val willItWeave = rand.nextInt(3)
          if (willItWeave == 1) then
            if (whichNeighbour == 0 && !grid(currentSquare.getGridPosY() - 2)(currentSquare.getGridPosX()).connected) then
              //println(s"Added an overpass")
              targetSquare.hasVecticalOverpass = true
              nextSquare = grid(currentSquare.getGridPosY() - 2)(currentSquare.getGridPosX())
              nextSquare.connected = true
              unconnected.remove(0)
            else if (whichNeighbour == 1 && !grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 2).connected) then
              //println(s"Added an overpass")
              targetSquare.hasHorizontalOverpass = true
              nextSquare = grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 2)
              nextSquare.connected = true
              unconnected.remove(0)
            else if (whichNeighbour == 2 && !grid(currentSquare.getGridPosY() + 2)(currentSquare.getGridPosX()).connected) then
              //println(s"Added an overpass")
              targetSquare.hasVecticalOverpass = true
              nextSquare = grid(currentSquare.getGridPosY() + 2)(currentSquare.getGridPosX())
              nextSquare.connected = true
              unconnected.remove(0)
            else if (whichNeighbour == 3 && !grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 2).connected) then
              //println(s"Added an overpass")
              targetSquare.hasHorizontalOverpass = true
              nextSquare = grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 2)
              nextSquare.connected = true
              unconnected.remove(0)
            else
              targetSquare.weavable = false
          else
            targetSquare.weavable = false
  
        else
          if (whichNeighbour == 0 && !targetSquare.connected) then
            wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX()).isBroken = true
            targetSquare.connected = true
            unconnected.remove(0)
          else if (whichNeighbour == 1 && !targetSquare.connected) then
            wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1).isBroken = true
            targetSquare.connected = true
            unconnected.remove(0)
          else if (whichNeighbour == 2 && !targetSquare.connected) then
            wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX()).isBroken = true
            targetSquare.connected = true
            unconnected.remove(0)
          else if (whichNeighbour == 3 && !targetSquare.connected) then
            wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX()).isBroken = true
            targetSquare.connected = true
            unconnected.remove(0)
  
        if (targetSquare.connected) then
          currentSquare.unconnectedNeighbours.remove(currentSquare.unconnectedNeighbours.indexOf(targetSquare))
          currentSquare = nextSquare
          addNeighboursToUnconnected(nextSquare)
          connected.prepend(nextSquare)
        else if (nextSquare.connected) then
          currentSquare.unconnectedNeighbours.remove(currentSquare.unconnectedNeighbours.indexOf(targetSquare))
          currentSquare = nextSquare
          addNeighboursToUnconnected(nextSquare)
          connected.prepend(nextSquare)

      if (currentSquare.unconnectedNeighbours.isEmpty && connected.nonEmpty) then
        connected.remove(0)
        if connected.nonEmpty then
          currentSquare = connected.head

  def pickEdges(): Unit =
    for (y <- wallGridVertical) do
      for (x <- y) do
        if (x.isEdge)
          edges += x
    for (y <- wallGridHorizontal) do
      for (x <- y) do
        if (x.isEdge)
          edges += x

  def pickGoals(amount: Int): Unit =
    for i <- 0 until amount do
      goals += edges(rand.nextInt(edges.length))
    for i <- goals do
      i.isGoal = true
      i.isBroken = true


  def getSquareSize: Int = squareSize

  def solve(): Buffer[Square] =
    var movements: Buffer[Square] = Buffer[Square](startingPoint)
    var intersections: Buffer[Tuple2[Buffer[Square], Buffer[Square]]] = Buffer[Tuple2[Buffer[Square], Buffer[Square]]]()
    val solutions: Buffer[Buffer[Square]] = Buffer[Buffer[Square]]()

    var everythingTried = false

    while (!everythingTried) do
      val currentSquare = movements.head
      //println(s"Trying to solve. Current square: x${currentSquare.getGridPosX()}, y${currentSquare.getGridPosY()}. Solutions atm: ${solution.length}. Movements length: ${movements.length}")
      var connectedNeighbours: Buffer[Square] = Buffer[Square]()

      // Check if can move directly to north or if theres goal
      val northNeighbourWall = wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX())
      if (northNeighbourWall.isBroken && !northNeighbourWall.isEdge) then
        if (!movements.contains(grid(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX()))) then
          connectedNeighbours += grid(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX())
      else if (northNeighbourWall.isGoal) then
        //println("Found a solution")
        solutions.prepend(movements.clone())

      // Check if can move directly to east or if theres goal
      val eastNeighbourWall = wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1)
      if (eastNeighbourWall.isBroken && !eastNeighbourWall.isEdge) then
        if (!movements.contains(grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1))) then
          connectedNeighbours += grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1)
      else if (eastNeighbourWall.isGoal) then
        //println("Found a solution")
        solutions.prepend(movements.clone())

      // Check if can move directly to south or if theres goal
      val southNeighbourWall = wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX())
      if (southNeighbourWall.isBroken && !southNeighbourWall.isEdge) then
        if (!movements.contains(grid(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX()))) then
          connectedNeighbours += grid(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX())
      else if (southNeighbourWall.isGoal) then
          //println("Found a solution")
          solutions.prepend(movements.clone())

      // Check if can move directly to west or if theres goal
      val westNeighbourWall = wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX())
      if (westNeighbourWall.isBroken && !westNeighbourWall.isEdge) then
        if (!movements.contains(grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 1))) then
          connectedNeighbours += grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 1)
      else if (westNeighbourWall.isGoal) then
          //println("Found a solution")
          solutions.prepend(movements.clone())

      // Check if there's overpass to north
      if (!northNeighbourWall.isEdge && grid(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX()).hasVecticalOverpass) then
        if (!movements.contains(grid(currentSquare.getGridPosY() - 2)(currentSquare.getGridPosX()))) then
          connectedNeighbours += grid(currentSquare.getGridPosY() - 2)(currentSquare.getGridPosX())

      // Check if there's overpass to east
      if (!eastNeighbourWall.isEdge && grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1).hasHorizontalOverpass) then
        if (!movements.contains(grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 2))) then
          connectedNeighbours += grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 2)

      // Check if there's overpass to south
      if (!southNeighbourWall.isEdge && grid(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX()).hasVecticalOverpass) then
        if (!movements.contains(grid(currentSquare.getGridPosY() + 2)(currentSquare.getGridPosX()))) then
          connectedNeighbours += grid(currentSquare.getGridPosY() + 2)(currentSquare.getGridPosX())

      // Check if there's overpass to west
      if (!westNeighbourWall.isEdge && grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 1).hasHorizontalOverpass) then
        if (!movements.contains(grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 2))) then
          connectedNeighbours += grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 2)

      // Check if current square counts as intersection and move to next square
      if (connectedNeighbours.length > 1) then
        //println("New intersection")
        val movementsBeforeNext = movements.clone()
        //println(s"Moving to neighbour ${connectedNeighbours.head.getGridPosX()} ${connectedNeighbours.head.getGridPosY()}")
        //println(s"Connected Neighbours length: ${connectedNeighbours.length}")
        movements.prepend(connectedNeighbours.head)
        connectedNeighbours = connectedNeighbours.drop(1).clone()
        intersections = intersections.prepend((movementsBeforeNext, connectedNeighbours))

      // Only one neighbour. Move there
      else if (connectedNeighbours.length == 1) then
        //println("Only one neighbour")
        movements.prepend(connectedNeighbours.head)

      // Backtrack to last intersection
      else
        //println("Dead end")
        // Check that there is a untried intersection left
        if (intersections.nonEmpty) then
          // Check that last intersection has paths that have not been tried already
          if (intersections.head._2.isEmpty) then
            intersections = intersections.drop(1)
          else
            //println(s"Movement length should reset to ${intersections.head._1.length}")
            movements = intersections.head._1
            movements.prepend(intersections.head._2.head)
            intersections.update(0, (intersections.head._1.clone(), intersections.head._2.drop(1)))
        else
          everythingTried = true


    // Calculate the shortest path
    var shortestSolution: Buffer[Square] = solutions.head
    for i <- solutions do
      if (i.length < shortestSolution.length) then
        shortestSolution = i

    //println(s"Shortest solution length: ${shortestSolution.length}")
    shortestSolution

}
