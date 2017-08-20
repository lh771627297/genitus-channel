/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moran.genitus.channel.audio

import com.google.inject.{AbstractModule, Provides, Singleton}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.genitus.forceps.hbase.HBaseClient
import org.genitus.lancet.util.codec.CodecFactory

/**
  * Guice glue code of hbase logic components.
  *
  * @author gwjiang (gwjiang@iflytek.com), 2016/9/9.
  */
class HBaseModule(val config: HBaseConf) extends AbstractModule {
  /**
    * @inheritdoc
    */
  override def configure(): Unit = {
    bind(classOf[HBaseConf]).toInstance(config)
  }

  /**
    * Provides hbase client.
    *
    * @param config hbase config.
    * @return hbase client.
    */
  @Singleton
  @Provides
  def provideHBaseClient(config: HBaseConf): HBaseClient = {
    HBaseClient.newBuilder(
      config.hbaseZkQuorum(),
      config.hbaseTable(),
      config.hbaseColFamily(),
      config.hbaseColQualifier())
      .enableWrite()
      .withBuffSize(config.hbaseBuffSize())
      .build()
  }

  /**
    * Provides hbase serivce.
    *
    * @param hbaseClient hbase client.
    * @param schema schema.
    * @return hbase serivce.
    */
  @Singleton
  @Provides
  def provideHBaseService(hbaseClient: HBaseClient, schema: Schema): HBaseService = {
    new HBaseService(hbaseClient,
      CodecFactory.getCodec(CodecFactory.DeflateType),
      new GenericDatumReader[GenericRecord](schema))
  }
}
