import "fomantic-ui/dist/semantic.min"
import "fomantic-ui/dist/semantic.min.css"

window.search = search

async function search() {
  let author = document.getElementById("author-input").value
  let words = document.getElementById("words-input").value
  let params = `search?author=${author}&q=${words}`
  console.log(`Ждём ответа с backend по запросу ${params}...`)
  let response = await fetch(params)
  let json = await response.json()
  console.log(`Ответ по запросу ${params} получен`)
  console.warn(json)
}