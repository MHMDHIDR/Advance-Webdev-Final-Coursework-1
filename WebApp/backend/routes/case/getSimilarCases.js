import pool from '../../utils/db.js';

export const getSimilarCases = async (req, res) => {
    const { phone_model: phoneModel } = req.params

    try {
        // Query to get similar cases based on phone_model and price
        const similarCasesQuery = `
          SELECT cv.*, co.name, co.price
          FROM cases_variants AS cv
          JOIN \`case\` AS c ON cv.case_id = c.id
          JOIN comparison AS co ON cv.id = co.case_variant_id
          WHERE c.phone_model LIKE ?
          LIMIT 5
        `;

        const [similarCasesRows] = await pool.query(similarCasesQuery, [`%${phoneModel}%`]);
        res.json(similarCasesRows);
    } catch (error) {
        console.error('Error fetching similar cases:', error.message);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};