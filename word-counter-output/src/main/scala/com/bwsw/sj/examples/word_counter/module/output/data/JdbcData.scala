package com.bwsw.sj.examples.word_counter.module.output.data

import com.bwsw.sj.engine.core.entities.JdbcEnvelope

/**
  *
  * @author Pavel Tomskikh
  */
class JdbcData extends JdbcEnvelope {
  var count: Int = 0
  var word: String = ""
  var test: String = "" // primary key
}
