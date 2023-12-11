import chai from 'chai'
import supertest from 'supertest'
import app from '../../index.js'

const expect = chai.expect
const request = supertest(app)

describe('GET /case/:page', () => {
  it('should get cases with default pagination', async () => {
    const response = await request.get('/case/1')
    expect(response.status).to.equal(200)
    expect(response.body).to.be.an('array')
  })

  it('should get limited cases when limited query parameter is provided', async () => {
    const response = await request.get('/case?limited=5')
    expect(response.status).to.equal(200)
    expect(response.body).to.be.an('array')
  })

  it('should handle errors and return 404 status', async () => {
    const response = await request.get('/case/invalid')
    expect(response.status).to.equal(404)
  })

  it('should handle invalid page parameter and return 404 status', async () => {
    // Use a valid but non-numeric value for page parameter
    const response = await request.get('/case/abc')
    expect(response.status).to.equal(404)
  })

  it('should handle out-of-range page parameter and return 400 status', async () => {
    const response = await request.get('/case/999999999999')
    // Update the expectation to 400
    expect(response.status).to.equal(400)
  })
})
