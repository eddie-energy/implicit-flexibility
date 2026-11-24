# Data Source for Spain (ESIOS)

## Overview

The ESIOS data source integrates the EDDIE Implicit Flexibility Tariff Information Framework with the Spanish [ESIOS (Electricity System Information)](https://www.esios.ree.es/en) service, operated by Red Eléctrica de España (REE).

It implements the framework interfaces required to expose tariff information and provider-specific discovery operations.

## Configuration

| Property                        | Purpose                                    | Required                    |
|---------------------------------|--------------------------------------------|-----------------------------|
| `data-source.es.esios.enabled`  | Enables or disables the ESIOS integration. | Yes                         |
| `data-source.es.esios.base-url` | Base URL of the ESIOS service.             | Yes when enabled            |
| `data-source.es.esios.token`    | Token used for provider authentication.    | As required by the provider |

## Tariff Query

**Endpoint:** `POST /countries/ES/tariffs`

**Example request**

```json
{
  "indicatorId": 600
}
```

**Example response**

<details>
<summary>🔍 Click to expand JSON response</summary>

```json
{
  "indicator": {
    "name": "Precio mercado SPOT Diario",
    "short_name": "Mercado SPOT",
    "id": 600,
    "composited": false,
    "step_type": "linear",
    "disaggregated": true,
    "magnitud": [
      {
        "name": "Precio €/MWh",
        "id": 23
      }
    ],
    "tiempo": [
      {
        "name": "Quince minutos",
        "id": 218
      }
    ],
    "geos": [
      {
        "geo_id": 1,
        "geo_name": "Portugal"
      },
      ...
    ],
    "values_updated_at": "2026-11-05T14:46:50.000+01:00",
    "values": [
      {
        "value": 275.3,
        "datetime": "2026-11-06T00:00:00.000+01:00",
        "datetime_utc": "2026-11-05T23:00:00Z",
        "tz_time": "2026-11-05T23:00:00.000Z",
        "geo_id": 1,
        "geo_name": "Portugal"
      },
      ...
    ]
  }
}
```

</details>

The client submits an `EsiosTariffQuery` to request the desired tariff information.

The query is composed of the following:

| Property      | Type             | Required | Description                                              |
|---------------|------------------|----------|----------------------------------------------------------|
| `indicatorId` | `int`            | Yes      | Identifier of the ESIOS indicator.                       |
| `language`    | `Language`       | No       | Language of the returned information.                    |
| `datetime`    | `OffsetDateTime` | No       | Specific timestamp for which values should be retrieved. |
| `start_date`  | `OffsetDateTime` | No       | Beginning of the requested time interval.                |
| `end_date`    | `OffsetDateTime` | No       | End of the requested time interval.                      |
| `time_agg`    | `Aggregation`    | No       | Aggregation operation applied over time.                 |
| `time_trunc`  | `TimeTruncation` | No       | Temporal grouping interval.                              |
| `geo_ids`     | `List<Integer>`  | No       | Identifiers of the geographical areas to retrieve.       |
| `geo_agg`     | `Aggregation`    | No       | Geographical aggregation operation.                      |
| `geo_trunc`   | `GeoTruncation`  | No       | Geographical grouping level.                             |

All properties except `indicatorId` are optional. If no filters are provided, the indicator is retrieved without additional filtering criteria.

The `language` determines which translations are returned by ESIOS. Supported values are `es` and `en`.

### Time filter

The time filtering `datetime` or `start_date` and `end_date` allow retrieving indicator values for a specific point in time or within a date range.
Timestamps use ISO 8601 format, including timezone information.

**Validation rules:**

- `datetime` cannot be combined with `startDate` or `endDate`.
- `startDate` and `endDate` must either both be provided or both be omitted.
- `startDate` must not be later than `endDate`.

### Time aggregation filter

The time aggregation filtering `time_agg` and `time_trunc` control how indicator values are aggregated over time.
Supported values for `time_agg` are `sum` and `avg`.

For `time_trunc`, the possible values are `five_minutes`, `ten_minutes`, `fifteen_minutes`, `hour`, `day`, `month` and `year`.

### Geo filter

The geo filtering property `geo_ids` allows restricting indicator values to specific geographical
areas and optionally aggregating them geographically using `geo_agg` and `geo_trunc`.
Supported geographical aggregation values are `sum` and `avg`.

The geographical truncation determines the level at which values are grouped. Examples according to the [ESIOS API](https://api.esios.ree.es/) include:

- `country`
- `electric_system`
- `electric_system_peninsula`
- `electric_system_canary`
- `electric_system_balearic`
- `electric_system_ceuta`
- `electric_system_melilla`
- `electric_system_peninsula_ccaa`
- `electric_system_canary_ccaa`
- `electric_system_balearic_ccaa`
- `electric_system_ceuta_ccaa`
- `electric_system_melilla_ccaa`
- `autonomous_community`
- `province`
- `electric_subsystem`
- `town`
- `drainage_basin`
