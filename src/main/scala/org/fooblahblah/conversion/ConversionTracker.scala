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
    case Hit =>
      log.info(s"Got a Hit")
      persist(calculateCTR(state.copy(hits = state.hits + 1))) { stats =>
        // update our internal state
        state = stats
        // Do some real work here ...
        log.info(s"Current CTR = ${stats.conversionRate}")
      }

    case Conversion =>
      log.info(s"Got a Conversion")
      persist(calculateCTR(state.copy(conversions = state.conversions + 1))) { stats =>
        // update our internal state
        state = stats
        // Do some real work here ...
        log.info(s"Current CTR = ${stats.conversionRate}")
      }
  }

  def calculateCTR(stats: SiteStatistics): SiteStatistics = {
    val ctr = if(stats.hits > 0)
      stats.conversions.toFloat / stats.hits
    else
      0.0

    stats.copy(conversionRate = ctr)
  }
}

case object Hit
case object Conversion

case class SiteStatistics(hits: Long = 0, conversions: Long = 0, conversionRate: Double = 0.0)