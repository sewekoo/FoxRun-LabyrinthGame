package labyrinthGame

class Player(level: Level) {
  var posX = level.startingPoint.getPosX() + 3 * level.startingPoint.getSize() / 8
  var posY = level.startingPoint.getPosY() + 3 * level.startingPoint.getSize() / 8
  
  def getPosX = posX
  def getPosY = posY
  
  def moveUp(): Unit = posY -= 5
  def moveDown(): Unit = posY += 5
  def moveRight(): Unit = posX += 5
  def moveLeft(): Unit = posX -= 5
}
