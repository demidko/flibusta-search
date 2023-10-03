import "fomantic-ui/dist/semantic.min"
import "fomantic-ui/dist/semantic.min.css"

window.search = search

async function search() {
  let resultGrid = document.getElementById("result-grid")
  let author = document.getElementById("author-input").value
  let query = document.getElementById("query-input").value
  resultGrid.innerHTML = `
    <div class="ui icon message" id = "result-message">
    <i class="notched circle loading icon"></i>
    <div class="content">
      <div class="header">
      ${query}
      </div>
      <p>Сейчас мы просматриваем все книги ${author}, это может занять около пяти минут. Пожалуйста, не обновляйте вкладку.</p>
    </div>
    </div>`
  let params = `search?author=${author}&q=${query}`
  let response = await fetch(params)
  let json = await response.json()
  if (json.length === 0) {
    resultGrid.innerHTML = `
      <div class="ui floating message">
        <p>К сожалению ничего найти не удалось 😔</p>
      </div>
    `
    return
  }
  for (let {author, book, quotes} of json) {
    let {id, name} = book
    let tag = `
      <div class="ui message">
        <div class="header">
        ${author} — <a href="https://flibusta.is/b/${id}/epub">${name}</a>
        </div>
        <ul class="list">`
    for (let quote of quotes) {
      tag += `<li>${quote}</li>`
    }
    tag += `
        </ul>
      </div>`
    resultGrid.insertAdjacentHTML("beforeend", tag)
  }
}


function removeMessage() {

}