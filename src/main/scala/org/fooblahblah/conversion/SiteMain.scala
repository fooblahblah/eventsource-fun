package org.fooblahblah.conversion

import akka.actor.{Props, ActorSystem}

object SiteMain extends App {
  val system     = ActorSystem()
  val ctrTracker = system.actorOf(Props[ConversionTracker])

  1 to 10 foreach { _ =>
    ctrTracker ! Hit
  }

  1 to 5 foreach { _ =>
    ctrTracker ! Conversion
  }

  system.shutdown()
}
