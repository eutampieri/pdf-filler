package pdf_filler

import com.gitlab.pdftk_java.com.lowagie.text.DocListener
import com.gitlab.pdftk_java.com.lowagie.text.Document
import com.gitlab.pdftk_java.com.lowagie.text.pdf.PdfWriter
import java.io.OutputStream
import java.lang.reflect.Constructor

class PdfWriterGetter {
    public fun getInstance(document: Document, os: OutputStream): PdfWriter {
        val constructor: Constructor<PdfWriter> = PdfWriter::class.java.declaredConstructors[0] as Constructor<PdfWriter>
        constructor.isAccessible = true

        val documentClass = Class.forName("com.gitlab.pdftk_java.com.lowagie.text.pdf.PdfDocument")
        val documentConstructor = documentClass.getDeclaredConstructor()
        documentConstructor.isAccessible = true
        val pdf = documentConstructor.newInstance()
        val writer: PdfWriter = constructor.newInstance(pdf, os)
        document.addDocListener(pdf as DocListener)
        pdf.javaClass.getMethod("setWriter", PdfWriter::class.java).invoke(pdf, writer)
        return writer
    }
}