package com.bwsw.sj.examples.word_counter.module.process

import com.bwsw.common.{JsonSerializer, ObjectSerializer}
import com.bwsw.sj.engine.core.entities.{Envelope, TStreamEnvelope}
import com.bwsw.sj.engine.core.environment.ModuleEnvironmentManager
import com.bwsw.sj.engine.core.regular.RegularStreamingExecutor
import com.bwsw.sj.examples.word_counter.entities.WordsCount

/**
  * Executor for regular module.
  *
  * @author Pavel Tomskikh
  */
class Executor(manager: ModuleEnvironmentManager) extends RegularStreamingExecutor[Array[Byte]](manager) {

  val inputStreamName = "word-counter-lines"
  val outputStreamName = "word-counter-words"
  val jsonSerializer = new JsonSerializer()
  val objectSerializer = new ObjectSerializer()

  override def onMessage(tstreamEnvelope: TStreamEnvelope[Array[Byte]]): Unit = {
    println(s"onMessage: ${tstreamEnvelope.stream}")
    val output = manager.getRoundRobinOutput(outputStreamName)
    tstreamEnvelope.data.foreach { rawLine =>
      val line = new String(rawLine)
      val count = line.split("\\s+").count(_ != "")
      val wordsCount = WordsCount(line, count)
      val serialized = objectSerializer.serialize(jsonSerializer.serialize(wordsCount))

      println(s"\t $wordsCount, $line")

      output.put(serialized)
    }
  }

  override def onAfterCheckpoint(): Unit = println("checkpoint")
}
