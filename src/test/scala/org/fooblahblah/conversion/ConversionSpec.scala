package org.fooblahblah.conversion

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.{ConfigValueFactory, ConfigFactory}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import org.specs2.matcher.ThrownExpectations

class ConversionSpec extends Specification {
  val config = ConfigFactory.load().withValue("akka.persistence.journal.plugin", ConfigValueFactory.fromAnyRef("akka.persistence.journal.inmem"))

  "ConversionTracker" should {
    "Calculate conversion rate" in new scope {
      tracker ! Hit
      tracker ! Hit
      tracker ! Hit
      tracker ! Hit
      tracker ! Conversion

      // Retrieve current internal tracker state and assert the CTR is correct
      tracker ! GetState
      expectMsg(SiteStatistics(4, 1, 0.25))
    }

    "Recover previous state" in new scope {
      tracker ! Hit
      tracker ! Hit
      tracker ! Conversion

      // Tell actor to kill itself with exception
      tracker ! ThrowUp

      // Get restored state
      tracker ! GetState
      expectMsg(SiteStatistics(2, 1, 0.5))
    }
  }

  class scope extends TestKit(ActorSystem("test", config)) with Scope with ThrownExpectations with ImplicitSender {
    val tracker = system.actorOf(Props[ConversionTracker])
  }
}
