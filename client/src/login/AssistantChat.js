import React, { useState, useContext, useEffect, useRef } from "react";
import { BudgetContext } from "../context/BudgetContext";
import { MealPlanContext } from "../context/MealPlanContext";
import "./AssistantChat.css";

function AssistantChat() {
  const [input, setInput] = useState("");
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);
  
  // Get budget data from your contexts
  const { transactions, caseCashEntries = [] } = useContext(BudgetContext);
  const { weeklyLimit, swipesUsed } = useContext(MealPlanContext);

  // Auto-scroll to bottom when messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Calculate current week's spending
  const calculateWeeklySpending = () => {
    const now = new Date();
    const startOfWeek = new Date(now);
    startOfWeek.setDate(now.getDate() - now.getDay());
    startOfWeek.setHours(0, 0, 0, 0);

    const personalSpent = transactions
      ?.filter(t => new Date(t.date) >= startOfWeek && t.type === "Expense")
      ?.reduce((sum, t) => sum + parseFloat(t.amount || 0), 0) || 0;

    const caseCashSpent = caseCashEntries
      ?.filter(t => new Date(t.date) >= startOfWeek)
      ?.reduce((sum, t) => sum + parseFloat(t.amount || 0), 0) || 0;

    return { personalSpent, caseCashSpent };
  };

  async function handleSend(event) {
    event.preventDefault();
    const trimmed = input.trim();
    if (!trimmed) return;

    setMessages(prev => [...prev, { from: "user", text: trimmed }]);
    setInput("");
    setLoading(true);

    try {
      const { personalSpent, caseCashSpent } = calculateWeeklySpending();
      
      const totalCaseCash = caseCashEntries.reduce((sum, entry) => {
        return sum + parseFloat(entry.amount || 0);
      }, 0);

      const response = await fetch("http://localhost:8080/assistant/ask", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          question: trimmed,
          weeklyBudgetPersonal: 100.0,
          spentThisWeekPersonal: personalSpent,
          caseCashTotalSemester: totalCaseCash,
          caseCashSpentThisWeek: caseCashSpent,
          mealSwipesWeeklyTotal: parseInt(weeklyLimit || 0),
          mealSwipesUsedThisWeek: parseInt(swipesUsed || 0)
        })
      });

      const data = await response.json();
      const botText = data.message || "I didn't receive a response from the assistant.";
      setMessages(prev => [...prev, { from: "bot", text: botText }]);
    } catch (err) {
      console.error("Error calling assistant:", err);
      setMessages(prev => [
        ...prev,
        { from: "bot", text: "Unable to connect to the assistant. Make sure the backend server is running on port 8080." }
      ]);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="assistant-chat-container">
      <h2>💬 Budget Assistant</h2>
      <div className="chat-messages">
        {messages.length === 0 && (
          <div className="chat-placeholder">
            Try asking: "Can I afford Starbucks for $6?" or "How am I doing on my budget?"
          </div>
        )}
        {messages.map((m, idx) => (
          <div
            key={idx}
            className={`message ${m.from === "user" ? "user-message" : "bot-message"}`}
          >
            <span className="message-bubble">
              {m.text}
            </span>
          </div>
        ))}
        {loading && (
          <div className="message bot-message">
            <span className="message-bubble loading">
              Assistant is thinking...
            </span>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>
      <form onSubmit={handleSend} className="chat-input-form">
        <input
          type="text"
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder="Ask me about your budget..."
          disabled={loading}
        />
        <button type="submit" disabled={loading || !input.trim()}>
          Send
        </button>
      </form>
    </div>
  );
}

export default AssistantChat;
