package com.moran.genitus.channel.tracker.router

import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import ch.qos.logback.core.status.Status
import com.codahale.metrics.annotation.Timed
import com.google.inject.Inject
import com.moran.genitus.channel.tracker.service.EsService
import org.slf4j.LoggerFactory


@Path("/tracker")
@Produces(Array(MediaType.APPLICATION_JSON))
class RestApiRouter @Inject()(
  val esService: EsService
) {

  /** logger. */
  private[this] val log = LoggerFactory.getLogger(getClass.getName)

  @Path("/query")
  @GET
  @Timed
  def getEsLogByJson(@PathParam("json") json: String): Response ={
    log.info("query es by json({})", json)

    try {
      val resp = esService.search(json)
      println("---------------------------------------------"+resp)
      Response.ok(resp).build()
    } catch {
      case ex: Exception =>
        log.error("Exception while serving request", ex)
        throw new WebApplicationException(Status.ERROR)
    }
  }


}
