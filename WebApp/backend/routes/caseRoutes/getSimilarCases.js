import pool from '../../utils/db.js'
export const getSimilarCases = async (req, res) => {
    const caseId = req.params.id

    try {
        const getPhoneModelQuery = 'SELECT phone_model FROM `case` WHERE id = ?'
        const [phoneModelRows] = await pool.query(getPhoneModelQuery, [caseId])

        if (phoneModelRows.length === 0) {
            res.status(404).json({ error: 'Case not found' })
            return
        }

        const phoneModel = phoneModelRows[0].phone_model

        // Query to get similar cases based on phone_model
        const similarCasesQuery = `
            SELECT cv.*, co.name, co.price
            FROM cases_variants AS cv
            JOIN \`case\` AS c ON cv.case_id = c.id
            JOIN comparison AS co ON cv.id = co.case_variant_id
            WHERE c.phone_model = ? AND cv.id <> ?
            LIMIT 5
        `

        const [similarCasesRows] = await pool.query(similarCasesQuery, [phoneModel, caseId])
        res.json(similarCasesRows)
    } catch (error) {
        console.error('Error fetching similar cases:', error.message)
        res.status(500).json({ error: 'Internal Server Error' })
    }
}