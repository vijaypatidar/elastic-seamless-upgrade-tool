package co.hyperflex.pluginmanager;

import java.util.Map;
import java.util.Optional;

public class DefaultPluginSourceResolver implements PluginSourceResolver {
  private final PluginRegistry registry;
  private final Map<String, String> pluginRepo;

  public DefaultPluginSourceResolver(PluginRegistry registry, Map<String, String> pluginRepo) {
    this.registry = registry;
    this.pluginRepo = pluginRepo;
  }

  @Override
  public String resolve(String pluginName, String version) {
    if (registry.isOfficial(pluginName)) {
      return pluginName; // official plugins use name only
    }
    return Optional.ofNullable(pluginRepo.get(pluginName))
        .map(url -> url + version)
        .orElseThrow(() -> new IllegalArgumentException("Unknown plugin: " + pluginName));
  }
}
