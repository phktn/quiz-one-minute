package com.mizo0203.quiz.one.minute

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import spark.Request
import spark.Response
import spark.Spark.*


object Main {
    private val objectMapper = ObjectMapper()

    @JvmStatic
    fun main(args: Array<String>) {

        // configure application.
        staticFiles.location("/static")

        // Starts the webapp on localhost and the port defined by the PORT
        // environment variable when present, otherwise on 8080.
        val port = System.getenv().getOrDefault("PORT", "8080").toInt()
        port(port)
        post("/upload") { req: Request, _: Response ->
            val fileUpload = ServletFileUpload(DiskFileItemFactory())
            val items: List<FileItem> = fileUpload.parseRequest(req.raw())
            val item: FileItem = items.stream()
                .filter { e: FileItem -> "problemListFile" == e.fieldName }
                .findFirst().get()
            return@post objectMapper.writeValueAsString(
                Problem.parseProblemListFile(item.inputStream)
                    .chunked(12)
                    .mapIndexed { index, problems -> Pair(index + 1, problems) }
                    .toMap()
            )
        }
    }
}
