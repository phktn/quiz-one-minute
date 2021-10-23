var reconnectWait;

window.addEventListener('DOMContentLoaded', () => {
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
        if (response.problemSet != null) {
            onSelectProblemSet(response.problemSet);
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

function onSelectProblemSet(problemSet) {
    for (let i = 1; i <= 12; i++) {
        document.getElementById('answer-' + i).innerText = problemSet.problems[i - 1].answer;
    }
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

function setNickname() {
    $.post('/quiz-one-minute/setNickname', $('#nickname').val());
}

function selectProblemSet(num) {
    $.post('/quiz-one-minute/selectProblemSet', String(num));
}

function startOneMinute() {
    $.post('/quiz-one-minute/startOneMinute');
}

function setCorrectAnswer(num) {
    $.post('/quiz-one-minute/setCorrectAnswer', String(num));
}
