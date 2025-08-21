# Script to convert old to new script format

A node script using [JSONata](https://jsonata.org/) to convert integration scripts with the old MAP/SPAT/BSM JSON format (ODE versions prior to v5.0.0) to the new format (ODE v5.0.0+)

## Prerequisites

* node
* npm

## Install

```bash
npm install
```

## Usage

```bash
node convert.js input-old-script-filename output-new-script-filename
```

### Example

```bash
node convert.js ../Script-BSMs-old.csv ../Script-BSMs.csv
```