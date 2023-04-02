package labyrinthGame

class Player(level: Level) {
  var posX = level.startingPoint.getPosX() + 3 * level.startingPoint.getSize() / 8
  var posY = level.startingPoint.getPosY() + 3 * level.startingPoint.getSize() / 8

  val size = level.getSquareSize / 4
  var currentSquare: Square = level.startingPoint
  var onVerticalOverpass: Boolean = false
  var onHorizontalOverpass: Boolean = false
  var verticalOverpassStart: Square = _
  var verticalOverpassTarget: Square = _
  var horizontalOverpassStart: Square = _
  var horizontalOverpassTarget: Square = _
  
  def getPosX = posX
  def getPosY = posY
  
  def moveUp(): Unit = if (checkCollision(0)) then posY -= 5
  def moveDown(): Unit = if (checkCollision(2)) then posY += 5
  def moveRight(): Unit = if (checkCollision(1)) then posX += 5
  def moveLeft(): Unit = if (checkCollision(3)) then posX -= 5

  def checkCollision(dir: Int): Boolean =
    var onSquare = false
    var squareResult = true
    for (i <- level.grid) do
      for (j <- i) do
        if (posX >= j.getPosX() && posX <= (j.getPosX() + level.getSquareSize - size) && posY >= j.getPosY() && posY <= (j.getPosY() + level.getSquareSize - size)) then
          currentSquare = j
          onSquare = true
          //println(s"Current square located. Square pos: x:${currentSquare.getGridPosX()} y: ${currentSquare.getGridPosY()}")
          if (dir == 0 && (posY >= currentSquare.getPosY() && posY <= currentSquare.getPosY() + 5)) then
            val upWall = level.wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX())
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
          if (dir == 1 && (posX >= currentSquare.getPosX() + level.getSquareSize - size - 5 && posX <= currentSquare.getPosX() + level.getSquareSize - size)) then
            val rightWall = level.wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1)
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
          if (dir == 2 && (posY >= currentSquare.getPosY() + level.getSquareSize - size - 5 && posY <= currentSquare.getPosY() + level.getSquareSize - size)) then
            val downWall = level.wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX())
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
          if (dir == 3 && (posX >= currentSquare.getPosX() && posX <= currentSquare.getPosX() + 5)) then
            val leftWall = level.wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX())
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

    squareResult
}
