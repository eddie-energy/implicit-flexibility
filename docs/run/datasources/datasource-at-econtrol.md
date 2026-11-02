# Data Source for Austria (E-Control)

## Overview

The E-Control data source integrates the EDDIE Implicit Flexibility Tariff Information Framework with the Austrian [E-Control](https://www.e-control.at/) service.

It implements the framework interfaces required to expose tariff information and provider-specific discovery operations.

## Configuration

| Property                            | Purpose                                        | Required                    |
|-------------------------------------|------------------------------------------------|-----------------------------|
| `data-source.at.e-control.enabled`  | Enables or disables the E-Control integration. | Yes                         |
| `data-source.at.e-control.base-url` | Base URL of the E-Control service.             | Yes when enabled            |
| `data-source.at.e-control.username` | Username used for provider authentication.     | As required by the provider |
| `data-source.at.e-control.password` | Password used for provider authentication.     | As required by the provider |

## Tariff Discovery

The E-Control implementation provides a step-by-step discovery workflow that allows a client to identify a suitable electricity or gas product and retrieve its tariff information.

The workflow consists of the following steps:

1. Discover available grid operators.
2. Discover brands associated with a selected grid operator.
3. Discover products for the selected grid operator and brand.
4. Select a product and obtain its product identifier.
5. Retrieve the requested tariff information.

The discovery process uses country-specific endpoints. For Austria, the following endpoints are supported for tariff discovery in addition to `/countries/AT/tariffs`.

| Method | Endpoint                                         | Description             |
|--------|--------------------------------------------------|-------------------------|
| GET    | `/countries/AT/tariffs/discovery`                | Start tariff discovery  |
| POST   | `/countries/AT/tariffs/discovery/grid-operators` | Discover grid operators |
| POST   | `/countries/AT/tariffs/discovery/brands`         | Discover brands         |
| POST   | `/countries/AT/tariffs/discovery/products`       | Discover products       |

### Step 1: Discover grid operators

**Endpoint:** `POST /countries/AT/tariffs/discovery/grid-operators`

The client starts the discovery process by submitting a grid-operator discovery request.

The request body must follow the schema defined by the E-Control implementation's grid-operator query type. The server processes the request through the E-Control data source and returns the available grid operators.

The response provides the information the client needs to select a grid operator before proceeding to the brand discovery step.

**Example request**

The following example illustrates the structure of the HTTP request. Replace the request body with the fields required by the actual grid-operator query schema.

```http
POST /countries/AT/tariffs/discovery/grid-operators HTTP/1.1
Content-Type: application/json
```

```json
{
  "zipCode": "4040",
  "energyType": "POWER"
}
```

**Example response**

The response below illustrates the expected documentation format.

<details>
<summary>🔍 Click to expand JSON response</summary>

```json
{
  "_embedded": {
    "gridOperatorList": [
      {
        "_links": {
          "brands": {
            "href": "http://localhost:8080/countries/AT/tariffs/brands"
          }
        },
        "assignType": "AUTO",
        "brandBusiness": 2951,
        "brandHome": 2951,
        "energyType": "POWER",
        "gridAreaId": 301,
        "id": 14951,
        "name": "LINZ NETZ GmbH",
        "status": "ACTIVE",
        "version": 0,
        "zipCode": "4040",
        "zipCodesCount": 0,
        "_templates": {
          "brands": {
            "method": "POST",
            "properties": [
              {
                "name": "customerGroup",
                "options": {
                  "inline": [
                    "HOME",
                    "BUSINESS",
                    "HOME_BUSINESS"
                  ]
                },
                "required": true
              },
              {
                "name": "energyType",
                "options": {
                  "inline": [
                    "POWER",
                    "GAS"
                  ]
                },
                "required": true
              },
              {
                "name": "gridOperatorId",
                "required": true,
                "type": "number"
              }
            ],
            "target": "http://localhost:8080/countries/AT/tariffs/brands"
          }
        }
      }
    ]
  }
}
```

</details>

Each returned grid operator should be presented with its identifying information and any additional attributes provided by the implementation.

The client uses the selected grid operator in the next discovery request.

### Step 2: Discover brands

**Endpoint:** `POST /countries/AT/tariffs/discovery/brands`

After selecting a grid operator, the client submits a brand discovery request.

The request includes the selection from the previous step, using the identifier and field names defined by the E-Control `BrandSearch` query type.

The server returns the brands available for the selected grid operator. The client can then choose a brand and continue to product discovery.

**Example request**

```http
POST /countries/AT/tariffs/discovery/brands HTTP/1.1
Content-Type: application/json
```

```json
{
  "gridOperatorId": 14951,
  "energyType": "POWER",
  "customerGroup": "HOME"
}
```

**Example response**

<details>
<summary>🔍 Click to expand JSON response</summary>

```json
{
  "_links": {
    "products": {
      "href": "http://localhost:8080/countries/AT/tariffs/products"
    }
  },
  "brandData": [
    {
      "gas": false,
      "id": 7651,
      "name": "AAE Naturstrom Vertrieb GmbH",
      "power": true
    },
    ...
  ],
  "defaultId": 2951,
  "_templates": {
    "products": {
      "method": "POST",
      "properties": [
        {
          "name": "brandId",
          "type": "number"
        },
        {
          "name": "comparisonOptions"
        },
        {
          "name": "customerGroup",
          "options": {
            "inline": [
              "HOME",
              "BUSINESS",
              "HOME_BUSINESS"
            ]
          },
          "required": true
        },
        {
          "name": "energyType",
          "options": {
            "inline": [
              "POWER",
              "GAS"
            ]
          },
          "required": true
        },
        {
          "name": "feedInRequestOptions"
        },
        {
          "name": "firstMeterOptions",
          "required": true
        },
        {
          "name": "gridAreaId",
          "required": true,
          "type": "number"
        },
        {
          "name": "gridOperatorId",
          "required": true,
          "type": "number"
        },
        {
          "name": "includeFeedInOptions"
        },
        {
          "name": "includeSwitchingDiscounts"
        },
        {
          "name": "membership",
          "type": "number"
        },
        {
          "name": "moveHome"
        },
        {
          "name": "priceView",
          "options": {
            "inline": [
              "EUR_PER_YEAR",
              "CENT_PER_YEAR",
              "ENERGY_PRICE",
              "SPOT_MARKET_MARGIN",
              "SPOT_MARKET_BREAKDOWN"
            ]
          }
        },
        {
          "name": "rateRequestDate",
          "type": "date"
        },
        {
          "name": "rcSearchMode",
          "options": {
            "inline": [
              "STANDARD",
              "PREVIEW",
              "EVALUATION",
              "WIDGET"
            ]
          }
        },
        {
          "name": "referencePeriod",
          "options": {
            "inline": [
              "ONE_YEAR",
              "TWO_YEARS",
              "THREE_YEARS"
            ]
          }
        },
        {
          "name": "requirements"
        },
        {
          "name": "secondMeterOptions"
        },
        {
          "name": "smartMeter"
        },
        {
          "name": "supplierId",
          "type": "number"
        },
        {
          "name": "zipCode",
          "required": true,
          "type": "number"
        }
      ],
      "target": "http://localhost:8080/countries/AT/tariffs/products"
    }
  }
}
```

</details>

The client should retain the selected brand's identifier and provide it, together with the relevant grid-operator selection, when discovering products.

The exact field names and response structure depend on the E-Control query and response types used by the implementation.

### Step 3: Discover products

**Endpoint:** `POST /countries/AT/tariffs/discovery/products`

The client submits a product discovery request using the selected grid operator and brand, together with the other criteria required by the product query.

**Example request**

```http
POST /countries/AT/tariffs/discovery/products HTTP/1.1
Content-Type: application/json
```

```json
{
  "zipCode": 4040,
  "gridOperatorId": 14951,
  "gridAreaId": 301,
  "energyType": "POWER",
  "customerGroup": "HOME",
  "brandId": 7651,
  "firstMeterOptions": {
    "standardConsumption": 5000
  }
}
```

**Example response**

<details>
<summary>🔍 Click to expand JSON response</summary>

```json
{
  "_links": {
    "tariff-information": {
      "href": "http://localhost:8080/countries/AT/tariffs"
    }
  },
  "defaultId": 1096623,
  "productData": [
    {
      "mainAssociationId": 23439,
      "mainId": 1096623,
      "name": "AAE Naturstrom",
      "productValidityFrom": 1654120800000,
      "regularCustomersFrom": 1654120800000
    },
    ...
  ],
  "_templates": {
    "query": {
      "method": "POST",
      "properties": [
        {
          "name": "informationType",
          "options": {
            "inline": [
              "CONTRACT",
              "PRICE_INFO"
            ]
          },
          "readOnly": true,
          "required": true
        },
        {
          "name": "productId",
          "readOnly": true,
          "regex": "^(?=\\s*\\S).*$",
          "required": true,
          "type": "text"
        }
      ],
      "target": "http://localhost:8080/countries/AT/tariffs"
    }
  }
}
```

</details>

The client selects a product from the returned results and retains its identifier for the next step.

**Endpoint:** `POST /countries/AT/tariffs`

Finally, the client submits an `EControlTariffQuery` to request the desired tariff information.

The query contains two fields:

| Field             | Description                                                              |
|-------------------|--------------------------------------------------------------------------|
| `productId`       | Identifier of the selected E-Control product.                            |
| `informationType` | Specifies which type of tariff information the client wants to retrieve. |

The `informationType` field supports the following values:

| Value        | Description                                             | Result                                                             |
|--------------|---------------------------------------------------------|--------------------------------------------------------------------|
| `CONTRACT`   | Requests contract information for the selected product. | The result of the E-Control contract retrieval operation.          |
| `PRICE_INFO` | Requests price information for the selected product.    | The result of the E-Control price-information retrieval operation. |

The returned result depends on the selected information type and the corresponding E-Control API response.

#### Request schema

The `EControlTariffQuery` request is represented in JSON as follows:

```json
{
  "informationType": "CONTRACT",
  "productId": "PRODUCT_ID"
}
```

`PRODUCT_ID` is a placeholder and must be replaced with the identifier obtained during product discovery.

The server then retrieves contract information for the specified product and returns the result to the client.
