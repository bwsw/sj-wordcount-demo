package com.bwsw.sj.examples.word_counter.module.process

import com.bwsw.common.{JsonSerializer, ObjectSerializer}
import com.bwsw.sj.engine.core.entities.TStreamEnvelope
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
  val state = manager.getState

  override def onInit(): Unit = {
    println("onInit")
    state.clear()
    println("\tdone")
  }

  override def onMessage(tstreamEnvelope: TStreamEnvelope[Array[Byte]]): Unit = {
    println(s"onMessage: ${tstreamEnvelope.stream}")

    tstreamEnvelope.data.foreach { rawLine =>
      val line = new String(rawLine)
      val words = line.split("\\s+").filter(_ != "")
      words.foreach { word =>
        println(s"\t$word")
        val oldCount =
          if (state.isExist(word)) state.get(word).asInstanceOf[Int]
          else 0
        state.set(word, oldCount + 1)
      }
    }
    println("\tdone")
  }

  override def onBeforeCheckpoint(): Unit = {
    println("onBeforeCheckpoint")
    val output = manager.getRoundRobinOutput(outputStreamName)
    state.getAll.foreach {
      case (w: String, c: Int) =>
        println(s"\t$w, $c")
        val wordsCount = WordsCount(w, c)
        val serialized = objectSerializer.serialize(jsonSerializer.serialize(wordsCount))
        output.put(serialized)
      case _ =>
    }
    println("\tdone")
  }

  override def onAfterCheckpoint(): Unit = {
    println("onAfterCheckpoint")
    state.clear()
    println("\tdone")
  }

}
