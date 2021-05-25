import scala.annotation.tailrec
import scala.collection.mutable.{ListBuffer, Queue}
import $file.Common, Common._ 


def run(jsonResponse: Map[String, String]): Unit = {
  val currencies = getCurrencies(jsonResponse).toSeq
  val edges = parseEdges(jsonResponse, currencies)
  val g = new Digraph(currencies.size)
  edges.foreach(g.addEdge)

  for {
    currency <- currencies
    val spt = new BellmanFordSP(g, currencies.indexOf(currency))
    cycle <- spt.negativeCycle.toSeq
  } yield {
    printResults(spt.dist, currency, currencies, List(cycle))
  }
}

class BellmanFordSP(g: Digraph, s: Int) {
  val distances = Array.fill[Double](g.numV)(Double.PositiveInfinity)
  val edgeTo = new Array[Edge](g.numV)
  val onQueue = Array.fill[Boolean](g.numV)(false)
  val queue = new Queue[Int]()
  var cost = 0
  var cycle: Option[List[Edge]] = None

  distances(s) = 0.0
  queue.enqueue(s)
  onQueue(s) = true

  @tailrec
  final def loop(): Unit = {
    if (!queue.isEmpty && !cycle.isDefined) {
      val v = queue.dequeue
      onQueue(v) = false
      relax(v)
      loop()
    }
  }

  loop()

  /** returns a negative cycle if it exists */
  def negativeCycle(): Option[List[Edge]] = cycle

  def findNegativeCycle(): Unit = {
    val spt = new Digraph(edgeTo.length)
    edgeTo foreach (v => if (v != null) spt addEdge v )
    cycle = new CycleFinder(spt).cycle
  }

  def relax(v: Int): Unit = {
    for {
      e <- g.adj(v)
      w = e.to
    } { 
      if (distances(w) > distances(v) + e.weight) {
        distances(w) = distances(v) + e.weight
        edgeTo(w) = e
        if (!onQueue(w)) {
          queue.enqueue(w)
          onQueue(w) = true
        }
      }
      if (cost % g.numV == 0) findNegativeCycle()
      cost += 1
    }
  }

  def dist = distances
}

case class Digraph(numV: Int) {
  var e = 0
  val _adj = Array.fill[List[Edge]](numV)(List[Edge]())

  def adj(numV: Int): List[Edge] = {
    _adj(numV)
  }

  /** add directed edge to digraph */
  def addEdge(ed: Edge): Unit = {
    val numV = ed.from
    _adj(numV) = ed :: _adj(numV)
    e += 1//e + 1
  }

  /** returns all edges in digraph */
  def edges(): List[Edge] = {
    val list = for {
      vV <- 0 until numV
      e <- adj(vV)
    } yield e
    list.toList
  }
}

class CycleFinder(g: Digraph) {
  val marked = Array.fill[Boolean](g.numV)(false)
  val onStack = Array.fill[Boolean](g.numV)(false)
  val edgeTo = new Array[Edge](g.numV)
  var _cycle: Option[List[Edge]] = None

  for {
    v <- 0 until g.numV
  if (!marked(v))
  } dfs(v)

  def dfs(v: Int): Unit = {
    onStack(v) = true
    marked(v) = true

    @tailrec
    def loopEdges(es: List[Edge]): Unit = es match {
      case e :: xs => {
        search(e)
        loopEdges(xs)
      }
      case _ =>
    }

    def search(e: Edge): Unit = {
      if (!_cycle.isDefined) {
        val w = e.to
        val newV = !marked(w)
        if (newV) {
          edgeTo(w) = e
          dfs(w)
        } else if (onStack(w)) {

          def traceBack(): Option[List[Edge]] = {
            _cycle = Some(List[Edge]())

            @tailrec
            def loop(x: Edge): Unit =
              if (x.from != w) {
                _cycle = Some(x :: _cycle.get)
                loop(edgeTo(x.from))
              } else _cycle = Some(x :: _cycle.get)

            loop(e)
            _cycle
          }
          val c = traceBack()
        }
      }
    }
    val es = g.adj(v)
    loopEdges(es)

    if (!_cycle.isDefined) onStack(v) = false
  }

  def cycle(): Option[List[Edge]] = _cycle
}
