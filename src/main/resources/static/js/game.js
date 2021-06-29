let canvas_width;
let canvas_height;
let window_width;
let window_height

$( document ).ready(function() {

    if (gameStatus === "NEW" || gameStatus === "IN_PROGRESS") {
        connectToSocket();
    }

    window_height = window.innerHeight - $("#navbar-element").height();
    window_width = window.innerWidth;
    canvas_width = window_width*0.55;
    canvas_height = window_height*0.7;

    /*let cnv = document.getElementById("canvas-game");
    cnv.height=canvas_height;
    cnv.width=canvas_width;
    cnv.style.border="2px solid black";*/


    console.log(chat);

    if (gameStatus === "IN_PROGRESS") {
        beginTheGame();
    }

    $("#form-chat").submit(function (event) {
        event.preventDefault();

        let msg = document.getElementById("form-chat-input").value;
        if (msg) {
            stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
                {
                    "type": "chat",
                    "message": msg
                }));
        }

        document.getElementById("form-chat").reset();
    });

    $("#btn-canvas-clear").click(function (event) {
        event.preventDefault();

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "clear canvas"
            }));

    });

    $("#btn-start-game").click(function (event) {
        event.preventDefault();

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "begin game"
            }));

    });

    $("#btn-game-link").click(function (event) {
        event.preventDefault();

        const el = document.createElement('textarea');
        el.value = gameUUID;
        el.setAttribute('readonly', '');
        el.style.position = 'absolute';
        el.style.left = '-9999px';
        document.body.appendChild(el);
        el.select();
        document.execCommand('copy');
        document.body.removeChild(el);
    });

    $("#link-game-cancel").click(function (event) {
        event.preventDefault();

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "cancel game"
            }));
    });

    $("#link-game-leave").click(function (event) {
        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "leave game"
            }));
        stompClient.disconnect();
    });

});






