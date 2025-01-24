const express = require("express");
const cors = require("cors");
const app = express();
const port = 8080;

// Middleware for parsing JSON and CORS
app.use(express.json());
app.use(cors());

let downloadLink = ""; // Variable to store the download link

// Endpoint to receive the notification from backend app
app.post("/api/image", (req, res) => {
  const { imageLink } = req.body;

  if (!imageLink) {
    return res.status(400).json({ message: "Lipsește link-ul imaginii!" });
  }

  // Update the download link
  downloadLink = imageLink;
  console.log(`Link-ul imaginii a fost actualizat: ${imageLink}`);
  res
    .status(200)
    .json({ message: "Link-ul imaginii a fost primit cu succes!" });
});

// Endpoint to get the current image link
app.get("/api/image", (req, res) => {
  res.json({ imageLink: downloadLink });
});

// Start the server
app.listen(port, () => {
  console.log(`Serverul rulează pe http://localhost:${port}`);
});
