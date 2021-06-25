let canvas_width;
let canvas_height;
let window_width;
let window_height

$( document ).ready(function() {
    connectToSocket();
    window_height = window.innerHeight - $("#navbar-element").height();
    window_width = window.innerWidth;
    canvas_width = window_height*0.7;
    canvas_height = window_width*0.45;

    let cnv = document.getElementById("canvas-game");
    cnv.height=canvas_width;
    cnv.width=canvas_height;
    cnv.style.border="1px solid blue";

    console.log(chat);

    initPainting();

    if(playerRole === "PAINTER") {
        hideChat();
    }
    else if(playerRole === "GUESSER") {
        disableCanvas();
    };


    $("#form-chat").submit(function (event) {
        event.preventDefault();

        let msg = document.getElementById("form-chat").msg.value;
        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "chat",
                "message": msg
            }));

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
});

function writeMessage(message){
    document.getElementById("chat").innerHTML =
        document.getElementById("chat").innerHTML + message + "<br />";
}

function hideChat() {
    document.getElementById("msg").style.display = "none";
}

