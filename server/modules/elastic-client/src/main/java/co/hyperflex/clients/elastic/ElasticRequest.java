package co.hyperflex.clients.elastic;

public class ElasticRequest<T> {
  private final String uri;
  private final Object body;
  private final Class<T> responseType;
  private final HttpMethod method;

  private ElasticRequest(Builder<T> builder) {
    this.uri = builder.uri;
    this.body = builder.body;
    this.responseType = builder.responseType;
    this.method = builder.method;
  }

  public String getUri() {
    return uri;
  }

  public Object getBody() {
    return body;
  }

  public Class<T> getResponseType() {
    return responseType;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public static Object builder(HttpMethod method) {
    return builder(Object.class);
  }

  public static <T> Builder<T> builder(Class<T> responseType) {
    return new Builder<>(responseType);
  }

  public static class Builder<T> {
    private String uri;
    private Object body;
    private HttpMethod method;
    private final Class<T> responseType;

    public Builder(Class<T> responseType) {
      this.responseType = responseType;
    }

    public Builder<T> uri(String uri) {
      this.uri = uri;
      return this;
    }

    public Builder<T> body(Object body) {
      this.body = body;
      return this;
    }

    public Builder<T> method(HttpMethod method) {
      this.method = method;
      return this;
    }

    public ElasticRequest<T> build() {
      if (uri == null || method == null) {
        throw new IllegalStateException("uri and method are required");
      }
      return new ElasticRequest<>(this);
    }
  }
}
