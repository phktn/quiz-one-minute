var reconnectWait;
let animationId = 0;

window.addEventListener('DOMContentLoaded', () => {
    log('DOMContentLoaded');
    reconnectWait = 0;
    reconnect();
})

function connect() {
    console.log('connect()');
    if (!window.EventSource) {
        console.log("YOUR BROWSER DOES NOT SUPPORT SSE");
        return;
    }
    const source = new EventSource('/quiz-one-minute/stream');
    source.addEventListener('message', function (e) {
        console.log('message data: ' + e.data);
        response = JSON.parse(e.data);
        if (response.delayMs != null) {
            document.getElementById('delayMs').innerHTML = response.delayMs;
        }
        if (response.hero != null) {
            document.getElementById('hero').className = response.hero;
        }
        if (response.problemSetNum > 0) {
            onSelectProblemSet(response.problemSetNum);
        }
    }, false);
    source.addEventListener('open', function (e) {
        console.log("Connecting to the chat server..." + e);
    }, false);
    source.addEventListener('error', function (e) {
        if (e.readyState == EventSource.CLOSED) {
            console.log("**** ERROR: " + e);
            reconnect();
        }
    }, false);
}

function selectProblemSet(num) {
    var request = new XMLHttpRequest();
    request.open('POST', '/quiz-one-minute/selectProblemSet', true);
    request.send(num);
}

function onSelectProblemSet(num) {
}

function startOneMinute() {
    var request = new XMLHttpRequest();
    request.open('POST', '/quiz-one-minute/startOneMinute', true);
    request.send(null);
}

function setCorrectAnswer(num) {
    var request = new XMLHttpRequest();
    request.open('POST', '/quiz-one-minute/setCorrectAnswer', true);
    request.send(num);
}

function reconnect() {
    if (reconnectWait == 0) {
        reconnectWait = 1;
        disableButton('接続中…');
        connect();
    } else {
        var waitSec = reconnectWait;
        reconnectWait *= 2;
        disableButton(waitSec + ' 秒後に再接続');
        var id = setInterval(function () {
            waitSec--;
            if (waitSec <= 0) {
                clearInterval(id);
                disableButton('接続中…');
                connect();
            } else {
                disableButton(waitSec + ' 秒後に再接続');
            }
        }, 1000);
    }
}

function disableButton(value) {
    log('disableButton() value: ' + value);
    document.getElementById('delayMs').innerText = '';
    document.getElementById('hero').className = 'hero is-dark';
}

function log(body) {
    document.getElementById('log').innerText = body + "\n" + document.getElementById('log').innerText;
}
