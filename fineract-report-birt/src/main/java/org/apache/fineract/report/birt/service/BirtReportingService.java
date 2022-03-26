package org.apache.fineract.report.birt.service;

import org.apache.fineract.infrastructure.report.annotation.ReportService;
import org.apache.fineract.infrastructure.report.config.ReportProperties;
import org.apache.fineract.infrastructure.report.service.ReportingProcessService;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.context.ApplicationContext;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;
import java.util.Map;

import static org.apache.fineract.infrastructure.core.utils.LocaleUtils.extractLocale;
import static org.apache.fineract.infrastructure.report.ReportConstants.*;

// TODO: @vidakovic we should remove any references to JAX-RS packages; not really necessary

@ReportService(type = "BIRT")
public class BirtReportingService implements ReportingProcessService {

    private final ReportProperties properties;
    // private final PlatformSecurityContext context;
    private final DataSource tenantDataSource;
    private final IReportEngine reportEngine;

    public BirtReportingService(final ReportProperties settings, ApplicationContext context, DataSource tenantDataSource) {
        this.properties = settings;
        // this.context = context;
        this.tenantDataSource = tenantDataSource;

        try {
            EngineConfig config = new EngineConfig();
            config.getAppContext().put("spring", context);
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            this.reportEngine = factory.createReportEngine(config);
        } catch (BirtException exception) {
            // TODO: throw proper Fineract platform exception
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Response processRequest(String reportName, MultivaluedMap<String, String> queryParams) {
        String type = queryParams.getFirst(QUERY_PARAM_OUTPUT_TYPE).toUpperCase();
        String mime = MIME_APPLICATION_OCTET_STREAM;
        Map<String, String> reportParams = getReportParams(queryParams);
        Locale locale = extractLocale(queryParams);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        IRunAndRenderTask runAndRenderTask = null;

        try {
            // TODO: @vidakovic fix this
            IReportRunnable report = reportEngine.openReportDesign(properties.getTemplateFolder() + File.separator + reportName);
            runAndRenderTask = reportEngine.createRunAndRenderTask(report);

            IRenderOption options = null;

            switch (type) {
                case TYPE_PDF:
                    options = new PDFRenderOption(new RenderOption());
                    // runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, request);
                    mime = MIME_APPLICATION_PDF;
                    break;
                case TYPE_HTML:
                    HTMLRenderOption htmlOptions = new HTMLRenderOption(new RenderOption());
                    htmlOptions.setBaseImageURL(properties.getBaseImageUrl());
                    htmlOptions.setImageDirectory(properties.getImageFolder());
                    // htmlOptions.setImageHandler(new HTMLServerImageHandler());
                    mime = MIME_TEXT_HTML;
                    break;
                case TYPE_XLS:
                    // TODO: @vidakovic fix this
                    break;
                case TYPE_XLSX:
                    // TODO: @vidakovic fix this
                    break;
                case TYPE_CSV:
                    // TODO: @vidakovic fix this
                    break;
            }

            if(options!=null) {
                options.setOutputFormat(type.toLowerCase());
                options.setOutputStream(os);
                runAndRenderTask.setRenderOption(options);
                runAndRenderTask.run();

                return Response.ok().entity(os.toByteArray()).type(mime).build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if(runAndRenderTask!=null) {
                runAndRenderTask.close();
            }
        }

        return Response.noContent().build();
    }

    @Override
    public Map<String, String> getReportParams(MultivaluedMap<String, String> queryParams) {
        return ReportingProcessService.super.getReportParams(queryParams);
    }

    @PreDestroy
    public void destroy() {
        reportEngine.destroy();
        Platform.shutdown();
    }

    /*
    private void addOptions(final IRenderOption options, final Map<String, String> queryParams) {
        try {
            for (Map.Entry<String, Object> paramDefEntry : options.getOptions()) {
                final String paramName = paramDefEntry.getName();

                if (!paramName.equals(REPORT_PARAM_USER_HIERARCHY) && !paramName.equals(REPORT_PARAM_USER_HIERARCHY) && !paramName.equals(REPORT_PARAM_USER_ID) && !paramName.equals(REPORT_PARAM_TENANT_URL) && !paramName.equals(REPORT_PARAM_USERNAME) && !paramName.equals(REPORT_PARAM_PASSWORD)) {
                    if (StringUtils.isBlank(pValue)) {
                        throw new PlatformDataIntegrityException("error.msg.reporting.error",
                            "Eclipse BIRT Parameter: " + paramName + " - not Provided");
                    }
                    final Class<?> clazz = paramDefEntry.getValueType();

                    if (clazz.getCanonicalName().equalsIgnoreCase("java.lang.Integer")) {
                        options.setOption(paramName, Integer.parseInt(pValue));
                    } else if (clazz.getCanonicalName().equalsIgnoreCase("java.lang.Long")) {
                        options.setOption(paramName, Long.parseLong(pValue));
                    } else if (clazz.getCanonicalName().equalsIgnoreCase("java.sql.Date")) {
                        options.setOption(paramName, Date.valueOf(pValue));
                    } else {
                        options.setOption(paramName, pValue);
                    }
                }
            }

            // Tenant database name and current user's office hierarchy
            // passed as parameters to allow multitenant Pentaho reporting
            // and data scoping
            final String tenant = ThreadLocalContextUtil.getTenant();
            final String tenantConnection = tenant.getConnection();
            String protocol = toProtocol(this.tenantDataSource);
            String tenantUrl = toJdbcUrl(protocol, tenantConnection.getSchemaServer(), tenantConnection.getSchemaServerPort(), tenantConnection.getSchemaName(), tenantConnection.getSchemaConnectionParameters());
            final var userhierarchy = currentUser.getOffice().getHierarchy();

            options.setOption(REPORT_PARAM_USER_HIERARCHY, userhierarchy);
            options.setOption(REPORT_PARAM_USER_ID, this.context.authenticatedUser().getId());
            options.setOption(REPORT_PARAM_TENANT_URL, tenantUrl);
            options.setOption(REPORT_PARAM_USERNAME, tenantConnection.getSchemaUsername());
            options.setOption(REPORT_PARAM_PASSWORD, tenantConnection.getSchemaPassword());
        } catch (final Exception e) {
            throw new PlatformDataIntegrityException("error.msg.reporting.error", e.getMessage());
        }
    }
    */
}
