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
