package co.hyperflex.pluginmanager;

public class KibanaPluginArtifactValidator implements PluginArtifactValidator {

  @Override
  public boolean verifyPlugin(String pluginUrl, String version) {
    return true;
  }

}
