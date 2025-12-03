/**
 * @module routes/mealplan
 * @description Routes related to the meal plan page
 */ 

const express = require('express');
const db = require('../db'); // Import database connection
require('dotenv').config();
const { v4: uuidv4 } = require("uuid");

const router = express.Router();

/**
 * Gets meal plan transaction history for a user.
 * @route GET /:userId
 * @access Private
 */
router.get("/:userId", (req, res) => {
    const { userId } = req.params;

    db.query(
        `SELECT mealswipe.location, mealswipe.notes
     FROM mealswipe 
     WHERE mealswipe.userID = ?`,
        [userId],
        (err, results) => {
            if (err) return res.status(500).json({ message: "Failed to fetch past transactions", error: err });
            res.json(results);
        }
    );
});

/**
 * Creates a new meal swipe transaction from the form
 * @route POST /
 * @access Private
 */
router.post("/", (req, res) => {
    const { userId, location, notes } = req.body;

    db.query(
        `INSERT INTO mealswipe (userID, location, notes)
        VALUES (?, ?, ?)`,
        [userId, location, notes],
        (err) => {
            if (err) return res.status(500).json({ error: err });
            res.json({ message: "CaseCash Transaction Recorded!" });
        }
    );
});

module.exports = router;