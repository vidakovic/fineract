/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.apachecon.report.service;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.apachecon.report.core.JasperReportingProperties;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.dataqueries.data.ReportExportType;
import org.apache.fineract.infrastructure.report.annotation.ReportService;
import org.apache.fineract.infrastructure.report.service.ReportingProcessService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@ReportService(type = "Jasper")
@Service
public class JasperReportingProcessService implements ReportingProcessService {

    private static final List<String> ALLOWED_OUTPUT_TYPES = List.of("HTML", "PDF", "XLS", "XLSX", "CSV", "TXT", "DOC", "DOCX");

    public static final String VITUALIZER_FILE = "file";

    public static final String VITUALIZER_SWAP = "swap";

    public static final String VITUALIZER_GZIP = "gzip";

    private final DataSource dataSource;

    private final JasperReportingProperties jasperReportingProperties;

    @PostConstruct
    public void init() {
        log.warn(">>>>>>>>>>>>>>>>>> Community over Code 2024: Jasper Reporting Service!!!");
    }

    @Override
    @SneakyThrows
    public Response processRequest(String templateName, MultivaluedMap<String, String> queryParams) {
        final var outputTypeParam = queryParams.getFirst("output-type");
        final var locale = ApiParameterHelper.extractLocale(queryParams);

        var format = "HTML";

        if (StringUtils.isNotBlank(outputTypeParam)) {
            format = outputTypeParam.toUpperCase();
        }

        if (!ALLOWED_OUTPUT_TYPES.contains(format)) {
            throw new PlatformDataIntegrityException("error.msg.invalid.outputType", "No matching Output Type: " + format);
        }

        String template = templateName + ".jrxml";

        if (locale != null && !"en".equalsIgnoreCase(locale.toString())) {
            template = templateName + "_" + locale.toString().toLowerCase() + ".jrxml";
        }

        log.warn("Report path: {}", template);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        generate(dataSource.getConnection(), template, getReportParams(queryParams), format, os);

        return Response.ok().entity(os.toByteArray()).type(getContentType(format)).build();
    }

    @Override
    public List<ReportExportType> getAvailableExportTargets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getReportParams(MultivaluedMap<String, String> queryParams) {
        final Map<String, String> reportParams = new HashMap<>();
        for (final Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            if (entry.getKey().startsWith("R_")) {
                reportParams.put(entry.getKey().substring(2), entry.getValue().get(0));
            }
        }
        return reportParams;
    }

    private void generate(Connection connection, String template, Map parameters, String format, OutputStream os) {
        if (template != null) {
            JRAbstractLRUVirtualizer virtualizer = null;

            switch (jasperReportingProperties.getVirtualizerType()) {
                case VITUALIZER_FILE:
                    virtualizer = new JRFileVirtualizer(2, jasperReportingProperties.getVirtualizerType());
                    virtualizer.setReadOnly(true);
                    parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
                break;
                case VITUALIZER_SWAP:
                    virtualizer = new JRSwapFileVirtualizer(2, new JRSwapFile("tmp", 1024, 1024), true);
                    virtualizer.setReadOnly(true);
                    parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
                break;
                case VITUALIZER_GZIP:
                    virtualizer = new JRGzipVirtualizer(2);
                    virtualizer.setReadOnly(true);
                    parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
                break;
                default:
                    log.debug("No report virtualizer set.");
            }

            try {
                final var jasperDesign = JRXmlLoader
                        .load(JasperReportingProcessService.class.getClassLoader().getResourceAsStream("reports/" + template));
                final var jasperReport = JasperCompileManager.compileReport(jasperDesign);
                final var jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

                final var exporter = getExporter(format);
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
                exporter.exportReport();

                os.flush();
                os.close();
            } catch (Exception ex) {
                log.error("Report export error: ", ex);
            } finally {
                try {
                    if (virtualizer != null) {
                        virtualizer.cleanup();
                    }
                } catch (Throwable ex) {
                    // ignore
                }
            }
        }
    }

    private JRAbstractExporter getExporter(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return new JRPdfExporter();
            case "HTML":
                var exporter = new HtmlExporter();
                // exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imageUrl);
                // exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
                // exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
                // exporter.setParameter(JRHtmlExporterParameter.SIZE_UNIT, "px");
                return exporter;
            case "DOC":
            case "DOCX":
                return new JRDocxExporter();
            case "XLS":
            case "XLSX":
                return new JRXlsxExporter();
            case "TXT":
                return new JRTextExporter();
            case "CSV":
                return new JRCsvExporter();
            default:
                throw new RuntimeException("Report output format not supported: " + format);
        }
    }

    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return APPLICATION_JSON;
            case "HTML":
                return TEXT_HTML;
            case "DOC":
                return "application/msword";
            case "DOCX":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "XLS":
                return "application/vnd.ms-excel";
            case "XLSX":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "TXT":
                return TEXT_PLAIN;
            case "CSV":
                return "text/csv";
            default:
                throw new RuntimeException("Report output format not supported: " + format);
        }
    }
}
