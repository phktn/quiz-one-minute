var reconnectWait;

window.onload = function (e) {
    log('onload');
    reconnectWait = 0;
    reconnect();
}

function connect() {
    log('connect()');
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        log('onreadystatechange readyState: ' + req.readyState + ' status: ' + req.status);
        var result = document.getElementById('result');
        switch (req.readyState) {
            case XMLHttpRequest.LOADING: // 3: ダウンロード中
                if (req.status == 200) { // 通信の成功時
                    reconnectWait = 0;
                    var lines = req.responseText.split("\n");
                    var newLine = lines[lines.length - 2];
                    log('onreadystatechange newLine: ' + newLine);
                    result.innerHTML = newLine;
                }
                break;
            case XMLHttpRequest.DONE:    // 4: 操作が完了した
                reconnect();
                break;
            case XMLHttpRequest.UNSENT:
            case XMLHttpRequest.OPENED:
            case XMLHttpRequest.HEADERS_RECEIVED:
            default:
                break;
        }
    }
    req.open('GET', '/fastest-finger-first/stream', true);
    req.send(null);
}

function send(id) {
    log('send id: ' + id);
    var request = new XMLHttpRequest();
    request.open('GET', '/fastest-finger-first/challenge?id=' + id, false);
    request.send(null);
    if (request.status === 200) {
        log(request.responseText);
        document.getElementById('delayMs').innerText = request.responseText;
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

function message(msg) {
    var request = new XMLHttpRequest();
    request.open('POST', '/fastest-finger-first/message', false);
    request.send(msg);
}

function disableButton(value) {
    log('disableButton() value: ' + value);
    var result = document.getElementById('result');
    result.innerHTML = '<input disabled type="button" value="' + value + '" />';
}

function log(body) {
    document.getElementById('log').innerText = body + "\n" + document.getElementById('log').innerText;
//    var request = new XMLHttpRequest();
//    request.open('POST', '/fastest-finger-first/log', false);
//    request.send(body);
}
