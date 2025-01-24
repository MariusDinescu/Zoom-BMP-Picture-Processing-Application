const express = require('express');
const snmp = require('net-snmp');
const MongoClient = require('mongodb').MongoClient;
const app = express();

const url = "mongodb://snmp_user:password123@localhost:27017/snmp_data?authSource=snmp_data";
const dbName = "snmp_data";
let db;

// Parametrii SNMP
const community = "public";
const oidCpu = "1.3.6.1.4.1.2021.11.10.0"; // OID utilizare CPU
const oidRam = "1.3.6.1.4.1.2021.4.6.0"; // OID utilizare RAM
const oidOs = "1.3.6.1.2.1.1.1.0"; // OID OS

const containere = [
    "co1", // C01
    "co2-broker", // C02
    "co3-consumer", // C03
    "co4-rmi", // C04
    "co5-rmi", // C05
];

// conexiune mongodb
const initDb = async () => {
    try {
        const client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
        await client.connect();
        db = client.db(dbName);
        console.log("Conectare la MongoDB cu succes.");
    } catch (err) {
        console.error("Eroare la conectarea la MongoDB:", err);
    }
};

const colecteazaDateSNMP = (target) => {
    const session = snmp.createSession(target, community);
    return new Promise((resolve, reject) => {
        session.get([oidCpu, oidRam, oidOs], function (error, varbinds) {
            if (error) {
                reject(`Eroare SNMP pentru ${target}: ${error}`);
            } else {
                const dateSnmp = {
                    targetIp: target,
                    cpuUsage: varbinds[0].value.toString(),
                    ramUsage: varbinds[1].value.toString(),
                    osName: varbinds[2].value.toString(),
                    timestamp: new Date()
                };
                resolve(dateSnmp);
            }
        });
    });
};

const salveazaDateSNMP = async (dateSnmp) => {
    try {
        const collection = db.collection("system_data");
        await collection.insertOne(dateSnmp);
        console.log(`Date SNMP pentru ${dateSnmp.targetIp} salvate in MongoDB.`);
    } catch (err) {
        console.error("Eroare la salvarea datelor in MongoDB:", err);
    }
};

const colecteazaSiSalveazaDate = async () => {
    console.log("Incepere colectare date SNMP...");
    for (const target of containere) {
        try {
            console.log(`Colectare date pentru container: ${target}`);
            const dateSnmp = await colecteazaDateSNMP(target);
            await salveazaDateSNMP(dateSnmp);
        } catch (err) {
            console.error(err);
        }
    }
};

setInterval(colecteazaSiSalveazaDate, 10 * 1000); // La fiecare 5 minute

// API ptr date snmp
app.get('/snmp-data', async (req, res) => {
    try {
        const collection = db.collection("system_data");
        const data = await collection.find({}).sort({ timestamp: -1 }).toArray();

        let html = `<html><head><title>Date SNMP</title><style>
            body { font-family: Arial, sans-serif; }
            .container { margin: 20px; padding: 15px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9; }
            h2 { color: #333; }
            .data { margin-bottom: 10px; }
            .data span { font-weight: bold; }
        </style></head><body>`;

        const dataGrupeaza = containere.map((container) => {
            let dataContainer = data.filter(item => item.targetIp === container);
            if (dataContainer.length > 0) {
                const ultimaData = dataContainer[0];
                return `
                    <div class="container">
                        <h2>Container: ${container}</h2>
                        <div class="data"><span>Utilizare CPU:</span> ${ultimaData.cpuUsage} %</div>
                        <div class="data"><span>Utilizare RAM:</span> ${ultimaData.ramUsage} MB</div>
                        <div class="data"><span>OS:</span> ${ultimaData.osName}</div>
                        <div class="data"><span>Timestamp:</span> ${new Date(ultimaData.timestamp).toLocaleString()}</div>
                    </div>
                `;
            } else {
                return `<div class="container"><h2>Container: ${container}</h2><div>Nu sunt date disponibile</div></div>`;
            }
        }).join('');

        html += dataGrupeaza;
        html += '</body></html>';

        res.send(html);
    } catch (err) {
        console.error("Eroare la recuperarea datelor SNMP:", err);
        res.status(500).send("Eroare la recuperarea datelor");
    }
});

const PORT = 3200;
app.listen(PORT, () => {
    console.log(`Serverul ruleaza pe http://localhost:${PORT}`);
	console.log(`Accesare endpoint SNMP este pe http://localhost:${PORT}/snmp-data`);
});

initDb().then(() => {
    colecteazaSiSalveazaDate(); // Prima rulare
});
