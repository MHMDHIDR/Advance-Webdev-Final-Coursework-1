import express from 'express';
import pool from '../utils/db.js';

const router = express.Router();

router.get('/', async (req, res) => {
    try {
        const query = `
          SELECT cv.*, c.phone_model
          FROM cases_variants AS cv
          JOIN \`case\` AS c ON cv.case_id = c.id;
        `;
        const [rows, fields] = await pool.query(query);
        res.json(rows);
    } catch (error) {
        console.error('Error fetching case variants:', error.message);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

export default router;
