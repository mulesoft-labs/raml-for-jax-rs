package org.raml.jaxrs.generator;

import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.NoopAnnotator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 1/10/17.
 * Just potential zeroes and ones
 */
class RamlToJaxRSGenerationConfig extends DefaultGenerationConfig {

    private AnnotationStyle jsonMapper;
    private Map<String, String> jsonMapperConfiguration = new HashMap<>();

    public RamlToJaxRSGenerationConfig(AnnotationStyle jsonMapper,
            Map<String, String> jsonMapperConfiguration) {
        this.jsonMapper = jsonMapper;
        this.jsonMapperConfiguration = jsonMapperConfiguration;
    }

    @Override
    public AnnotationStyle getAnnotationStyle() {
        return jsonMapper;
    }

    @Override
    public boolean isIncludeJsr303Annotations() {
        return getConfiguredValue("isIncludeJsr303Annotations", false);
    }

    @Override
    public boolean isUseCommonsLang3() {
        return getConfiguredValue("useCommonsLang3", false);
    }

    @Override
    public boolean isGenerateBuilders() {
        return getConfiguredValue("generateBuilders", true);
    }

    @Override
    public boolean isIncludeHashcodeAndEquals() {
        return getConfiguredValue("includeHashcodeAndEquals", false);
    }

    @Override
    public Class getCustomAnnotator() {
        String className = getConfiguredValueStr("customAnnotator", null);
        if (className == null) {
            return NoopAnnotator.class;
        } else {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {

                throw new GenerationException(e);
            }
        }
    }

    @Override
    public boolean isIncludeToString() {
        return getConfiguredValue("includeToString", false);
    }

    @Override
    public boolean isUseLongIntegers() {
        return getConfiguredValue("useLongIntegers", false);
    }

    @Override
    public boolean isIncludeConstructors() {
        return getConfiguredValue("includeConstructors", super.isIncludeConstructors());
    }

    @Override
    public boolean isConstructorsRequiredPropertiesOnly() {
        return getConfiguredValue("constructorsRequiredPropertiesOnly", super
                .isConstructorsRequiredPropertiesOnly());
    }

    @Override
    public boolean isIncludeAccessors() {
        return getConfiguredValue("includeAccessors", super.isIncludeAccessors());
    }

    private boolean getConfiguredValue(final String key, final boolean def) {
        if (jsonMapperConfiguration == null || jsonMapperConfiguration.isEmpty()) {
            return def;
        }

        final String val = jsonMapperConfiguration.get(key);
        return val != null ? Boolean.parseBoolean(val) : def;
    }

    @Override
    public boolean isUseJodaDates() {
        return getConfiguredValue("useJodaDates", false);
    }

    @Override
    public boolean isUseJodaLocalDates() {
        return getConfiguredValue("useJodaLocalDates", false);
    }

    @Override
    public boolean isUseJodaLocalTimes() {
        return getConfiguredValue("useJodaLocalTimes", false);
    }

    @Override
    public String getDateTimeType() {
        return getConfiguredValueStr("dateTimeType", null);
    }

    @Override
    public String getDateType() {
        return getConfiguredValueStr("dateType", null);
    }

    @Override
    public String getTimeType() {
        return getConfiguredValueStr("timeType", null);
    }

    private String getConfiguredValueStr(final String key, final String def) {
        if (jsonMapperConfiguration == null || jsonMapperConfiguration.isEmpty()) {
            return def;
        }
        final String val = jsonMapperConfiguration.get(key);
        return val != null ? val : def;
    }
}
