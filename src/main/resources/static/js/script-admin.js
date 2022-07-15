var reconnectWait;
var quiz_window;

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
    const source = new EventSource(contextPath + '/stream');
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
    quiz_window.document.getElementById('nickname').innerText = $('#nickname').val();
}

function selectProblemSet(num) {
    $.post(contextPath + '/selectProblemSet', String(num), (data) => {
        console.log(`message data: ${data}`);
        onSetCorrectAnswerTotal(0);
    });
}

function startOneMinute() {
    $.post(contextPath + '/startOneMinute', (data) => {
        console.log(`message data: ${data}`);
        onSetCorrectAnswerTotal(0);
    });
}

function setCorrectAnswer(num) {
    $.post(contextPath + '/setCorrectAnswer', String(num), (data) => {
        console.log(`message data: ${data}`);
        response = JSON.parse(data);
        if (response.correctAnswerNum > 0) {
            onSetCorrectAnswerNum(response.correctAnswerNum);
        }
        if (response.correctAnswerTotal > -1) {
            onSetCorrectAnswerTotal(response.correctAnswerTotal);
        }
    });
}

function onSetCorrectAnswerNum(num) {
    quiz_window.document.getElementById('correct-answer-lamp-' + num).className = 'visible-danger';
}

function onSetCorrectAnswerTotal(total) {
    quiz_window.document.getElementById('correct-answer-total').innerText = total;
    switch (total) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
            quiz_window.document.getElementById('correct-answer-total').style = '';
            quiz_window.document.getElementById('prize').innerText = '0';
            quiz_window.document.getElementById('prize').style = 'color: hsl(348, 100%, 61%);';
            break;
        case 6:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: lime';
            quiz_window.document.getElementById('prize').innerText = '500';
            quiz_window.document.getElementById('prize').style = 'color: lime';
            break;
        case 7:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: darkorange';
            quiz_window.document.getElementById('prize').innerText = '1,000';
            quiz_window.document.getElementById('prize').style = 'color: darkorange';
            break;
        case 8:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: darkorange';
            quiz_window.document.getElementById('prize').innerText = '1,500';
            quiz_window.document.getElementById('prize').style = 'color: darkorange';
            break;
        case 9:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: darkorange';
            quiz_window.document.getElementById('prize').innerText = '2,000';
            quiz_window.document.getElementById('prize').style = 'color: darkorange';
            break;
        case 10:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: darkorange';
            quiz_window.document.getElementById('prize').innerText = '2,500';
            quiz_window.document.getElementById('prize').style = 'color: darkorange';
            break;
        case 11:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: darkorange';
            quiz_window.document.getElementById('prize').innerText = '3,000';
            quiz_window.document.getElementById('prize').style = 'color: darkorange';
            break;
        case 12:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: aqua';
            quiz_window.document.getElementById('prize').innerText = '5,000';
            quiz_window.document.getElementById('prize').style = 'color: aqua';
            break;
        default:
            quiz_window.document.getElementById('correct-answer-total').style = 'color: hsl(348, 100%, 61%);';
            quiz_window.document.getElementById('prize').innerText = 'Error';
            quiz_window.document.getElementById('prize').style = 'color: hsl(348, 100%, 61%);';
            break;
    }
}

function openQuizWindow() {
    quiz_window = window.open('/quiz-one-minute/','','width=670,height=790,menubar');
}
