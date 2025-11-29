import { createContext, useState, useEffect } from "react";

export const MealPlanContext = createContext();

export const MealPlanProvider = ({ user, children }) => {
  const storageKey = user && user.email ? `meal_swipes_${user.email}` : "meal_swipes_guest";

  const [weeklyLimit, setWeeklyLimit] = useState(19);
  const [swipesUsed, setSwipesUsed] = useState(0);
  const [swipeHistory, setSwipeHistory] = useState([]);

  useEffect(() => {
    const data = localStorage.getItem(storageKey);
    if (data) {
      const parsed = JSON.parse(data);
      setWeeklyLimit(parsed.weeklyLimit || 19);
      setSwipesUsed(parsed.swipesUsed || 0);
      setSwipeHistory(parsed.swipeHistory || []);
    }
  }, [storageKey]);

  const save = (updated) => {
    localStorage.setItem(
      storageKey,
      JSON.stringify(updated)
    );
  };

  const addSwipe = (location, notes) => {
    const now = new Date();
    const newEntry = {
      date: now.toLocaleDateString("en-GB"),
      time: now.toLocaleTimeString("en-GB", { hour: "2-digit", minute: "2-digit" }),
      location,
      notes: notes || "-"
    };

    const updatedHistory = [newEntry, ...swipeHistory];
    const updated = {
      weeklyLimit,
      swipesUsed: swipesUsed + 1,
      swipeHistory: updatedHistory
    };

    setSwipeHistory(updatedHistory);
    setSwipesUsed(swipesUsed + 1);
    save(updated);
  };

  return (
    <MealPlanContext.Provider
      value={{
        weeklyLimit,
        swipesUsed,
        swipeHistory,
        addSwipe
      }}
    >
      {children}
    </MealPlanContext.Provider>
  );
};
