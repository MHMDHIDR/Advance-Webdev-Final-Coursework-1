import pool from '../../utils/db.js'
export const getCases = async (req, res) => {
    try {
        const query = `
          SELECT cv.*, co.name, co.price
          FROM cases_variants AS cv
          JOIN \`case\` AS c ON cv.case_id = c.id
          JOIN comparison AS co ON cv.id = co.case_variant_id
        `
        const [rows] = await pool.query(query)
        res.json(rows)
    } catch (error) {
        console.error('Error fetching case variants:', error.message)
        res.status(500).json({ error: 'Internal Server Error' })
    }
}