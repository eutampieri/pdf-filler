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
            val reader = PdfReader(pdfRaw.streamProvider())
            call.respondOutputStream(ContentType.parse("application/pdf"), HttpStatusCode.OK) {
                try {
                    val form = XfdfReader(formRaw.streamProvider())
                    PdfFiller(reader).fill(form, this)
                } catch (e: Exception) {
                    val form = FdfReader(formRaw.streamProvider())
                    PdfFiller(reader).fill(form, this)
                }
            }
            call.respondText(reader.acroForm.fields[0].toString())
        }
    }
}
