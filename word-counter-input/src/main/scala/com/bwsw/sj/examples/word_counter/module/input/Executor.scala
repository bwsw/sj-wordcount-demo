package com.bwsw.sj.examples.word_counter.module.input

import com.bwsw.sj.engine.core.entities.InputEnvelope
import com.bwsw.sj.engine.core.environment.InputEnvironmentManager
import com.bwsw.sj.engine.core.input.{InputStreamingExecutor, Interval}
import io.netty.buffer.ByteBuf

/**
  * Executor for input module.
  *
  * @author Pavel Tomskikh
  */
class Executor(manager: InputEnvironmentManager) extends InputStreamingExecutor(manager) {

  val wordCounterStream = "word-counter-lines"
  val partition = 0

  override def tokenize(buffer: ByteBuf): Option[Interval] = {
    val startIndex = buffer.readerIndex()
    val writerIndex = buffer.writerIndex()
    val endIndex = buffer.indexOf(startIndex, writerIndex, '\n')
    println(s"tokenize: $startIndex, $writerIndex, $endIndex")

    if (endIndex != -1) Some(Interval(startIndex, endIndex))
    else None
  }

  override def parse(buffer: ByteBuf, interval: Interval): Option[InputEnvelope] = {
    val length = interval.finalValue - interval.initialValue
    val dataBuffer = buffer.slice(interval.initialValue, length)
    val data = new Array[Byte](length)
    dataBuffer.getBytes(0, data)
    buffer.readerIndex(interval.finalValue + 1)
    val key = new String(data)
    println(s"parse: ${interval.initialValue}, ${interval.finalValue}, $key")

    Some(new InputEnvelope(
      key,
      Array((wordCounterStream, partition)),
      false,
      data))
  }
}

