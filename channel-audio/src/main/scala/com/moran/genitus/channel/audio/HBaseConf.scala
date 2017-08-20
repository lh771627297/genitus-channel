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

import org.rogach.scallop.ScallopConf

/**
  * HBase Configuration.
  *
  * @author gwjiang (gwjiang@iflytek.com), 2016/8/22.
  */
trait HBaseConf extends ScallopConf {
  /** HBase zk quorum. */
  lazy val hbaseZkQuorum = opt[String]("hbase_zk_quorum",
    descr = "HBase zookeeper quorum",
    default = Some("172.28.3.20,172.28.1.11,172.28.1.12"),
    required = true,
    noshort = true)

  /** HBase table. */
  lazy val hbaseTable = opt[String]("hbase_table",
    descr = "HBase table",
    default = Some("toycloud"),
    required = true,
    noshort = true)

  /** HBase column family */
  lazy val hbaseColFamily = opt[String]("hbase_col_family",
    descr = "HBase column family",
    default = Some("r"),
    required = true,
    noshort = true)

  /** HBase column qualifier */
  lazy val hbaseColQualifier = opt[String]("hbase_col_qualifier",
    descr = "HBase column qualifier",
    default = Some("d"),
    required = true,
    noshort = true)

  /** HBase buffer size. */
  lazy val hbaseBuffSize = opt[Int]("hbase_buff_size",
    descr = "HBase buffer size",
    default = Some(1024 * 1024 * 6),
    required = false,
    noshort = true)
}
