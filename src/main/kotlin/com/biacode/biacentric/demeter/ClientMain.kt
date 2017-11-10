package com.biacode.biacentric.demeter

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.zeroturnaround.exec.ProcessExecutor


/**
 * Created by Arthur Asatryan.
 * Date: 11/9/17
 * Time: 1:28 AM
 */
fun main(args: Array<String>) {
    val output = ProcessExecutor()
            .command("git", "log", "--format=format:{\"authorName\": \"%aN\", \"authorEmail\": \"%ae\", \"commiterName\": \"%s\"},")
            .readOutput(true).execute()
            .outputUTF8()
    if (output.isNotEmpty()) {
        val transformOutput = "[${output.subSequence(0, output.length - 1)}]"
        val mapper = ObjectMapper()
        //JSON from String to Object
        val gitLogMessages: List<GitLogMessage> = mapper.readValue(transformOutput, mapper.typeFactory.constructCollectionType(List::class.java, GitLogMessage::class.java))
        println(gitLogMessages)
    }
}

data class GitLogMessage(
        @JsonProperty("authorName")
        val authorName: String,
        @JsonProperty("authorEmail")
        val authorEmail: String,
        @JsonProperty("commiterName")
        val commiterName: String
)