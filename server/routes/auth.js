/**
 * @module routes/auth
 * @description Routes related to user authentication: register, login, token validation, password reset.
 */
const express = require('express');
const bcrypt = require('bcryptjs'); // Hashing passwords
const jwt = require('jsonwebtoken'); // Authentication tokens
const nodemailer = require('nodemailer'); // to send email to reset password
const db = require('../db'); // Import database connection
require('dotenv').config();

const router = express.Router();

/**
 * Registers a new user.
 * @route POST /register
 * @access Public
 */
router.post('/register', async (req, res) => {
    try {
        const { username, email, password} = req.body;

        // Normalize email to avoid case sensitivity issues
        const normalizedEmail = email.toLowerCase();

        // Hash password before storing
        const hashedPassword = await bcrypt.hash(password, 10);

        db.query(
            'INSERT INTO users (username, email, password) VALUES (?, ?, ?)',
            [username, normalizedEmail, hashedPassword],
            (err, result) => {
                if (err) {
                    console.error("Error inserting user:", err);
                    return res.status(500).json({ message: "Registration failed", error: err });
                }

                res.status(201).json({ message: "User registered successfully", user: { username, email: normalizedEmail, userType } });
            }
        );
    } catch (error) {
        console.error("Registration error:", error);
        res.status(500).json({ message: "Server error" });
    }
});


/**
 * Logs in a user and returns a JWT.
 * @route POST /login
 * @access Public
 */
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        const normalizedEmail = email.toLowerCase();

        db.query('SELECT * FROM users WHERE email = ?', [normalizedEmail], async (err, results) => {
            if (err) return res.status(500).json({ message: "Server error" });
            if (results.length === 0) return res.status(401).json({ message: "Invalid email or password" });

            const user = results[0];
            const passwordMatch = await bcrypt.compare(password, user.password_hash);
            if (!passwordMatch) return res.status(401).json({ message: "Invalid email or password" });

            // Generate JWT Token
            const token = jwt.sign({ id: user.id, email: user.email, userType: user.user_type }, process.env.JWT_SECRET, { expiresIn: "1h" });

            res.json({
                message: "Login successful",
                token,
                user: { id: user.id, username: user.username, email: user.email, userType: user.user_type }
            });
        });
    } catch (error) {
        res.status(500).json({ message: "Server error" });
    }
});

/**
 * Validates an access token.
 * @route POST /validate-token
 * @access Private
 */
router.post('/validate-token', (req, res) => {
    const token = req.headers.authorization?.split(" ")[1];
    if (!token) return res.status(401).json({ message: "Unauthorized" });

    jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
        if (err) return res.status(403).json({ message: "Invalid token" });
        res.json({ message: "Token is valid", user: decoded });
    });
});

// Configure email transporter
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    }
});

/**
 * Sends a password reset link via email.
 * @route POST /forgot-password
 * @access Public
 */
router.post('/forgot-password', (req, res) => {
    const { email } = req.body;

    db.query('SELECT * FROM users WHERE email = ?', [email], (err, results) => {
        if (err || results.length === 0) return res.status(404).json({ message: "User not found" });

        const user = results[0];
        const resetToken = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, { expiresIn: '15m' });

        const resetLink = `${process.env.RESET_LINK}/${resetToken}`;

        const mailOptions = {
            from: process.env.EMAIL_USER,
            to: user.email,
            subject: 'Password Reset Request',
            text: `Click the link below to reset your password:\n\n${resetLink}\n\nThis link will expire in 15 minutes.`
        };

        transporter.sendMail(mailOptions, (error, info) => {
            if (error) return res.status(500).json({ message: "Email failed to send", error });
            res.json({ message: "Password reset email sent" });
        });
    });
});

/**
 * Resets a user's password using a token.
 * @route POST /reset-password/:token
 * @access Public
 */
router.post('/reset-password/:token', async (req, res) => {
    const { token } = req.params;
    const { password } = req.body;

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        const hashedPassword = await bcrypt.hash(password, 10);
        db.query('UPDATE users SET password_hash = ? WHERE id = ?', [hashedPassword, decoded.id], (err, result) => {
            if (err) return res.status(500).json({ message: "Password reset failed", error: err });
            res.json({ message: "Password updated successfully" });
        });
    } catch (err) {
        return res.status(400).json({ message: "Invalid or expired token" });
    }
});

module.exports = router;

