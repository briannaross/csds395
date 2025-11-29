import React, { useState, useEffect } from "react";
import "./mealplan.css";
import { useNavigate } from "react-router-dom";

function MealSwipe() {
  const navigate = useNavigate();

  const [location, setLocation] = useState("");
  const [notes, setNotes] = useState("");
  const [swipes, setSwipes] = useState([]);

  useEffect(() => {
    const saved = JSON.parse(localStorage.getItem("mealSwipes")) || [];
    setSwipes(saved);
  }, []);

  const handleAddSwipe = () => {
    if (!location) return;

    const newSwipe = {
      date: new Date().toLocaleDateString("en-GB"),
      time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      location,
      notes: notes || "-"
    };

    const updated = [newSwipe, ...swipes];
    setSwipes(updated);
    localStorage.setItem("mealSwipes", JSON.stringify(updated));

    setLocation("");
    setNotes("");
  };

  return (
    <div className="mealplan-page">

      <div className="meal-header">
        <h1>Meal Swipes</h1>

        <button className="back-dashboard-btn" onClick={() => navigate("/dashboard")}>
          ← Back to Dashboard
        </button>
      </div>

      <div className="meal-content">

        <div className="meal-card">
          <h2>Add a Meal Swipe</h2>
          <p className="weekly-count">Weekly Swipes: {swipes.length} / 19</p>

          <select
            className="input-box"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
          >
            <option value="">Select Location</option>
            <option value="Fribley">Fribley</option>
            <option value="Leutner">Leutner</option>
            <option value="Grab-It">Grab-It</option>
            <option value="Tink">Tink</option>
          </select>

          <input
            className="input-box"
            placeholder="Notes (optional)"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
          />

          <button className="add-btn" onClick={handleAddSwipe}>
            Add Swipe
          </button>
        </div>


        <div className="meal-card history-card">
          <h2>Swipe History</h2>

          <table className="history-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Time</th>
                <th>Location</th>
                <th>Notes</th>
              </tr>
            </thead>

            <tbody>
              {swipes.map((s, index) => (
                <tr key={index}>
                  <td>{s.date}</td>
                  <td>{s.time}</td>
                  <td>{s.location}</td>
                  <td>{s.notes}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
}

export default MealSwipe;
