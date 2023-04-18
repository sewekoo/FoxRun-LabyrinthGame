package labyrinthGame

class Player(level: Level) {
  // Current x position
  var posX = level.startingPoint.getPosX() + 3 * level.startingPoint.getSize() / 8
  // Current y position
  var posY = level.startingPoint.getPosY() + 3 * level.startingPoint.getSize() / 8
  
  // Size for player
  val size = level.getSquareSize / 3
  // Current square player is occupying
  var currentSquare: Square = level.startingPoint
  // Current invisible wall player is on (if between squares)
  var currentWall: Wall = _
  // Player is travelling on vertical overpass
  var onVerticalOverpass: Boolean = false
  // Player is travelling on horizontal overpass
  var onHorizontalOverpass: Boolean = false
  // Square that overpass started from to check if player returned
  var verticalOverpassStart: Square = _
  // Square thats the target to check whether player crossed the overpass
  var verticalOverpassTarget: Square = _
  // Square that overpass started from to check if player returned
  var horizontalOverpassStart: Square = _
  // Square thats the target to check whether player crossed the overpass
  var horizontalOverpassTarget: Square = _
  // Starting x coordinate
  val startX = posX
  // Starting y coordinat
  val startY = posY
  
  // Getter methods for position
  def getPosX = posX
  def getPosY = posY
  
  // Resets player to starting square
  def resetPos() =
    posX = startX
    posY = startY
  
  // Methods to move player
  def moveUp(): Unit = if (checkCollision(0)) then posY -= 4
  def moveDown(): Unit = if (checkCollision(2)) then posY += 4
  def moveRight(): Unit = if (checkCollision(1)) then posX += 4
  def moveLeft(): Unit = if (checkCollision(3)) then posX -= 4
  
  // Method for collision checking
  def checkCollision(dir: Int): Boolean =
    var onSquare = false
    var squareResult = true
    
