import chai from 'chai'
import supertest from 'supertest'
import app from '../../index.js'

const expect = chai.expect
const request = supertest(app)

/**
 * Testing the GET /case/byId/:id endpoint
 * This endpoint returns a case details based on the case ID
 * The endpoint should return a 200 status code if the case is found
 * The endpoint should return a 404 status code if the case is not found
 * The endpoint should return a 500 status code if there is an error
 * The endpoint should return a JSON object with the following properties:
 * - phone_model
 * - case_id
 * - name
 * - price
 * - url
 */
describe('GET /case/byId/:id', () => {
  it('should handle missing case ID parameter and return 404 status', async () => {
    const response = await request.get('/case/byId/')
    expect(response.status).to.equal(404)
  })

  it('should get case details by ID', async () => {
    const response = await request.get('/case/byId/20')

    expect(response.status).to.equal(200)
    expect(response.body).to.be.an('object')
    // Testing based on the response structure
    // like testing the expected properties in a response:
    expect(response.body).to.have.property('phone_model')
    expect(response.body).to.have.property('case_id')
    expect(response.body).to.have.property('name')
    expect(response.body).to.have.property('price')
    expect(response.body).to.have.property('url')
  })

  it('should handle non-numeric case ID and return 400 status', async () => {
    const response = await request.get('/case/byId/non_numeric_id')
    expect(response.status).to.equal(400)
  })

  it('should handle a valid case ID with no matching data and return 404 status', async () => {
    const response = await request.get('/case/byId/9999999999')
    expect(response.status).to.equal(404)
    expect(response.body).to.have.property('error', 'Case not found')
  })
})
