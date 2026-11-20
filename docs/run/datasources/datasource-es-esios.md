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

## Tariff Discovery

The ESIOS data source provides a discovery workflow that allows clients to explore available indicators before requesting tariff information.

The following endpoints are available:

| Method | Endpoint                                     | Description                                         |
|--------|----------------------------------------------|-----------------------------------------------------|
| GET    | `/countries/ES/tariffs/discovery`            | Entry point for the ESIOS tariff discovery workflow |
| GET    | `/countries/ES/tariffs/discovery/indicators` | Retrieve the list of available ESIOS indicators     |

### Discover indicators

The client starts the process by requesting the list of supported indicatory by ESIOS.

The response contains:

- The indicator information.
- A tariff information affordance referencing the tariff query endpoint.

The affordance describes the expected request type (`EsiosTariffQuery`) and allows clients to construct a valid tariff information request.

**Example request**

The following example illustrates the structure of the HTTP request with no request body required.

**Endpoint:** `GET /countries/ES/tariffs/discovery/indicators`

**Example response**

<details>
<summary>🔍 Click to expand JSON response</summary>

```json
{
  "_links": {
    "tariff-information": {
      "href": "http://localhost:8080/countries/ES/tariffs"
    }
  },
  "indicators": [
    {
      "name": "Generación programada PBF Hidráulica UGH",
      "id": 1,
      "short_name": "Hidráulica UGH",
      "description": "<p>Es el programa de energía diario, con desglose horario, de las diferentes Unidades de Programación correspondientes a ventas y adquisiciones de energía en el sistema eléctrico peninsular español. En concreto este indicador se refiere a las unidades de programación con tipo de producción hidráulica UGH.</p><p>Este programa es establecido por el OS a partir de la casación del OM y de las nominaciones de programas de todas y cada una de las Unidades de Programación que le han sido comunicadas por los sujetos titulares de dichas Unidades de Programación, incluyendo las correspondientes a la ejecución de contratos bilaterales con entrega física de los cuales se ha confirmado la ejecución.</p><p><b>Publicación:</b> diariamente a partir de las 13:45 horas con la información del día D+1.</p>"
    },
    ...
  ],
  "meta": {
    "size": 1982
  },
  "_templates": {
    "query": {
      "method": "POST",
      "properties": [
        {
          "name": "datetime",
          "type": "datetime"
        },
        {
          "name": "end_date",
          "type": "datetime"
        },
        {
          "name": "geo_agg",
          "options": {
            "inline": [
              "sum",
              "avg"
            ]
          }
        },
        {
          "name": "geo_ids"
        },
        {
          "name": "geo_trunc",
          "options": {
            "inline": [
              "country",
              "electric_system",
              "electric_system_peninsula",
              "electric_system_canary",
              "electric_system_balearic",
              "electric_system_ceuta",
              "electric_system_melilla",
              "electric_system_peninsula_ccaa",
              "electric_system_canary_ccaa",
              "electric_system_balearic_ccaa",
              "electric_system_ceuta_ccaa",
              "electric_system_melilla_ccaa",
              "autonomous_community",
              "province",
              "electric_subsystem",
              "town",
              "drainage_basin"
            ]
          }
        },
        {
          "name": "indicatorId",
          "required": true,
          "type": "number"
        },
        {
          "name": "language",
          "options": {
            "inline": [
              "es",
              "en"
            ]
          }
        },
        {
          "name": "start_date",
          "type": "datetime"
        },
        {
          "name": "time_agg",
          "options": {
            "inline": [
              "sum",
              "avg"
            ]
          }
        },
        {
          "name": "time_trunc",
          "options": {
            "inline": [
              "five_minutes",
              "ten_minutes",
              "fifteen_minutes",
              "hour",
              "day",
              "month",
              "year"
            ]
          }
        }
      ],
      "target": "http://localhost:8080/countries/ES/tariffs"
    }
  }
}
```

</details>

The client uses the selected indicator in the next tariff query request.

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
  "name": "Precio mercado SPOT Diario",
  "id": 600,
  "short_name": "Mercado SPOT",
  "composited": false,
  "step_type": "linear",
  "disaggregated": true,
  "geos": [
    {
      "geo_id": 1,
      "geo_name": "Portugal"
    },
    ...
  ],
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
