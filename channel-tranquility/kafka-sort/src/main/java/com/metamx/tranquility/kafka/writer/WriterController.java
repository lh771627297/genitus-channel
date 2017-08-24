/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.metamx.tranquility.kafka.writer;

import com.google.common.primitives.Ints;
import com.metamx.common.logger.Logger;
import com.metamx.common.scala.net.curator.Disco;
import com.metamx.tranquility.config.DataSourceConfig;
import com.metamx.tranquility.finagle.FinagleRegistry;
import com.metamx.tranquility.finagle.FinagleRegistryConfig;
import com.metamx.tranquility.kafka.model.MessageCounters;
import com.metamx.tranquility.kafka.model.PropertiesBasedKafkaConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Manages the creation and operation of TranquilityEventWriters.
 */
public class WriterController
{
  private static final Logger log = new Logger(WriterController.class);
  private static final RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(1000, 500, 30000);

  private List<DataSourceConfig<PropertiesBasedKafkaConfig>> dataSourceConfigList;
  private Map<String, TranquilityEventWriter> writers = new ConcurrentHashMap<>();
  private Map<String, CuratorFramework> curators = new ConcurrentHashMap<>();
  private Map<String, FinagleRegistry> finagleRegistries = new ConcurrentHashMap<>();

  public WriterController(Map<String, DataSourceConfig<PropertiesBasedKafkaConfig>> dataSourceConfigs)
  {
    this.dataSourceConfigList = new ArrayList<>(dataSourceConfigs.values());
    dataSourceConfigList.sort((o1, o2) -> o2.propertiesBasedConfig().getTopicPatternPriority()
            .compareTo(o1.propertiesBasedConfig().getTopicPatternPriority()));

    log.info("Ready: [topicPattern]|[keyPattern] -> dataSource mappings:");
    for (DataSourceConfig<PropertiesBasedKafkaConfig> dataSourceConfig : this.dataSourceConfigList) {
      log.info(
          "  [%s]|[%s] -> %s (priority: %d)",
          dataSourceConfig.propertiesBasedConfig().getTopicPattern(),
          dataSourceConfig.propertiesBasedConfig().getKeyPattern(),
          dataSourceConfig.dataSource(),
          dataSourceConfig.propertiesBasedConfig().getTopicPatternPriority()
      );
    }
  }

  public synchronized TranquilityEventWriter getWriter(String topic,String key)
  {
    if (!writers.containsKey(topic+"|"+key)) {
      // create a EventWriter using the spec mapped to the first matching topicPatter and key
      for (DataSourceConfig<PropertiesBasedKafkaConfig> dataSourceConfig : dataSourceConfigList) {
        if (Pattern.matches(dataSourceConfig.propertiesBasedConfig().getTopicPattern(), topic)
                && Pattern.matches(dataSourceConfig.propertiesBasedConfig().getKeyPattern(), key)) {
          log.info(
              "Creating EventWriter for topic | key  [%s] | [%s] - using dataSource [%s]",
              topic,
              key,
              dataSourceConfig.dataSource()
          );
          writers.put(topic+"|"+key, createWriter(dataSourceConfig));
          return writers.get(topic+"|"+key);
        }
      }
      throw new RuntimeException(String.format("Kafka topicFilter allowed topic [%s] but no kafka-key[%s] is mapped", topic,key));
    }

    return writers.get(topic+"|"+key);
  }

  public Map<String, MessageCounters> flushAll() throws InterruptedException
  {
    Map<String, MessageCounters> flushedEvents = new HashMap<>();
    for (Map.Entry<String, TranquilityEventWriter> entry : writers.entrySet()) {
      entry.getValue().flush();
      flushedEvents.put(entry.getKey(), entry.getValue().getMessageCounters());
    }

    return flushedEvents;
  }

  public void stop()
  {
    for (Map.Entry<String, TranquilityEventWriter> entry : writers.entrySet()) {
      entry.getValue().stop();
    }

    for (Map.Entry<String, CuratorFramework> entry : curators.entrySet()) {
      entry.getValue().close();
    }
  }

  protected TranquilityEventWriter createWriter(
      DataSourceConfig<PropertiesBasedKafkaConfig> dataSourceConfig
  )
  {
    final String curatorKey = dataSourceConfig.propertiesBasedConfig().zookeeperConnect();
    if (!curators.containsKey(curatorKey)) {
      final int zkTimeout = Ints.checkedCast(
          dataSourceConfig.propertiesBasedConfig()
                          .zookeeperTimeout()
                          .toStandardDuration()
                          .getMillis()
      );

      final CuratorFramework curator = CuratorFrameworkFactory.builder()
                                                              .connectString(
                                                                  dataSourceConfig.propertiesBasedConfig()
                                                                                  .zookeeperConnect()
                                                              )
                                                              .connectionTimeoutMs(zkTimeout)
                                                              .retryPolicy(RETRY_POLICY)
                                                              .build();
      curator.start();
      curators.put(curatorKey, curator);
    }

    final String finagleKey = String.format("%s:%s", curatorKey, dataSourceConfig.propertiesBasedConfig().discoPath());
    if (!finagleRegistries.containsKey(finagleKey)) {
      finagleRegistries.put(
          finagleKey, new FinagleRegistry(
              FinagleRegistryConfig.builder().build(),
              new Disco(curators.get(curatorKey), dataSourceConfig.propertiesBasedConfig())
          )
      );
    }

    return new TranquilityEventWriter(
        dataSourceConfig,
        curators.get(curatorKey),
        finagleRegistries.get(finagleKey)
    );
  }
}
