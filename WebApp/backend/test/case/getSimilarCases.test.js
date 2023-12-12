import { expect } from 'chai'
import supertest from 'supertest'
import app from '../../index.js'

const request = supertest(app)

describe('GET /case/similar/:id', () => {
  it('should get similar cases', async () => {
    const response = await request.get('/case/similar/300')
    expect(response.status).to.equal(200)

    // Check that the response is an array
    expect(response.body).to.be.an('array')

    // Check that each item in the response has the expected properties
    response.body.forEach(similarCase => {
      expect(similarCase).to.have.property('phone_model')
      expect(similarCase).to.have.property('name')
      expect(similarCase).to.have.property('price')
      expect(similarCase).to.have.property('url')
      expect(similarCase).to.have.property('website')
    })

    // Check that there are at least 4 items if the response is not empty
    if (response.body.length > 0) {
      expect(response.body.length).to.be.at.least(4)
    }
  })

  it('should handle non-numeric case ID and return 400 status', async () => {
    const response = await request.get('/case/similar/non_numeric_id')
    expect(response.status).to.equal(400)
  })

  it('should handle case with no similar cases and return empty array', async () => {
    const response = await request.get('/case/similar/2')
    expect(response.status).to.equal(200)

    // Check that the response is an array
    expect(response.body).to.be.an('array')

    // Check that the array is empty
    expect(response.body).to.have.lengthOf(0)
  })
})
