package labyrinthGame

class Wall(posX: Int, posY: Int, length: Int, width: Int, horizontal: Boolean) {
  var isEdge = false
  var isBroken = false
  var isGoal = false
  
  // Accessors:
  
  def getPosX(): Int = this.posX
  
  def getPosY(): Int = this.posY
  
  def getLength(): Int = this.length
  
  def getWidth(): Int = this.width
  
  def isHorizontal: Boolean = this.horizontal
}
