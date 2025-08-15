const jsonata = require('jsonata');

const fs = require('node:fs');
const { createReadStream } = require('node:fs');

const { createInterface } = require('node:readline');
const convert_bsm = fs.readFileSync('./convert-bsm.jsonata', 'utf8');
const convert_spat = fs.readFileSync('./convert-spat.jsonata', 'utf8');
const convert_map = fs.readFileSync('./convert-map.jsonata', 'utf8');
const bsmExpression = jsonata(convert_bsm);
const spatExpression = jsonata(convert_spat);
const mapExpression = jsonata(convert_map);

async function convertBsm(bsm) {
    const result = await bsmExpression.evaluate(JSON.parse(bsm));
    return JSON.stringify(result);
}

async function convertSpat(spat) {
    const result = await spatExpression.evaluate(JSON.parse(spat));
    return JSON.stringify(result);
}

async function convertMap(map) {
    const result = await mapExpression.evaluate(JSON.parse(map));
    return JSON.stringify(result);
}

if (process.argv.length < 4) {
    console.log("Usage:\nnode convert.js input-old-script-filename output-new-script-filename");
    return;
}

const infile = process.argv[2];
const outfile = process.argv[3];


(async () => {

    const fileStream = createReadStream(infile);
    const rl = createInterface({
        input: fileStream,
        crlfDelay: Infinity,
    });

    let outStr = '';

    for await (const line of rl) {
        const i1 = line.indexOf(",");
        const i2 = line.indexOf(",", i1 + 1);
        const type = line.substring(0, i1);

        if (type === 'BSM' || type === 'SPAT' || type === 'MAP') {
            const timestamp = line.substring(i1 + 1, i2);
            const msg = line.substring(i2 + 1);
            let convertedMsg;
            if (type === 'BSM') {
                convertedMsg = await convertBsm(msg);
            } else if (type === 'SPAT') {
                convertedMsg = await convertSpat(msg);
            } else if (type === 'MAP') {
                convertedMsg = await convertMap(msg);
            }
            outStr += type + "," + timestamp + "," + convertedMsg + '\n';
        } else {
            // Processed types: don't need be converted
            outStr += line + '\n';
        }
    }

    fs.writeFileSync(outfile, outStr);
})()