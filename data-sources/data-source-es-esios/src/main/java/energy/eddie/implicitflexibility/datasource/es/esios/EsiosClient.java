package energy.eddie.implicitflexibility.datasource.es.esios;

import energy.eddie.implicitflexibility.datasource.es.esios.config.EsiosProperties;
import energy.eddie.implicitflexibility.datasource.es.esios.models.Indicator;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnum;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosTariffQuery;
import energy.eddie.implicitflexibility.transport.exception.ClientExceptionHandler;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

@Component
public class EsiosClient {

    private final RestClient restClient;
    private final ClientExceptionHandler clientExceptionHandler;
    private final ObjectMapper objectMapper;

    public EsiosClient(
            RestClient.Builder restClientBuilder,
            EsiosProperties properties,
            ClientExceptionHandler clientExceptionHandler,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClientBuilder.baseUrl(properties.baseUrl().toString())
                                           .defaultHeaders(headers -> headers.setBearerAuth(
                                                   properties.token()))
                                           .build();
        this.clientExceptionHandler = clientExceptionHandler;
        this.objectMapper = objectMapper;
    }

    public Indicator getIndicatorTariff(EsiosTariffQuery query) {
        String indicator = "indicator";
        return clientExceptionHandler.execute(
                indicator,
                () -> {
                    JsonNode response = restClient.get()
                                                  .uri(buildIndicatorUri(query))
                                                  .retrieve()
                                                  .body(JsonNode.class);

                    if (response == null || !response.hasNonNull(indicator)) {
                        throw new IllegalStateException("ESIOS response does not contain an indicator");
                    }

                    return objectMapper.treeToValue(
                            response.get(indicator),
                            Indicator.class
                    );
                },
                EsiosDataSource.DATA_SOURCE_ID
        );
    }

    private URI buildIndicatorUri(EsiosTariffQuery query) {
        var builder = UriComponentsBuilder.fromPath("/indicators/{indicatorId}");

        addParam(builder, "locale", query.getLanguage());

        addParam(builder, "datetime", query.getDatetime());
        addParam(builder, "start_date", query.getStartDate());
        addParam(builder, "end_date", query.getEndDate());

        addParam(builder, "time_agg", query.getTimeAggregation());
        addParam(builder, "time_trunc", query.getTimeTruncation());


        if (query.getGeoIds() != null && !query.getGeoIds().isEmpty()) {
            builder.queryParam("geo_ids[]", query.getGeoIds().toArray());
        }

        addParam(builder, "geo_agg", query.getGeoAggregation());
        addParam(builder, "geo_trunc", query.getGeoTruncation());

        return builder.buildAndExpand(query.getIndicatorId())
                      .encode()
                      .toUri();
    }

    private void addParam(
            UriComponentsBuilder builder,
            String parameter,
            @Nullable Object value
    ) {
        if (value == null) {
            return;
        }

        Object parameterValue = value instanceof EsiosEnum esiosEnum
                ? esiosEnum.getValue()
                : value;

        builder.queryParam(parameter, parameterValue);
    }
}
