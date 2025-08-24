package co.hyperflex.clients.elastic;

public class ElasticRequest<T> {
  private final String uri;
  private final Object body;
  private final Class<T> responseType;
  private final HttpMethod method;

  public ElasticRequest(String uri, Object body, Class<T> responseType, HttpMethod method) {
    this.uri = uri;
    this.body = body;
    this.responseType = responseType;
    this.method = method;
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

  public enum HttpMethod {
    GET, POST, PUT, DELETE
  }
}

