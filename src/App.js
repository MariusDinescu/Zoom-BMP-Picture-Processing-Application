import React, { useState } from "react";
import "./App.css";

function App() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [zoomLevel, setZoomLevel] = useState(100);
  const [loading, setLoading] = useState(false);
  const [imageProcessed, setImageProcessed] = useState(false);
  const [processedImageLink, setProcessedImageLink] = useState("");

  // incarcare imagine
  const uploadImage = async () => {
    if (!selectedFile) {
      alert("Selecteaza un fisier!");
      return;
    }

    if (selectedFile.type !== "image/bmp") {
      alert("Fisierul selectat trebuie sa fie de tip BMP!");
      return;
    }

    if (selectedFile.size > 5 * 1024 * 1024) {
      alert("Fisierul este prea mare, limita este 5MB.");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("zoom", zoomLevel.toString());

    try {
      setLoading(true);

      const response = await fetch("http://localhost:8081/api/upload", {
        method: "POST",
        body: formData,
        headers: {
          Accept: "application/json",
        },
        mode: "cors",
      });

      if (response.ok) {
        alert("Imaginea a fost incarcata cu succes!");
      } else {
        const errorText = await response.text();
        alert("Eroare la incarcarea imaginii: " + errorText);
      }
    } catch (error) {
      alert("Eroare la trimiterea cererii: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  // functie obtinere link imagine 
  const fetchProcessedImage = async () => {
    try {
      const response = await fetch("http://localhost:8081/api/processed-image");
      const data = await response.json();
      if (data.imageLink) {
        setProcessedImageLink(data.imageLink);
        setImageProcessed(true);
      } else {
        alert("Imaginea nu a fost procesata inca.");
      }
    } catch (error) {
      console.error("Eroare la obtinerea imaginii procesate:", error);
    }
  };

  const downloadImage = async (imageLink) => {
    try {
      // verificare link daca e valid
      if (!imageLink) {
        alert("Link-ul imaginii procesate nu este valid.");
        return;
      }

      // descarcare imagine
      const response = await fetch(imageLink);

      if (response.ok) {
        const blob = await response.blob();
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob); // creare url temporar ptr blob
        link.download = "imagine_procesata.bmp"; // nume fisier descarcat

        document.body.appendChild(link);
        link.click();

        document.body.removeChild(link);
      } else {
        alert("Eroare la descarcarea imaginii.");
      }
    } catch (error) {
      console.error("Eroare la procesul de descarcare:", error);
      alert("A aparut o eroare la descarcarea imaginii.");
    }
  };



  return (
    <div className="App">
      <h1>Incarcare imagine BMP si Zoom</h1>
      <div className="form-container">
        <label>
          Alege un fisier BMP:
          <input
            type="file"
            accept=".bmp"
            onChange={(e) => setSelectedFile(e.target.files[0])}
          />
        </label>

        <label>
          Nivel de zoom:
          <input
            type="number"
            value={zoomLevel}
            onChange={(e) =>
              setZoomLevel(Math.max(10, Math.min(400, e.target.value)))
            }
            min="10"
            max="400"
          />
          <span>%</span>
        </label>

        <button onClick={uploadImage} disabled={!selectedFile || loading}>
          {loading ? "Incarcare..." : "Incarca imagine"}
        </button>
      </div>

      {/* Afișează imaginea procesată */}
      {imageProcessed && processedImageLink && (
        <div className="download-section">
          <h2>Procesare completa!</h2>
          <p>
            Imaginea procesata:
            <img
              src={processedImageLink}
              alt="Imagine procesata"
              style={{ width: "100%", maxWidth: "600px" }}
            />
          </p>
          <button onClick={() => window.open(processedImageLink, "_blank")}>
            Deschide imaginea
          </button>
          <button onClick={() => downloadImage(processedImageLink)}>
            Descarca imaginea
          </button>
        </div>
      )}

      <button onClick={fetchProcessedImage}>Obtine ultima imagine procesata</button>

      <button onClick={() => window.location.reload()}>Reincarca pagina</button>
    </div>
  );
}

export default App;
