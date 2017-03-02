package com.bwsw.sj.examples.word_counter.module.output

import com.bwsw.sj.engine.core.entities.{Envelope, TStreamEnvelope}
import com.bwsw.sj.engine.core.environment.OutputEnvironmentManager
import com.bwsw.sj.engine.core.output.OutputStreamingExecutor
import com.bwsw.sj.examples.word_counter.entities.WordCount
import com.bwsw.sj.examples.word_counter.module.output.data.JdbcData

/**
  * Executor for output module.
  *
  * @author Pavel Tomskikh
  */
class Executor(manager: OutputEnvironmentManager) extends OutputStreamingExecutor[WordCount](manager) {

  override def onMessage(envelope: TStreamEnvelope[WordCount]): List[Envelope] = {
    println(s"onMessage: ${envelope.stream}, ${envelope.data.length}")
    envelope.data.map { wordCount =>
      val jdbcData = new JdbcData
      jdbcData.count = wordCount.count
      jdbcData.word = wordCount.word

      println(s"\t${wordCount.count}, ${wordCount.word}")

      jdbcData
    }
  }
}
