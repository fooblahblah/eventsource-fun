package org.fooblahblah.conversion

import akka.persistence.EventsourcedProcessor
import akka.actor.ActorLogging

class ConversionTracker extends EventsourcedProcessor with ActorLogging {

  var state = SiteStatistics() // initial state

  def receiveRecover = {
    case stats: SiteStatistics =>
      log.info(s"Recovered statistics $stats")
      state = stats
  }

  def receiveCommand = {
    case GetState =>
      sender ! state

    case Hit =>
      persist(calculateCTR(state.copy(hits = state.hits + 1))) { stats =>
        // update our internal state
        state = stats
        // Do some real work here ...
      }

    case Conversion =>
      persist(calculateCTR(state.copy(conversions = state.conversions + 1))) { stats =>
        // update our internal state
        state = stats
        // Do some real work here ...
      }

    case ThrowUp =>
      throw new Exception("I'm throwing up now")
  }

  def calculateCTR(stats: SiteStatistics): SiteStatistics = {
    val ctr = if(stats.hits > 0)
      stats.conversions.toFloat / stats.hits
    else
      0.0

    stats.copy(conversionRate = ctr)
  }
}

case object Conversion
case object GetState
case object Hit
case object ThrowUp // use for testing restart

case class SiteStatistics(hits: Long = 0, conversions: Long = 0, conversionRate: Double = 0.0)