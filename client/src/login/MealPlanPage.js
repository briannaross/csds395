import React, { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { MealPlanContext } from "../context/MealPlanContext";
import "./MealPlanPage.css";

const MealPlanPage = () => {
  const navigate = useNavigate();
  const { weeklyLimit, setWeeklyLimit, swipesUsed, swipeHistory, addSwipe, editSwipe, deleteSwipe } =
    useContext(MealPlanContext);

  const [location, setLocation] = useState("");
  const [notes, setNotes] = useState("");
  const [editIndex, setEditIndex] = useState(null);
  const [editMode, setEditMode] = useState(false);

  const handleAddSwipe = () => {
    if (!location) return alert("Please select a location");

    if (editIndex !== null) {
      editSwipe(editIndex, location, notes);
      setEditIndex(null);
      setEditMode(false);
    } else {
      addSwipe(location, notes);
    }

    setLocation("");
    setNotes("");
  };

  const beginEdit = (index) => {
    if (!editMode) return;
    setEditIndex(index);
    const entry = swipeHistory[index];
    setLocation(entry.location);
    setNotes(entry.notes === "-" ? "" : entry.notes);
  };

  const handleDelete = () => {
    if (editIndex === null) return;
    deleteSwipe(editIndex);
    setEditIndex(null);
    setLocation("");
    setNotes("");
    setEditMode(false);
  };

  const handleLimitChange = (e) => {
    const value = e.target.value;
    setWeeklyLimit(value === "Unlimited" ? "Unlimited" : parseInt(value));
  };

  return (
    <div className="meal-page">
      <div className="meal-header">
        <h1>Meal Swipes</h1>
        <button className="back-dashboard" onClick={() => navigate("/dashboard")}>
          ← Back to Dashboard
        </button>
      </div>

      <div className="meal-content">
        <div className="add-swipe-card">
          <h2>{editIndex !== null ? "Edit Swipe" : "Add a Meal Swipe"}</h2>
          
          <div className="limit-selector">
            <label htmlFor="weekly-limit">Weekly Meal Plan:</label>
            <select 
              id="weekly-limit"
              value={weeklyLimit} 
              onChange={handleLimitChange}
              className="limit-dropdown"
            >
              <option value="10">10 Swipes/Week</option>
              <option value="14">14 Swipes/Week</option>
              <option value="17">17 Swipes/Week</option>
              <option value="Unlimited">Unlimited</option>
            </select>
          </div>

          <p className="weekly-counter">
            Weekly Swipes: {swipesUsed} / {weeklyLimit}
          </p>

          <select value={location} onChange={(e) => setLocation(e.target.value)}>
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
            {editIndex !== null ? "Save Changes" : "Add Swipe"}
          </button>

          {editIndex !== null && (
            <button className="delete-swipe-btn" onClick={handleDelete}>
              Delete Swipe
            </button>
          )}
        </div>

        <div className="history-card">
          <div className="history-header">
            <h2>Swipe History</h2>
            <button
              className="edit-mode-btn"
              onClick={() => {
                setEditMode(!editMode);
                setEditIndex(null);
                setLocation("");
                setNotes("");
              }}
            >
              {editMode ? "Done Editing" : "Edit"}
            </button>
          </div>

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
                  <tr
                    key={i}
                    className={editMode ? "row-editable" : ""}
                    onClick={() => beginEdit(i)}
                  >
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
