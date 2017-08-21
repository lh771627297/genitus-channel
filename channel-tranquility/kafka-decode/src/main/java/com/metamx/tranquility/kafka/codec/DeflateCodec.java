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

package com.metamx.tranquility.kafka.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

/**
 * Deflate util.
 * Implements DEFLATE (RFC1951) compression and decompression.
 *
 * Note that there is a distinction between RFC1951 (deflate)
 * and RFC1950 (zlib).  zlib adds an extra 2-byte header
 * at the front, and a 4-byte checksum at the end.  The
 * code here, by passing "true" as the "nowrap" option to
 * {@link Inflater} and {@link Deflater}, is using
 * RFC1951.
 *
 * @author gwjiang (gwjiang@iflytek.com), 2016/8/16.
 */
public class DeflateCodec {

  /** output. */
  private ByteArrayOutputStream outputBuffer;

  /** inflater */
  private Inflater inflater;

  public byte[] decompress(byte[] data) {
    ByteArrayOutputStream baos = getOutputBuffer(data.length);
    try (InflaterOutputStream ios = new InflaterOutputStream(baos, getInflater())) {
      ios.write(data, 0, data.length);
    }catch (IOException e){
      e.printStackTrace();
    }
    return baos.toByteArray();
  }

  /**
   * Gets and initialize the inflater for use.
   *
   * @return Inflater instance.
   */
  private Inflater getInflater() {
    if (null == inflater) {
      inflater = new Inflater(true);
    }
    inflater.reset();
    return inflater;
  }


  /**
   * Gets and initialize the output buffer for use.
   *
   * @param suggestedLength length.
   * @return output buffer.
   */
  private ByteArrayOutputStream getOutputBuffer(int suggestedLength) {
    if (null == outputBuffer) {
      outputBuffer = new ByteArrayOutputStream(suggestedLength);
    }
    outputBuffer.reset();
    return outputBuffer;
  }
}
