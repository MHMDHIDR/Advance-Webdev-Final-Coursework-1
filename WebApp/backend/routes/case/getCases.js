import pool from '../../utils/db.js'
import { ITEMS_PER_PAGE } from '../../utils/const.js'

/**
 *
 * @desc    Get cases variants for comparison
 * @route   GET /api/case/:page
 * @access  Public
 * @param {*} req
 * @param {*} res
 * @return  {Object} { rows }
 * @return  {Object} { error }
 */
export const getCases = async (req, res) => {
  try {
    const page = parseInt(req.params.page) || 1
    const offset = (page - 1) * ITEMS_PER_PAGE

    //get all cases variants
    const query = `
        SELECT cv.*, co.name, co.price
        FROM cases_variants AS cv
        JOIN \`case\` AS c ON cv.case_id = c.id
        JOIN comparison AS co ON cv.id = co.case_variant_id
        LIMIT ${ITEMS_PER_PAGE} OFFSET ${offset}
    `

    const [rows] = await pool.query(query)
    res.json(rows)
  } catch (error) {
    console.error('Error fetching case variants:', error.message)
    res.status(500).json({ error: 'Internal Server Error' })
  }
}
