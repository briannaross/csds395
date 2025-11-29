import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { MealPlanContext } from "../context/MealPlanContext";

const MealSwipe = () => {
  const navigate = useNavigate();
  const { weeklyLimit, swipesUsed } = useContext(MealPlanContext);

  return (
    <div
      className="balance-card meal-swipe-card"
      onClick={() => navigate("/meal-plan")}
      style={{ cursor: "pointer" }}
    >
      Meal Swipes Left:
      <strong style={{ marginLeft: "6px" }}>
        {weeklyLimit - swipesUsed} / {weeklyLimit}
      </strong>
    </div>
  );
};

export default MealSwipe;
