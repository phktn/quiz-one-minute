let quiz_window = null;
let animationId = 0;
let problems = null;

function setNickname() {
    quiz_window.document.getElementById('nickname').innerText = $('#nickname').val();
}

function selectProblemSet(num) {
    $.post('/selectProblemSet', String(num), (data) => {
        console.log(`message data: ${data}`);
        response = JSON.parse(data);
        onSelectProblemSet(response.problemSet);
        onSetCorrectAnswerTotal(0);
    });
}

function startOneMinute() {
    $.post('/startOneMinute', (data) => {
        console.log(`message data: ${data}`);
        onSetCorrectAnswerTotal(0);
        startAnimationOneMinute();
    });
}

function setCorrectAnswer(num) {
    $.post('/setCorrectAnswer', String(num), (data) => {
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

function startAnimation() {
    let elements = [];
    let animationCnt = 0;
    for (let i = 1; i <= 60; i++) {
        elements.push(quiz_window.document.getElementById('light-' + ('00' + i).slice(-2)));
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
        quiz_window.document.getElementById('correct-answer-lamp-' + i).className = `invisible`;
    }
    for (let i = 1; i <= 60; i++) {
        elements.push(quiz_window.document.getElementById('light-' + ('00' + i).slice(-2)));
    }
    if (animationId != 0) {
        clearInterval(animationId);
    }
    animationId = setInterval(function handler() {
        if (animationCnt % 5 == 0) {
            quiz_window.document.getElementById('question').innerHTML = '';
            quiz_window.document.getElementById('answer').innerHTML = '';
        }
        if (animationCnt < 60 && animationCnt % 5 == 0) {
            quiz_window.document.getElementById('question').innerHTML = problems[Math.floor(animationCnt / 5)].question;
        }
        if (animationCnt > 2 && animationCnt % 5 == 2) {
            quiz_window.document.getElementById('answer').innerHTML = problems[Math.floor(animationCnt / 5) - 1].answer;
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
        quiz_window.document.getElementById('problem-set-lamp-selected-' + i).className = 'problem-set-lamp-off';
        quiz_window.document.getElementById('problem-set-lamp-selectable-' + i).className = 'problem-set-lamp-selectable-on';
    }
}

function onSelectProblemSet(problemSet) {
    problems = problemSet.problems;
    quiz_window.document.getElementById('question').innerHTML = '';
    quiz_window.document.getElementById('answer').innerHTML = '';
    for (let i = 1; i <= 10; i++) {
        quiz_window.document.getElementById('problem-set-lamp-selected-' + i).className = 'problem-set-lamp-off';
    }
    if (problemSet.num != 0) {
        quiz_window.document.getElementById('problem-set-lamp-selected-' + problemSet.num).className = 'problem-set-lamp-selected-on';
        quiz_window.document.getElementById('problem-set-lamp-selectable-' + problemSet.num).className = 'problem-set-lamp-off';
    }
    for (let i = 1; i <= 12; i++) {
        document.getElementById('answer-' + i).innerText = problemSet.problems[i - 1].answer;
    }
}

function openQuizWindow() {
    quiz_window = window.open('/', '', 'width=670,height=790,menubar');
    quiz_window.addEventListener('DOMContentLoaded', () => {
        console.log('DOMContentLoaded');
        initProblemSetLamp();
        startAnimation();
        selectProblemSet(0);
    })
}
