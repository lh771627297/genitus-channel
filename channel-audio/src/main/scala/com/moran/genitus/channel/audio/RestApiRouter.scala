package com.moran.genitus.channel.audio

import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import ch.qos.logback.core.status.Status
import com.codahale.metrics.annotation.Timed
import com.google.inject.Inject
import org.slf4j.LoggerFactory



@Path("/audio")
@Produces(Array(MediaType.APPLICATION_JSON))
class RestApiRouter @Inject()(
  val hbaseService: HBaseService
) {

  /** logger. */
  private[this] val log = LoggerFactory.getLogger(getClass.getName)

  @Path("/{sid}")
  @GET
  @Timed
  def getAudioBySid(@PathParam("sid") sid: String): Response ={
    log.info("get audio by sid({})", sid)

    try {
      val resp = hbaseService.getAudio(sid)
      println("---------------------------------------------"+resp)
      Response.ok(resp).build()
    } catch {
      case ex: Exception =>
        log.error("Exception while serving request", ex)
        Response
        throw new WebApplicationException(Status.ERROR)
    }
  }


}
