package co.hyperflex.clients.elastic.dto.nodes;

public class Jvm {
  private JvmMemoryStats mem;

  public JvmMemoryStats getMem() {
    return mem;
  }

  public void setMem(JvmMemoryStats mem) {
    this.mem = mem;
  }
}
