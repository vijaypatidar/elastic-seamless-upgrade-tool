package co.hyperflex.clients.kibana;

import co.hyperflex.common.http.HttpMethod;
import java.util.HashMap;
import java.util.Map;

public class KibanaRequest<T> {
  private final String uri;
  private final Object body;
  private final Class<T> responseType;
  private final HttpMethod method;
  private final Map<String, Object> headers;

  private KibanaRequest(Builder<T> builder) {
    this.uri = builder.uri;
    this.body = builder.body;
    this.responseType = builder.responseType;
    this.method = builder.method;
    this.headers = builder.headers;
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

  public Map<String, Object> getHeaders() {
    return headers;
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
    private Map<String, Object> headers;
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

    public Builder<T> header(Map<String, Object> headers) {
      this.headers = headers;
      return this;
    }

    public Builder<T> addHeader(String name, Object value) {
      if (headers == null) {
        headers = new HashMap<>();
      }
      headers.put(name, value);
      return this;
    }


    public Builder<T> method(HttpMethod method) {
      this.method = method;
      return this;
    }

    public KibanaRequest<T> build() {
      if (uri == null || method == null) {
        throw new IllegalStateException("uri and method are required");
      }
      return new KibanaRequest<>(this);
    }
  }
}
