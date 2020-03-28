var reconnectWait;

window.onload = function (e) {
    log('onload');
    reconnectWait = 0;
    reconnect();
}

function connect() {
    console.log('connect()');
    if (!window.EventSource) {
        console.log("YOUR BROWSER DOES NOT SUPPORT SSE");
        return;
    }
    const source = new EventSource('/fastest-finger-first/stream');
    source.addEventListener('message', function (e) {
        console.log('message data: ' + e.data);
        response = JSON.parse(e.data);
        document.getElementById('delayMs').innerHTML = response.delayMs;
        document.getElementById('hero').className = response.hero;
        document.getElementById('result').innerHTML = response.button;
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

function send(id) {
    var nickname = document.getElementById('nickname').value;
    log('send id: ' + id + ' nickname: ' + nickname);
    var request = new XMLHttpRequest();
    request.open('GET', '/fastest-finger-first/challenge?id=' + id + '&nickname=' + nickname, false);
    request.send(null);
    if (request.status === 200) {
        log(request.responseText);
        if (request.responseText) {
            response = JSON.parse(request.responseText);
            document.getElementById('delayMs').innerHTML = response.delayMs;
            document.getElementById('hero').className = response.hero;
        }
    }
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

function start() {
    var request = new XMLHttpRequest();
    request.open('POST', '/fastest-finger-first/start', false);
    request.send(null);
}

function skip(msg) {
    var request = new XMLHttpRequest();
    request.open('POST', '/fastest-finger-first/skip', false);
    request.send(msg);
}

function advance() {
    var request = new XMLHttpRequest();
    request.open('POST', '/fastest-finger-first/advance', false);
    request.send(null);
}

function message(msg) {
    var request = new XMLHttpRequest();
    request.open('POST', '/fastest-finger-first/message', false);
    request.send(msg);
}

function disableButton(value) {
    log('disableButton() value: ' + value);
    document.getElementById('delayMs').innerText = '';
    document.getElementById('hero').className = 'hero';
    document.getElementById('result').innerHTML = '<p></p><input class="button is-primary is-large is-fullwidth" disabled type="button" value="' + value + '" />';
}

function log(body) {
    document.getElementById('log').innerText = body + "\n" + document.getElementById('log').innerText;
}
