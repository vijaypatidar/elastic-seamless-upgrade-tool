package co.hyperflex.pluginmanager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Component;

@Component
public class ElasticPluginArtifactValidator implements PluginArtifactValidator {

  @Override
  public boolean verifyPlugin(String pluginUrl, String targetEsVersion) {
    try (InputStream in = new URL(pluginUrl).openStream();
         ZipInputStream zip = new ZipInputStream(in)) {

      ZipEntry entry;
      while ((entry = zip.getNextEntry()) != null) {
        if (entry.getName().equals("plugin-descriptor.properties")) {
          Properties props = new Properties();
          props.load(zip);

          String pluginName = props.getProperty("name");
          String pluginEsVersion = props.getProperty("elasticsearch.version");
          String pluginJavaVersion = props.getProperty("java.version");

          if (pluginName == null || pluginEsVersion == null || pluginJavaVersion == null) {
            throw new IllegalArgumentException("Invalid plugin: missing required fields");
          }

          if (!pluginEsVersion.equals(targetEsVersion)) {
            throw new IllegalStateException(
                "Plugin built for Elasticsearch " + pluginEsVersion
                    + " but target cluster is " + targetEsVersion);
          }

          return true; // valid plugin
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    throw new IllegalArgumentException("Invalid plugin: no plugin-descriptor.properties found");
  }

}
