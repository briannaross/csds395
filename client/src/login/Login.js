import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./login.css";

function Login() {
  const [isLogin, setIsLogin] = useState(true);
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [username, setUsername] = useState("");
  const [registerEmail, setRegisterEmail] = useState("");
  const [registerPassword, setRegisterPassword] = useState("");

  const handleLogin = (e) => {
    e.preventDefault();
    const storedUser = JSON.parse(localStorage.getItem(email));

    if (storedUser && storedUser.password === password) {
      localStorage.setItem("loggedInUser", JSON.stringify(storedUser));
      navigate("/dashboard");
    } else {
      alert("Invalid credentials. Please try again or register.");
    }
  };

  const handleRegister = (e) => {
    e.preventDefault();
    const newUser = { username, email: registerEmail, password: registerPassword };
    localStorage.setItem(registerEmail, JSON.stringify(newUser));
    alert("Registration successful. You can now log in.");
    setIsLogin(true);
  };

  return (
    <div className="page-container">
      <div className="login-box">
        <h1 className="app-title">SpartanSpend!</h1>
        <p className="subtitle">Track your Meal Plan, CaseCash, and Personal Funds</p>

        <div className="tab-container">
          <button className={isLogin ? "tab active" : "tab"} onClick={() => setIsLogin(true)}>
            Login
          </button>
          <button className={!isLogin ? "tab active" : "tab"} onClick={() => setIsLogin(false)}>
            Register
          </button>
        </div>

        {isLogin ? (
          <form onSubmit={handleLogin} className="form">
            <input
              type="email"
              placeholder="Email"
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Password"
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <button type="submit" className="submit-btn">
              Login
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegister} className="form">
            <input
              type="text"
              placeholder="Username"
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <input
              type="email"
              placeholder="Email"
              onChange={(e) => setRegisterEmail(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Password"
              onChange={(e) => setRegisterPassword(e.target.value)}
              required
            />
            <button type="submit" className="submit-btn">
              Register
            </button>
          </form>
        )}
      </div>
    </div>
  );
}

export default Login;
