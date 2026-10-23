# API Overview

The EDDIE Implicit Flexibility API exposes endpoints for country discovery, tariff information and provider-specific tariff discovery.

## Countries

| Method | Endpoint                   | Description                                |
|--------|----------------------------|--------------------------------------------|
| GET    | `/countries`               | List supported countries                   |
| GET    | `/countries/{countryCode}` | Retrieve supported endpoints for a country |

## Tariffs

| Method | Endpoint                           | Description                            |
|--------|------------------------------------|----------------------------------------|
| GET    | `/countries/{countryCode}/tariffs` | Discover available tariff interactions |
| POST   | `/countries/{countryCode}/tariffs` | Retrieve tariff information            |

## Country-Specific Requests

The request structures and available discovery operations may differ between countries and data sources.

Check out the country-specific documentation for details.