package co.hyperflex.prechecks.registry;

import co.hyperflex.prechecks.core.Precheck;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PrecheckAutoRegistrar {

  public PrecheckAutoRegistrar(List<Precheck<?>> discoveredPrechecks, PrecheckRegistry registry) {
    for (Precheck<?> precheck : discoveredPrechecks) {
      registry.register(precheck);
    }
  }

}