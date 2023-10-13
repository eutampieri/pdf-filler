package pdf_filler

import com.gitlab.pdftk_java.com.lowagie.text.pdf.FdfReader
import com.gitlab.pdftk_java.com.lowagie.text.pdf.PdfReader
import com.gitlab.pdftk_java.com.lowagie.text.pdf.PdfStamperImp
import com.gitlab.pdftk_java.com.lowagie.text.pdf.XfdfReader
import java.io.OutputStream

class PdfFiller(private var pdf: PdfReader) {

    fun fill(form: FdfReader, outputStream: OutputStream) {
        // Code from com.gitlab.pdftk_java.filter
        val writer = PdfStamperImp(this.pdf, outputStream, '\u0000', false /* append mode */)
        val fields = writer.getAcroFields()
        writer.setFormFlattening(true)
        fields.setGenerateAppearances(true)
        fields.setFields(form)
        writer.close()
    }
    fun fill(form: XfdfReader, outputStream: OutputStream) {
        val writer = PdfStamperImp(this.pdf, outputStream, '\u0000', false /* append mode */)
        val fields = writer.getAcroFields()
        writer.setFormFlattening(true)
        fields.setGenerateAppearances(true)
        fields.setFields(form)
        writer.close()
    }
}