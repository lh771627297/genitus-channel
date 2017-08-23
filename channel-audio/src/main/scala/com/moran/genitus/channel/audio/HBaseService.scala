package com.moran.genitus.channel.audio

import java.io.IOException
import java.nio.ByteBuffer

import com.google.common.util.concurrent.AbstractIdleService
import com.google.inject.Inject
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{DatumReader, DecoderFactory}
import org.genitus.forceps.hbase.HBaseClient
import org.genitus.lancet.util.codec.Codec
import org.slf4j.LoggerFactory

class HBaseService @Inject()(
  val hbaseClient: HBaseClient,
  val codec: Codec,
  val reader: DatumReader[GenericRecord]
)extends AbstractIdleService{

  private[this] val log = LoggerFactory.getLogger(getClass.getName)

  override def shutDown(): Unit = {
    log.info("shutdown HBaseService ...")
    hbaseClient.close()
  }

  override def startUp(): Unit = {
    log.info("start HBaseService ...")
  }

  def getAudio(sid: String): Array[Byte] = {
    log.info("sid = ({})", sid)
    try {
      val data = codec.decompress(hbaseClient.getLogs(sid).get(0).data.array())
      if (data != null) {
        val decoder = DecoderFactory.get.binaryDecoder(data, null)
        val record = reader.read(null, decoder)
        record.get("mediaData").asInstanceOf[GenericRecord].get("data").asInstanceOf[ByteBuffer].array().clone()
      }else{
        null
      }
    } catch {
      case e: IOException =>
        log.error("get log error: ", e.getMessage)
        null
    }
  }

}
