package foundation.stack.datamill.configuration;

import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PropertiesFile {
    private final Properties properties;
    private final String propertiesLocation;

    PropertiesFile(String propertiesLocation) throws IOException {
        this.propertiesLocation = propertiesLocation;
        this.properties = new Properties();

        File propertiesFile = new File(propertiesLocation);
        if (propertiesFile.isFile()) {
            try (FileInputStream fileStream = new FileInputStream(propertiesFile)) {
                properties.load(fileStream);
            }
        } else {
            try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(propertiesLocation)) {
                properties.load(resourceStream);
            }
        }
    }

    public Optional<String> getOptional(String name) {
        String value = getProperty(name, false);
        return Optional.ofNullable(value);
    }

    private String getProperty(String name, boolean required) {
        String value = properties.getProperty(name);

        if (value == null && required) {
            throw new IllegalStateException("Expected " + name + " to be found in file " + propertiesLocation);
        }

        return value;
    }

    public Value getRequired(String name) {
        return new StringValue(getProperty(name, true));
    }
}
