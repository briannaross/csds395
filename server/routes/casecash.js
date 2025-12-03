/**
 * @module routes/casecash
 * @description Routes related to the CaseCash page
 */ 

const express = require('express');
const db = require('../db'); // Import database connection
require('dotenv').config();
const { v4: uuidv4 } = require("uuid");

const router = express.Router();

/**
 * Gets transaction history for a user.
 * @route GET /:userId
 * @access Private
 */
router.get("/:userId", (req, res) => {
    const { userId } = req.params;

    db.query(
        `SELECT casecash.date, casecash.location, casecash.amount
     FROM casecash 
     WHERE casecash.userID = ? 
     ORDER BY casecash.date DESC`,
        [userId],
        (err, results) => {
            if (err) return res.status(500).json({ message: "Failed to fetch past transactions", error: err });
            res.json(results);
        }
    );
});

/**
 * Creates a new CaseCash transaction from the form
 * @route POST /
 * @access Private
 */
router.post("/", (req, res) => {
    const { userId, date, location, amount} = req.body;

    db.query(
        `INSERT INTO casecash (userID, date, location, amount)
        VALUES (?, ?, ?, ?)`,
        [userId, date, location, amount],
        (err) => {
            if (err) return res.status(500).json({ error: err });
            res.json({ message: "CaseCash Transaction Recorded!" });
        }
    );
});

module.exports = router;