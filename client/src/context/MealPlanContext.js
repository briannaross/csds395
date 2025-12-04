import React, { createContext, useState, useEffect } from "react";

export const MealPlanContext = createContext();

export const MealPlanProvider = ({ user = null, children }) => {
  // Use user-specific storage key or guest key
  const storageKey = user && user.email ? `meal_swipes_${user.email}` : "meal_swipes_guest";
  
  const [weeklyLimit, setWeeklyLimitState] = useState(19);
  const [swipesUsed, setSwipesUsed] = useState(0);
  const [swipeHistory, setSwipeHistory] = useState([]);

  // Load data from localStorage on mount or when user changes
  useEffect(() => {
    const data = localStorage.getItem(storageKey);
    if (data) {
      const parsed = JSON.parse(data);
      setWeeklyLimitState(parsed.weeklyLimit || 19);
      setSwipesUsed(parsed.swipesUsed || 0);
      setSwipeHistory(parsed.swipeHistory || []);
    }
  }, [storageKey]);

  // Save to localStorage
  const save = (updated) => {
    localStorage.setItem(storageKey, JSON.stringify(updated));
  };

  // Add new swipe
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

  // Edit existing swipe
  const editSwipe = (index, location, notes) => {
    const updatedHistory = [...swipeHistory];
    updatedHistory[index] = {
      ...updatedHistory[index],
      location,
      notes: notes || "-"
    };
    const updated = {
      weeklyLimit,
      swipesUsed,
      swipeHistory: updatedHistory
    };
    setSwipeHistory(updatedHistory);
    save(updated);
  };

  // Delete swipe
  const deleteSwipe = (index) => {
    const updatedHistory = swipeHistory.filter((_, i) => i !== index);
    const updated = {
      weeklyLimit,
      swipesUsed: swipesUsed - 1,
      swipeHistory: updatedHistory
    };
    setSwipeHistory(updatedHistory);
    setSwipesUsed(swipesUsed - 1);
    save(updated);
  };

  const setWeeklyLimit = (newLimit) => {
    const updated = {
      weeklyLimit: newLimit,
      swipesUsed,
      swipeHistory
    };
    setWeeklyLimitState(newLimit);
    save(updated);
  };

  return (
    <MealPlanContext.Provider
      value={{
        weeklyLimit,
        setWeeklyLimit, 
        swipesUsed,
        swipeHistory,
        addSwipe,
        editSwipe,
        deleteSwipe
      }}
    >
      {children}
    </MealPlanContext.Provider>
  );
};
