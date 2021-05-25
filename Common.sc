case class Edge(from: Int, to: Int, weight: Double) 

def parseForexString(str: String): (String, String) = str.split("_") match {
  case Array(from, to) => (from, to)
  case _ => throw new Throwable(s"Coult not parse the forex pair string $str") 
}

def parseEdges(jsonResponse: Map[String, String], currencies: Seq[String]): List[Edge] = {
  jsonResponse.foldLeft(List[Edge]()) { case (acum, (k, v)) => {
      val (forex1, forex2) = parseForexString(k)
      if(forex1 == forex2) acum
      else {
        val curr1 = currencies.indexOf(forex1)
        val curr2 = currencies.indexOf(forex2)
        val logWeight = -scala.math.log(v.toDouble)
        val newEdge = new Edge(curr1, curr2, logWeight)
        newEdge :: acum
      }
    }
  }
}

def getCurrencies(jsonResponse: Map[String, String]): Set[String] = {
  jsonResponse.foldLeft(Set[String]()) { case (acum, (k, _)) => {
      val (forex1, forex2) = parseForexString(k)
      Set(forex1, forex1) ++ acum
    }
  }
}

def printDist(dist: Seq[Double], margin: String, currencies: Seq[String]): Unit = {
  println(s"${margin}Distances")
  dist.foldLeft(0){ case (acum, d) =>
    println(s"${margin * 2}${currencies(acum)} -> $d")
    acum + 1
  }
}

def printCycle(cycle: List[Edge], margin: String, currencies: Seq[String]) = {
  var stake = 1.0
  var pos = 0

  for (i <- 0 to cycle.size -1){
    val e = cycle(i)
    val init = stake
    val end = stake * scala.math.exp(-e.weight)
    val from = currencies(e.from)
    val to = currencies(e.to)
    i match {
      case 0 => print(s"${margin*2}$init $from => ")
      case _ if(i == cycle.size) => print(" => $end $to")
      case _ => print(s"$init $from => $end $to")
    }
    stake = end
  }
  println("")
}

def printResults(dist: Seq[Double], currency: String, currencies: Seq[String],
 cycles: List[List[Edge]]) = {
  val margin = " "*4
  println(s"Procesing $currency")
  printDist(dist, margin, currencies)
  println(s"${margin}Arbitrage oportunities")
  cycles.foreach(c => printCycle(c, margin, currencies))
  println("")
}