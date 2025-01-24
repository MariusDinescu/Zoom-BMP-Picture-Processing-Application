const express = require("express");
const mysql = require("mysql2");
const bodyParser = require("body-parser");
const app = express();
const port = 3100;
const cors = require("cors");


app.use(bodyParser.json());
app.use(bodyParser.raw({ type: "application/octet-stream", limit: "50mb" }));
app.use(cors({
        origin: "http://localhost:3000",
        methods: ["GET","POST"],
        allowedHeaders: ["Content-Type","Authorization"],
}));
// conexiune MySQL
const db = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "root",
  database: "co6_photos",
});

db.connect((err) => {
  if (err) {
    console.error("Eroare la conectarea la baza de date:", err.message);
    process.exit(1);
  }
  console.log("Conectat la baza de date MySQL.");
});

app.post("/save-image", (req, res) => {
  const imageData = req.body;

  const sql = "INSERT INTO processed_images (image_data) VALUES (?)";
  db.query(sql, [imageData], (err, result) => {
    if (err) {
      console.error("Eroare la salvarea imaginii:", err.message);
      return res.status(500).send("Eroare la salvarea imaginii.");
    }

    const imageLink = `http://localhost:3100/images/${result.insertId}`;
    console.log("Imaginea a fost salvata cu  succes in baza de date.");
    res.status(200).send(imageLink);
  });
});

app.get("/images/:id", (req, res) => {
  const imageId = req.params.id;

  const sql = "SELECT image_data FROM processed_images WHERE id = ?";
  db.query(sql, [imageId], (err, result) => {
    if (err) {
      console.error("Eroare la accesarea imaginii:", err.message);
      return res.status(500).send("Eroare la accesarea imaginii.");
    }
    if (result.length === 0) {
      return res.status(404).send("Imaginea nu a fost găsită.");
    }
    res.type("image/bmp").send(result[0].image_data);
  });
});

app.post("/notify", (req, res) => {
  const { status, downloadLink } = req.body;

  if (status === "done" && downloadLink) {
    // Simulăm o notificare către front-end
    console.log(
      `Imaginea a fost procesata si este disponibila pentru descarcare: ${downloadLink}`
    );

    // Redirecționare către link-ul de descărcare al imaginii
    res
      .status(200)
      .send(`Imaginea procesata este gata. Descarca de la: ${downloadLink}`);
  } else {
    res.status(400).send("Payload invalid.");
  }
});

app.listen(port, () => {
  console.log(`Serverul rulează la http://172.18.0.5:${port}`);
});
