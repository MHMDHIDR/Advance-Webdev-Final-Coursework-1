import pool from '../../utils/db.js'

/**
 *
 * @desc    Get case details by id for comparison
 * @route   GET /api/case/:id
 * @access  Public
 * @param {*} req
 * @param {*} res
 * @return  {Object} { rows }
 * @return  {Object} { error }
 */
export const getCaseById = async (req, res) => {
  const { id: caseId } = req.params

  try {
    //get case details by id
    const query = `
        SELECT cv.*, co.name, c.phone_model, co.price, co.url
        FROM cases_variants AS cv
        JOIN \`case\` AS c ON cv.case_id = c.id
        JOIN comparison AS co ON cv.id = co.case_variant_id
        WHERE cv.id = ?
    `
    const [rows] = await pool.query(query, [caseId])

    if (rows.length === 0) {
      res.status(404).json({ error: 'Case not found' })
    } else {
      res.json(rows[0])
    }
  } catch (error) {
    console.error('Error fetching case details:', error.message)
    res.status(500).json({ error: 'Internal Server Error' })
  }
}
