
import argparse
from llm import analyze_url
from utils import append_to_json_file

elastic_version_urls = {
    "8.0": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.0.html",
    "8.1": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.1.html",
    "8.2": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.2.html",
    "8.3": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.3.html",
    "8.4": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.4.html",
    "8.5": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.5.html",
    "8.6": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.6.html",
    "8.7": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.7.html",
    "8.8": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.8.html",
    "8.9": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.9.html",
    "8.10": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.10.html",
    "8.11": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.11.html",
    "8.12": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.12.html",
    "8.13": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.13.html",
    "8.14": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.14.html",
    "8.15": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.15.html",
    "8.16": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.16.html",
    "8.17": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.17.html",
    "8.18": "https://www.elastic.co/guide/en/elasticsearch/reference/8.19/migrating-8.19.html"
}


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Analyze Elasticsearch breaking changes docs")
    parser.add_argument("--version", required=True, help="Elasticsearch version (e.g., 8.5)")
    version = parser.parse_args().version
    test_url = elastic_version_urls.get(version)   

    result = analyze_url(test_url, version)
    if result:
        append_to_json_file(result, file_path="breaking.json")
