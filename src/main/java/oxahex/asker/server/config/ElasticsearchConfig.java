//package oxahex.asker.server.config;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import co.elastic.clients.transport.ElasticsearchTransport;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import lombok.RequiredArgsConstructor;
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class ElasticsearchConfig {
//
//	@Value("${spring.elasticsearch.uris}")
//	private String uri;
//
//	/**
//	 * Elasticsearch 클라이언트 빈 등록
//	 */
//	@Bean
//	public ElasticsearchClient elasticsearchClient() {
//		RestClient restClient = RestClient
//				.builder(HttpHost.create(uri))
//				.build();
//		ElasticsearchTransport transport = new RestClientTransport(
//				restClient, new JacksonJsonpMapper());
//		return new ElasticsearchClient(transport);
//	}
//}
