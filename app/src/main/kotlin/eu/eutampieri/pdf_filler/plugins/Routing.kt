package eu.eutampieri.pdf_filler.plugins

import com.gitlab.pdftk_java.com.lowagie.text.pdf.FdfReader
import com.gitlab.pdftk_java.com.lowagie.text.pdf.PdfReader
import com.gitlab.pdftk_java.com.lowagie.text.pdf.XfdfReader
import eu.eutampieri.pdf_filler.PdfFiller
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    routing {
        get("/") {
            val classloader = Thread.currentThread().getContextClassLoader()
            call.respondOutputStream(ContentType.parse("text/html"), HttpStatusCode.OK) {
                classloader.getResourceAsStream("index.html")?.copyTo(this)
            }
        }
        post("/fill") {
            val data: MultiPartData
            try {
                data = call.receiveMultipart()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.toString())
                return@post
            }
            val parts = data.readAllParts()
            val pdfOption = parts.stream().filter { return@filter it.name == "pdf" }.findFirst()
            val formOption = parts.stream().filter { return@filter it.name == "form" }.findFirst()
            if(formOption.isEmpty || pdfOption.isEmpty) {
                call.respond(HttpStatusCode.BadRequest, "Missing PDF or XFDF/FDF.")
                return@post
            }
            val pdfRaw = pdfOption.get() as PartData.FileItem
            val formRaw = formOption.get() as PartData.FileItem

            // Actual PDF form filling
            val reader: PdfReader
            try {
                reader= PdfReader(pdfRaw.streamProvider())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.toString())
                return@post
            }
            try {
                val form = XfdfReader(formRaw.streamProvider())
                call.respondOutputStream(ContentType.parse("application/pdf"), HttpStatusCode.OK) {
                    PdfFiller(reader).fill(form, this)
                }
            } catch (e: Exception) {
                try {
                    val form = FdfReader(formRaw.streamProvider())
                    call.respondOutputStream(ContentType.parse("application/pdf"), HttpStatusCode.OK) {
                        PdfFiller(reader).fill(form, this)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Form data is not in FDF/XFDF format.")
                }
            }
        }
    }
}
