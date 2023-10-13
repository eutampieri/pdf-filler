/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package pdf_filler

import com.gitlab.pdftk_java.com.lowagie.text.pdf.PdfReader
import com.gitlab.pdftk_java.com.lowagie.text.pdf.PdfStamperImp
import com.gitlab.pdftk_java.com.lowagie.text.pdf.XfdfReader
import java.nio.file.Files
import java.nio.file.Paths

class App {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    val reader = PdfReader("file.pdf")
    val form = XfdfReader("file.xfdf")
    val outputStream = Files.newOutputStream(Paths.get("/tmp/test.pdf"))

    // Code from com.gitlab.pdftk_java.filter
    val writer = PdfStamperImp(reader, outputStream, '\u0000', false /* append mode */)
    val fields = writer.getAcroFields()
    writer.setFormFlattening(true)
    fields.setGenerateAppearances(true)
    fields.setFields(form)
    writer.close()
}