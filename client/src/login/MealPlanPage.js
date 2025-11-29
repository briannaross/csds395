import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { MealPlanContext } from "../context/MealPlanContext";
import "./MealPlanPage.css";

const MealPlanPage = () => {
  const navigate = useNavigate();
  const { weeklyLimit, swipesUsed, swipeHistory, addSwipe } =
    useContext(MealPlanContext);

  const [location, setLocation] = useState("");
  const [notes, setNotes] = useState("");

  const handleAddSwipe = () => {
    if (!location) return alert("Please select a location");
    addSwipe(location, notes);
    setLocation("");
    setNotes("");
  };

  return (
    <div className="meal-page">

      {/* HEADER */}
      <div className="meal-header">
        <h1>Meal Swipes</h1>
        <button className="back-dashboard" onClick={() => navigate("/dashboard")}>
          ← Back to Dashboard
        </button>
      </div>

      {/* MAIN CONTENT */}
      <div className="meal-content">

        {/* LEFT CARD */}
        <div className="add-swipe-card">
          <h2>Add a Meal Swipe</h2>
          <p className="weekly-counter">
            Weekly Swipes: {swipesUsed} / {weeklyLimit}
          </p>

          <select
            value={location}
            onChange={(e) => setLocation(e.target.value)}
          >
            <option value="">Select Location</option>
            <option value="Fribley">Fribley</option>
            <option value="Leutner">Leutner</option>
            <option value="Tink">Tink</option>
            <option value="Grab-It">Grab-It</option>
          </select>

          <input
            type="text"
            placeholder="Notes (optional)"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
          />

          <button className="add-swipe-btn" onClick={handleAddSwipe}>
            Add Swipe
          </button>
        </div>

        {/* RIGHT CARD HISTORY */}
        <div className="history-card">
          <h2>Swipe History</h2>

          <div className="history-table-container">
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
                {swipeHistory.map((s, i) => (
                  <tr key={i}>
                    <td>{s.date}</td>
                    <td>{s.time}</td>
                    <td>{s.location}</td>
                    <td>{s.notes || "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

        </div>
      </div>
    </div>
  );
};

export default MealPlanPage;
