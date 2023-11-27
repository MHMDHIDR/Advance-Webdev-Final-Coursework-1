import pool from '../../utils/db.js';

export const searchResults = async (req, res) => {
    const { query: searchQuery } = req.query;

    try {
        //search based on phone model
        const searchQuerySQL = `
            SELECT cv.*, co.name, co.price
            FROM cases_variants AS cv
            JOIN \`case\` AS c ON cv.case_id = c.id
            JOIN comparison AS co ON cv.id = co.case_variant_id
            WHERE c.phone_model = ?
        `;
        const [searchResultsRows] = await pool.query(searchQuerySQL, [searchQuery, `%${searchQuery}%`]);

        res.json(searchResultsRows);
    } catch (error) {
        console.error('Error fetching search results:', error.message);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};
