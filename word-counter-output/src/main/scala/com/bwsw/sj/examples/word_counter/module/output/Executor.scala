package com.bwsw.sj.examples.word_counter.module.output

import com.bwsw.common.{JsonSerializer, ObjectSerializer}
import com.bwsw.sj.engine.core.entities.{Envelope, TStreamEnvelope}
import com.bwsw.sj.engine.core.environment.OutputEnvironmentManager
import com.bwsw.sj.engine.core.output.OutputStreamingExecutor
import com.bwsw.sj.examples.word_counter.entities.WordsCount
import com.bwsw.sj.examples.word_counter.module.output.data.JdbcData

/**
  * Executor for output module.
  *
  * @author Pavel Tomskikh
  */
class Executor(manager: OutputEnvironmentManager)
  extends OutputStreamingExecutor[Array[Byte]](manager) {

  val jsonSerializer = new JsonSerializer()
  val objectSerializer = new ObjectSerializer()

  override def onMessage(envelope: TStreamEnvelope[Array[Byte]]): List[Envelope] = {
    println(s"onMessage: ${envelope.stream}, ${envelope.data.length}")
    envelope.data.map { data =>
      val wordsCount = jsonSerializer.deserialize[WordsCount](
        objectSerializer.deserialize(data).asInstanceOf[String])
      val jdbcData = new JdbcData
      jdbcData.count = wordsCount.count
      jdbcData.string = wordsCount.string

      println(s"\t${wordsCount.count}, ${wordsCount.string}")

      jdbcData
    }
  }
}
