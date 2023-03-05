package labyrinthGame

import scala.collection.mutable.Buffer

class Square(gridPosX: Int, gridPosY: Int, posX: Int, posY: Int, size: Int) {
  
  val pos: Tuple2[Int, Int] = (posX, posY)
  
  var connected: Boolean = false
  
  var hasVecticalOverpass: Boolean = false
  
  var hasHorizontalOverpass: Boolean = false
  
  var weavable: Boolean = false
  
  var unconnectedNeighbours: Buffer[Square] = Buffer[Square]()
  
  
  // Accessors
  def getPosX(): Int = this.pos._1
  
  def getPosY(): Int = this.pos._2
  
  def getSize(): Int = this.size
  
  def getGridPosX(): Int = this.gridPosX
  
  def getGridPosY(): Int = this.gridPosY
}
