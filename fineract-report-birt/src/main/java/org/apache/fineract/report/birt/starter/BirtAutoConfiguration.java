package org.apache.fineract.report.birt.starter;

import org.apache.fineract.infrastructure.report.config.ReportProperties;
import org.apache.fineract.infrastructure.report.service.ReportingProcessService;
import org.apache.fineract.report.birt.service.BirtReportingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class BirtAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ReportingProcessService reportingProcessService(ReportProperties properties, ApplicationContext context, DataSource dataSource) {
        return new BirtReportingService(properties, context, dataSource);
    }
}
