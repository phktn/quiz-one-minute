var reconnectWait;
let animationId = 0;
let problems = null;

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
    const source = new EventSource(contextPath + '/stream');
    source.addEventListener('message', function (e) {
        console.log('message data: ' + e.data);
        response = JSON.parse(e.data);
        if (response.problemSet != null) {
            onSelectProblemSet(response.problemSet);
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
        if (animationCnt % 5 == 0) {
            document.getElementById('question').innerHTML = '';
            document.getElementById('answer').innerHTML = '';
        }
        if (animationCnt < 60 && animationCnt % 5 == 0) {
            document.getElementById('question').innerHTML = problems[Math.floor(animationCnt / 5)].question;
        }
        if (animationCnt > 2 && animationCnt % 5 == 2) {
            document.getElementById('answer').innerHTML = problems[Math.floor(animationCnt / 5) - 1].answer;
        }
        for (let i = 0; i < animationCnt && i < 60; i++) {
            elements[i].className = `invisible`;
        }
        for (let i = animationCnt; i < 60; i++) {
            elements[i].className = `visible-success`;
        }
        animationCnt++;
        if (animationCnt == 66) {
            clearInterval(animationId);
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

function onSelectProblemSet(problemSet) {
    problems = problemSet.problems;
    if (problemSet.num == 0) {
        return;
    }
    document.getElementById('question').innerHTML = '';
    document.getElementById('answer').innerHTML = '';
    for (let i = 1; i <= 10; i++) {
        document.getElementById('problem-set-lamp-selected-' + i).className = 'problem-set-lamp-off';
    }
    document.getElementById('problem-set-lamp-selected-' + problemSet.num).className = 'problem-set-lamp-selected-on';
    document.getElementById('problem-set-lamp-selectable-' + problemSet.num).className = 'problem-set-lamp-off';
}

function reconnect() {
    if (reconnectWait == 0) {
        reconnectWait = 1;
        connect();
    } else {
        var waitSec = reconnectWait;
        reconnectWait *= 2;
        var id = setInterval(function () {
            waitSec--;
            if (waitSec <= 0) {
                clearInterval(id);
                connect();
            } else {
            }
        }, 1000);
    }
}

function log(body) {
    document.getElementById('log').innerText = body + "\n" + document.getElementById('log').innerText;
}
