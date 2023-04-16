package labyrinthGame

class Player(level: Level) {
  var posX = level.startingPoint.getPosX() + 3 * level.startingPoint.getSize() / 8
  var posY = level.startingPoint.getPosY() + 3 * level.startingPoint.getSize() / 8

  val size = level.getSquareSize / 3
  var currentSquare: Square = level.startingPoint
  var currentWall: Wall = _
  var onVerticalOverpass: Boolean = false
  var onHorizontalOverpass: Boolean = false
  var verticalOverpassStart: Square = _
  var verticalOverpassTarget: Square = _
  var horizontalOverpassStart: Square = _
  var horizontalOverpassTarget: Square = _
  val startX = posX
  val startY = posY
  
  def getPosX = posX
  def getPosY = posY
  
  def resetPos() =
    posX = startX
    posY = startY
  
  def moveUp(): Unit = if (checkCollision(0)) then posY -= 4
  def moveDown(): Unit = if (checkCollision(2)) then posY += 4
  def moveRight(): Unit = if (checkCollision(1)) then posX += 4
  def moveLeft(): Unit = if (checkCollision(3)) then posX -= 4

  def checkCollision(dir: Int): Boolean =
    var onSquare = false
    var squareResult = true
    val leftWall = level.wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX())
    val downWall = level.wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX())
    val rightWall = level.wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1)
    val upWall = level.wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX())
    for (i <- level.grid) do
      for (j <- i) do
        if (posX >= j.getPosX() && posX <= (j.getPosX() + level.getSquareSize - size + 5) && posY >= j.getPosY() - 7 && posY <= (j.getPosY() + level.getSquareSize - size + 5)) then
          currentSquare = j
          onSquare = true
          println(s"Current square located. Square pos: x:${currentSquare.getGridPosX()} y: ${currentSquare.getGridPosY()}")
          if (dir == 0 && (posY >= currentSquare.getPosY() - 7 && posY <= currentSquare.getPosY() - 2)) then

            if (upWall.isBroken) then
              squareResult = true
            else if (!upWall.isEdge && level.grid(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX()).hasVecticalOverpass) then
              println("Player placed on vertical overpass")
              squareResult = true
              onVerticalOverpass = true
              verticalOverpassStart = currentSquare
              verticalOverpassTarget = level.grid(currentSquare.getGridPosY() - 2)(currentSquare.getGridPosX())
            else
              squareResult = false
          if (dir == 1 && (posX >= currentSquare.getPosX() + level.getSquareSize - size - 2 && posX <= currentSquare.getPosX() + level.getSquareSize - size + 5)) then

            if (rightWall.isBroken) then
              squareResult = true
            else if (!rightWall.isEdge && level.grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1).hasHorizontalOverpass) then
              println("Player placed on horizontal overpass")
              squareResult = true
              onHorizontalOverpass = true
              horizontalOverpassStart = currentSquare
              horizontalOverpassTarget = level.grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 2)
            else
              squareResult = false
          if (dir == 2 && (posY >= currentSquare.getPosY() + level.getSquareSize - size - 2 && posY <= currentSquare.getPosY() + level.getSquareSize - size + 5)) then

            if (downWall.isBroken) then
              squareResult = true
            else if (!downWall.isEdge && level.grid(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX()).hasVecticalOverpass) then
              println("Player placed on vertical overpass")
              squareResult = true
              onVerticalOverpass = true
              verticalOverpassStart = currentSquare
              verticalOverpassTarget = level.grid(currentSquare.getGridPosY() + 2)(currentSquare.getGridPosX())
            else
              squareResult = false
          if (dir == 3 && (posX >= currentSquare.getPosX() && posX <= currentSquare.getPosX() + 6)) then
            if (leftWall.isBroken) then
              squareResult = true
            else if (!leftWall.isEdge && level.grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 1).hasHorizontalOverpass) then
              println("Player placed on horizontal overpass")
              squareResult = true
              onHorizontalOverpass = true
              horizontalOverpassStart = currentSquare
              horizontalOverpassTarget = level.grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 2)
            else
              squareResult = false

    if (onVerticalOverpass) then
      if (dir == 0 || dir == 2) then
        squareResult = true
        if (currentSquare == verticalOverpassTarget) then
          onVerticalOverpass = false
          println("Player taken off vertical overpass. Reached target.")
        else if (dir == 2 && currentSquare == verticalOverpassStart && posY > currentSquare.getPosY() + 5 && posY <= currentSquare.getPosY() + 10) then
          onVerticalOverpass = false
          println("Player taken off vertical overpass. Return to original. Movement down")
        else if (dir == 0 && currentSquare == verticalOverpassStart && posY < currentSquare.getPosY() + level.getSquareSize - size - 5 && posY >= currentSquare.getPosY() + level.getSquareSize - size - 10) then
          onVerticalOverpass = false
          println("Player taken off vertical overpass. Return to original. Movement up")
      else
        squareResult = false

    if (onHorizontalOverpass) then
      if (dir == 1 || dir == 3) then
        squareResult = true
        if (currentSquare == horizontalOverpassTarget) then
          onHorizontalOverpass = false
          println("Player taken off horizontal overpass. Reached target.")
        else if (dir == 1 && currentSquare == horizontalOverpassStart && posX > currentSquare.getPosX() + 5 && posX <= currentSquare.getPosX() + 10) then
          onHorizontalOverpass = false
          println("Player taken off horizontal overpass. Return to original. Movement right.")
        else if (dir == 3 && currentSquare == horizontalOverpassStart && posX < currentSquare.getPosX() + level.getSquareSize - size - 5 && posX >= currentSquare.getPosX() + level.getSquareSize - size - 10) then
          onHorizontalOverpass = false
          println("Player taken off horizontal overpass. Return to original. Movement left.")
      else
        squareResult = false

    if (!onSquare) then
      if (posX < currentSquare.getPosX()) then currentWall = leftWall
      else if (posX > (currentSquare.getPosX() + level.getSquareSize - size)) then currentWall = rightWall
      else if (posY < currentSquare.getPosY()) then
        println("Current wall set to up wall")
        println(posX >= currentSquare.getPosX() + level.getSquareSize - size - 5)
        println(posX <= currentSquare.getPosX() + level.getSquareSize - size + 10)
        println(dir == 1)
        currentWall = upWall
      else if (posY > (currentSquare.getPosY() + level.getSquareSize - size)) then currentWall = downWall
      if (!currentWall.isGoal) then
        if (dir == 0 && (posY >= currentSquare.getPosY() - 7 && posY <= currentSquare.getPosY() - 2)) then
          if (currentWall == leftWall) then
            if (!upWall.isBroken) then squareResult = false
            else if (!upWall.isEdge) then
              val leftWallOfUp = level.wallGridVertical(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX())
              if (!leftWallOfUp.isBroken) then squareResult = false
            else if(!leftWall.isEdge) then
              val upWallOfLeft = level.wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 1)
              if (!upWallOfLeft.isBroken) then squareResult = false
            else squareResult = true
          else if (currentWall == rightWall) then
            if (!upWall.isBroken) then squareResult = false
            else if (!upWall.isEdge) then
              val rightWallOfUp = level.wallGridVertical(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX() + 1)
              if (!rightWallOfUp.isBroken) then squareResult = false
            else if (!rightWall.isEdge) then
              val upWallOfRight = level.wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1)
              if (!upWallOfRight.isBroken) then squareResult = false
            else squareResult = true
          else squareResult = true
        if (dir == 1 && (posX >= currentSquare.getPosX() + level.getSquareSize - size - 2 && posX <= currentSquare.getPosX() + level.getSquareSize - size + 5)) then
          if (currentWall == upWall) then
            println("Inside up wall")
            if (!rightWall.isBroken) then squareResult = false
            else if (!rightWall.isEdge) then
              val upWallOfRight = level.wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1)
              println("Up wall of right is not broken")
              if (!upWallOfRight.isBroken) then squareResult = false
            else if(!upWall.isEdge) then
              val rightWallOfUp = level.wallGridVertical(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX() + 1)
              if (!rightWallOfUp.isBroken) then squareResult = false
              else squareResult = true
          else if (currentWall == downWall) then
            if (!rightWall.isBroken) then squareResult = false
            else if (!rightWall.isEdge) then
              val downWallOfRight = level.wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX() + 1)
              if (!downWallOfRight.isBroken) then squareResult = false
            else if(!upWall.isEdge) then
              val rightWallOfDown = level.wallGridVertical(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX() + 1)
              if (!rightWallOfDown.isBroken) then squareResult = false
            else squareResult = true
          else squareResult = true
        if (dir == 2 && (posY >= currentSquare.getPosY() + level.getSquareSize - size - 5 && posY <= currentSquare.getPosY() + level.getSquareSize - size)) then
          if (currentWall == leftWall) then
            if (!downWall.isBroken) then squareResult = false
            else if (!downWall.isEdge) then
              val leftWallOfDown = level.wallGridVertical(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX())
              if (!leftWallOfDown.isBroken) then squareResult = false
            else if(!leftWall.isEdge) then
              val downWallOfLeft = level.wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX() - 1)
              if (!downWallOfLeft.isBroken) then squareResult = false
            else squareResult = true
          else if (currentWall == rightWall) then
            if (!downWall.isBroken) then squareResult = false
            else if (!downWall.isEdge) then
              val rightWallOfDown = level.wallGridVertical(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX() + 1)
              if (!rightWallOfDown.isBroken) then squareResult = false
            else if (!rightWall.isEdge) then
              val downWallOfRight = level.wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX() + 1)
              if (!downWallOfRight.isBroken) then squareResult = false
            else squareResult = true
          else squareResult = true
        if (dir == 3 && (posX >= currentSquare.getPosX() && posX <= currentSquare.getPosX() + 6)) then
          if (currentWall == upWall) then
            if (!leftWall.isBroken) then squareResult = false
            else if (!leftWall.isEdge) then
              val upWallOfLeft = level.wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 1)
              if (!upWallOfLeft.isBroken) then squareResult = false
            else if(!upWall.isEdge) then
              val leftWallOfUp = level.wallGridVertical(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX())
              if (!leftWallOfUp.isBroken) then squareResult = false
              else squareResult = true
          else if (currentWall == downWall) then
            if (!leftWall.isBroken) then squareResult = false
            else if (!leftWall.isEdge) then
              val downWallOfLeft = level.wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX() - 1)
              if (!downWallOfLeft.isBroken) then squareResult = false
            else if(!upWall.isEdge) then
              val leftWallOfDown = level.wallGridVertical(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX())
              if (!leftWallOfDown.isBroken) then squareResult = false
            else squareResult = true
          else squareResult = true


    squareResult
}
