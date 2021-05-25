interp.load.ivy("com.lihaoyi" %% "requests" % "0.6.5")
interp.load.ivy("com.lihaoyi" %% "upickle" % "1.3.8")
import upickle.default._
import $file.Functional
import $file.Reference

@main
def main(mode: String) = {
  val jsonResponse = requests.get("https://fx.priceonomics.com/v1/rates/")
  val parsedResponse = read[Map[String, String]](jsonResponse)

  mode.toLowerCase match {
    case "f" => runFunctional(parsedResponse)
    case "r" => runReference(parsedResponse)
    case "b" => 
      runReference(parsedResponse)
      runFunctional(parsedResponse)
    case p => println(s"Param $p was not reconized")
  }
}


def runFunctional(response: Map[String, String]) = {
  println("Functional Solution:")
  println("")
  Functional.run(response)  
}

def runReference(response: Map[String, String]) = {
  println("Reference Solution:")
  println("")
  Reference.run(response)  
}