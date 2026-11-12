package energy.eddie.implicitflexibility.datasource.es.esios.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import energy.eddie.implicitflexibility.datasource.es.esios.models.Aggregation;
import energy.eddie.implicitflexibility.datasource.es.esios.models.GeoTruncation;
import energy.eddie.implicitflexibility.datasource.es.esios.models.Language;
import energy.eddie.implicitflexibility.datasource.es.esios.models.TimeTruncation;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.InputType;

import java.time.OffsetDateTime;
import java.util.List;

@ValidEsiosTariffQuery
public class EsiosTariffQuery {

    @NotNull
    private int indicatorId;

    @Nullable
    private Language language;

    @Nullable
    @InputType("datetime")
    private OffsetDateTime datetime;

    @JsonProperty("start_date")
    @Nullable
    @InputType("datetime")
    private OffsetDateTime startDate;

    @JsonProperty("end_date")
    @Nullable
    @InputType("datetime")
    private OffsetDateTime endDate;

    @JsonProperty("time_agg")
    @Nullable
    private Aggregation timeAggregation;

    @JsonProperty("time_trunc")
    @Nullable
    private TimeTruncation timeTruncation;

    @JsonProperty("geo_ids")
    @Nullable
    private List<Integer> geoIds;

    @JsonProperty("geo_agg")
    @Nullable
    private Aggregation geoAggregation;

    @JsonProperty("geo_trunc")
    @Nullable
    private GeoTruncation geoTruncation;

    public EsiosTariffQuery() {
    }

    public EsiosTariffQuery(
            int indicatorId,
            @Nullable Language language,
            @Nullable OffsetDateTime startDate,
            @Nullable OffsetDateTime endDate,
            @Nullable Aggregation timeAggregation,
            @Nullable TimeTruncation timeTruncation,
            @Nullable List<Integer> geoIds,
            @Nullable Aggregation geoAggregation,
            @Nullable GeoTruncation geoTruncation
    ) {
        this.indicatorId = indicatorId;
        this.language = language;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeAggregation = timeAggregation;
        this.timeTruncation = timeTruncation;
        this.geoIds = geoIds;
        this.geoAggregation = geoAggregation;
        this.geoTruncation = geoTruncation;
    }

    public EsiosTariffQuery(
            int indicatorId,
            @Nullable Language language,
            @Nullable OffsetDateTime datetime
    ) {
        this.indicatorId = indicatorId;
        this.language = language;
        this.datetime = datetime;
    }

    public int getIndicatorId() {
        return indicatorId;
    }

    @Nullable
    public Language getLanguage() {
        return language;
    }

    @Nullable
    public OffsetDateTime getDatetime() {
        return datetime;
    }

    @Nullable
    public OffsetDateTime getStartDate() {
        return startDate;
    }

    @Nullable
    public OffsetDateTime getEndDate() {
        return endDate;
    }

    @Nullable
    public Aggregation getTimeAggregation() {
        return timeAggregation;
    }

    @Nullable
    public TimeTruncation getTimeTruncation() {
        return timeTruncation;
    }

    @Nullable
    public List<Integer> getGeoIds() {
        return geoIds;
    }

    @Nullable
    public Aggregation getGeoAggregation() {
        return geoAggregation;
    }

    @Nullable
    public GeoTruncation getGeoTruncation() {
        return geoTruncation;
    }

    public void setIndicatorId(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    public void setLanguage(@Nullable Language language) {
        this.language = language;
    }

    public void setDatetime(@Nullable OffsetDateTime datetime) {
        this.datetime = datetime;
    }

    public void setStartDate(@Nullable OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(@Nullable OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    public void setTimeAggregation(@Nullable Aggregation timeAggregation) {
        this.timeAggregation = timeAggregation;
    }

    public void setTimeTruncation(@Nullable TimeTruncation timeTruncation) {
        this.timeTruncation = timeTruncation;
    }

    public void setGeoIds(@Nullable List<Integer> geoIds) {
        this.geoIds = geoIds;
    }

    public void setGeoAggregation(@Nullable Aggregation geoAggregation) {
        this.geoAggregation = geoAggregation;
    }

    public void setGeoTruncation(@Nullable GeoTruncation geoTruncation) {
        this.geoTruncation = geoTruncation;
    }
}
