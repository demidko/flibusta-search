import "fomantic-ui/dist/semantic.min"
import "fomantic-ui/dist/semantic.min.css"
import {makeTextFileLineIterator} from "./streams.js"
import "./debug"

window.searchQuotes = searchQuotes

async function searchQuotes() {
  let author = getAuthor()
  let query = getQuery()
  showProgressBlock(author)
  let quotesBlock = document.getElementById("quotes-block")
  cleanPreviousQuotes(quotesBlock)
  for await (let line of makeTextFileLineIterator(`/search?author=${author}&q=${query}`)) {
    if(line.startsWith("[")) {
      let json = JSON.parse(line)
      console.warn(json)
      let [author, id, title, quote] = json
      let htmlQuote = `
        <div class="ui positive floating message">
          <div class="header"><a href="https://flibusta.is/b/${id}">${author} — ${title}</a></div>
          <p>${quote}</p>
        </div>`
      quotesBlock.insertAdjacentHTML("beforeend", htmlQuote)
    } else {
      logLine(line)
    }
  }
  hideProgressBlock()
}

function cleanPreviousQuotes(el) {
  let children = el.children
  let length = children.length
  for(let i = length - 1; i >= 0; --i) {
    let child = children.item(i)
    if(child.id === "content-delimiter") {
      break
    }
    child.remove()
  }
}

function showProgressBlock(author) {
  document.getElementById("progress-block").style.visibility = "visible"
  logLine(`Сейчас мы просматриваем все книги автора ${author}, это может занять около пяти минут.`)
}

function logLine(line) {
  document.getElementById("log-line").value = line
}

function hideProgressBlock() {
  document.getElementById("progress-block").style.visibility = "hidden"
}

function getAuthor() {
  return document.getElementById("author-input").value
}

function getQuery() {
  return document.getElementById("query-input").value
}

async function stream(url, lambda) {
  const response = await fetch(url);
  const reader = response.body.getReader();
  const decoder = new TextDecoder('utf8');
  while (true) {
    const {done, value} = await reader.read();
    let chunk = decoder.decode(value);
    lambda(chunk);
    if (done) {
      return;
    }
  }
}
