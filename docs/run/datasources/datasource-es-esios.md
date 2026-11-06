# Data Source for Spain (ESIOS)

## Overview

The ESIOS data source integrates the EDDIE Implicit Flexibility Tariff Information Framework with the Spanish [ESIOS](https://www.esios.ree.es/en) service.

It implements the framework interfaces required to expose tariff information and provider-specific discovery operations.

## Configuration

| Property                        | Purpose                                    | Required                    |
|---------------------------------|--------------------------------------------|-----------------------------|
| `data-source.es.esios.enabled`  | Enables or disables the ESIOS integration. | Yes                         |
| `data-source.es.esios.base-url` | Base URL of the ESIOS service.             | Yes when enabled            |
| `data-source.es.esios.token`    | Token used for provider authentication.    | As required by the provider |