    // Each wall of current square
    val leftWall = level.wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX())
    val downWall = level.wallGridHorizontal(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX())
    val rightWall = level.wallGridVertical(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1)
    val upWall = level.wallGridHorizontal(currentSquare.getGridPosY())(currentSquare.getGridPosX())
    // First check if player is on some square and if so update current square
    for (i <- level.grid) do
      for (j <- i) do
        if (posX >= j.getPosX() && posX <= (j.getPosX() + level.getSquareSize - size + 5) && posY >= j.getPosY() - 7 && posY <= (j.getPosY() + level.getSquareSize - size + 5)) then
          currentSquare = j
          onSquare = true
          // Check if player is going up and is close to wall. Then check if that wall is broken
          if (dir == 0 && (posY >= currentSquare.getPosY() - 7 && posY <= currentSquare.getPosY() - 2)) then
            if (upWall.isBroken) then
              squareResult = true
            else if (!upWall.isEdge && level.grid(currentSquare.getGridPosY() - 1)(currentSquare.getGridPosX()).hasVecticalOverpass) then
              // Sets player on vertical overpass to disable sideways movement
              squareResult = true
              onVerticalOverpass = true
              verticalOverpassStart = currentSquare
              verticalOverpassTarget = level.grid(currentSquare.getGridPosY() - 2)(currentSquare.getGridPosX())
            else
              squareResult = false
          // Check if player is going right and is close to wall. Then check if that wall is broken
          if (dir == 1 && (posX >= currentSquare.getPosX() + level.getSquareSize - size - 2 && posX <= currentSquare.getPosX() + level.getSquareSize - size + 5)) then
            if (rightWall.isBroken) then
              squareResult = true
            else if (!rightWall.isEdge && level.grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 1).hasHorizontalOverpass) then
              // If entering horizontal overpass, sets flags accordingly to disable up/down movement.
              squareResult = true
              onHorizontalOverpass = true
              horizontalOverpassStart = currentSquare
              horizontalOverpassTarget = level.grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() + 2)
            else
              squareResult = false
          // Check if player is going down and there is a wall
          if (dir == 2 && (posY >= currentSquare.getPosY() + level.getSquareSize - size - 2 && posY <= currentSquare.getPosY() + level.getSquareSize - size + 5)) then
            if (downWall.isBroken) then
              squareResult = true
            else if (!downWall.isEdge && level.grid(currentSquare.getGridPosY() + 1)(currentSquare.getGridPosX()).hasVecticalOverpass) then
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
              squareResult = true
              onHorizontalOverpass = true
              horizontalOverpassStart = currentSquare
              horizontalOverpassTarget = level.grid(currentSquare.getGridPosY())(currentSquare.getGridPosX() - 2)
            else
              squareResult = false
    
    // If player is on vertical overpass, check if reached target or returned to allow sideways movement.
    if (onVerticalOverpass) then
      if (dir == 0 || dir == 2) then
        squareResult = true
        if (currentSquare == verticalOverpassTarget) then
          onVerticalOverpass = false
        else if (dir == 2 && currentSquare == verticalOverpassStart && posY > currentSquare.getPosY() + 5 && posY <= currentSquare.getPosY() + 10) then
          onVerticalOverpass = false
        else if (dir == 0 && currentSquare == verticalOverpassStart && posY < currentSquare.getPosY() + level.getSquareSize - size - 5 && posY >= currentSquare.getPosY() + level.getSquareSize - size - 10) then
          onVerticalOverpass = false
      else
        squareResult = false
    
    // If player is on horizontal overpass, check if reached target or returned to original square.
    if (onHorizontalOverpass) then
      if (dir == 1 || dir == 3) then
        squareResult = true
        if (currentSquare == horizontalOverpassTarget) then
          onHorizontalOverpass = false
        else if (dir == 1 && currentSquare == horizontalOverpassStart && posX > currentSquare.getPosX() + 5 && posX <= currentSquare.getPosX() + 10) then
          onHorizontalOverpass = false
        else if (dir == 3 && currentSquare == horizontalOverpassStart && posX < currentSquare.getPosX() + level.getSquareSize - size - 5 && posX >= currentSquare.getPosX() + level.getSquareSize - size - 10) then
          onHorizontalOverpass = false
      else
        squareResult = false
        
    // If player is not on square then must be between squares on a broken wall.
    // Check on which wall player is and then disable to pass walls on left/right or up/down
    if (!onSquare) then
      if (posX < currentSquare.getPosX()) then currentWall = leftWall
      else if (posX > (currentSquare.getPosX() + level.getSquareSize - size)) then currentWall = rightWall
      else if (posY < currentSquare.getPosY()) then
        currentWall = upWall
      else if (posY > (currentSquare.getPosY() + level.getSquareSize - size)) then currentWall = downWall
      if (!currentWall.isGoal) then
        if (dir == 0 && (posY >= currentSquare.getPosY() - 7 && posY <= currentSquare.getPosY() - 2)) then
          if (currentWall == leftWall) then
            squareResult = false
          else if (currentWall == rightWall) then
            squareResult = false
        if (dir == 1 && (posX >= currentSquare.getPosX() + level.getSquareSize - size - 3 && posX <= currentSquare.getPosX() + level.getSquareSize - size + 10)) then
          if (currentWall == upWall) then
            squareResult = false
          else if (currentWall == downWall) then
            squareResult = false
        if (dir == 2 && (posY >= currentSquare.getPosY() + level.getSquareSize - size - 2 && posY <= currentSquare.getPosY() + level.getSquareSize - size + 5)) then
          if (currentWall == leftWall) then
            squareResult = false
          else if (currentWall == rightWall) then
            squareResult = false
        if (dir == 3 && (posX >= currentSquare.getPosX() && posX <= currentSquare.getPosX() + 6)) then
          if (currentWall == upWall) then
            squareResult = false
          else if (currentWall == downWall) then
            squareResult = false
            
    squareResult
}
