package reports

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, File, OutputStream }
import java.sql.{ Connection, DriverManager }
import java.util

import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.{ JasperCompileManager, JasperFillManager, JasperPrint }
import net.sf.jasperreports.export.{ SimpleExporterInput, SimpleOutputStreamExporterOutput }

import scala.collection.mutable

object ReportBuilder {
  private var reportCache = new mutable.HashMap[String, Boolean].empty

  def generateCompileFileName(jrxml: String): String = s"/tmp/report_${jrxml}_.jasper"

  def compile(jrxml: String): Unit = if (reportCache.getOrElse(jrxml, true)) {
    JasperCompileManager.compileReportToFile(
      new File(".").getCanonicalFile + "/app/reports/" + jrxml,
      generateCompileFileName(jrxml))
    reportCache += (jrxml -> false)
  }

  def toPdf(jrxml: String): ByteArrayInputStream =
    try {
      val os: OutputStream = new ByteArrayOutputStream()
      val reportParams: util.Map[String, Object] = new util.HashMap()
      val con: Connection = DriverManager.getConnection(
        s"jdbc:mysql://localhost/RWS_DB?user=root&password=${sys.env("MYSQL_ROOT_PASSWORD")}&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=PST8PDT")
      compile(jrxml)

      val jrprint: JasperPrint =
        JasperFillManager.fillReport(generateCompileFileName(jrxml), reportParams, con)
      val exporter: JRPdfExporter = new JRPdfExporter()
      exporter.setExporterInput(new SimpleExporterInput(jrprint))
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os))
      exporter.exportReport()

      new ByteArrayInputStream(os.asInstanceOf[ByteArrayOutputStream].toByteArray)
    } catch {
      case e: Exception => throw new RuntimeException(e)
    }
}
