package org.apache.fineract.validation.config;

import jakarta.validation.MessageInterpolator;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import yakworks.i18n.icu.ICUBundleMessageSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ValidationConfig {

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ICUBundleMessageSource();
        messageSource.setBasenames("classpath:fineract/validation/messages");
        messageSource.setCacheSeconds(3600);
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }

    @Bean
    public MessageInterpolator messageInterpolator() {
        var resourceBundleLocator = new MessageSourceResourceBundleLocator(messageSource());
        var messageInterpolator = new ResourceBundleMessageInterpolator(resourceBundleLocator);
        return new RecursiveLocaleContextMessageInterpolator(messageInterpolator);
    }

    @Bean
    @Primary
    public Validator validator() {
        var localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setMessageInterpolator(messageInterpolator());

        return localValidatorFactoryBean;
    }

    private static final class RecursiveLocaleContextMessageInterpolator extends AbstractMessageInterpolator {

        private static final Pattern PATTERN_PLACEHOLDER = Pattern.compile("\\{([^}]+)\\}");

        private final MessageInterpolator interpolator;

        private RecursiveLocaleContextMessageInterpolator(MessageInterpolator interpolator) {
            this.interpolator = interpolator;
        }

        @Override
        public String interpolate(MessageInterpolator.Context context, Locale locale, String message) {
            int level = 0;
            while (containsPlaceholder(message) && (level++ < 2)) {
                message = this.interpolator.interpolate(message, context, locale);
            }
            return message;
        }

        private boolean containsPlaceholder(String code) {
            return PATTERN_PLACEHOLDER.matcher(code).find();
        }
    }
}
