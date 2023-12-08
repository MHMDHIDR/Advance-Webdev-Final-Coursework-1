import pool from '../../utils/db.js'
import { ITEMS_PER_PAGE } from '../../utils/const.js'

/**
 *
 * @desc    Get search result based on phone model
 * @route   GET /api/search/:page
 * @access  Public
 * @param {*} req
 * @param {*} res
 * @return  {Object} { searchResultsRows }
 * @return  {Object} { error }
 */
export const searchResults = async (req, res) => {
  const { query: searchQuery } = req.query
  const page = parseInt(req.params.page) || 1
  const offset = (page - 1) * ITEMS_PER_PAGE

  try {
    //search based on phone model and case name
    const searchQuerySQL = `
        SELECT cv.*, co.name, co.price, c.phone_model
        FROM cases_variants AS cv
        JOIN \`case\` AS c ON cv.case_id = c.id
        JOIN comparison AS co ON cv.id = co.case_variant_id
        WHERE c.phone_model = ?
        LIMIT ${ITEMS_PER_PAGE} OFFSET ${offset}
    `

    const [searchResultsRows] = await pool.query(searchQuerySQL, [
      searchQuery,
      `%${searchQuery}%`
    ])

    res.json(searchResultsRows)
  } catch (error) {
    console.error('Error fetching search results:', error.message)
    res.status(500).json({ error: 'Internal Server Error' })
  }
}
