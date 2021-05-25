import scala.collection.immutable.ArraySeq
import $file.Common, Common._ 


def run(jsonResponse: Map[String, String]): Unit = {
  val currencies = getCurrencies(jsonResponse).toSeq
  val edges = parseEdges(jsonResponse, currencies)
  val graph = Graph(currencies.size, edges)
  
  for {
    currency <- currencies
  } {
    val index = currencies.indexOf(currency)
    val result = runBellmanFord(graph, index)
    val cycles = findCycles(graph, result).map(toEdges(_, edges))
    printResults(result.dist, currency, currencies, cycles.toList)
  }  
}

case class Graph(v: Int, edges: List[Edge])

case class BFState(dist: ArraySeq[Double], edgeTo: Map[Int, Edge]){

  def relax(e: Edge): BFState = {
    if(shouldRelax(e)) {
      val newWeight = dist(e.from) + e.weight
      BFState(dist.updated(e.to, newWeight), edgeTo + (e.to -> e))
    } else this
  }

  def shouldRelax(e: Edge): Boolean = dist(e.from) + e.weight < dist(e.to)
}

object BFState {
  def init(g: Graph, s: Int): BFState = {
    val dist = ArraySeq.fill[Double](g.v)(Double.PositiveInfinity).updated(s, 0.0)
    val edgeTo = Map[Int, Edge]()
    BFState(dist, edgeTo)
  }
}

def runBellmanFord(g: Graph, s: Int): BFState = {
  (0 to g.v -1).foldLeft[BFState](BFState.init(g, s)) { case (acum, _) =>
    g.edges.foldLeft[BFState](acum){ case (state, edge) => state.relax(edge)}
  }
}

def findCycles(g: Graph, state: BFState): Set[List[Int]] = {
  val seen = Array.fill[Boolean](g.v)(false)
  g.edges.foldLeft(List[List[Int]]()){ case (acum, edge) =>
    if (!seen(edge.to) && state.shouldRelax(edge)) {
      val (cycle, index) = findCycle(edge, state, seen)
      cycle.drop(index).reverse +: acum 
    } else acum
  }.toSet
}

def findCycle(edge: Edge, state: BFState, seen: Array[Boolean]): (List[Int], Int) = {
  var cycle: List[Int] = List()
  var pivot = edge.to
  var cycleFound = false
  while(!cycleFound){
    seen(pivot) = true
    cycle = cycle.appended(pivot)
    pivot = state.edgeTo(pivot).from
    if(pivot == edge.to || cycle.contains(pivot)) {
      cycleFound = true
    } 
  }
  val index = cycle.indexOf(pivot)
  (cycle.appended(pivot), index)
}

def toEdges(cycle: List[Int], edges: List[Edge]): List[Edge] = {
  cycle.drop(1)
    .zip(cycle.dropRight(1))
    .flatMap{ case (from, to) => edges.find(e => e.from == from && e.to == to)}
}