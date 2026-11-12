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

import java.net.URI;

@Component
public class EsiosClient {

    private final RestClient restClient;
    private final ClientExceptionHandler clientExceptionHandler;

    public EsiosClient(
            RestClient.Builder restClientBuilder,
            EsiosProperties properties,
            ClientExceptionHandler clientExceptionHandler
    ) {
        this.restClient = restClientBuilder.baseUrl(properties.baseUrl().toString())
                                           .defaultHeaders(headers -> headers.setBearerAuth(
                                                   properties.token()))
                                           .build();
        this.clientExceptionHandler = clientExceptionHandler;
    }

    public Indicator getIndicatorTariff(EsiosTariffQuery query) {
        return clientExceptionHandler.execute(
                "indicator",
                () -> restClient.get()
                                .uri(buildIndicatorUri(query))
                                .retrieve()
                                .requiredBody(Indicator.class),
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
