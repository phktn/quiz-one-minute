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
        if (response.correctAnswerNum > 0) {
            onSetCorrectAnswer(response.correctAnswerNum);
        }
        if (response.startOneMinute == true) {
            startAnimationOneMinute();
        }
    }, false);
    source.addEventListener('open', function (e) {
        console.log("Connecting to the chat server..." + e);
        initProblemSetLamp();
        startAnimation();
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
    request.open('GET', '/quiz-one-minute/challenge?id=' + id + '&nickname=' + nickname, false);
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

function startAnimation() {
    let elements = [];
    let animationCnt = 0;
    for (let i = 1; i <= 60; i++) {
        elements.push(document.getElementById('light-' + ('00' + i).slice(-2)));
    }
    if (animationId != 0) {
        clearInterval(animationId);
    }
    animationId = setInterval(function handler() {
        animationCnt++;
        for (const i in elements) {
            if (Math.floor((i - animationCnt) / 5) % 3 == 0) {
                elements[i].className = `invisible`;
            } else {
                elements[i].className = `visible-success`;
            }
        }
        return handler;
    }(), 200);
}

function startAnimationOneMinute() {
    let elements = [];
    let animationCnt = 0;
    for (let i = 1; i <= 12; i++) {
        document.getElementById('correct-answer-lamp-' + i).className = `invisible`;
    }
    for (let i = 1; i <= 60; i++) {
        elements.push(document.getElementById('light-' + ('00' + i).slice(-2)));
    }
    if (animationId != 0) {
        clearInterval(animationId);
    }
    animationId = setInterval(function handler() {
        animationCnt++;
        if (animationCnt == 60) {
            clearInterval(animationId);
        }
        for (let i = 0; i < animationCnt; i++) {
            elements[i].className = `invisible`;
        }
        for (let i = animationCnt; i < 60; i++) {
            elements[i].className = `visible-success`;
        }
        return handler;
    }(), 1000);
}

function initProblemSetLamp() {
    for (let i = 1; i <= 10; i++) {
        document.getElementById('problem-set-lamp-selected-' + i).className = 'problem-set-lamp-off';
        document.getElementById('problem-set-lamp-selectable-' + i).className = 'problem-set-lamp-selectable-on';
    }
}

function onSelectProblemSet(num) {
    for (let i = 1; i <= 10; i++) {
        document.getElementById('problem-set-lamp-selected-' + i).className = 'problem-set-lamp-off';
    }
    document.getElementById('problem-set-lamp-selected-' + num).className = 'problem-set-lamp-selected-on';
    document.getElementById('problem-set-lamp-selectable-' + num).className = 'problem-set-lamp-off';
}

function onSetCorrectAnswer(num) {
    document.getElementById('correct-answer-lamp-' + num).className = 'visible-danger';
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
    request.open('POST', '/quiz-one-minute/start', false);
    request.send(null);
}

function skip(msg) {
    var request = new XMLHttpRequest();
    request.open('POST', '/quiz-one-minute/skip', false);
    request.send(msg);
}

function advance() {
    var request = new XMLHttpRequest();
    request.open('POST', '/quiz-one-minute/advance', false);
    request.send(null);
}

function message(msg) {
    var request = new XMLHttpRequest();
    request.open('POST', '/quiz-one-minute/message', false);
    request.send(msg);
}

function disableButton(value) {
    log('disableButton() value: ' + value);
    document.getElementById('delayMs').innerText = '';
    document.getElementById('hero').className = 'hero is-dark';
}

function log(body) {
    document.getElementById('log').innerText = body + "\n" + document.getElementById('log').innerText;
}
