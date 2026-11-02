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
import io.ktor.utils.io.toByteArray


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
            var pdf: ByteArray? = null
            var form: ByteArray? = null

            data.forEachPart {
                if (it is PartData.FileItem) {
                    when (it.name) {
                        "pdf" -> pdf = it.provider().toByteArray()
                        "form" -> form = it.provider().toByteArray()
                    }
                }
            }
            if(form == null || pdf == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing PDF or XFDF/FDF.")
                return@post
            }

            // Actual PDF form filling
            val reader: PdfReader
            try {
                reader = PdfReader(pdf)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.toString())
                return@post
            }
            try {
                val xfdf = XfdfReader(form)
                call.respondOutputStream(ContentType.parse("application/pdf"), HttpStatusCode.OK) {
                    PdfFiller(reader).fill(xfdf, this)
                }
            } catch (_: Exception) {
                try {
                    val fdf = FdfReader(form)
                    call.respondOutputStream(ContentType.parse("application/pdf"), HttpStatusCode.OK) {
                        PdfFiller(reader).fill(fdf, this)
                    }
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Form data is not in FDF/XFDF format.")
                }
            }
        }
    }
}
