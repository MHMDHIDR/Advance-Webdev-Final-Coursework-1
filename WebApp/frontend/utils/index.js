export const getPageNumber = () => {
  //Get page number from query parameter
  const queryParams = new URLSearchParams(window.location.search)
  const page = parseInt(queryParams.get('page')) || 1
  return page
}

export const scrollHeader = () => {
  document.addEventListener('DOMContentLoaded', function () {
    const header = document.querySelector('.header')

    window.addEventListener('scroll', function () {
      const scrollTop = window.scrollY
      if (scrollTop > 200) {
        header.classList.add('animate-slidedown')
      } else {
        header.classList.remove('animate-slidedown')
      }
    })
  })
}
