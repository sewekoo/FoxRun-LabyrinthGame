package labyrinthGame

import scala.collection.mutable.Buffer

class Square(gridPosX: Int, gridPosY: Int, posX: Int, posY: Int, size: Int) {
  
  // Squares coordinates
  val pos: Tuple2[Int, Int] = (posX, posY)
  
  // Is the square connected to labyrinth
  var connected: Boolean = false
  
  // Does square has vertical overpass
  var hasVecticalOverpass: Boolean = false
  
  var hasHorizontalOverpass: Boolean = false
  
  // Can an overpass be added
  var weavable: Boolean = false
  
  var unconnectedNeighbours: Buffer[Square] = Buffer[Square]()
  
  
  // Accessors
  def getPosX(): Int = this.pos._1
  
  def getPosY(): Int = this.pos._2
  
  def getSize(): Int = this.size
  
  def getGridPosX(): Int = this.gridPosX
  
  def getGridPosY(): Int = this.gridPosY
}
